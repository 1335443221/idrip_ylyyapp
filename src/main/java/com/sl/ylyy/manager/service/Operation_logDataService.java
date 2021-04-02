package com.sl.ylyy.manager.service;


import com.sl.ylyy.manager.entity.Operation_log;

import java.util.Map;

import com.sl.ylyy.common.utils.PageUtil;


public interface Operation_logDataService {
	public PageUtil<Operation_log> findPage(PageUtil<Operation_log> page);
	
	public PageUtil<Operation_log> findPage2(PageUtil<Operation_log> page);
	
	public void insertOperation_log(Map<String,Object> map);
	
}
