package org.lkg.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.lkg.entity.Novel;
import org.lkg.entity.SysDTO;
import org.lkg.service.BaseServiceImpl;
import org.lkg.txt.util.NovelUtil;
import org.lkg.util.ResultStatus;
/**
 * 服务端--下载业务功能
 * @description:
 * @author: 浮~沉
 * @version: 1.0
 * @data 2020年1月7日 下午4:57:59
 * @CopyRight lkg.nb.com
 */
public class DownloadService extends BaseServiceImpl<Novel>{

	private static final int size=1024*4;
	
	
	@Override
	protected void execute() throws IOException {
		SysDTO<?> dto=new SysDTO<>();
		// 1.通过工具类获得下载的文件对象
		// 2.将File及时传给客户端
		// 3.客户端保存到本地的目录
		File file=NovelUtil.GetNovelFile(getData());
		boolean isContine=true;
		if(!file.exists()) {
			dto.setResust(ResultStatus.FILE_NOT_FOUND);
			isContine=false;
		}else if(!file.canRead()) {
			dto.setResust(ResultStatus.FILE_NOT_READ);
			isContine=false;
		}
		getOut().writeObject(dto);
		//服务端由于 该方法执行完之后自己释放资源 destory 所以else不需要做任何处理
		if(isContine) {
			byte[] buffer=new byte[size]; //4k
			InputStream inputStream=null;
			inputStream=new FileInputStream(file);
			int len=-1;
			while((len=inputStream.read(buffer))!=-1) {
				//发送给客户端读取实际的长度,这样可以避免最后不够size 大小的空信息(垃圾信息)
				getOut().write(buffer,0,len);
			}
			
			getOut().flush();
			//读完后必须给客户端一个结束信号  否则客户端会持续阻塞
			getSocket().shutdownOutput();
			if(inputStream!=null)
				inputStream.close();
		}else {
			
		}
		
		
	}

}
