package com.ego.portal.controller;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ego.portal.service.TbContentService;

@Controller
public class TbContentController {
	@Resource
	private TbContentService tbContentServiceimpl;
	@RequestMapping("showBigPic")
	public String showBigPic(Model model){
		model.addAttribute("ad1", tbContentServiceimpl.showBigPic());
		return "index";
	}
	
}
