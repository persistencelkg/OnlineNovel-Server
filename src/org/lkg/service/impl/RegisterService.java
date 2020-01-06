package org.lkg.service.impl;

import java.io.IOException;

import org.dom4j.Document;
import org.lkg.entity.SysDTO;
import org.lkg.entity.Users;
import org.lkg.service.BaseServiceImpl;
import org.lkg.txt.util.UsersXmlUtil;
import org.lkg.util.ResultStatus;

public class RegisterService extends BaseServiceImpl<Users>{
	
	@Override
	public void execute() throws IOException {
		ResultStatus status=UsersXmlUtil.doRegister(this.getData());
		SysDTO<?> dto=new SysDTO<>();
		System.out.println("服务器注册用户:"+getData().getUserName());
		dto.setResust(status);
		this.getOut().writeObject(dto);
	}

}
