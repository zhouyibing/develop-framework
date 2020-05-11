package com.yipeng.framework.common.model.db;

import com.yipeng.framework.common.constants.BooleanEnum;
import com.yipeng.framework.common.model.db.AccessObject;
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
public class BaseModel<K extends Comparable> extends AccessObject implements Serializable{

    private static final long serialVersionUID = 2341576501122011554L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private K id;

    @Column(insertable = false, updatable = false)
    /** 创建时间*/
    private Date createTime;

    @Column(insertable = false, updatable = false)
    /** 更新时间*/
    private Date updateTime;
    /** 逻辑删除标识*/
    private Integer logicDelete = BooleanEnum.FALSE.getCode();

    /**
     * 定义主键名称
     * 子类如果想要改名，重写这个方法
     * @return
     */
    public String primaryKeyName(){
        return "id";
    }
}
