package com.yipeng.framework.common.web.controller;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ReflectUtil;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.yipeng.framework.common.api.BaseApi;
import com.yipeng.framework.common.exception.ErrorCode;
import com.yipeng.framework.common.exception.ExceptionUtil;
import com.yipeng.framework.common.model.*;
import com.yipeng.framework.common.param.PageParam;
import com.yipeng.framework.common.result.Result;
import com.yipeng.framework.common.service.BaseService;
import com.yipeng.framework.common.utils.Precondition;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.validation.Valid;
import java.lang.reflect.ParameterizedType;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

/**
 * @author: yibingzhou
 */
public class BaseController<R, S extends BaseService> implements BaseApi {
    private final static String HEADER_TOKEN = "token";
    protected final static String ALL = "*";

    @Autowired
    protected S service;
    private Class<R> defaultResultClass;
    /**
     * 自定义的各方法返回结果类型，没有定义取defaultResultClass
     */
    private Map<String, Class> resultClassMap = new HashMap<>();

    private Map<String, Set<Intensifier>> intensifierMap = new HashMap<>();
    private Comparator<Intensifier> intensifierComparable = (o1, o2) -> {
        if(o1.getPriority() == o2.getPriority()) return 0;
        return o1.getPriority() > o2.getPriority() ? -1 : 1;
    };

    protected void addIntensifier(Intensifier intensifier) {
        if(intensifier == null) return;
        Set<Intensifier> intensifiers = intensifierMap.get(intensifier.getName());
        if(null == intensifiers) {
            intensifiers = Sets.newTreeSet(intensifierComparable);
            intensifierMap.put(intensifier.getName(), intensifiers);
        }
        intensifiers.add(intensifier);
    }

    protected void setResultClass(String methodName, Class clzz) {
        resultClassMap.put(methodName, clzz);
    }

    private Class getResultClass(String methodName) {
        Class clzz = resultClassMap.get(methodName);
        if(clzz != null) return clzz;
        if(defaultResultClass != null ) return defaultResultClass;
        try {
            ParameterizedType pt = (ParameterizedType) this.getClass().getGenericSuperclass();
            defaultResultClass = (Class<R>) pt.getActualTypeArguments()[0];
            return defaultResultClass;
        } catch (Exception e) {
            throw ExceptionUtil.doThrow(ErrorCode.SERVER_INTERNAL_ERROR.msg(e.getMessage()));
        }
    }

    /**
     * 从header里获取token
     * @return
     */
    public String getAccessToken() {
        String token = getHeader(HEADER_TOKEN);
        if (StringUtils.isEmpty(token)) {
            ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            token = sra.getRequest().getParameter(HEADER_TOKEN);
        }
        return token;
    }

    /**
     * 从请求里获取指定名称的header
     * @return
     */
    public String getHeader(String name) {
        ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        String value = sra.getRequest().getHeader(name);
        return value;
    }

    @Override
    @PostMapping("/createIfAbsent")
    @ApiOperation("不存在时创建")
    public <P extends BaseParam> Result createIfAbsent(@Valid @RequestBody P param) {
        String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        return enhance(methodName, param, (p) -> service.createIfAbsent(p,p));
    }

    @Override
    @PostMapping("/create")
    @ApiOperation("创建")
    public <P extends BaseParam> Result create(@Valid @RequestBody P param) {
        String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        return enhance(methodName, param, (p) -> service.create(p));
    }

    @Override
    @PostMapping("/save")
    @ApiOperation("保存")
    public <P extends BaseParam> Result save(@Valid @RequestBody P param) {
        String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        return enhance(methodName, param, (p) -> service.save(p));
    }

    protected  <T, R> Result enhance(String intensifierName, T param, Function<T, R> main) {
        Object ret = enhanceNoResultWrapped(intensifierName, param, main);
        return Result.success(ret);
    }
    protected <T,R> Object enhanceNoResultWrapped(String intensifierName, T param, Function<T, R> main) {
        //核心逻辑调用前的处理
        Set<Intensifier> myIntensifiers = intensifierMap.get(intensifierName);
        Set<Intensifier> allIntensifiers = intensifierMap.get(ALL);
        Set<Intensifier> intensifiers = Sets.newTreeSet(intensifierComparable);
        if(allIntensifiers != null) intensifiers.addAll(allIntensifiers);
        if(myIntensifiers != null) intensifiers.addAll(myIntensifiers);
        if(CollectionUtil.isNotEmpty(intensifiers)) {
            for(Intensifier intensifier: intensifiers) {
                Object ret = intensifier.getBefore() == null ? null : intensifier.getBefore().apply(param);
                if(ret != null && intensifier.isUseBeforeEnhanceResult()) {
                    if(!param.getClass().isAssignableFrom(ret.getClass())) throw ExceptionUtil.doThrow(ErrorCode.PARAM_TYPE_NOT_MATCH.msg("前置处理返回结果类型必须是传入参数类型同类或子类"));
                    param = (T) ret;
                }
            }
        }
        R res = main.apply(param);
        //核心逻辑调用后的处理
        if(CollectionUtil.isNotEmpty(allIntensifiers)) {
            for(Intensifier intensifier: allIntensifiers) {
                Object ret = intensifier.getAfter() == null ? null : intensifier.getAfter().apply(param);
                if(ret != null && intensifier.isUseBeforeEnhanceResult()) {
                    param = (T) ret;
                }
            }
        }
        return res;
    }

    @Override
    @PostMapping("/saveList")
    @ApiOperation("批量保存")
    public <P extends BaseParam> Result saveList(@Valid @RequestBody ValidList<P> params) {
        String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        return enhance(methodName, params, (p) -> service.save(p));
    }

