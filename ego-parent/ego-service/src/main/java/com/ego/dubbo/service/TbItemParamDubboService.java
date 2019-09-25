package com.ego.dubbo.service;

import com.ego.commons.pojo.EasyUIDataGrid;
import com.ego.pojo.TbItemParam;

public interface TbItemParamDubboService {
	/**
	 * 分页查询数据
	 * @param page
	 * @param rows
	 * @return 包含:当前页显示的数据和总条数
	 */
	EasyUIDataGrid showPage(int page,int rows);
	/**
	 * 批量删除
	 * @param ids
	 * @return
	 */
	int delByIds(String ids) throws Exception;
	
	/**
	 * 根据类目id查询参数模板
	 * @param id
	 * @return
	 */
	TbItemParam selByCatId(Long id);
	
	/**
	 * 新增，支持组件自增
	 * @param param
	 * @return
	 */
	int insParam(TbItemParam param);
}
