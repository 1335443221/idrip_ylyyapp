package com.sl.ylyy.manager.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.github.pagehelper.PageHelper;
import com.sl.ylyy.manager.dao.Operation_logDao;
import com.sl.ylyy.manager.entity.Operation_log;
import com.sl.ylyy.manager.service.Operation_logDataService;
import com.sl.ylyy.common.utils.PageUtil;


@Service("Operation_logDataImpl")
public class Operation_LogDataServiceImpl implements Operation_logDataService {


	@Autowired
	private Operation_logDao Operation_logDao;

	@Override
	public void insertOperation_log(Map<String,Object> map) {
		Operation_logDao.insertOperation_log(map);  //新增
	}

	@Override
	public PageUtil<Operation_log> findPage(PageUtil<Operation_log> page) {
		PageHelper.startPage(page.getPageindex(), page.getPagesize());   //分页
		List<Operation_log> list=Operation_logDao.findPage(page);  //分页之后的集合
		PageUtil<Operation_log> pageInfo=new PageUtil<Operation_log>();  //存到page里
		pageInfo.setData(list);   //把查询结果存到page里
		pageInfo.setPagesize(page.getPagesize());
		pageInfo.setRecordCount(Operation_logDao.findPage(page).size());   //总记录数
		return pageInfo;
	}

	@Override
	public PageUtil<Operation_log> findPage2(PageUtil<Operation_log> page) {
		PageHelper.startPage(page.getPageindex(), page.getPagesize());   //分页
		List<Operation_log> list=Operation_logDao.findPage2(page);  //分页之后的集合
		PageUtil<Operation_log> pageInfo=new PageUtil<Operation_log>();  //存到page里
		pageInfo.setData(list);   //把查询结果存到page里
		pageInfo.setPagesize(page.getPagesize());
		pageInfo.setRecordCount(Operation_logDao.findPage2(page).size());   //总记录数
		return pageInfo;
	}

}
