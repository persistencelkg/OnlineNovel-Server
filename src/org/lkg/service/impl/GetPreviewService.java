package org.lkg.service.impl;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.lkg.entity.Novel;
import org.lkg.entity.SysDTO;
import org.lkg.service.BaseServiceImpl;
import org.lkg.txt.util.NovelUtil;
import org.lkg.util.ResultStatus;

/***
 * 服务端--小说预览
 * @description:
 * @author: 浮~沉
 * @version: 1.0
 * @data 2020年1月7日 上午11:05:47
 * @CopyRight lkg.nb.com
 */
public class GetPreviewService extends BaseServiceImpl<Novel>{

	/**
	 * 根据即将预览的小说获取 小说前几个章节
	 */
	@Override
	protected void execute() throws IOException {
		SysDTO<String> dto=new SysDTO<>();
		try {
			dto.setData(NovelUtil.getNovelPreview(getData()));
		} catch (FileNotFoundException e) {
			dto.setResust(ResultStatus.FILE_NOT_FOUND);
			e.printStackTrace();
		}catch (IOException e) {
			dto.setResust(ResultStatus.FILE_NOT_READ);
			e.printStackTrace();
		}
		getOut().writeObject(dto);
		
	}

}
