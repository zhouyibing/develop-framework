                                      开发规范v1.0
                                      
一. 编码规范
    
    文件编码
   1. 所有代码源文件、HTML页面、HTTP参数、数据库存储采用默认UTF-8编码
   
    代码风格
   
   1. 类名用Camel大小写，首字母大小。如：MyClass
   
   2. 变量名常量名不能使用首字母简写形式，尽量使用全称或简单缩写。首字母简写的名称应当是专有名词，专业术语，行业标准名称，
   大家都认可的名称。如：I18N（internationalization）,ASCII,UTF8,HTTP
   
   3. 变量名，方法名用Camel大小写，首字母小写。如：myVar
   
   4. 包名全部小写。如：com.fido.framework
   
   5. 常量名全部大写，单词之间以下划线分隔。如:ROLE_VIP
   
   6. 代码缩进，统一采用4个空格，不用TAB字符。
   
   7. 大括号与if,else,for,do,while语句一起使用，即使只有一条语句或是空，也
   该把大括号写上；左大括号前不换行，右大括号前换行。如：
   
            if(somveVlaue > 0) {
               
               //do something 
               
            }
   
   8. 二元、三元操作符两边各留一个空格。例如：
   
            int a = 10;
            
            if (10 == a && "abc".equals(str)) { 
               
               //....
               
            }
   
   9. 逗号、分号位于语句中间位置时，后留一个空格。如：
   
            func(a, b, c);
            
            for (int i = 0; i < n; i++) {
               
               //...
               
            }
   
   10. 用于划分语句块的小括号、大括号前后留一个空格，但函数调用的小括号
   前不留空格，大括号单独一行时后面也不留空格。如：
   
            if (10 == a && "abc".equals(str)) { 
            
               //...
               
            }
            
            func(a, b, c);
            
            String var = (String) obj;
   
   11. 代码按照逻辑分段，段与段之间用一个空行分隔
   
    数据类型
   
   1. 财务等对精度要求高的数据只能使用整型或BigDecimal,禁止使用浮点型，避免“一分钱”问题
      
      使用BigDecimal的注意事项：
    
   1.1 只允许使用BigDecimal的3个构造函数，
   
       public BigDecimal(int val) 
       
       public BigDecimal(long val) 
       
       public BigDecimal(String val)
   
   禁止使用浮点数构造函数： public BigDecimal(double val)
   
   1.3 根据BigDecimal.signum()的返回值判断大于/等于/小于0
   
   1.4 两个BigDecimal比较应使用compareTo，禁止使用equals:
   
        new BigDecimal("19.0").equals(new BigDecimal("19.00"))   结果为 false;
   
   1.5 JSON序列化时，BigDecimal必须转为字符串形式，保存其精度信息
   
    
    异常处理与资源释放
   
   1. 数据库连接、文件流、网络连接等都提供了close,disconnect,release等释放资源的API，要在finally中安全
   地释放它们：
       
           Connection conn = null;
           
           PrepareStatement ps = null;
           
           ResultSet rs = null;
           
           try { 
           
               conn = getConnection();
               
               ps = conn.prepareStatement();
               
               ps.setString(1, "");
               
               //...
               
               rs = ps.executeQuery();
               
               while (rs.next()) { 
                
                   //...
               
               }    
               
           } finally { 
              
              if (rs != null) { //非空判断很重要，如果前面发生异常，rs对象不一定存在
                  
                  try { 
                  
                      rs.close();
                      
                  } catch (SQLException e) { //必须捕捉   close可能抛出的异常，让后续 ps与conn的释放语句有机会执行
                      
                      //ignored
                      
                  }
              }
              
              if (ps != null) {
                  
                  try {
                      
                      ps.close();
                      
                  } catch (SQLException e) {
                  
                      //ignored
                  
                  }
                  
              }
              
              if (conn != null) {
              
                  try {
                      
                      conn.close();
                      
                  } catch (SQLException e) { 
                  
                      //ignored
                      
                  }
                  
              }
              
           }
   
   尤其要注意，一次释放多个资源时，要捕捉每一个资源的释放异常，避免前面的资源释放异常导致代码跳出当前方法，后续的资源
   得不到释放。
   
   上述连接释放（必须考虑到close会抛出异常）代码：
    
     if(conn != null) { 
     
         try { 
         
         } catch (SQLException e) {
         
             //ignored
             
         }
     }
   
   也可用jakarta commons库的IOUtils.closeQuietly(conn)来替代，简化代码   
   
    事务与远程调用
   
   1. 在事务中进行远程调用往往带来巨大的隐患。考虑这样一个资金模块的转账服务实现：
      
      STEP 1:接收到客户端的转账请求；
      
      STEP 2:启动事务；
      
      STEP 3:将转账申请单保存到数据库（为表述方便，我们称之为落库）；
      
      STEP 4:远程调用银行的划账接口；
      
      STEP 5:更新数据库中本次转账结果；
      
      STEP 6:提交事务；
      
      当我们与银行的系统都持续稳定时，转账功能运行正常。但是，如果步骤  4
      中银行的划账接口超时，整个事务就回滚了，也就是说，我们在数据库中找不到
      任何转账请求的痕迹！更可怕的是，银行的划账操作可能已经完成了！
      
      为杜绝上述重大隐患，任何调用远程接口的服务实现需要遵循如下规范：
      
      （1）服务必须拒绝来自客户端的重复请求。
      
      （2 ）服务必须检查业务记录是否已存在，若已存在则返回明确的错误码。
      同时，作为数据正确性的最后一道防线，数据库中必须有业务记录的
      唯一索引；
      
      （3 ）只能在异步任务中调用远程接口，绝不允许在落库事务中直接调用；
      
      （4 ）异步任务本身必须判断是否已经执行过（可通过相应业务记录的数据
      库状态来判断）；
      
      （5）在调用远程接口前记录本次调用，并且设置调用状态为“未知”（因
      为不知道是还没调用，还是调用了没拿到结果），接口返回后保存本
      次调用结果。下次任务运行时遇到未知状态的调用记录，需要先查询
      上次调用成功没有，再决定下一步做什么；
      
   类似的，在事务中调用其它服务，更新缓存失败，都会造成落库事务的回滚，要杜绝！！！
   
    日志输出
    
   以英文输出日志，日志内容必须携带关键业务信息，例如订单ID、用户ID等，如果是异常日志，还需打印堆栈。如：
   
    log.error("failed to update order" + orderId, exception); 

  判断字符串是否为空
   
  在web程序中判断一个字段是否为空时，仅检查它是否为null或者length是否为0是不够的，因为还有空白字符（空格、换行等不可见字符）的存在。
  应使用 jakarta commons的  StringUtils.isBlank()来判断字段是否为空。
   
    字符串连接操作
  
  禁止在循环中对字符串相加，应使用StringBuffer/StringBuilder进行高效的字符串连接，避免内存的频繁法分配。如：
  
    StringBuffer sb = new StringBuffer(estimatedLength);
    for (int i = 0; i < n; i++) {
        sb.append(someStr[i]);
    }
    
    常量比较
  
  常量与变量比较时，建议将常量放在左操作数位置。如：
     
     if (10 == a && "abc".equals(str))
  
     
