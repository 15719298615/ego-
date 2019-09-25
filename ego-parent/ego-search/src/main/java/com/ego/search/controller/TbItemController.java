package com.ego.search.controller;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ego.search.service.TbItemService;

@Controller
public class TbItemController {
	@Resource
	private TbItemService tbItemServiceImpl;
	
	@RequestMapping(value="solr/init",produces="text/html;charset=utf-8")
	@ResponseBody
	public String init(){
		long cur = System.currentTimeMillis();
		try {
			tbItemServiceImpl.init();
			long now = System.currentTimeMillis();
			return "初始化时间："+(now-cur)/1000+"秒";
		} catch (Exception e) {
			e.printStackTrace();
			return "初始化失败";
		} 
		
	}
	/**
	 * 搜索功能
	 * @param model
	 * @param q
	 * @param rows
	 * @param page
	 * @return
	 */
	@RequestMapping("search.html")
	public String search(Model model,String q,@RequestParam(defaultValue="1") int page,@RequestParam(defaultValue="12") int rows){
		try {
			q=new String(q.getBytes("iso-8859-1"),"utf-8");
			Map<String, Object> map = tbItemServiceImpl.selByQuery(q, page, rows);
			model.addAttribute("query",q);
			model.addAttribute("itemList", map.get("itemList"));
			model.addAttribute("totalPages", map.get("totalPages"));
			model.addAttribute("page", page);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return "search";
	}
	/**
	 * 新增
	 * @param item
	 * @return
	 */
	@RequestMapping("solr/add")
	@ResponseBody
	public int add(@RequestBody Map<String,Object> map){
		try {
			return tbItemServiceImpl.add((LinkedHashMap)map.get("item"),map.get("desc").toString());
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return 0;
	}
	
	
}
