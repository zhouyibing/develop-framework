###服务器配置###
server:
  port: ${base.serverPort}
  tomcat:
    uri-encoding: UTF-8
    max-theads: 20
    maxConnections: 300

###开发框架里的一些配置###
dev-framework:
  ###是否允许跨域访问###
  allowedCrossDomain: true
  ###应用基本信息###
  appInfo:
    appId: '${base.appId}'
    infoUrl: '${base.appInfoUrl}'
    pingUrl: '${base.appPingUrl}'
    disconnectUrl: '${base.appDisconnectUrl}'
  ###api接口配置信息 ignorePaths:需要过滤的接口路径，支持ant matcher
  ###classPathUrlPrefix:指定类路径(可包名)下的url前缀（可多个,多个时只会选择你第一个匹配到的路径，所以把最准确的路径写到前面） 类路径支持ant matcher规则###
  api:
    ignorePaths: ''
    classPathUrlPrefix: '${project.basePackage}.controller.**@/api'

###日志配置###
logging:
  level:
    root: INFO
    com.yipeng.*: DEBUG
    org.apache.ibatis: DEBUG
    com.github.pagehelper: DEBUG
    com.github.pagehelper.mapper: INFO
    org.springframework.jdbc.datasource: DEBUG
    org.springframework: INFO
###数据访问配置###
spring:
  application:
    name: ${project.projectArtifact}
  datasource:
    type: com.alibaba.druid.pool.DruidDataSource
    driver-class-name: ${datasource.driverClass}
    url: ${datasource.url}
    username: ${datasource.username}
    password: ${datasource.password!''}
    druid:
      initialSize: 5
      minIdle: 5
      maxActive: 20
      maxWait: 60000
      timeBetweenEvictionRunsMillis: 60000
      minEvictableIdleTimeMillis: 300000
      validationQuery: SELECT 1 FROM DUAL
      testWhileIdle: true
      testOnBorrow: false
      testOnReturn: false
      poolPreparedStatements: true
      maxPoolPreparedStatementPerConnectionSize: 20
      filters: stat
      connectionProperties: druid.stat.mergeSql=true;druid.stat.slowSqlMillis=5000
      useGlobalDataSourceStat: true
      stat-view-servlet:
        allow: 127.0.0.1
        enabled: true
        login-password: admin
        login-username: admin
        reset-enable: false
        url-pattern: /druid/*
      web-stat-filter:
        enabled: true
        url-pattern: /*
####mybatis&&通用Mapper###
mapper:
  identity: MYSQL
mybatis:
  executorType: SIMPLE
####分页插件###
pagehelper:
  helperDialect: mysql
  params: count=countSql
  reasonable: true
  supportMethodsArguments: true

###swagger配置###
swagger:
  basePackage: ${project.basePackage}
  title: ${project.projectArtifact}服务api
  description: ${project.projectArtifact}服务api
  version: 1.1
  contact:
    name: ${base.authorName}
    email: ${base.authorEmail!''}
  headers: 'token'
  needTokenPaths: