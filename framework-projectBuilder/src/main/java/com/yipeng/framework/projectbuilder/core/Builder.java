package com.yipeng.framework.projectbuilder.core;

import com.yipeng.framework.projectbuilder.model.*;
import com.yipeng.framework.projectbuilder.utils.BuildProperties;
import com.yipeng.framework.projectbuilder.utils.DataBaseUtil;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;

/**
 * @author: yibingzhou
 */
public class Builder {

    private static String ALL = "all";
    private static Map<String,Set<String>> notInModelMap;
    private static Map<String,Set<String>> notInParamMap;
    private static Map<String,Set<String>> notInResultMap;
    private static Set<String> noDao;
    private static Set<String> noService;
    private static Set<String> noController;
    private static Map<String,Object> baseInfo;

    public static void build() {
       notInModelMap = notInMap(BuildProperties.getString("builder.tables.notInModel"));
       notInParamMap = notInMap(BuildProperties.getString("builder.tables.notInParam"));
       notInResultMap = notInMap(BuildProperties.getString("builder.tables.notInResult"));
       noComponent();
       ProjectMeta projectMeta = getProjectMeta();
       Collection<Table> tables = DataBaseUtil.getTables();
       //生成table对象模型
       List<DbModelMeta> dbModelMetaList = new ArrayList<>();
       tables.forEach(table -> {
           dbModelMetaList.add(convertToDbModelMeta(projectMeta.getBasePackage(),table));
       });
        buildStandardPackage(projectMeta.getRootPath(), projectMeta.getRootClassPath());
        for(DbModelMeta dbModelMeta : dbModelMetaList){
            generator(dbModelMeta, projectMeta.getRootClassPath()+File.separator+"model"+File.separator+"db",dbModelMeta.getName()+"Model.java", "DbModel.ftl");
            generator(dbModelMeta, projectMeta.getRootClassPath()+File.separator+"mapper",dbModelMeta.getName()+"Mapper.java", "Mapper.ftl");
            if(!noDao.contains(dbModelMeta.getOriginalTableName())) {
                generator(dbModelMeta, projectMeta.getRootClassPath() + File.separator + "dao", dbModelMeta.getName() + "Dao.java", "Dao.ftl");
            }
            if(!noService.contains(dbModelMeta.getOriginalTableName())) {
                generator(dbModelMeta, projectMeta.getRootClassPath() + File.separator + "service", dbModelMeta.getName() + "Service.java", "Service.ftl");
            }
            if(!noController.contains(dbModelMeta.getOriginalTableName())) {
                generator(dbModelMeta, projectMeta.getRootClassPath() + File.separator + "controller", dbModelMeta.getName() + "Controller.java", "Controller.ftl");
                generator(dbModelMeta, projectMeta.getRootClassPath()+File.separator+"api",dbModelMeta.getName()+"Facade.java", "Facade.ftl");
                dbModelMeta.setSerialId(String.valueOf(generateSerialId()));
                generator(dbModelMeta, projectMeta.getRootClassPath()+File.separator+"result",dbModelMeta.getName()+"Result.java", "Result.ftl");
                generator(dbModelMeta, projectMeta.getRootClassPath()+File.separator+"param",dbModelMeta.getName()+"Param.java", "Param.ftl");
            }
        }
        //build spring-boot bootstrap
        if (!StringUtils.isEmpty(projectMeta.getApplicationName())) {
            generator(projectMeta, projectMeta.getRootClassPath(), projectMeta.getApplicationName() + "Application.java", "Bootstrap.ftl");
        }
        //build pom
        if( !projectMeta.isNoPom()) {
            generator(projectMeta, projectMeta.getRootPath(), "pom.xml", "Pom.ftl");

        }
        //copy readme
        generator(projectMeta, projectMeta.getRootPath(),"README.md", "README.md");
        //build properties
        Map<String,Object> dataSourcInfo = getDataSourceInfo();
        Map<String,Object> baseInfo = getBaseInfo();
        Map<String,Object> dataMap = new HashMap<>();
        dataMap.put("project", projectMeta);
        dataMap.put("datasource", dataSourcInfo);
        dataMap.put("base", baseInfo);
        if (!StringUtils.isEmpty(projectMeta.getApplicationName())) {
            generator(dataMap, projectMeta.getRootClassPath() + File.separator + "constant", projectMeta.getApplicationName() + "ErrorCode.java", "ErrorCode.ftl");
        }

        if (!projectMeta.isNoProperty()) {
            generator(dataMap, projectMeta.getRootPath() + File.separator + "src" + File.separator + "main" + File.separator + "resources", "application.yml", "Properties.ftl");
        }
    }

