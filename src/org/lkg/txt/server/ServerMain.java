 package org.lkg.txt.server;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;

import org.lkg.entity.SysDTO;
import org.lkg.service.ServerService;
import org.lkg.service.impl.ServiceFactory;
import org.lkg.txt.util.GetProperies;

public class ServerMain {

	public static void main(String[] args) throws IOException,NumberFormatException{
		 new ServerMain().start();
	}
	
	public <E extends Serializable> void start() throws IOException,NumberFormatException{
		ServerSocket serverSocket=new ServerSocket(
				Integer.valueOf(GetProperies.getValue("socket.server.port")));
		System.out.println("-------服务器启动成功-----------");
		while(true) {
			//持续监听客户端
			Socket socket=serverSocket.accept();
			System.out.println("客户端:"+socket.getInetAddress()+"连接成功!");
			
			//服务端先读后写
			ObjectInputStream in=new ObjectInputStream(socket.getInputStream());
			ObjectOutputStream out=new ObjectOutputStream(socket.getOutputStream());
			
			try {
//				v1:使用? 由于并不知道客户端传来具体对象暂时使用?进行代替
//				SysDTO<?> dto=(SysDTO<?>) in.readObject();
//				System.out.println("请求:"+dto.getType());
//				ServerService serverService=ServiceFactory.getService(dto.getType());
//				serverService.init(socket, in, out, dto.getData());
				
				//v2:规避警告 
				//可以发现ServerService的数据对象类型和SysDTO中的数据对象是一致的
				//那么我们可以统一定义个泛型E去约定  这样避免了黄色下划线警告
				@SuppressWarnings("unchecked")
				SysDTO<E> dto=(SysDTO<E>) in.readObject();
				System.out.println("请求:"+dto.getType());
				
				ServerService<E> serverService=ServiceFactory.getService(dto.getType());
				serverService.init(socket, in, out, dto.getData());
				
				new Thread(serverService).start();
				
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}finally {
				//serverSocket.close();
			}
		}
		
		
	}
}
