package org.lkg.service.impl;

import java.io.IOException;
import java.io.Serializable;

import org.lkg.entity.SysDTO;
import org.lkg.entity.Users;
import org.lkg.service.BaseServiceImpl;
import org.lkg.txt.util.UsersXmlUtil;
import org.lkg.util.ResultStatus;

public class LoginInService extends BaseServiceImpl<Users>{
	@Override
	public void execute() throws IOException {
		//登录步骤:
		//1.获得登录结果
		//2.如果登录成功需要考虑: 
		//   a)同时查询小说分类,一起将结果反馈给客户端
		//   b)直接返回登录结果,客户端重新发送请求-获得小说分类
		ResultStatus status=UsersXmlUtil.doLogin(getData());
		SysDTO<?> dto=new SysDTO<>();
		System.out.println("服务器接收登录用户:"+getData().getUserName());
		dto.setResust(status);
		this.getOut().writeObject(dto);
	}
}
