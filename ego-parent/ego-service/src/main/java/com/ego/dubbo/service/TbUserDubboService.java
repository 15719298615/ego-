package com.ego.dubbo.service;

import com.ego.pojo.TbUser;

public interface TbUserDubboService {
	/**
	 * 根据有户名和密码查询登录
	 * @return
	 */
	TbUser selByUser(TbUser user);
}
