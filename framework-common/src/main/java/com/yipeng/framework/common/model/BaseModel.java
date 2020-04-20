package com.yipeng.framework.common.model;

import com.yipeng.framework.common.constants.BooleanEnum;
import com.yipeng.framework.common.constants.Direction;
import com.yipeng.framework.common.constants.annotation.ConvertExclude;
import com.yipeng.framework.common.constants.annotation.FieldMapping;
import com.yipeng.framework.common.service.converter.BooleanIntegerConverter;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

/**
 * 基本表结构模型
 * @author: yibingzhou
 */
@Data
public class BaseModel<K extends Number> extends AccessObject implements Serializable{

    private static final long serialVersionUID = 2341576501122011554L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private K id;

    @Column(insertable = false, updatable = false)
    /** 创建时间*/
    @ConvertExclude
    private Date createTime;

    /** 创建者*/
    @ConvertExclude
    private String creatorId;

    /** 更新者*/
    @ConvertExclude
    private String updaterId;

    @Column(insertable = false, updatable = false)
    /** 更新时间*/
    @ConvertExclude
    private Date updateTime;

    /** 逻辑删除标识*/
    @FieldMapping(name = "deleted",direction = Direction.OUT, converter = BooleanIntegerConverter.class)
    @ConvertExclude
    private Integer logicDelete = BooleanEnum.FALSE.getCode();
}
