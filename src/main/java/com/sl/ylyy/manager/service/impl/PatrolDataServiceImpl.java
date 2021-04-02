package com.sl.ylyy.manager.service.impl;

import java.text.SimpleDateFormat;
import java.util.*;

import com.alibaba.fastjson.JSONObject;
import com.sl.ylyy.app_1.dao.PatrolMapper;
import com.sl.ylyy.app_1.dao.PatrolTypeMapper;
import com.sl.ylyy.manager.entity.PatrolInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.sl.ylyy.manager.dao.PatrolDao;
import com.sl.ylyy.manager.service.PatrolDataService;
import com.sl.ylyy.common.utils.PageUtil;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;


@Service("PatrolDataImpl")
public class PatrolDataServiceImpl implements PatrolDataService {


	@Autowired
	private PatrolDao PatrolDao;
	@Autowired
	private PatrolTypeMapper patrolTypeMapper;
	@Autowired
	private PatrolMapper patrolMapper;

	@Override
	public ArrayList<PatrolInfo> getAllPatrol(String rname) {
		// TODO Auto-generated method stub
		return PatrolDao.getAllPatrol(rname);
	}

	@Override
	@Transactional
	public void deletePatrolById(Integer id) {
		// TODO Auto-generated method stub
		PatrolDao.deletePatrolById(id);
		PatrolDao.deletePatrolLogByPid(id);

	}

	/*@Override
	public void insertPatrol(PatrolInfo PatrolInfo) {
		// TODO Auto-generated method stub
		PatrolDao.insertPatrol(PatrolInfo);

	}*/

	@Override
	public void updatePatrolById(PatrolInfo PatrolInfo) {
		// TODO Auto-generated method stub
		PatrolDao.updatePatrolById(PatrolInfo);
		
	}

	@Override
	public PatrolInfo getPatrolById(Integer id) {
		// TODO Auto-generated method stub
		return PatrolDao.getPatrolById(id);
	}

	@Override
	public PageUtil<Map<String,Object>> findPage(Map<String,Object> map, HttpSession session) {
		PageUtil<Map<String, Object>> pageInfo=new PageUtil<Map<String, Object>>();
		int pageindex =Integer.parseInt(String.valueOf(map.get("pageindex")));  //页码
		PageHelper.startPage(pageindex,pageInfo.getPagesize());  //分页
		List<Map<String,Object>> list=PatrolDao.findPage(map);  //分页后的数据

		List<Map<String,Object>> type_data=PatrolDao.patrol_type_list(map);  //type数据

		List<Map<String,Object>> time_data=PatrolDao.patrol_time_list(map);  //type数据

		Map<String,Object> smap= JSONObject.parseObject(String.valueOf(session.getAttribute("activeAdmin")));
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");   //把时间转换成年月日
		long create_at=0;
		for(int i=0;i<list.size();i++){
		create_at=Long.parseLong(String.valueOf(list.get(i).get("create_at")))*1000;
		list.get(i).put("create_at",sdf.format(create_at));
		}

		map.put("time_data", time_data);
		map.put("type_data", type_data);
		pageInfo.setData(list);  //把数据存到page中

		pageInfo.setRecordCount(PatrolDao.findPage(map).size());  //总记录数
		pageInfo.setPageindex(pageindex);  //返回当前页
		map.put("lastpage", pageInfo.getLastPage());  //上一页
		map.put("nextpage", pageInfo.getNextpage(pageInfo.getTotalPagecount()));  //下一页
		pageInfo.setParams(map);  //返回模糊查询信息
		return pageInfo;
	}


	@Override
	public ArrayList<Map<String, Object>> getPatrolType(Map<String, Object> map) {
		return PatrolDao.getPatrolType(map);
	}

	@Override
	public ArrayList<Map<String, Object>> getPatrolPoint(Map<String, Object> map) {
		return PatrolDao.getPatrolPoint(map);
	}

	@Override
	public ArrayList<Map<String, Object>> patrolTime(Map<String, Object> map) {
		return PatrolDao.patrolTime(map);
	}
	@Override
	public ArrayList<Map<String, Object>> patrolTimeInterval(Map<String, Object> map) {
		return PatrolDao.patrolTimeInterval(map);
	}
	@Override
	public ArrayList<Map<String, Object>> departmentList(Map<String, Object> map) {
		return PatrolDao.departmentList(map);
	}

	@Override
	public ArrayList<Map<String, Object>> getPatrolCountByTime(Map<String, Object> map) {
        Map<String, Object> time=PatrolDao.patrolTime(map).get(0);
        List<String> count= Arrays.asList(String.valueOf(time.get("count")).split(","));

        ArrayList<Map<String, Object>> result=new ArrayList<>();
        for (String i:count){
            Map<String, Object> resultMap=new HashMap<>();
            resultMap.put("count",i);
            resultMap.put("count_name",i+"次");
            result.add(resultMap);
        }
		return result;
	}


	@Override
	public String insertPatrol(Map<String, Object> map) {
		Integer departmentId = Integer.parseInt(String.valueOf(map.get("department_id")));
		Integer patrol_point_id = Integer.parseInt(map.get("patrol_point_id").toString());
		Integer time_type = Integer.parseInt(map.get("time_type").toString());

		String alais = patrolTypeMapper.selectTimeTypeAliasById(time_type);
		List<String> alaisList = patrolMapper.selectTimeTypes(departmentId,patrol_point_id);
		if(alaisList.contains(alais)){
			return "本部门该时段和巡检点已存在巡检任务";
		}

		//早班晚班、周巡检、月巡检不能同时存在在一个巡检计划中
		if(((alais.equals("early")||alais.equals("evening"))&&(alaisList.contains("weekly")||alaisList.contains("monthly")))
				||((alais.equals("weekly")||alais.equals("monthly"))&&(alaisList.contains("early")||alaisList.contains("evening")))||
				((alais.equals("weekly"))&&alaisList.contains("monthly"))||
				((alais.equals("monthly"))&&alaisList.contains("weekly"))){
			return "巡检时间段有重叠";
		}

        map.put("create_at",Math.round(System.currentTimeMillis()/1000));
		PatrolDao.insertPatrol(map);
		return "1";
	}
}
