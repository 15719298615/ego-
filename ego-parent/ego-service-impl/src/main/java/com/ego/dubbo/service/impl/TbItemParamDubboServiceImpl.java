package com.ego.dubbo.service.impl;

import java.util.List;

import javax.annotation.Resource;

import com.ego.commons.pojo.EasyUIDataGrid;
import com.ego.dubbo.service.TbItemParamDubboService;
import com.ego.mapper.TbItemParamMapper;
import com.ego.pojo.TbItemParam;
import com.ego.pojo.TbItemParamExample;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

public class TbItemParamDubboServiceImpl implements TbItemParamDubboService {
	@Resource
	private TbItemParamMapper tbItemParamMapper; 
	@Override
	public EasyUIDataGrid showPage(int page, int rows) {
		//先设置分页条件
		PageHelper.startPage(page, rows);
		//设置潮汛的sql语句
		//XXXXExample（） 设置了什么，相当于在sql语句中where中添加条件
		List<TbItemParam> list = tbItemParamMapper.selectByExampleWithBLOBs(new TbItemParamExample());
		//根据程序员自己编写的sql语句结合分页插件产生最终的结果，封装到PageInfo中
		PageInfo<TbItemParam> pi = new PageInfo<>(list);
		
		//设置返回结果
		EasyUIDataGrid datagrid = new EasyUIDataGrid();
		datagrid.setRows(pi.getList());
		datagrid.setTotal(pi.getTotal());
		return datagrid;
	}
	@Override
	public int delByIds(String ids) throws Exception {
		int index = 0;
		String[] idStr = ids.split(",");
		for (String id : idStr) {
			index+=tbItemParamMapper.deleteByPrimaryKey(Long.parseLong(id));
			
		}
		if(index==idStr.length){
			return 1;
		}else{
			throw new Exception("删除失败.可能原因：数据已经不存在");
		}
	}
	@Override
	public TbItemParam selByCatId(Long catId) {
		TbItemParamExample example = new TbItemParamExample();
		example.createCriteria().andItemCatIdEqualTo(catId);
		List<TbItemParam> list = tbItemParamMapper.selectByExampleWithBLOBs(example);
		
		if(list!=null&&list.size()>0){
			//要求每个类目只能由一个模板
			return list.get(0);
		}
		return null;
	}
	@Override
	public int insParam(TbItemParam param) {
		// TODO Auto-generated method stub
		return tbItemParamMapper.insertSelective(param);
	}

}
