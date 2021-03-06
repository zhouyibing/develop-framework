package com.fido.framework.projectbuilder.model;

import lombok.Data;

/**
 * @author: yibingzhou
 */
@Data
public class ProjectMeta {
    private String baseDir;
    private String basePackage;
    private String rootPath;
    private String projectArtifact;
    private String projectGroup;
    private String rootClassPath;
    private String projectVersion;
    private String applicationName;
    private boolean noPom = false;
    private boolean noProperty = false;
}
