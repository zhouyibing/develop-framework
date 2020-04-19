package com.yipeng.framework.common.model;

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
public class BaseModel extends AccessObject implements Serializable{

    private static final long serialVersionUID = 2341576501122011554L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(insertable = false, updatable = false)
    /** 创建时间*/
    private Date createTime;

    /** 创建者*/
    private String creatorId;

    /** 更新者*/
    private String updaterId;

    @Column(insertable = false, updatable = false)
    /** 更新时间*/
    private Date updateTime;

    /** 逻辑删除标识*/
    private Integer logicDelete = 0;
}
