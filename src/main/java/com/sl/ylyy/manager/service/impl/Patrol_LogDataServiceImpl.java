package com.sl.ylyy.manager.service.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.sl.ylyy.manager.dao.MalfunctionDao;
import com.sl.ylyy.manager.dao.Patrol_LogDao;
import com.sl.ylyy.manager.entity.Patrol_Log;
import com.sl.ylyy.manager.service.Patrol_LogDataService;
import com.sl.ylyy.common.utils.PageUtil;


@Service("Patrol_LogDataImpl")
public class Patrol_LogDataServiceImpl implements Patrol_LogDataService {


	@Autowired
	private Patrol_LogDao Patrol_LogDao;
	@Autowired
	private MalfunctionDao malfunctionDao;

	@Override
	public ArrayList<Patrol_Log> getAllPatrol_Log(String Stg) {
		// TODO Auto-generated method stub
		return Patrol_LogDao.getAllPatrol_Log(Stg);
	}

	public void deletePatrol_LogById(Integer id) {
		// TODO Auto-generated method stub
		Patrol_LogDao.deletePatrol_LogById(id);
		
	}

	@Override
	public void insertPatrol_Log(Patrol_Log Patrol_Log) {
		// TODO Auto-generated method stub
		Patrol_LogDao.insertPatrol_Log(Patrol_Log);
		
	}

	@Override
	public void updatePatrol_LogById(Patrol_Log Patrol_Log) {
		// TODO Auto-generated method stub
		Patrol_LogDao.updatePatrol_LogById(Patrol_Log);
		
	}

	@Override
	public PageUtil<Patrol_Log> findPage(PageUtil<Patrol_Log> page){
		PageHelper.startPage(page.getPageindex(), page.getPagesize());   //分页
		List<Patrol_Log> list=Patrol_LogDao.findPage(page);  //分页之后的集合
		
		for(int i=0;i<list.size();i++){
			Map<String, Object> map=new HashMap<String, Object>();
			map.put("userid", list.get(i).getPatrolUserInfo().getId()); //人
			
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
				Date date;
				try {
					date = simpleDateFormat.parse(list.get(i).getTime());
					long s= Long.parseLong(String.valueOf(date.getTime()));  //转换为今天的时间戳
					map.put("stime", s/1000);   //一天的起始时间
					map.put("etime", s/1000+86400);   //一天的结束时间
				     
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		       
			list.get(i).setMalcount(malfunctionDao.getMalPatrolCount(map));  //故障次数
		}
		
		PageUtil<Patrol_Log> pageInfo=new PageUtil<Patrol_Log>();  //存到page里
		pageInfo.setData(list);   //把查询结果存到page里
		pageInfo.setRecordCount(Patrol_LogDao.findPage(page).size());   //总记录数
		return pageInfo;
	}

	@Override
	public Patrol_Log getPatrol_LogById(Integer id) {
		// TODO Auto-generated method stub
		return Patrol_LogDao.getPatrol_LogById(id);
	}

	@Override
	public PageUtil<Patrol_Log> report(PageUtil<Patrol_Log> page) {
		List<Patrol_Log> list=Patrol_LogDao.findPage(page);  //分页之后的集合
		for(int i=0;i<list.size();i++){
			Map<String, Object> map=new HashMap<String, Object>();
			map.put("userid", list.get(i).getPatrolUserInfo().getId()); //人
			
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
				Date date;
				try {
					date = simpleDateFormat.parse(list.get(i).getTime());
					long s= Long.parseLong(String.valueOf(date.getTime()));  //转换为今天的时间戳
					map.put("stime", s/1000);   //一天的起始时间
					map.put("etime", s/1000+86400);   //一天的结束时间
				     
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		       
			list.get(i).setMalcount(malfunctionDao.getMalPatrolCount(map));  //故障次数
		}
		PageUtil<Patrol_Log> pageInfo=new PageUtil<Patrol_Log>();  //存到page里
		pageInfo.setData(list);   //把查询结果存到page里
		pageInfo.setRecordCount(Patrol_LogDao.findPage(page).size());   //总记录数
		return pageInfo;
	}
	

}
