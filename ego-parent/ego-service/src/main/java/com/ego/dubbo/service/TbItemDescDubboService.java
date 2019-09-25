package com.ego.dubbo.service;

import com.ego.pojo.TbItemDesc;

public interface TbItemDescDubboService {
	/**
	 * 新增
	 * @param tbItemDesc
	 * @return
	 */
	int insDesc(TbItemDesc tbItemDesc);
	
	/**
	 * 根据主键查询商品描述对象
	 * @return
	 */
	TbItemDesc selByItenId(long itemid);
}
