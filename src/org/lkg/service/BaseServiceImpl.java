package org.lkg.service;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;

import org.lkg.entity.Users;

/**
 * 
 * 为所有实现ServerService的类 提供基础实现 
 * 
 * @description:由于所有实现ServerSerivice类 都要实现init 和destory 所以提出来
 * 从而让具体的实现类都继承这个类  所以这个类必然是抽象类
 * @author: 浮~沉
 * @version: 1.0
 * @data 2019年12月27日 下午5:05:10
 */
public abstract class BaseServiceImpl<T extends Serializable> implements ServerService<T>{
	private Socket socket;
	private ObjectInputStream in;
	private ObjectOutputStream out;
	private T data;
	
	@Override
	public void init(Socket socket, ObjectInputStream in, ObjectOutputStream out, T data) {
		this.socket=socket;
		this.in=in;
		this.out=out;
		this.data=data;
	}

	@Override
	public void destory() throws IOException {
		out.close();
		in.close();
		socket.close();
	}
	
	
	/**
	 * 引入这个方法目的: 让run方法执行该方法后 方便统一实现destroy
	 * 否则子类就不知道何时该释放资源,也就是以后子类的业务都在execute方法中
	 * 
	 * 注意:作为基类方法 设置protected好处只能只会被子类调用从而降低被其他类调用的风险
	 *     但是,当具体的子类实现后应该设置为public 目的方便其被调用
	 * @throws IOException IO异常
	 */
	protected abstract void execute() throws IOException;
	
	
	/**
	 * 每个线程执行完毕,及时释放,开发中应该提供默认实现:
	 * 强制让开发人员去实现destroy从而避免资源没有被及时释放
	 */
	@Override
	public void run() {
		try {
			execute();
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			try {
				destory();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