    private static ProjectMeta getProjectMeta() {
        String baseDir = BuildProperties.getString("builder.baseDir");
        String basePackage = BuildProperties.getString("builder.basePackage");
        String projectArtifact = BuildProperties.getString("builder.projectArtifact");
        String projectGroup = BuildProperties.getString("builder.projectGroup");
        String projectVersion = BuildProperties.getString("builder.projectVersion");
        String applicationName = BuildProperties.getString("builder.applicationName");
        String noPom = BuildProperties.getString("builder.noPom");
        String noProperty = BuildProperties.getString("builder.noProperty");

        if (StringUtils.isEmpty(baseDir)) {
            throw new NullPointerException("builder.baseDir can not be null");
        }
        if (StringUtils.isEmpty(basePackage)) {
            throw new NullPointerException("builder.basePackage can not be null");
        }
        if (StringUtils.isEmpty(projectArtifact)) {
            throw new NullPointerException("builder.projectArtifact can not be null");
        }
        if (StringUtils.isEmpty(projectGroup)) {
            throw new NullPointerException("builder.projectGroup can not be null");
        }
        if (StringUtils.isEmpty(projectVersion)) {
            throw new NullPointerException("builder.projectVersion can not be null");
        }
        ProjectMeta projectMeta = new ProjectMeta();
        projectMeta.setProjectArtifact(projectArtifact);
        projectMeta.setBaseDir(baseDir);
        projectMeta.setBasePackage(basePackage);
        projectMeta.setProjectGroup(projectGroup);
        projectMeta.setProjectVersion(projectVersion);
        //第一位大写
        if(!StringUtils.isEmpty(applicationName)) {
            projectMeta.setApplicationName(applicationName.substring(0, 1).toUpperCase().concat(applicationName.substring(1)));
        }
        projectMeta.setRootPath(projectRoot(projectMeta.getBaseDir(), projectMeta.getProjectArtifact()));
        projectMeta.setRootClassPath(generateRootClassPath(projectMeta.getRootPath(), projectMeta.getBasePackage()));
        projectMeta.setNoPom(StringUtils.isEmpty(noPom) ? false : Boolean.valueOf(noPom));
        projectMeta.setNoProperty(StringUtils.isEmpty(noProperty) ? false : Boolean.valueOf(noProperty));
        return projectMeta;
    }

    private static void noComponent() {
        String noDaoStr = BuildProperties.getString("builder.tables.noDao");
        String noServiceStr = BuildProperties.getString("builder.tables.noService");
        String noControllerStr = BuildProperties.getString("builder.tables.noController");
        noDao = new HashSet<>();
        noService = new HashSet<>();
        noController = new HashSet<>();
        if(!StringUtils.isEmpty(noDaoStr)) {
            Collections.addAll(noDao,noDaoStr.split(","));
            noService.addAll(noDao);
        }
        if(!StringUtils.isEmpty(noServiceStr)) {
            Collections.addAll(noService,noServiceStr.split(","));
            noController.addAll(noService);
        }
        if(!StringUtils.isEmpty(noControllerStr)) {
            Collections.addAll(noController,noControllerStr.split(","));
        }
    }

