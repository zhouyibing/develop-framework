1.所有api接口先建立facade接口对象 放在api包里

这样做的目的是为以后的微服务化做准备，微服务的调用都是通过api接口调用，而不是直接请求controller.

这个api包下的接口，后面会加入feign

先统一标准

2.所有对外访问接口controller 放在controller包下，并且
实现的controller必须继承 BaseController<Result,Service>

BaseController第一个泛型是controller接口的默认返回结果类型

第二个泛型参数是controller对应的核心业务逻辑service

3.所有核心业务逻辑service 放在service包下

实现的service必须继承 BaseService<Model, Dao>

BaseService第一个泛型是数据库表模型对象

第二个泛型参数数据库访问Dao

4.所有数据库访问类dao 放在dao包下

实现的dao必须继承 BaseDao<Model, Mapper>

BaseDao第一个泛型是数据库表模型对象

第二个泛型参数数据库访问mapper

5.数据库mapper对象放置在mapper包下面

6.所有数据库表模型对象的定义放在model.db里

7.model.biz包下面，放置业务逻辑处理(service)里使用到的自定义的对象

8.param包下面，放置controller用于接收外部调用的参数对象

9.result包下面，放置controller返回给外部调用的结果对象

10.constant包下面，放置常用的一些常量，如果枚举，final static变量等

11.utils包下面，放置常用的一些工具类


请参照执行！！！
