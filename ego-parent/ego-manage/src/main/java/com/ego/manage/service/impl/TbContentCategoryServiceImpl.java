package com.ego.manage.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.ego.commons.pojo.EasyUiTree;
import com.ego.commons.pojo.EgoResult;
import com.ego.commons.utils.IDUtils;
import com.ego.dubbo.service.TbContentCategoryDubboService;
import com.ego.manage.service.TbContentCategoryService;
import com.ego.pojo.TbContentCategory;

@Service
public class TbContentCategoryServiceImpl implements TbContentCategoryService {
	@Reference
	private TbContentCategoryDubboService tbContentCateGoryDubboServiceImpl;
	
	@Override
	public List<EasyUiTree> showCategory(long id) {
		List<EasyUiTree> listTree = new ArrayList<>();
		
		List<TbContentCategory> list = tbContentCateGoryDubboServiceImpl.selByPid(id);
		for (TbContentCategory cate : list) {
			EasyUiTree tree = new EasyUiTree();
			tree.setId(cate.getId());
			tree.setText(cate.getName());
			tree.setState(cate.getIsParent()?"closed":"open");
			
			listTree.add(tree);
		}
		
		return listTree;
	}

	@Override
	public EgoResult create(TbContentCategory cate) {
		
		EgoResult er = new EgoResult();
		
		
		//判断当前节点名称是否存在
		List<TbContentCategory> children = tbContentCateGoryDubboServiceImpl.selByPid(cate.getParentId());
		for (TbContentCategory child : children) {
			if(child.getName().equals(cate.getName())){
				er.setData("该分类名称已存在！");
				return er;
			}
		}
		
		
		Date date = new Date();
		cate.setCreated(date);
		cate.setUpdated(date);
		cate.setStatus(1);
		cate.setSortOrder(1);
		cate.setIsParent(false);
		long id = IDUtils.genItemId();
		cate.setId(id);
		int index = tbContentCateGoryDubboServiceImpl.insTbContentCategory(cate);
		if(index>0){
			TbContentCategory parent = new TbContentCategory();
			parent.setId(cate.getParentId());
			parent.setIsParent(true);
			tbContentCateGoryDubboServiceImpl.updIsParentById(parent);
			
		}
		
		er.setStatus(200);
		Map<String,Long> map = new HashMap<>();
		map.put("id", id);
		
		er.setData(map);
		return er;
		
	}

	@Override
	public EgoResult updata(TbContentCategory cate) {
		//查询当前节点的信息
		TbContentCategory cateSelect = tbContentCateGoryDubboServiceImpl.selById(cate.getId());
		EgoResult er = new EgoResult();
		//查询当前节点的父节点的所有子节点的信息
		List<TbContentCategory> children = tbContentCateGoryDubboServiceImpl.selByPid(cateSelect.getParentId());
		//判断当前节点名称是否存在
		for (TbContentCategory child : children) {
			if(child.getName().equals(cate.getName())){
				er.setData("该分类名称已存在！");
				return er;
			}
		}
		
		int index = tbContentCateGoryDubboServiceImpl.updIsParentById(cate);
		if(index>0){
			er.setStatus(200);
		}
		
		return er;
	}

	@Override
	public EgoResult delete(TbContentCategory cate) {
		EgoResult er = new EgoResult();
		cate.setStatus(0);
		int index = tbContentCateGoryDubboServiceImpl.updIsParentById(cate);
		if(index>0){
			TbContentCategory curr = tbContentCateGoryDubboServiceImpl.selById(cate.getId());
			
			List<TbContentCategory> list = tbContentCateGoryDubboServiceImpl.selByPid(curr.getParentId());
			if(list==null||list.size()==0){
				
				TbContentCategory parent = new TbContentCategory();
				parent.setId(curr.getParentId());
				parent.setIsParent(false);
				int result = tbContentCateGoryDubboServiceImpl.updIsParentById(parent);
				if(result>0){
					er.setStatus(200);
				}
			}else{
				er.setStatus(200);
			}
		}
		return er;
	}
	
}