二. 项目工程规范
   
   1. 所有api接口先建立facade接口对象 放在api包里
   
   这样做的目的是为以后的微服务化做准备，微服务的调用都是通过api接口调用，而不是直接请求controller.
   
   这个api包下的接口，后面会加入feign
   
   先统一标准
   
   2. 所有对外访问接口controller 放在controller包下，并且
   
   实现的controller必须继承 BaseController<Result,Service>
   
   BaseController第一个泛型是controller接口的默认返回结果类型
   
   第二个泛型参数是controller对应的核心业务逻辑service
   
   3. 所有核心业务逻辑service 放在service包下
   
   实现的service必须继承 BaseService<Model, Dao>
   
   BaseService第一个泛型是数据库表模型对象
   
   第二个泛型参数数据库访问Dao
   
   4. 所有数据库访问类dao 放在dao包下
   
   实现的dao必须继承 BaseDao<Model, Mapper>
   
   BaseDao第一个泛型是数据库表模型对象
   
   第二个泛型参数数据库访问mapper
   
   5. 数据库mapper对象放置在mapper包下面
   
   6. 所有数据库表模型对象的定义放在model.db里
   
   7. model.biz包下面，放置业务逻辑处理(service)里使用到的自定义的对象
   
   8. param包下面，放置controller用于接收外部调用的参数对象
   
   9. result包下面，放置controller返回给外部调用的结果对象
   
   10. constant包下面，放置常用的一些常量，如果枚举，final static变量等
   
   11. utils包下面，放置常用的一些工具类
   
   12. 项目的所有错误代码定义在 XXXErrorCode里，错误代码定义使用ErrorCode对象
   
三. 数据库设计规范

   1. 表名全部小写，第一个字母表示模块后跟"_"（c:客户，p:产品，m:商家，f:财务，o:订单，b:基础服务）
      
   2. 表名以固定格式结尾，_info（信息表）,_record(XXX记录)，_map(映射/关联关系表)，_tmp(临时表)，_cfg(配置表)，_log(日志表)
      _his(历史表)。禁止其他后缀结尾
   
   3. 大字段(text,blob或较长的varchar等)需设计独立表存储
   
   4. 字段名全部小写，以下划线连接多个单词
   
   5. 金额字段类型一般定义为decimal(20,2),某系进度要求低的场景例外。对应的java类型为BigDecimal
   
   6. 数字字段不允许为空。对应的java类型为int或long,short
   
   7. 枚举字段采用字符串形式表示，不允许为空。对应的   Java类型为  String；
   
   8. 布尔字段类型定义为   tinyint(1)，不允许为空，禁止以 is_开头。对应的  Java
      类型为 bool；
   
   9. 时间字段类型定义为timestamp，对应的 Java类型为  java.util.Date；
   
   10. 所有表默认加上create_time,update_time,logic_delete字段。对于后端管理系统/运营系统，对业务数据/用户数据
   进行操作时，应该对相应的表加上creator_id和updater_id，记录操作人。
   
           `create_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时
           间',
           `creator_id` varchar(28) NOT NULL COMMENT '创建者',
           `update_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE
           CURRENT_TIMESTAMP(3) COMMENT '最后更新时间',
           `updater_id` varchar(28) NOT NULL COMMENT '最后更新者'
   
    索引定义规范
  
   普通索引以idx_开头，唯一索引以uk_开头，主键以pk_开头。禁止其他前缀！！！
   
          
四.其他
   
   1. 使用Spring的@Transactional标签要求在事务中执行相关方法。如：
   
    @Transactional
    public void createOrder(OrderForm o) {}     
   
   特别注意，因为Spring AOP机制的缺陷，相关方法必须同时满足以下两个条件才能让@Transactional标签生效：
      
      （1） 方法是 public 的；
      
      （2） 必须从对象的外部调用；
      其中第二点也就是说，在对象内部的其它方法调用 this.createOrder()是无法
      启动事务的。为了解决这个问题，我们必须用一种丑陋的办法：
      
      （1） 将对象实例注入为自己的成员变量；
      
      （2） 用这个成员变量调用对象自身的事务性方法；
      
      例如：
      
      @Autowired
      private OrderServiceImpl that;
      public void otherMethod(OrderForm o) {
       that.createOrder(o); // use that, do NOT use this
      }
      @Transactional
      public void createOrder(OrderForm o) {}
    
   因为标注@Transactional的方法，实际会被Spring AOP生成代理方法。具体原因请参考：
   
   https://www.cnblogs.com/moxiaotao/p/9776964.html
   
   @Async也是同样的道理        
  
  如需便捷实用事务/异步操作，可参考如下帮助类
   
       com.fido.framework.common.utils.AsyncHelper
       
       com.fido.framework.common.utils.TransactionalHelper   
       
       
 code review & 项目评审 时严格按照以上执行！！！！！     
 
 code review & 项目评审 时严格按照以上执行！！！！！     
 
 code review & 项目评审 时严格按照以上执行！！！！！