package org.lkg.txt.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
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
import org.lkg.util.ResultStatus;

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
	 * 接收小说的节点模板字符串
	 */
	private static String novelPattern=null;
	
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
				List<Element> list2=root2.elements("novel");
				
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
								fileName.getText().trim(),classifcation));
				}
				novelMap.put(classifcation.getClassName(),map);
			}
			
			
			loadNovelPattern();
			
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
	
	
	/**
	 * 根据小说对其进行预览
	 * 注:不能直接拿去fileName 因为序列化是没有该属性的
	 *    同样也不能直接去拿小说的目录信息
	 * 因此不能寄希望从客户端获得分类 再去获得路径  应该基于该工具类的集合进行遍历
	 * 1.判断缓存中是否有预览信息有 直接返回 
	 * 2.正常拼接路径 获得预览信息
	 * @param novel 客户端传来的novel
	 * @return 预览小说的部分内容
	 * @throws FileNotFoundException,IOException  服务端出现的异常不应该被捕捉 要抛出来告诉客户端服务器这边出现了什么问题
	 */
	public static String getNovelPreview(Novel novel) throws FileNotFoundException,IOException{
		//根据客户端传来仅仅可利用的getClassifcation 和getName去寻找当前服务端存储的novel实体
		Novel serverNovel=novelMap.get(novel.getClassifcation().getClassName()).
				get(novel.getName());
		
		if(serverNovel.getPreview()!=null)	
			return serverNovel.getPreview();
		else{
			File file = GetNovelFile(novel);
			//读文本使用字符流
			Reader reader=null;
			reader = new FileReader(file);
			char[] content=new char[128];
			reader.read(content);
			/**
			 * 为了节省IO开销 当我们第一次读取某个小说时,应该本能的存储它的预览信息
			 * 这样方便下一次可以直接读取缓存 ,所以这里就应该想到去添加novel类信息
			 */
			serverNovel.setPreview(new String(content));
			reader. close();
			return serverNovel.getPreview();
		
		}
		
	}

	
	/**
	 * 获取下载小说文件
	 * @param novel 客户端传入的小说对象
	 * @return 小说存储的文件
	 */
	public static File GetNovelFile(Novel novel) {
		Novel serverNovel=novelMap.get(novel.getClassifcation().getClassName()).
				get(novel.getName());
		String catalog=classMap.get(novel.getClassifcation().getClassName()).getCatalog();
		//确保路径的正确
		if(!catalog.endsWith(File.separator))
			catalog+=File.separator;
		//注意是服务端的实体novel 而不是客户端传入的 因为它未被序列化
		String fileName=serverNovel.getFileName();
		String path=catalog+fileName;
		File file=new File(path);
		return file;
	}
	
	
	/**
	 * 将客户端上传的 文件写入本地
	 * @param novel
	 * @return
	 */
	public static File writeNovelToLocal(Novel novel) {
		String catalog=classMap.get(novel.getClassifcation().getClassName()).getCatalog();
		//确保路径的正确
		if(!catalog.endsWith(File.separator))
			catalog+=File.separator;
		String path=catalog+novel.getName()+".txt";
		return new File(path);
		
	}
	
	
	/**
	 * 上传时判断是否和服务端的小说重名
	 * @param novel 客户端即将上传的小说
	 * @return 
	 */
	public static boolean isExistFile(Novel novel) {
		return novelMap.get(novel.getClassifcation().getClassName()).containsKey(novel.getName());
	}

	
	/**
	 * 将上传的小说保存在xml文件中
	 * 注:写操作应该注意同步问题
	 * @param novel  上传的小说
	 * @return  返回服务端执行结果
	 */
	public static synchronized ResultStatus saveNovelToXml(Novel novel) {
		if(isExistFile(novel)) return ResultStatus.FILE_EXIT;
		
		//设定小说的文件名
		novel.setFileName(novel.getName()+".txt");
		
		//获得新小说所属类别的xml配置文件   将新小说插入进去  
		File getConfigfile=new File(classMap.get(novel.getClassifcation().getClassName()).getConfig());
		
		//新小说节点的信息
		String newNodeInfo=String.format(novelPattern,novel.getName(),novel.getAuthor(),novel.getDesc(),novel.getFileName());
	
		//新节点的信息追加到原xml最后一个节点的后面
		//1.先拿到原有xml的信息
		//2.插入到根节点结束标记之前
		//3.写回原文件
		//4.更新novelMap集合
		StringBuffer oldXmlInfo=bufferReaderUtil(getConfigfile);
		int index=oldXmlInfo.lastIndexOf("</novellist>");
		String newXmlInfo=oldXmlInfo.insert(index,newNodeInfo).toString();
		
		//写回原xml文件
		FileWriter writer=null;
		try {
			writer=new FileWriter(getConfigfile);
			writer.write(newXmlInfo);
			writer.flush();
			novelMap.get(novel.getClassifcation().getClassName()).put(novel.getName(),novel);
			return ResultStatus.UPLOAD_SUCCESS;
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
		return ResultStatus.UPLOAD_FAIL;
	}
	
	/**
	 * 初始化时  加载小说的创建节点的模板 
	 * 避免以后新添节点时I/O浪费
	 */
	public static void loadNovelPattern() {
		File file=new File(GetProperies.getValue("server.novel.model"));
		novelPattern=bufferReaderUtil(file).toString();
	}

	/**
	 * 使用bufferReader 逐行读取文件工具方法
	 * @param file 读取的目标文件
	 */
	private static StringBuffer bufferReaderUtil(File file) {
		Reader reader=null;
		//由于需要逐行读取,那么可以使用缓冲字符流
		BufferedReader bReader=null;
		try {
			StringBuffer buffer=new StringBuffer();
			bReader=new BufferedReader(new FileReader(file));
			String line=null;
			while((line=bReader.readLine())!=null) {
				 buffer.append(line).append("\n");
			}
			return buffer;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
		
			try {	
				if(bReader!=null) bReader.close();
				if(reader!=null) reader.close();
				
			}catch (IOException e) {
					e.printStackTrace();
			}
		}
		return null;
	}
}
