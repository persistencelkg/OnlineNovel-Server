package org.lkg.service.impl;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.lkg.service.ServerService;
import org.lkg.txt.util.GetProperies;

/**
 * Service业务静态工厂  
 * @description: 方便一次使用
 * @author: 浮~沉
 * @version: 1.0
 * @data 2019年12月27日 下午5:52:09
 */
public class ServiceFactory {
	 
	/**
	 *这里的文件路径不应该写死,这样以后如果修改还出大动干戈 
	 *解决的办法放在配置文件中
	 */
	private static final String path=GetProperies.getValue("server.config.service");
	
	
	/**
	 * 存放 读取services.xml的键值对
	 */
	private static Map<String,String> servicesMap=new HashMap<>();
	
	
	static {
	   Document document=null;
	   SAXReader reader=new SAXReader();
	   try {
		  
		   document=reader.read(new File(path));
		   Element root=document.getRootElement();
		   
		   //读取所有service元素
		   @SuppressWarnings("unchecked")
		   List<Element> list=root.elements("service");
		   for (Element element : list) {
			   //System.out.println(element.attributeValue("key").trim()+"-"+element.getText());
			   //根据属性名key获得属性值 
			   //trim是有必要的:由于xml的文本遍历出来会考虑空格的情况!
			   servicesMap.put(element.attributeValue("key").trim(),element.getText());
		   }
		}catch (DocumentException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 根据业务方法获得对应的业务功能
	 * @param key 业务功能的key
	 * @return 返回的业务功能的具体实现类
	 * 由于并不知道具体的类型 所以加上泛型方法约束T 具体的使用类型交给调用者
	 * 其实也可以将T声明在类名旁,但是这样就违背了工厂的显示语义        
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Serializable> ServerService<T> getService(String key){
		String className=servicesMap.get(key);
		if(className==null)
			throw new UnsupportedOperationException("非法的业务功能!");
		try {
			return (ServerService<T>) Class.forName(className).newInstance();
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			e.printStackTrace();
			//直接结束程序,配置文件读取失败自然没有往下去的必要
			throw new RuntimeException(e);
		}

	}
}
