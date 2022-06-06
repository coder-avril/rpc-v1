package com.lding.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.context.ApplicationContext;
import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RpcServer {
    private int port ;
    private ApplicationContext applicationContext;

    //服务启动类--> 启动一个ServerSocket
    //该注解在项目启动的时候执行这个方法，也可以理解为在spring容器启动的时候执行
    @PostConstruct
    public void startup(){
        try {
            //1 创建一个socket通信
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("服务端启动成功, 等待客户端连接.....");
            //2 接收客户端请求
            while(true){
                //等待客户端连接 如果没有客户端连接, 会阻塞在这里
                Socket socket = serverSocket.accept();
                try(
                        ObjectInputStream ois =new ObjectInputStream(socket.getInputStream());
                        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream())
                ){
                    //读取内容
                    RpcRequest rpcRequest = (RpcRequest) ois.readObject();
                    System.out.println("服务端收到客户端的请求: obj = " + rpcRequest);
                    //调用业务方法执行代码
                    //1 通过类名找到对应的Bean对象
                    //IProductService
                    String className = rpcRequest.getClassName();
                    Object bean = applicationContext.getBean(className);
                    //字节码对象
                    Class<?> clazz = bean.getClass();

                    //2 通过反射调用类的对应方法
                    String methodName = rpcRequest.getMethodName();
                    Class<?>[] parameterTypes = rpcRequest.getParameterTypes();
                    Object[] parameters = rpcRequest.getParameters();
                    //方法对象
                    Method method = null;
                    //反射的返回结果
                    Object result = null;
                    if(parameterTypes == null || parameterTypes.length == 0){
                        method = clazz.getMethod(methodName);
                        result = method.invoke(bean);
                    }else{
                        method = clazz.getMethod(methodName, parameterTypes);
                        result = method.invoke(bean, parameters);
                    }
                    //发送数据到客户端
                    RpcResponse rpcResponse = new RpcResponse();
                    rpcResponse.setSuccess(true);
                    rpcResponse.setResult(result);
                    oos.writeObject(rpcResponse);
                    //刷新缓冲区域
                    oos.flush();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