    private static Map<String,Set<String>> notInMap(String notIn) {
        Map<String ,Set<String>> notInMap = new HashMap<>();
        if (StringUtils.isEmpty(notIn)) {
            return notInMap;
        }
        String[] splitter = notIn.split(";");
        Arrays.stream(splitter).forEach(item -> {
            String[] tableFields = item.split("#");
            if(tableFields.length >1 ) {
                Set<String> notInFields = notInMap.get(tableFields[0]);
                if(notInFields == null) {
                    notInFields = new HashSet<>();
                    notInMap.put(tableFields[0], notInFields);
                }
                String[] fields = tableFields[1].split(",");
                for (String field : fields) {
                    if(!StringUtils.isEmpty(field)) {
                        notInFields.add(field);
                    }
                }
            }
        });
        return notInMap;
    }

    private static void buildStandardPackage(String projectRootPath, String rootClassPath) {
        String apiDir = rootClassPath+File.separator+"api";
        String constantDir = rootClassPath+File.separator+"constant";
        String paramDir = rootClassPath+File.separator+"param";
        String utilsDir = rootClassPath+File.separator+"utils";
        String resourcesDir = projectRootPath+File.separator+"src"+File.separator+"main"+File.separator+"resources";
        String testDir = projectRootPath+File.separator+"src"+File.separator+"test";
        String testJavaDir = testDir+File.separator+"java";
        String testResourcesDir = testDir+File.separator+"resources";
        String configurationDir = rootClassPath+File.separator+"configuration";
        mkdirs(apiDir,configurationDir,constantDir, paramDir, utilsDir, resourcesDir, testDir, testJavaDir, testResourcesDir);
    }

    private static Map<String, Object> getDataSourceInfo() {
        Map<String, Object> dataSourcInfo = new HashMap<>();
        dataSourcInfo.put("driverClass",BuildProperties.getString("builder.datasource.driver-class-name"));
        dataSourcInfo.put("url",BuildProperties.getString("builder.datasource.url"));
        dataSourcInfo.put("username",BuildProperties.getString("builder.datasource.username"));
        dataSourcInfo.put("password",BuildProperties.getString("builder.datasource.password"));
        return dataSourcInfo;
    }

    private static Map<String, Object> getBaseInfo() {
        if (baseInfo != null) {
            return baseInfo;
        }
        baseInfo = new HashMap<>();
        baseInfo.put("serverPort",BuildProperties.getString("builder.base.serverPort"));
        baseInfo.put("authorName",BuildProperties.getString("builder.base.authorName"));
        baseInfo.put("authorEmail",BuildProperties.getString("builder.base.authorEmail"));
        baseInfo.put("appId",BuildProperties.getString("builder.base.appId"));
        baseInfo.put("appInfoUrl",BuildProperties.getString("builder.base.appInfoUrl"));
        baseInfo.put("appPingUrl",BuildProperties.getString("builder.base.appPingUrl"));
        baseInfo.put("appDisconnectUrl",BuildProperties.getString("builder.base.appDisconnectUrl"));
        baseInfo.put("apiPrefix",BuildProperties.getString("builder.base.apiPrefix"));
        return baseInfo;
    }

    private static void mkdirs(String... paths) {
        for(String path : paths) {
            File f = new File(path);
            if (!f.exists()) {
                f.mkdirs();
            }
        }
    }
    private static String projectRoot(String baseDir, String applicationName) {
        return baseDir+File.separator+applicationName;
    }

    private static String generateRootClassPath(String projectRoot, String basePackage) {
        return projectRoot+File.separator+"src"+File.separator+"main"+File.separator+"java"+File.separator+(basePackage.replaceAll("\\.", Matcher.quoteReplacement(File.separator)));
    }

    private static DbModelMeta convertToDbModelMeta(String basePackage, Table table) {
        DbModelMeta dbModelMeta = new DbModelMeta();
        dbModelMeta.setPackageStr(basePackage);
        dbModelMeta.setName(convertSnakeToPasca(table.getFormattedName()));
        dbModelMeta.setCamelName(convertSnakeToCamel(table.getFormattedName()));
        dbModelMeta.setOriginalTableName(table.getOriginalName());
        if (!StringUtils.isEmpty(table.getRemarks()) && table.getRemarks().lastIndexOf("表") == table.getRemarks().length()-1) {
            table.setRemarks(table.getRemarks().substring(0,table.getRemarks().length()-1));
        }
        dbModelMeta.setComment(table.getRemarks());
        dbModelMeta.setPrimaryKeyName(table.getPrimaryKeyName());
        dbModelMeta.setSerialId(String.valueOf(generateSerialId()));
        convertToFieldMeta(dbModelMeta, table.getColumns());
        return dbModelMeta;
    }

