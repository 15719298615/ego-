package com.ego.dubbo.service.impl;

import java.util.List;

import javax.annotation.Resource;

import com.ego.dubbo.service.TbOrderDubboService;
import com.ego.mapper.TbOrderItemMapper;
import com.ego.mapper.TbOrderMapper;
import com.ego.mapper.TbOrderShippingMapper;
import com.ego.pojo.TbOrder;
import com.ego.pojo.TbOrderItem;
import com.ego.pojo.TbOrderShipping;

public class TbOrderDubboServiceImpl implements TbOrderDubboService {
	@Resource
	private TbOrderMapper tbOrderMapper;
	@Resource
	private TbOrderItemMapper tbOrderItemMapper;
	@Resource
	private TbOrderShippingMapper tbOrderShippingMapper;
	
	@Override
	public int insOrder(TbOrder order, List<TbOrderItem> list, TbOrderShipping shipping) throws Exception {
		int index = tbOrderMapper.insert(order);
		for (TbOrderItem tbOrderItem : list) {
			index+=tbOrderItemMapper.insert(tbOrderItem);
		}
		index+=tbOrderShippingMapper.insert(shipping);
		if(index==2+list.size()){
			return 1;
		}else{
			throw new Exception("创建订单失败！");
		}
	}
	
}
