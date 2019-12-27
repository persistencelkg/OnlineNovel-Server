package org.lkg.txt.server;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import org.lkg.entity.SysDTO;
import org.lkg.txt.util.GetProperies;

public class ServerMain {

	public static void main(String[] args) throws IOException,NumberFormatException{
		 new ServerMain().start();
	}
	
	public void start() throws IOException,NumberFormatException{
		ServerSocket serverSocket=new ServerSocket(
				Integer.valueOf(GetProperies.getValue("socket.server.port")));
		System.out.println("-------服务器启动成功-----------");
		
		//获得客户端
		Socket socket=serverSocket.accept();
		System.out.println("客户端:"+socket.getInetAddress()+"连接成功!");
		
		//服务端先读后写
		ObjectInputStream in=new ObjectInputStream(socket.getInputStream());
		ObjectOutputStream out=new ObjectOutputStream(socket.getOutputStream());
		
		try {
			//使用? 由于并不知道客户端传来具体对象暂时使用?进行代替
			SysDTO<?> dto=(SysDTO<?>) in.readObject();
			System.out.println("请求:"+dto.getType());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
}
