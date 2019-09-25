package com.ego.dubbo.service;

import java.util.List;

import com.ego.pojo.TbContentCategory;

public interface TbContentCategoryDubboService {
	/**
	 * 根据父id查询子Id
	 * @param id
	 * @return
	 */
	List<TbContentCategory> selByPid(long id); 
	
	/**
	 * 新增
	 * @param cate
	 * @return
	 */
	int insTbContentCategory(TbContentCategory cate);
	
	
	/**
	 * 修改isparent属性通过id
	 * @param id
	 * @param isParent
	 * @return
	 */
	int updIsParentById(TbContentCategory cate);
	
	
	/**
	 * 根据id查询内容类目详细信息
	 */
	
	TbContentCategory selById(long id);
	
	
	
}
