package org.lkg.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import org.lkg.entity.Classifcation;
import org.lkg.entity.Novel;
import org.lkg.entity.SysDTO;
import org.lkg.service.BaseServiceImpl;
import org.lkg.txt.util.NovelUtil;
import org.lkg.util.ResultStatus;

/**
 * 服务端-处理上传请求
 * @description: 需要优先解决重名问题
 * 1.要么进行重名验证  文件名=小说名+.txt
 *   如果重名要求客户端改名后上传!
 * 2.随机算法 --UUID 
 * 	 不需要重名验证,算法会自动生成一个不重复32位文件名
 * @author: 浮~沉
 * @version: 1.0
 * @data 2020年1月8日 上午10:44:32
 * @CopyRight lkg.nb.com
 */
public class UploadService extends BaseServiceImpl<Novel>{

	@Override
	protected void execute() throws IOException {
		SysDTO<?> dto=new SysDTO<>();
		ResultStatus status=NovelUtil.saveNovel(getData());
		dto.setResust(status);
		getOut().writeObject(dto);
		
		
		if(status==ResultStatus.UPLOAD_SUCCESS) {
			//接收客户端上传的小说
			byte[] bytes=new byte[1024*1];
			//写入文本文件
			OutputStream outputStream=null;
			outputStream=new FileOutputStream(NovelUtil.writeNovelToLocal(getData()));
			int len=-1;
			while((len=getIn().read(bytes))!=-1) {
				outputStream.write(bytes,0,len);
			}
			
			outputStream.flush();
			if (outputStream!=null) {
				outputStream.close();
			}
		}else {
			
		}
	}
	/* 随机算法--UUID
	 * public static void main(String[] args) {
		java.util.UUID uuid=UUID.randomUUID();
		System.out.println(uuid.toString().replaceAll("-",""));
	}*/
}