    private static Long generateSerialId() {
        return RandomUtils.nextLong(1000000000000000000L,9000000000000000000L);
    }


    private static void convertToFieldMeta(DbModelMeta dbModelMeta, List<Column> columns) {
       if (CollectionUtils.isEmpty(columns)) {
           return;
       }
       List<FieldMeta> fieldMetas = new ArrayList<>();
        Set<String> notInModelFields = notInModelMap.get(dbModelMeta.getOriginalTableName());
        Set<String> allNotInModelFields = notInModelMap.get(ALL);
        Set<String> notInParamFields = notInParamMap.get(dbModelMeta.getOriginalTableName());
        Set<String> allNotInParamFields = notInParamMap.get(ALL);
        Set<String> notInResultFields = notInResultMap.get(dbModelMeta.getOriginalTableName());
        Set<String> allNotInResultFields = notInResultMap.get(ALL);
        AtomicBoolean hasCreatorId = new AtomicBoolean(false);
        AtomicBoolean hasUpdaterId = new AtomicBoolean(false);
       columns.forEach(column -> {
           FieldMeta fieldMeta = new FieldMeta();
           fieldMeta.setOriginalFieldName(column.getColumnName());
           fieldMeta.setFieldComment(column.getRemarks());
           fieldMeta.setFieldName(convertSnakeToCamel(column.getColumnName()));
           if(notInModelFields != null) {
               fieldMeta.setNotInModel(notInModelFields.contains(fieldMeta.getOriginalFieldName()));
           }
           if (!fieldMeta.isNotInModel()) {
               fieldMeta.setNotInModel(allNotInModelFields.contains(fieldMeta.getOriginalFieldName()));
           }
           if(notInParamFields != null) {
               fieldMeta.setNotInParam(notInParamFields.contains(fieldMeta.getOriginalFieldName()));
           }
           if (!fieldMeta.isNotInParam()) {
               fieldMeta.setNotInParam(allNotInParamFields.contains(fieldMeta.getOriginalFieldName()));
           }
           if(notInResultFields != null) {
               fieldMeta.setNotInResult(notInResultFields.contains(fieldMeta.getOriginalFieldName()));
           }
           if (!fieldMeta.isNotInResult()) {
               fieldMeta.setNotInResult(allNotInResultFields.contains(fieldMeta.getOriginalFieldName()));
           }
           toJavaType(dbModelMeta,fieldMeta, column.getColumnType());
           fieldMetas.add(fieldMeta);
           if(fieldMeta.getFieldName().equals("creatorId")) {
               hasCreatorId.set(true);
           }
           if(fieldMeta.getFieldName().equals("updaterId")) {
               hasUpdaterId.set(true);
           }
       });
        if(hasCreatorId.get() && hasUpdaterId.get()) {
            dbModelMeta.setHasManagedFields(true);
        }
       dbModelMeta.setFields(fieldMetas);
    }

