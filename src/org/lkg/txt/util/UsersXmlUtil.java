package org.lkg.txt.util;


import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.lkg.entity.Users;
import org.lkg.util.ResultStatus;

/**
 * 
 * 读取Users.xml节点工具类
 * 注意:为了不频繁的操作xml文件 我们需要一次性拿到节点映射到Map集合中
 * @description:
 * @author: 浮~沉
 * @version: 1.0
 * @data 2019年12月27日 下午11:33:57
 */
public class UsersXmlUtil {
	
	private static final String path=GetProperies.getValue("server.config.user");
	
	private static Map<String, Users> userMap=new HashMap<>();
	
	static {
		Document document=null;
		SAXReader reader=new SAXReader();
		try {
			document=reader.read(new File(path));
			
			Element root=document.getRootElement();
			//获得所有user元素
			@SuppressWarnings("unchecked")
			List<Element> list=root.elements("user");
			for (Element element : list) {
				@SuppressWarnings("unchecked")
				Element uname=element.element("username");
				Element upass=element.element("password");
				userMap.put(uname.getText(),new Users(uname.getText().trim(),upass.getText().trim()));
			}
			
		} catch (DocumentException e) {
			e.printStackTrace();
			//这里直接抛出异常,目的是如果配置文件读取失败就没有执行下去的必意!
			throw new RuntimeException(e);
		}
	}
	
	
	
	/**
	 * 登录-用户遍历map
	 * @param users 登录用户
	 * @return 返回遍历结果
	 */
	public static ResultStatus doLogin(Users users){
		Users check=null;
		if((check=userMap.get(users.getUserName()))!=null) {
			if(check.getPassword().equals(users.getPassword())) {
				return ResultStatus.LOGIN_SUCCESS;
			}else
				return ResultStatus.LOGIN_FAIL;
		}
		return ResultStatus.USERNAME_NOT_EXIST;
		
	}
	
  
	/**
	 * 注册-将用户信息写入Users.xml
	 * 注意:在多线程情况下 应该考虑同步
	 * @param user  注册用户
	 * @return		注册结果
	 */
	public static synchronized ResultStatus doRegister(Users user) {
		return ResultStatus.USERNAME_EXIST;
	}
	
}
