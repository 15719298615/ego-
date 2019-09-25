package com.ego.order.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.ego.commons.pojo.EgoResult;
import com.ego.commons.pojo.TbItemChild;
import com.ego.commons.utils.CookieUtils;
import com.ego.commons.utils.HttpClientUtil;
import com.ego.commons.utils.IDUtils;
import com.ego.commons.utils.JsonUtils;
import com.ego.dubbo.service.TbItemDubboService;
import com.ego.dubbo.service.TbOrderDubboService;
import com.ego.order.pojo.MyOrderParam;
import com.ego.order.service.TbOrderService;
import com.ego.pojo.TbItem;
import com.ego.pojo.TbOrder;
import com.ego.pojo.TbOrderItem;
import com.ego.pojo.TbOrderShipping;
import com.ego.redis.dao.impl.JedisDaoImpl;
@Service
public class TbOrderServiceImpl implements TbOrderService {
	@Resource
	private JedisDaoImpl jedisDaoImpl;
	@Value("${cart.key}")
	private String cartKey;
	@Value("${passport.url}")
	private String passportUrl;
	@Reference
	private TbItemDubboService tbItemDubboServiceImpl;
	@Reference
	private TbOrderDubboService tbOrderDubboServiceImpl;
	@Override
	public List<TbItemChild> showOrderCart(List<Long> ids, HttpServletRequest req) {
		String token = CookieUtils.getCookieValue(req, "TT_TOKEN");
		String result = HttpClientUtil.doPost(passportUrl+token);
		EgoResult er = JsonUtils.jsonToPojo(result, EgoResult.class);
		String key =cartKey+((LinkedHashMap)er.getData()).get("username");
		String json = jedisDaoImpl.get(key);
		List<TbItemChild> list = JsonUtils.jsonToList(json, TbItemChild.class);
		
		List<TbItemChild> listNew = new ArrayList<>();
		for (TbItemChild child : list) {
			for (Long id: ids) {
				if((long)child.getId()==(long)id){
					//判断购买量是否大于等于库存
					TbItem tbItem = tbItemDubboServiceImpl.selById(id);
					if(tbItem.getNum()>=child.getNum()){
						child.setEnough(true);
					}else{
						child.setEnough(false);
					}
					listNew.add(child);
				}
			}
			
		}
		
		return listNew;
	}
	@Override
	public EgoResult create(MyOrderParam param,HttpServletRequest req) {
		//订单表数据
		TbOrder order = new TbOrder();
		order.setPayment(param.getPayment());
		order.setPaymentType(param.getPaymentType());
		long id = IDUtils.genItemId();
		order.setOrderId(id+"");
		Date date = new Date();
		order.setCloseTime(date);
		order.setUpdateTime(date);
		//用户id
		String token = CookieUtils.getCookieValue(req, "TT_TOKEN");
		String result = HttpClientUtil.doPost(passportUrl+token);
		EgoResult er = JsonUtils.jsonToPojo(result, EgoResult.class);
		Map user =(LinkedHashMap)er.getData();
		order.setUserId(Long.parseLong(user.get("id").toString()));
		order.setBuyerNick(user.get("username").toString());
		order.setBuyerRate(0);
		
		//订单-商品表
		for (TbOrderItem item : param.getOrderItems()) {
			item.setId(IDUtils.genImageName()+"");
			item.setOrderId(id+"");;
		}
		
		//收货人信息
		TbOrderShipping shipping = param.getOrderShipping();
		shipping.setOrderId(id+"");
		shipping.setCreated(date);
		shipping.setUpdated(date);
		
		EgoResult erResult = new EgoResult();
		try {
			int index = tbOrderDubboServiceImpl.insOrder(order, param.getOrderItems(), shipping);
			if(index>0){
				erResult.setStatus(200);
				//删除购买的商品
				String json = jedisDaoImpl.get(cartKey+user.get("username"));
				List<TbItemChild> listCart = JsonUtils.jsonToList(json, TbItemChild.class);
				List<TbItemChild> listCartNew = new ArrayList<>();
				for (TbItemChild Child : listCart) {
					for (TbOrderItem item : param.getOrderItems()) {
						if(Child.getId().longValue()==Long.parseLong(item.getItemId())){
							listCartNew.add(Child);
						}
					}
				}
				
				for (TbItemChild myNew : listCartNew) {
					listCart.remove(myNew);
					
				}
				//删除
				jedisDaoImpl.set(cartKey+user.get("username"), JsonUtils.objectToJson(listCart));
				
				
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return erResult;
	}
	
}
