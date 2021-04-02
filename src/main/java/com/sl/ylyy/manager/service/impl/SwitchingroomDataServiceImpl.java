package com.sl.ylyy.manager.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.sl.ylyy.app_1.entity.Switchingroom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.sl.ylyy.manager.dao.SwitchingroomDao;
import com.sl.ylyy.manager.dao.Tip_Room_RelationDao;
import com.sl.ylyy.manager.service.SwitchingroomDataService;
import com.sl.ylyy.common.utils.PageUtil;


@Service("SwitchingroomDataImpl")
public class SwitchingroomDataServiceImpl implements SwitchingroomDataService {


	@Autowired
	private SwitchingroomDao SwitchingroomDao;
	
	@Autowired
	private Tip_Room_RelationDao tip_Room_RelationDao;

	@Override
	public ArrayList<Switchingroom> getAllSwitchingroom(String Stg) {
		// TODO Auto-generated method stub
		return SwitchingroomDao.getAllSwitchingroom(Stg);
	}

	public void deleteSwitchingroomById(Integer id) {
		// TODO Auto-generated method stub
		SwitchingroomDao.deleteSwitchingroomById(id);
		
	}

	@Override
	public void insertSwitchingroom(Map<String, Object> map) {
		// TODO Auto-generated method stub
		
		SwitchingroomDao.insertSwitchingroom(map);  //新增
	}

	@Override
	public void updateSwitchingroomById(Map<String, Object> map) {
		// TODO Auto-generated method stub
		SwitchingroomDao.updateSwitchingroomById(map);
		
	}

	@Override
	public PageUtil<Switchingroom> findPage(PageUtil<Switchingroom> page) {
		PageHelper.startPage(page.getPageindex(), page.getPagesize());   //分页
		List<Switchingroom> list=SwitchingroomDao.findPage(page);  //分页之后的集合
		PageUtil<Switchingroom> pageInfo=new PageUtil<Switchingroom>();  //存到page里
		pageInfo.setData(list);   //把查询结果存到page里
		pageInfo.setPagesize(page.getPagesize());
		pageInfo.setRecordCount(SwitchingroomDao.findPage(page).size());   //总记录数
		return pageInfo;
	}

	@Override
	public Switchingroom getSwitchingroomById(Integer id) {
		// TODO Auto-generated method stub
		return SwitchingroomDao.getSwitchingroomById(id);
	}

	@Override
	public int getMaxSequenceSwitchingroom() {
		// TODO Auto-generated method stub
		return SwitchingroomDao.getMaxSequenceSwitchingroom();
	}

	@Override
	public Switchingroom getSwitchingroomBySequence(Map<String, Object> map) {
		// TODO Auto-generated method stub
		return SwitchingroomDao.getSwitchingroomBySequence(map);
	}

	@Override
	public List<Switchingroom> getSwitchingroombyS(Switchingroom s) {
		// TODO Auto-generated method stub
		return SwitchingroomDao.getSwitchingroombyS(s);
	}

	@Override
	public List<Switchingroom> getSwitchingroombyTip(int tid) {
		// TODO Auto-generated method stub
		return SwitchingroomDao.getSwitchingroombyTip(tid);
	}
	

}
