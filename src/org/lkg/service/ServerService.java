package org.lkg.service;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;

/**
 * 服务端处理的业务接口 --可以并发执行
 * @description: 此时Service是个天然接口
 * @author: 浮~沉
 * @version: 1.0
 * @data 2019年12月27日 下午2:13:04
 */
public interface ServerService<T extends Serializable> extends Runnable{

	/**
	 * 服务端处理义务的初始化
	 * @param socket 获取socket
	 * @param in	   获取输入流
	 * @param out    获取输出流
	 * @param data	  传输的数据对象
	 */
	public void init(Socket socket,ObjectInputStream in,ObjectOutputStream out,T data);
	
}
