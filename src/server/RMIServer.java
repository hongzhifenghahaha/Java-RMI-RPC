package server;

import rmi.RemoteMeeting;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * Class <em>RMIServer</em> is a class register the RMI object
 *
 * @author Sun Shuo
 */
public class RMIServer {

    /**
     * 创建RMI注册中心，把RemoteMeeting注册到注册中心
     */
    public static void main(String[] args) {
        try{
            // 定义绑定名
            String bindName = "remoteMeeting";
            // 创建实现类
            RemoteMeeting remoteMeeting = new RemoteMeeting();
            // 创建RMI注册器，并监听1099端口
            LocateRegistry.createRegistry(1099);
            // 获取创建的注册器
            Registry registry = LocateRegistry.getRegistry();
            // 把 bindName 与 remoteMeeting 绑定
            registry.bind(bindName,remoteMeeting);
            System.out.println("RemoteMeeting server ready");

        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
