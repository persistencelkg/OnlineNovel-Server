package org.lkg.service.impl;

import java.io.IOException;

import org.lkg.entity.Classifcation;
import org.lkg.entity.Novel;
import org.lkg.entity.SysDTO;
import org.lkg.service.BaseServiceImpl;
import org.lkg.txt.util.NovelUtil;

/**
 * 服务端--获取小说集合
 * @description: 需要根据分类获得对象小说集合
 * @author: 浮~沉
 * @version: 1.0
 * @data 2020年1月6日 下午10:54:57
 * @CopyRight lkg.nb.com
 */
public class GetNoveInfoByClassesNameService extends BaseServiceImpl<Classifcation> {

	@Override
	protected void execute() throws IOException {
		SysDTO<Novel[]> dto=new SysDTO<>();
		dto.setData(NovelUtil.getNovel(this.getData()));

		getOut().writeObject(dto);
	}

}
