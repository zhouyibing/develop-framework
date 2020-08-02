###服务器配置###
server:
  port: ${base.serverPort}
  tomcat:
    uri-encoding: UTF-8
    max-theads: 20
    maxConnections: 300

###开发框架里的一些配置###
dev-framework:
  ###是否允许跨域访问，默认false###
  allowedCrossDomain: true
  ###应用基本信息###
  appInfo:
    ###应用id
    appId: '${base.appId}'
    ###去应用管理中心获取应用信息的请求地址
    infoUrl: '${base.appInfoUrl}'
    ###应用管理中心的心跳地址
    pingUrl: '${base.appPingUrl}'
    ###应用管理中心主动断开连接的地址，应用管理中心在30秒后没有接收到心跳，标记应用已停止运行
    disconnectUrl: '${base.appDisconnectUrl}'
  ###api接口配置信息
  api:
    ###restful接口调用超时值ms，默认100
    timeout: 100
    ###是否开启restful接口日志打印，默认false
    debug: false
    ###httpValue，http请求拦截，进行一些日志打印，token验证，权限验证等，后续会加入些流控措施
    httpValue:
      ###httpValue拦截地址,默认/**,多个逗号分隔
      pathPatterns: '${base.apiPrefix}'
      ###哪些路径需要token验证，默认空，都不需要，多个逗号分隔
      tokenPathPattern:
      ###哪些路径需要权限访问验证,默认空，都不需要，多个逗号分隔
      authPathPattern:
    ###需要忽略掉的请求路径(忽略后的路径请求会报404，同时接口不会在swagger文档中显示),支持ant matcher，多个逗号分隔
    ignorePaths: ''
    ###指定类路径(可包名)下的url前缀（可多个,多个时只会选择你第一个匹配到的路径，所以把最准确的路径写到前面） 类路径支持ant matcher规则 多个逗号分隔 @后面接url前缀###
    classPathUrlPrefix: '${project.basePackage}.controller.**@${base.apiPrefix}'

###日志配置###
logging:
  level:
    root: INFO
    com.fido.*: DEBUG
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
  ###基本包扫描路径
  basePackage: ${project.basePackage}
  ###接口文档title
  title: ${project.projectArtifact}服务api
  ###接口文档描述
  description: ${project.projectArtifact}服务api
  ###接口版本
  version: 1.1
  ###控制哪个接口的模型对象里哪些字段不展示在swagger里,@前为接口路径，@后第一个参数为接口的参数索引，后接需要过滤的字段','分割；如果接口有多个参数需要过滤用@连接
  modelPropertyFilterConfig: '/**/createIfAbsent@0,id;
                              /**/create@0,id;
                              /**/delete@0,selective,id;
                              /**/get@0,selective,id;
                              /**/logicDelete@0,selective,id'
  ###联系人信息
  contact:
    name: ${base.authorName}
    email: ${base.authorEmail!''}
  ###需要设置的请求头名称，多个逗号分隔
  headers: 'token'
  ###哪些接口需要使用token header，多个逗号分隔
  needTokenPaths: