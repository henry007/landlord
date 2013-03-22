斗地主消息系统说明
====================

系统架构
--------------------

本系统是在java servlet上封装了一个基于http协议和json格式的的消息队列。 简单的来说就是一层层的回调.

mobileClient --> servlet --> messageService --> messageHandler

所有的http访问都被消息系统封装为Request对象, 我们只需要实现一个messageHandler，然后在回调中读取Request对象，通过session发送Response对象，就完成了一个服务
如果需要拦截所有的消息， 我们只需要实现一个Preprocessor（拦截所有收到的消息） 和Postprocessor(拦截所有发出的消息）
总体来说，使用此消息系统只需要关注3个类，在com.hurray.lordserver.engine.stub中

*  MessageHandler
*  PreProcessor
*  PostProcessor

消息体系
--------------------

本系统内一共存在4种消息, 都是继承至Message 接口, 他们分别是

* Request
    * ScheduleReq

* Response
    * Push


Request消息是所有从客户端发往服务器的消息, Response则是服务器返回给客户端的消息,  ScheduleReq是继承至Request, 是系统模拟的回调消息,  Push则是继承至Resposne, 是系统主动下发给客户端额消息

消息体的格式
------------------

消息采用JSON编码, 每实现一个消息, 必须继承并追加实现write和read两个方法, 在这两个方法中手动写入json数据和读取并解析数据, 只所以不采用自动生成json消息的原因是为了客户端升级的兼容性. 对于复杂的数据比如数组, 系统采用简单的封装方式,比如int[]{1,2,3}就直接以"1,2,3"表示, MessageHelper中提供了这个表达式的支持和生成,请查询之


配置和代码
--------------------

系统采用spring作为组件配置系统， 目前项目中一共有3个spring文件

*   applicationContext.xml 负责基础的组件管理，包括dao,service, hibernate等
*   gameEngine.xml  负责消息系统自身的组件管理
*   gameLogic.xml   建议把消息系统的回调类都放到这里面， 方便管理

在web.xml中注册了

        <context-param>
             <param-name>contextConfigLocation</param-name>
             <param-value>classpath:applicationContext.xml, classpath:gameEngine.xml, classpath:gameLogic.xml</param-value>
         </context-param>
         <listener>
             <listener-class>com.hurray.lordserver.engine.support.SelfContextLoaderListener</listener-class>
         </listener>


其中SelfContextLoaderListener是继承org.springframework.web.context.ContextLoaderListener来实现spring自动， 之所以要继承是因为系统需要通过反射来设置所有的消息处理器
web.xml中还有

       <servlet>
             <servlet-name>messageServlet</servlet-name>
             <servlet-class>com.hurray.lordserver.engine.support.MessageServlet</servlet-class>
       </servlet>
       <servlet-mapping>
             <servlet-name>messageServlet</servlet-name>
             <url-pattern>/msg</url-pattern>
       </servlet-mapping>

消息系统通过这个servlet来获取http请求，把它们转成request对象，并把在队列中的response对象输出出去

例子-如何实现一个MessageHandler
--------------------

首先需要实现一个MessageHandler的具体子类

    public class RegisterUserExample implements MessageHandler<RegisterReq> {


        @Override
        public void handle(RegisterReq message,GameSession session) {
        	RegisterResp resp = message.createResponse();
            session.send(resp);
        }
    }

然后需要在spring中配置此类

    <bean id="registerUser" class="com.hurray.lordserver.gamelogic.RegisterUserExample"/>

然后重启服务， 这个类会在启动的时候被发现并自动注册上RegisterReq的处理器，  一个消息允许出现多个处理器


如何调试
--------------------
启动后访问 http://YOUR_SERVER/console/ 可以查看当前系统中正在监听的消息， 同时通过点击Test连接可以直接发送模拟消息到系统中， 查看反馈

有问题请联系 heyizhou@hurray.com.cn