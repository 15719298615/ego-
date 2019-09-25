package com.ego.passport.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ego.commons.pojo.EgoResult;
import com.ego.pojo.TbUser;

public interface TbUserService {
	/**
	 * 登录
	 * @return
	 */
	EgoResult login(TbUser user,HttpServletRequest req,HttpServletResponse resp);
	
	/**
	 * 根据token查询用户信息
	 * @return
	 */
	EgoResult getUserInfoByToken(String token);
	/**
	 * 推出
	 * @param token
	 * @param req
	 * @param resp
	 * @return
	 */
	EgoResult logout(String token,HttpServletRequest req,HttpServletResponse resp);
}
