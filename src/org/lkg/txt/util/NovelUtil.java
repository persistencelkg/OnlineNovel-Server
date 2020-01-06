package org.lkg.txt.util;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.lkg.entity.Classifcation;
import org.lkg.entity.Novel;

/**
 * 读取小说的配置信息
 * @description:
 * @author: 浮~沉
 * @version: 1.0
 * @data 2020年1月6日 上午10:23:22
 * @CopyRight lkg.nb.com
 */
public class NovelUtil {
	//1.加载小说分类信息
	//2.加载每个分类下的小说信息
	
	private static String path=GetProperies.getValue("server.config.class");
	
	/**
	 * 根据分类名存储小说分类信息
	 */
	private static Map<String,Classifcation> classMap=new HashMap<String, Classifcation>();;
 	
	
	/**
	 * 根据分类名 存储其下的所有小说配置信息(value:根据小说名存储所有小说内容信息)
	 */
	private static Map<String,Map<String, Novel>> novelMap=new HashMap<>();
	/**
	 * 记录class.xml的dom4j文档
	 */
	private static Document document=null;
	
	/**
	 * 读取不同类型的小说文档
	 */
	private static Document document2=null;
	
	static {
		SAXReader reader=new SAXReader();
		try {
			document=reader.read(new File(path));
			Element root=document.getRootElement();
			
			//读取分类信息节点
			@SuppressWarnings("unchecked")
			List<Element> list=root.elements("class");
			for (Element element : list) {
				Element className=element.element("classname");
				Element catalog=element.element("catalog");
				Element config=element.element("config");
				classMap.put(className.getText().trim(),new Classifcation(
						className.getText().trim(),
						catalog.getText().trim(),
						config.getText().trim()));
			}
			
			//读取每个分类的所有小说配置信息
			for (Classifcation classifcation : classMap.values()) {
				document2=reader.read(new File(classifcation.getConfig()));
				Element root2=document2.getRootElement();
				@SuppressWarnings("unchecked")
				List<Element> list2=root.elements("novel");
				
				//根据每个小说名存储小说内容信息  每有一个分类就需要new一次
				HashMap<String,Novel> map=new HashMap<>();
				
				for (Element element : list2) {
						Element name=element.element("name");
						Element author=element.element("author");
						Element description=element.element("description");
						Element fileName=element.element("filename");
						
						map.put(name.getText().trim(), new Novel(
								name.getText().trim(),
								author.getText().trim(),
								description.getText().trim(),
								fileName.getText().trim()));
				}
				novelMap.put(classifcation.getClassName(),map);
			}
			
			
		} catch (DocumentException e) {
			e.printStackTrace();
			//没有执行下去的必要
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 获取所有小说的分类集合工具
	 * 可以避免服务器的存储结构被暴露给客户端
	 */
	
	public static Classifcation[] getClassfilcation(){
		return classMap.values().toArray(new Classifcation[0]);
	}
	
	/**
	 * 获得某个类型的所有小说集合
	 * 
	 * @param classifcation
	 * @return
	 */
	public static Novel[] getNovel(Classifcation classifcation){
		return novelMap.get(classifcation.getClassName()).values().toArray(new Novel[0]);
	}

}
