# Rshs-RPC
## Overall Farmwork
![image](https://github.com/RshsLY/Rshs-RPC/assets/37995682/6579e83a-45ed-4a13-9585-b417adcdac0f)

## Quickly Start
- 导入api依赖
- 配置并启动Redis
- 启动Regisity Center
- 启动provider
- consumer消费对应接口的服务

## Implement Notices
- 注册中心：使用Redis解耦，便于横向纵向扩展，即Regisity Center和Redis都可以搭建集群。Hash结构存储，[ Key：接口类全限定名，InnerKey：host+ip，Value：当前时间+30s ]。
- 心跳保持：每20s向注册中心申请续期，即更新redis里相应InnerKey的过期过期时间。
- 负载均衡：采用随机/or其他算法取出一个InnerKey，如果InnerKey的Vaule已经小于当前时间，即过期，将此InnerKey删除，循环上述操作，直到获得合法InnerKey返回。**使用LUA脚本保证原子性**。
- 序列化：实现了Java序列化和JSON序列化，可以在配置文件里切换序列化算法。使用Json序列化时，需要将RPC参数类型和返回值类型记录在entity里，便于在反序列化时将Object类型向下转型。
- Rshs注解：导入api依赖后基于注解快速实现RPC（spring导入Registrar，扫描注解，装入bean定义信息，使用BeanPostProcessor对类进行增强）。
- 动态代理：使用BeanPostProcessor对类进行增强时，使用jdk动态代理，在invoke方法内实现rpc，并在服务端根据rpcRequest实体信息使用反射调用相应方法。
- channel复用和回收：ServerBootstrap在channel 30s没请求时关闭相应channel，客户端单例某个addr的channel并复用。
- 粘包和拆包：基于约定消息格式和长度解决（LengthFieldBasedFrameDecoder）
- 客户端消费完成的消息回调：基于记录信息id的消息发送后使用CompleteFeature.get阻塞，在响应入栈InboundHandler后将相应的CompleteFeature设为complete，阻塞的线程唤醒。
