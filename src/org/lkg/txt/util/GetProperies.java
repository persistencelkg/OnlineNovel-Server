package org.lkg.txt.util;
/**
 * 工具类-读取所有配置信息
 * @description:
 * @author: 浮~沉
 * @version: 1.0
 * @data 2019年12月27日 上午11:35:28
 * @CopyRight lkg.nb.com
 */

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class GetProperies {
	private static Properties properties=new Properties();
	
	static {
		InputStream inputStream=null;
		try {
			inputStream=new FileInputStream("config\\server.properties");
			properties.load(inputStream);
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			if(inputStream!=null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}	
		}
	}
	
	public static String getValue(String key) {
		return properties.getProperty(key)==null ? "" : properties.getProperty(key);
	}
}
