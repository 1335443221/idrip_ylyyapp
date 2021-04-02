package com.sl.ylyy.manager.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.sl.ylyy.manager.entity.PatrolPointInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.sl.ylyy.manager.dao.PatrolDao;
import com.sl.ylyy.manager.dao.Patrol_PointDao;
import com.sl.ylyy.manager.service.Patrol_PointDataService;
import com.sl.ylyy.common.utils.PageUtil;


@Service("Patrol_PointDataImpl")
public class Patrol_PointDataServiceImpl implements Patrol_PointDataService {


	@Autowired
	private Patrol_PointDao Patrol_PointDao;
	
	@Autowired
	private PatrolDao patrolDao;

	@Override
	public ArrayList<PatrolPointInfo> getAllPatrol_Point(String ppname) {
		// TODO Auto-generated method stub
		return Patrol_PointDao.getAllPatrol_Point(ppname);
	}

	public void deletePatrol_PointById(Integer id) {
		// TODO Auto-generated method stub
		patrolDao.deletePatrolByPId(id);
		Patrol_PointDao.deletePatrol_PointById(id);
	}

	@Override
	public int insertPatrol_Point(Map<String, Object> map){
		long ts = Math.round(new Date().getTime()/1000);
		
		map.put("create_at", ts);
		return Patrol_PointDao.insertPatrol_Point(map);
		
	}

	@Override
	public int updatePatrol_PointById(Map<String, Object> map) {
		long ts = Math.round(new Date().getTime()/1000);
		map.put("create_at", ts);
		return Patrol_PointDao.updatePatrol_PointById(map);
		
	}

	@Override
	public PageUtil<PatrolPointInfo> findPage(PageUtil<PatrolPointInfo> page) {
		PageHelper.startPage(page.getPageindex(), page.getPagesize());   //分页
		List<PatrolPointInfo> list=Patrol_PointDao.findPage(page);  //分页之后的集合
		PageUtil<PatrolPointInfo> pageInfo=new PageUtil<PatrolPointInfo>();  //存到page里
		pageInfo.setData(list);   //把查询结果存到page里
		pageInfo.setPagesize(page.getPagesize());
		pageInfo.setRecordCount(Patrol_PointDao.findPage(page).size());   //总记录数
		return pageInfo;
	}
	
	

	@Override
	public PatrolPointInfo getPatrol_PointById(Integer id) {
		// TODO Auto-generated method stub
		return Patrol_PointDao.getPatrol_PointById(id);
	}
	

}
