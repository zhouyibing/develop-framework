package com.fido.framework.core.constants.annotation;

import com.fido.framework.core.constants.CaseRule;
import com.fido.framework.core.constants.Direction;
import com.fido.framework.core.service.converter.Converter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * 字段转换时设置映射关系
 * @author: yibingzhou
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Repeatable(FieldMappings.class)
public @interface FieldMapping {

    /**
     * 映射的字段名称
     * 映射的source对象字段,默认和source字段同名
     * @return
     */
    @AliasFor("name")
    String value() default StringUtils.EMPTY;

    @AliasFor("value")
    String name() default StringUtils.EMPTY;

    /***
     * 自定义转换器，class必须实现 @see com.fido.framework.common.service.converter.Converter接口
     * @return
     */
    Class converter() default Converter.class;

    /**
     * 大小写规则
     * @return
     */
    CaseRule caseRule() default CaseRule.DEFAULT;

    /**
     * 映射方向
     * 输出名为name的字段
     * 绑定名为name的字段
     * @return
     */
    Direction direction() default Direction.BOTH;
}
