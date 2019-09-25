package com.ego.manage.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.ego.commons.pojo.EasyUIDataGrid;
import com.ego.commons.utils.HttpClientUtil;
import com.ego.commons.utils.IDUtils;
import com.ego.commons.utils.JsonUtils;
import com.ego.dubbo.service.TbItemDescDubboService;
import com.ego.dubbo.service.TbItemDubboService;
import com.ego.manage.service.TbItemService;
import com.ego.pojo.TbItem;
import com.ego.pojo.TbItemDesc;
import com.ego.pojo.TbItemParamItem;
@Service
public class TbItemServiceImpl implements TbItemService {
	@Reference
	private TbItemDubboService tbItemDubboServiceImpl;
	@Reference
	private TbItemDescDubboService tbItemDescDubboService;
	@Value("${search.url}")
	private String url;
	@Override
	public EasyUIDataGrid show(int page, int rows) {
		
		return tbItemDubboServiceImpl.show(page, rows);
	}

	@Override
	public int update(String ids, byte status) {
		int index = 0;
		TbItem item = new TbItem();
		String[] idstr = ids.split(",");
		for (String string : idstr) {
			item.setId(Long.parseLong(string));
			item.setStatus(status);
			index+=tbItemDubboServiceImpl.updItemStatus(item);
		}
		if(index==1){
			return 1;
		}
		return 0;
	}

	@Override
	public int save(TbItem item, String desc,String itemParams) throws Exception {
		
		
		
		//不考虑事务回滚
//		long id = IDUtils.genItemId(); 
//		item.setId(id);
//		Date date = new Date();
//		item.setCreated(date);
//		item.setUpdated(date);
//		item.setStatus((byte)1);
//		int index = tbItemDubboServiceImpl.insTbItem(item);
//		if(index>0){
//			TbItemDesc tbItemDesc = new TbItemDesc();
//			tbItemDesc.setItemDesc(desc);
//			tbItemDesc.setItemId(id);
//			tbItemDesc.setCreated(date);
//			tbItemDesc.setUpdated(date);
//			index+=tbItemDescDubboService.insDesc(tbItemDesc);
//		}
//		if(index==2){
//			return 1;
//		}
		//调用dubbo中考虑事务回滚功能的方法
		
		long id = IDUtils.genItemId();
		item.setId(id);
		Date date = new Date();
		item.setCreated(date);
		item.setUpdated(date);
		item.setStatus((byte)1);
		
			TbItemDesc tbItemDesc = new TbItemDesc();
			tbItemDesc.setItemDesc(desc);
			tbItemDesc.setItemId(id);
			tbItemDesc.setCreated(date);
			tbItemDesc.setUpdated(date);
			
		TbItemParamItem paramItem = new TbItemParamItem();
		paramItem.setCreated(date);
		paramItem.setUpdated(date);
		paramItem.setItemId(id);
		paramItem.setParamData(itemParams);
		
		
		
			int index = 0;
					index = tbItemDubboServiceImpl.insTbItemDesc(item, tbItemDesc,paramItem);
		
					
		final TbItem itemFinal = item;
		final String descFinal = desc;
		new Thread(){
			public void run(){
				Map<String,Object> map = new HashMap<>();
				map.put("item", itemFinal);
				map.put("desc", descFinal);
				
				HttpClientUtil.doPostJson(url, JsonUtils.objectToJson(map));
				//使用java代码调用其他项目的控制器			
			}
		}.start();		
					
		return index;
	}
	
	
}
