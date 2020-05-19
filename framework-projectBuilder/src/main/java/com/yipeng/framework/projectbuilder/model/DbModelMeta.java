package com.yipeng.framework.projectbuilder.model;

import lombok.Data;

import java.util.List;
import java.util.Set;

/**
 * @author: yibingzhou
 */
@Data
public class DbModelMeta {
    private String packageStr;
    private String primaryKeyName;
    private String primaryKeyType = "Long";
    private String serialId;
    private String name;
    private String camelName;
    private String originalTableName;
    private String comment;
    private List<FieldMeta> fields;
    private Set<String> importList;
    private boolean hasManagedFields;
}