    @Override
    @PostMapping("/delete")
    @ApiOperation("删除")
    public <P extends BaseParam> Result delete(@Valid @RequestBody P param) {
        String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        return enhance(methodName, param, (p) -> service.deleteAllMatch(p));
    }

    @Override
    @PostMapping("/deleteByPk")
    @ApiOperation("根据主键删除")
    public <P extends BaseParam> Result deleteByPk(@ApiParam(name = "pk", value = "主键", required = true) @RequestBody P pk) {
        Comparable pkValue  = getPk(pk);
        String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        return enhance(methodName, pkValue, (p) -> service.deleteByPk(p));
    }

    @Override
    @PostMapping("/deleteByPkList")
    @ApiOperation("批量根据主键删除")
    public <P extends BaseParam> Result deleteByPkList(@ApiParam(name = "pk", value = "主键列表", required = true) @RequestBody List<P> pks) {
        List list = getPks(pks);
        String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        return enhance(methodName, list, (p) -> service.deleteByPks(p));
    }

    @Override
    @PostMapping("/logicDelete")
    @ApiOperation("逻辑删除")
    public <P extends BaseParam> Result logicDelete(@RequestBody P param) {
        String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        return enhance(methodName, param, (p) -> service.logicDeleteAllMatch(p));
    }

    @Override
    @PostMapping("/logicDeleteByPk")
    @ApiOperation("根据主键逻辑删除")
    public <P extends BaseParam> Result logicDeleteByPk(@ApiParam(name = "pk", value = "主键", example = "1", required = true) @RequestBody P pk) {
        Comparable pkValue  = getPk(pk);
        String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        return enhance(methodName, pkValue, (p) -> service.logicDeleteByPk(p));
    }

    @Override
    @PostMapping("/logicDeleteByPkList")
    @ApiOperation("批量根据主键逻辑删除")
    public <P extends BaseParam> Result logicDeleteByPkList(@ApiParam(name = "pk", value = "主键列表", required = true) @RequestBody List<P> pks) {
        List list = getPks(pks);
        String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        return enhance(methodName, list, (p) -> service.logicDeleteByPks(p));
    }

    @Override
    @PostMapping("/update")
    @ApiOperation("更新")
    public <P extends BaseParam> Result update(@Valid @RequestBody P param) {
        Comparable pkValue = getPk(param);
        String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        return enhance(methodName, param, (p) -> service.update(pkValue, p));
    }

    @Override
    @PostMapping("/updateList")
    @ApiOperation("批量更新")
    public <P extends BaseParam> Result updateList(@Valid @RequestBody ValidList<P> param) {
        String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        return enhance(methodName, param, (p) -> {
            AtomicInteger failed = new AtomicInteger(0);
            AtomicInteger success = new AtomicInteger(0);
            List<Comparable> list = getPks(param);
            for(int i=0;i<list.size();i++) {
                if(service.update(list.get(i), param.get(i))) {
                    success.incrementAndGet();
                } else {
                    failed.incrementAndGet();
                }
            }
            ResultOverview resultOverview = new ResultOverview();
            resultOverview.setFail(failed.get());
            resultOverview.setSuccess(success.get());
            return resultOverview;
        });
    }

    @Override
    @PostMapping("/getByPk")
    @ApiOperation("根据主键查询")
    public <P extends BaseParam> Result getByPk(@ApiParam(name = "pk", value = "主键",required = true) @RequestBody P pk) {
        Comparable pkValue  = getPk(pk);
        String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        return enhance(methodName, pkValue, (p) -> service.queryByPk(p, getResultClass(methodName)));
    }

    @Override
    @PostMapping("/getByPks")
    @ApiOperation("批量根据主键查询")
    public <P extends BaseParam> Result getByPkList(@ApiParam(name = "pks", value = "主键列表", required = true) @RequestBody List<P> pks) {
        List list = getPks(pks);
        String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        return enhance(methodName, list, (p) -> service.queryByPks(p,getResultClass(methodName)));
    }

    @Override
    @PostMapping("/get")
    @ApiOperation("条件查询")
    public <P extends BaseParam> Result get(@Valid @RequestBody P param) {
        String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        return enhance(methodName, param, (p) -> service.queryAllMatch(p,getResultClass(methodName)));
    }

    @Override
    @PostMapping("/page")
    @ApiOperation("分页查询")
    public <P extends BaseParam> PageInfo page(@Valid @RequestBody PageParam<P> pageParam) {
        String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        return (PageInfo) enhanceNoResultWrapped(methodName, pageParam, (p) -> service.pageAllMatch(p.getParams(),p.getOrderBy(), p.getOrderType(), p.getCurrent(), p.getPageSize(), getResultClass(methodName)));
    }

    private <P extends BaseParam> Comparable getPk(P param) {
        Precondition.checkNotNull(param, "主键不能为空");
        Object pkValue  = ReflectUtil.getFieldValue(param, param.primaryKey());
        Precondition.checkNotNull(pkValue, "主键不能为空");
        return (Comparable) pkValue;
    }

    private <P extends BaseParam> List<Comparable> getPks(List<P> pks) {
        Precondition.checkNotEmpty(pks, "主键列表不能为空");
        List<Comparable> list = Lists.newArrayList();
        pks.forEach(pk -> {
            Object pkValue = ReflectUtil.getFieldValue(pk, pk.primaryKey());
            Precondition.checkNotNull(pkValue, "主键不能为空");
            list.add((Comparable) pkValue);
        });
        return list;
    }
}
