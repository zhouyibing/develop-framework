package com.yipeng.framework.core.model.db;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Transient;

/**
 * 标记一个类可用用作于数据库记录的读取
 * 即标记对象可用作数据库的查询参数和返回结果集的存储
 * NOTE:
 * 1.实现该接口的类，字段必须设置为对象类型，基本类型使用其包装类
 * 因为在构造查询参数时可以过滤掉非空字段，最好不要给字段设置默认值。
 * 2.特别重要：不要将字段名设置为“selective”，selective做为框架的保留名称。会在进行数据库查询/存取的时候用该selective的参数值做判断
 * 3.必须实现无参构造方法
 * @author: yibingzhou
 */
public class AccessObject {

    /**
     * 保存或者查询实体时，null的属性是否传递
     * true 不保留null
     * false 保留null
     * @return
     */
    @Transient
    @Getter
    @Setter
    @ApiModelProperty("保存或者查询实体时，null的属性是否传递")
    private Boolean selective = true;
}
