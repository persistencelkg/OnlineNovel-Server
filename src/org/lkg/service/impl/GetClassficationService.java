package org.lkg.service.impl;

import java.io.IOException;
import java.io.Serializable;

import org.lkg.entity.Classifcation;
import org.lkg.entity.SysDTO;
import org.lkg.service.BaseServiceImpl;
import org.lkg.txt.util.NovelUtil;
import org.lkg.util.ResultStatus;
/**
 * 获取小说的所有分类业务
 * @description:
 * @author: 浮~沉
 * @version: 1.0
 * @data 2020年1月6日 下午8:24:15
 * @CopyRight lkg.nb.com
 */
public class GetClassficationService extends BaseServiceImpl<Serializable>{

	@Override
	protected void execute() throws IOException {
		SysDTO<Classifcation[]> dto=new SysDTO<>();
		dto.setData(NovelUtil.getClassfilcation());
		
		this.getOut().writeObject(dto);
	}

}
