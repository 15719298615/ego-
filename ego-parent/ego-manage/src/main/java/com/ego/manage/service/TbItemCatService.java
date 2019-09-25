package com.ego.manage.service;

import java.util.List;

import com.ego.commons.pojo.EasyUiTree;

public interface TbItemCatService {
	/**
	 * 很具父菜单id显示所有子菜单
	 * @param pid
	 * @return
	 */
	List<EasyUiTree> show(Long pid);
	
	
	
}
