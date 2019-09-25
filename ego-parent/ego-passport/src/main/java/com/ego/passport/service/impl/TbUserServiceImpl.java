package com.ego.passport.service.impl;

import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.ego.commons.pojo.EgoResult;
import com.ego.commons.utils.CookieUtils;
import com.ego.commons.utils.JsonUtils;
import com.ego.dubbo.service.TbUserDubboService;
import com.ego.passport.service.TbUserService;
import com.ego.pojo.TbUser;
import com.ego.redis.dao.JedisDao;
@Service
public class TbUserServiceImpl implements TbUserService {
	
	@Reference
	private TbUserDubboService tbUserDubboServiceImpl; 
	@Resource
	JedisDao jedisDaoImpl;
	public EgoResult login(TbUser user,HttpServletRequest req,HttpServletResponse resp){
		EgoResult er = new EgoResult();
		TbUser userSelect = tbUserDubboServiceImpl.selByUser(user);
		if(userSelect!=null){
			er.setStatus(200);
			//当用户登陆成功之后把用户信息放到redis中
			String key = UUID.randomUUID().toString();
			jedisDaoImpl.set(key, JsonUtils.objectToJson(userSelect));
			jedisDaoImpl.expire(key, 60*60*24*7);
			//产生cookie
			CookieUtils.setCookie(req, resp, "TT_TOKEN", key, 60*60*24*7);
		}else{
			er.setMsg("用户名密码错误！");
		}
		return er;
	}
	@Override
	public EgoResult getUserInfoByToken(String token) {
		EgoResult er = new EgoResult();
		String json = jedisDaoImpl.get(token);
		if(json!=null&&!json.equals("")){
			TbUser tbUser = JsonUtils.jsonToPojo(json, TbUser.class);
			//可以把密码清空
			tbUser.setPassword(null);
			er.setStatus(200);
			er.setMsg("OK");
			er.setData(tbUser);
		}else{
			er.setMsg("获取失败");
		}
		
		return er;
	}
	@Override
	public EgoResult logout(String token, HttpServletRequest req, HttpServletResponse resp) {
		jedisDaoImpl.del(token);
		CookieUtils.deleteCookie(req, resp, "TT_TOKEN");
		EgoResult er = new EgoResult();
		er.setStatus(200);
		er.setMsg("OK");
		return er;
	}
	
	
}
