package com.yipeng.framework.core.model.biz;

import com.yipeng.framework.core.exception.ErrorCode;
import com.yipeng.framework.core.exception.ExceptionUtil;
import com.yipeng.framework.core.utils.PathMatcherUtil;
import com.yipeng.framework.core.utils.Precondition;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * swagger model字段过滤
 * @author: yibingzhou
 */
@Component
public class ModelPropertyFilter implements InitializingBean {
    @Value("${swagger.modelPropertyFilterConfig:}")
    private String modelPropertyFilterConfig;

    private Map<String, Map<Integer, FilterParam>> filterParamMap = new ConcurrentHashMap<>();

    public void put(String path, FilterParam filterParam) {
        Map<Integer, FilterParam> in = filterParamMap.get(path);
        if(in == null) {
            in = new ConcurrentHashMap<>();
            filterParamMap.put(path, in);
        }
        FilterParam param = in.get(filterParam.modelParamPos);
        if(param == null) {
            in.put(filterParam.modelParamPos, filterParam);
        } else {
            param.fields.addAll(filterParam.fields);
        }
    }

    public FilterParam getFilterParam(String path, int pos) {
        if(filterParamMap.isEmpty()) {
            return null;
        }
        //优先完全匹配
        Map<Integer, FilterParam> in = filterParamMap.get(path);
        FilterParam filterParam = in == null ? null : in.get(pos);
        if(filterParam == null) {
            //后模糊匹配
            for(String key :filterParamMap.keySet()) {
                if(PathMatcherUtil.match(key, path)) {
                    in = filterParamMap.get(key);
                    filterParam = in.get(pos);
                    if(filterParam != null) {
                        break;
                    }
                }
            }
        }
        return filterParam;
    }

    private void parse(String modelPropertyFilterConfig) {
        if(StringUtils.isBlank(modelPropertyFilterConfig)) {return;}
        String[] items = modelPropertyFilterConfig.split(";");
        if(items == null || items.length == 0) {return;}
        for(String item : items) {
            String[] pathFileds = item.trim().split("@");
            if(pathFileds.length < 2) {continue;}
            for(int i = 1; i < pathFileds.length; i++) {
                String[] slots = pathFileds[i].split(",");
                if(!Precondition.checkNumber(slots[0])) {
                    throw ExceptionUtil.doThrow(ErrorCode.ILLEGAL_ARGUMENT.msg("参数位置必须是整数:"+item));
                }
                put(pathFileds[0], new FilterParam().modelParamPos(Integer.parseInt(slots[0])).addField(1, slots));
            }
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        parse(modelPropertyFilterConfig);
    }

    @Data
    public static class FilterParam {

        /** model参数对象的位置 0开始*/
        private int modelParamPos;

        /** 需要过滤哪些字段 */
        private Set<String> fields;

        public FilterParam modelParamPos(int pos) {
            this.modelParamPos = pos;
            return this;
        }

        public FilterParam addField(String... fs) {
            return addField(0, fs);
        }

        public FilterParam addField(int startPos, String... fs) {
            if(null == fs) {
                return this;
            }
            if(this.fields == null) {
                this.fields = new HashSet<>();
            }
            for(int i = startPos;i < fs.length; i++) {
                this.fields.add(fs[i]);
            }
            return this;
        }
    }
}
