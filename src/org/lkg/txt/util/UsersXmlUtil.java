package org.lkg.txt.util;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
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
	/**
	 * 从配置文件中获得user.xml路径
	 */
	private static final String path=GetProperies.getValue("server.config.user");
	
	/**
	 * 将用户名和user对象映射
	 */
	private static Map<String, Users> userMap=new HashMap<>();
	
	/**
	 * 全局文档对象
	 */
	private static Document document=null;
	
	static {
		SAXReader reader=new SAXReader();
		try {
			document=reader.read(new File(path));
			
			Element root=document.getRootElement();
			//获得所有user元素
			@SuppressWarnings("unchecked")
			List<Element> list=root.elements("user");
			for (Element element : list) {
				Element uname=element.element("username");
				Element upass=element.element("password");
				userMap.put(uname.getText().trim(),new Users(uname.getText().trim(),upass.getText().trim()));
			}
			
		} catch (DocumentException e) {
			e.printStackTrace();
			//这里直接抛出异常,目的是如果配置文件读取失败就没有执行下去的必要!
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
	 * 步骤:1.新建节点信息
	 * 	   2.写入文件
	 *     3.更新UserMap,否则用户读取不到及时的信息
	 * 注意:在多线程情况下 应该考虑同步
	 * @param user  注册用户
	 * @return		注册结果
	 */
	public static synchronized ResultStatus doRegister(Users user) {
		if(isExistUser(user.getUserName()))
			return ResultStatus.USERNAME_EXIST;
		
		//新建注册节点信息
		Element root=document.getRootElement();
		Element newNode=root.addElement("user");
		Element uname=newNode.addElement("username");
		uname.setText(user.getUserName());
		Element upass=newNode.addElement("password");
		upass.setText(user.getPassword());
		
		//写入xml文件,并设置良好的阅读输出
		XMLWriter writer=null;
		try {
			writer=new XMLWriter(new OutputStreamWriter(
					new FileOutputStream(path),"UTF-8"),OutputFormat.createPrettyPrint());
			writer.write(document);
			writer.flush();

			//将用户信息更新到map中
			userMap.put(user.getUserName(),user);
			
			return ResultStatus.REGIST_SUCCESS;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			if(writer!=null)
				try {
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		//处理异常的善后工作
		document.remove(newNode);
		userMap.remove(user.getUserName());
		return ResultStatus.REGIST_FAIL;
	}
	
	
	/**
	 * 判断用户名是否存在
	 * @param uname 传入的用户名
	 * @return 
	 */
	protected static boolean isExistUser(String uname) {
		return userMap.containsKey(uname);
	}
}
