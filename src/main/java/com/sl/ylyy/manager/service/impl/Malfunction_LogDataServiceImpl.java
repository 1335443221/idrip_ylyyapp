package com.sl.ylyy.manager.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.sl.ylyy.manager.entity.MalfunctionLogInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.sl.ylyy.manager.dao.Malfunction_LogDao;
import com.sl.ylyy.manager.dao.UserDao;
import com.sl.ylyy.manager.service.Malfunction_LogDataService;
import com.sl.ylyy.common.utils.PageUtil;


@Service("Malfunction_LogDataImpl")
public class Malfunction_LogDataServiceImpl implements Malfunction_LogDataService {


	@Autowired
	private Malfunction_LogDao Malfunction_LogDao;

	@Autowired
	private UserDao userDao;
	
	
	@Override
	public ArrayList<MalfunctionLogInfo> getAllMalfunction_Log(String rname) {
		// TODO Auto-generated method stub
		return Malfunction_LogDao.getAllMalfunction_Log(rname);
	}

	public void deleteMalfunction_LogById(Integer id) {
		Malfunction_LogDao.deleteMalfunction_LogById(id);
	}

	@Override
	public void insertMalfunction_Log(MalfunctionLogInfo MalfunctionLogInfo) {
		// TODO Auto-generated method stub
		Malfunction_LogDao.insertMalfunction_Log(MalfunctionLogInfo);
		
	}

	@Override
	public void updateMalfunction_LogById(MalfunctionLogInfo MalfunctionLogInfo) {
		// TODO Auto-generated method stub
		Malfunction_LogDao.updateMalfunction_LogById(MalfunctionLogInfo);
		
	}

	
	@Override
	public PageUtil<MalfunctionLogInfo> findPage(PageUtil<MalfunctionLogInfo> page) {
		PageHelper.startPage(page.getPageindex(),page.getPagesize());    //分页
		List<MalfunctionLogInfo> list=Malfunction_LogDao.findPage(page);  //分页之后的集合
		
		//把所有支援人都存进去
				for(int i=0;i<list.size();i++){
					for(int j = 0; j<list.get(i).getSupportInfoList().size(); j++){
						if(list.get(i).getSupportInfoList().get(j).getSupport_staff()==null||list.get(i).getSupportInfoList().get(j).getSupport_staff().equals(",")){
						}else{
						List<String> sulist = Arrays.asList(list.get(i).getSupportInfoList().get(j).getSupport_staff().split(","));
						list.get(i).getSupportInfoList().get(j).setUserInfoList(userDao.getSupportList(sulist));
						}
					}
				}
				
				
		PageUtil<MalfunctionLogInfo> pageInfo=new PageUtil<MalfunctionLogInfo>();  //存到page里
		pageInfo.setData(list);   //把查询结果存到page里
		pageInfo.setPagesize(page.getPagesize());
		pageInfo.setRecordCount(Malfunction_LogDao.findPage(page).size());   //总记录数
		return pageInfo;
	}

	
	@Override
	public PageUtil<MalfunctionLogInfo> report(PageUtil<MalfunctionLogInfo> page) {
		List<MalfunctionLogInfo> list=Malfunction_LogDao.findPage(page);  //分页之后的集合
		//把所有支援人都存进去
		for(int i=0;i<list.size();i++){
			for(int j = 0; j<list.get(i).getSupportInfoList().size(); j++){
				if(list.get(i).getSupportInfoList().get(j).getSupport_staff().equals(",")){
				}else{
				List<String> sulist = Arrays.asList(list.get(i).getSupportInfoList().get(j).getSupport_staff().split(","));
				list.get(i).getSupportInfoList().get(j).setUserInfoList(userDao.getSupportList(sulist));
				}
			}
		}
		PageUtil<MalfunctionLogInfo> pageInfo=new PageUtil<MalfunctionLogInfo>();  //存到page里
		pageInfo.setData(list);   //把查询结果存到page里
		pageInfo.setRecordCount(Malfunction_LogDao.findPage(page).size());   //总记录数
		return pageInfo;
	}
	

}