    /**
     * JDBC Type           Java Type
     * CHAR                String
     * VARCHAR             String
     * LONGVARCHAR         String
     * NUMERIC             java.math.BigDecimal
     * DECIMAL             java.math.BigDecimal
     * BIT                 boolean
     * BOOLEAN             boolean
     * TINYINT             byte
     * SMALLINT            short
     * INTEGER             int
     * BIGINT              long
     * REAL                float
     * FLOAT               double
     * DOUBLE              double
     * BINARY              byte[]
     * VARBINARY           byte[]
     * LONGVARBINARY       byte[]
     * DATE                java.sql.Date
     * TIME                java.sql.Time
     * TIMESTAMP           java.sql.Timestamp
     * @param jdbcType
     * @return
     */
    private static void toJavaType(DbModelMeta dbModelMeta, FieldMeta fieldMeta, String jdbcType) {
        String javaType = "String";
        String importStr = null;

        switch (jdbcType) {
            case "BIGINT" :
                javaType = "Long";
                break;
            case "INT" :
                javaType =  "Integer";
                break;
            case "TINYINT" :
                javaType =  "Byte";
                fieldMeta.setByte(true);
                break;
            case "VARCHAR" :
                javaType =  "String";
                break;
            case "TEXT" :
                javaType =  "String";
                break;
            case "CHAR" :
                javaType =  "String";
                break;
            case "LONGVARCHAR" :
                javaType =  "String";
                break;
            case "DECIMAL" :
                importStr = "java.math.BigDecimal";
                javaType = "BigDecimal";
                fieldMeta.setBigDecimal(true);
                break;
            case "NUMERIC" :
                importStr = "java.math.BigDecimal";
                javaType = "BigDecimal";
                fieldMeta.setBigDecimal(true);
                break;
            case "BIT" :
                javaType = "Byte";
                break;
            case "BOOLEAN" :
                javaType = "Boolean";
                break;
            case "SMALLINT" :
                javaType = "Short";
                fieldMeta.setShort(true);
                break;
            case "REAL" :
                javaType = "Float";
                fieldMeta.setFloat(true);
                break;
            case "DOUBLE" :
                javaType = "Double";
                fieldMeta.setDouble(true);
                break;
            case "FLOAT" :
                javaType = "Double";
                fieldMeta.setDouble(true);
                break;
            case "DATE" :
                importStr = "java.util.Date";
                javaType = "Date";
                fieldMeta.setDate(true);
                break;
            case "TIME" :
                importStr = "java.sql.Time";
                javaType = "Date";
                fieldMeta.setDate(true);
                break;
            case "TIMESTAMP" :
                importStr = "java.util.Date";
                javaType = "Date";
                fieldMeta.setDate(true);
                break;
            default:
                break;
        }
        fieldMeta.setFieldType(javaType);
        Set<String> importList = dbModelMeta.getImportList();
        if(importList == null) {
            importList = new HashSet<>();
            dbModelMeta.setImportList(importList);
        }
        if(importStr != null) {
            importList.add(importStr);
        }
    }

    /**
     * 由下划线(蛇型）命名法，转化成帕斯卡命名法
     *
     * @param name
     * @return
     */
    private static String convertSnakeToPasca(String name) {
        if (name == null || name.length() == 0) {
            return name;
        }
        name = convertSnakeToCamel(name);
        char[] nameChars = name.toCharArray();
        nameChars[0] -= 32;
        return String.valueOf(nameChars);
    }

    /**
     * 由下划线(蛇型）命名法，转化成驼峰命名法
     *
     * @param name
     */
    private static String convertSnakeToCamel(String name) {
        if (name == null || name.length() == 0) {
            return name;
        }

        char[] nameChars = name.toCharArray();
        boolean previousLine = false;
        for (int i=0; i<nameChars.length; i++) {
            if (previousLine) {
                nameChars[i] -= 32;
                previousLine = false;
            }
            if (nameChars[i] == "_".charAt(0)) {
                previousLine = true;
            }
        }
        return String.valueOf(nameChars).replaceAll("_", "");
    }

    private static void generator(Map<String,Object> dataMap, String rootPath, String fileName, String templateName) {
        Configuration configuration = new Configuration();
        Writer out = null;
        String path = Thread.currentThread().getContextClassLoader().getResource("templates").getPath();
        try {
            configuration.setDirectoryForTemplateLoading(new File(path));
            Template template = configuration.getTemplate(templateName);
            File rootDir = new File(rootPath);
            if(!rootDir.exists()){
                rootDir.mkdirs();
            }
            File docFile = new File(rootPath+File.separator+fileName);
            out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(docFile)));
            template.process(dataMap, out);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != out) {
                    out.flush();
                    out.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }

    private static void generator(Object o, String rootPath, String fileName, String templateName) {
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("object", o);
        Map<String,Object> baseInfo = getBaseInfo();
        dataMap.put("base", baseInfo);
        generator(dataMap, rootPath, fileName, templateName);
    }
}
