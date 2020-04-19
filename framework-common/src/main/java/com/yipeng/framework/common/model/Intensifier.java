package com.yipeng.framework.common.model;

import com.yipeng.framework.common.constants.Priority;
import lombok.Getter;

import java.util.function.Function;


/**
 * 增强器
 * @author: yibingzhou
 */
public class Intensifier {
    @Getter
    private Function before;
    @Getter
    private Function after;
    /** 是否使用前置增强处理后的返回结果*/
    @Getter
    private boolean useBeforeEnhanceResult = true;
    /** 是否使用后置增强处理后的返回结果*/
    @Getter
    private boolean useAfterEnhanceResult = true;

    /** 增强器名称*/
    @Getter
    private String name;

    /** 优先级*/
    @Getter
    private Integer priority = 0;

    public Intensifier(String name) {
        this.name = name;
    }

    public Intensifier before(Function func) {
        this.before = func;
        return this;
    }

    public Intensifier after(Function func) {
        this.after = func;
        return this;
    }

    public Intensifier useBeforeEnhanceResult(boolean used) {
        this.useBeforeEnhanceResult = used;
        return this;
    }

    public Intensifier useAfterEnhanceResult(boolean used) {
        this.useAfterEnhanceResult = used;
        return this;
    }

    public Intensifier priority(Integer priority) {
        if(priority != null) {
            this.priority = priority;
        }
        return this;
    }
}
