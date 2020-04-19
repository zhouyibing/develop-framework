package com.yipeng.framework.common.param;

import com.yipeng.framework.common.exception.ErrorCode;
import com.yipeng.framework.common.exception.ExceptionUtil;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.validation.Valid;
import java.io.Serializable;
import java.util.regex.Pattern;

/**
 * 分页请求参数
 * @author: yibingzhou
 */
@Data
@Slf4j
public class PageParam<T> implements Serializable {

    private static final Pattern PATTERN_ORDER_BY = Pattern.compile("[a-zA-z_]+[\\w\\.]*");
    public final static int DEFAULT_PAGE_SIZE = 10;
    public static final int PAGE_SIZE_LIMIT = 100;

    private static final long serialVersionUID = 3638102033998845020L;

    /** 当前页*/
    private int current = 1;

    /** 每页条数*/
    private int pageSize = DEFAULT_PAGE_SIZE;

    /** 查询参数*/
    @Valid
    private T params;

    /** 排序字段名称*/
    @Getter
    private String orderBy;

    /** 是否降序*/
    @Getter
    @Setter
    private boolean desc;

    public void setOrderBy(String orderBy) {
        if (StringUtils.isNotBlank(orderBy) && !PATTERN_ORDER_BY.matcher(orderBy).matches()) {
            log.warn("parameter 'orderBy' is illegal,ignore this setting.");
        } else {
            this.orderBy = orderBy;
        }
    }

    public void setCurrent(int page) {
        if (page < 1) {
            page = 1;
        }

        this.current = page;
    }

    public void setPageSize(int pageSize) {
        if (pageSize <=0 || pageSize > PAGE_SIZE_LIMIT) {
            pageSize = DEFAULT_PAGE_SIZE;
            log.warn("pageSize should be large than {} and less than {}", 0, PAGE_SIZE_LIMIT);
        }
        this.pageSize = pageSize;
    }

    /**
     * 排序类型
     */
    public String getOrderType() {
        return desc ? "desc" : "asc";
    }
}
