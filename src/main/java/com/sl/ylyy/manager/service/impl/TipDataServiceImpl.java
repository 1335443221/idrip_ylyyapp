package com.sl.ylyy.manager.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sl.ylyy.manager.entity.TipInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.sl.ylyy.manager.dao.SwitchingroomDao;
import com.sl.ylyy.manager.dao.TipDao;
import com.sl.ylyy.manager.service.TipDataService;
import com.sl.ylyy.common.utils.PageUtil;
import com.sl.ylyy.common.utils.QRCodeUtil;


@Service("tipDataImpl")
public class TipDataServiceImpl implements TipDataService {


	@Autowired
	private TipDao tipDao;
	
	@Autowired
	private SwitchingroomDao switchingroomDao;

	@Override
	public ArrayList<TipInfo> getAllTip(String Stg) {
		// TODO Auto-generated method stub
		return tipDao.getAllTip(Stg);
	}

	public void deleteTipById(Integer id) {
		// TODO Auto-generated method stub
		switchingroomDao.deleteSwitchingroomByTid(id);
		tipDao.deleteTipById(id);
		
		
	}

	/**
	 * 新增安全防护点
	 */
	@Override
	public int insertTip(String json) {
		Map<String,Object> map=JSONObject.parseObject(json); //解析数据
		
		Map<String, Object> tipmap =new HashMap<>();
		tipmap.put("location", map.get("location"));
		tipmap.put("equipment", map.get("equipment"));
		tipmap.put("source", map.get("source"));
		tipmap.put("result", map.get("result"));
		tipmap.put("lec", map.get("lec"));
		tipmap.put("tclass", map.get("tclass"));
		tipmap.put("measure", map.get("measure"));
		tipmap.put("staff", map.get("staff"));
		tipmap.put("userid", map.get("userid"));
		int result=tipDao.insertTip(tipmap);//新增tip
		int id=0;
		if(result>0){
			id=tipDao.getMaxIdTip();
			String qrcode=QRCodeUtil.cQrCode(String.valueOf(id));  //生成并上传二维码
			tipmap.put("qrcode", qrcode);
			tipmap.put("tipid", id);
			tipDao.updateTipById(tipmap);
			
			JSONArray fileArray=JSONObject.parseArray(String.valueOf(map.get("fileList")));
			for(int i=0;i<fileArray.size();i++){
				Map<String,Object> fileMap=fileArray.getJSONObject(i);
				Map<String, Object> room_map =new HashMap<>();
				room_map.put("url", fileMap.get("url"));
				room_map.put("sequence",fileMap.get("sequence"));
				room_map.put("title",fileMap.get("title"));
				room_map.put("tid", id);
			   switchingroomDao.insertSwitchingroom(room_map);
			}
		}
		
		return id;
	}

	
	
	
	
	
	@Override
	public int updateTipById(String json) {
		Map<String,Object> map=JSONObject.parseObject(json); //解析数据
		Map<String, Object> tipmap =new HashMap<>();
		tipmap.put("location", map.get("location"));
		tipmap.put("equipment", map.get("equipment"));
		tipmap.put("source", map.get("source"));
		tipmap.put("result", map.get("result"));
		tipmap.put("lec", map.get("lec"));
		tipmap.put("tclass", map.get("tclass"));
		tipmap.put("measure", map.get("measure"));
		tipmap.put("staff", map.get("staff"));
		tipmap.put("userid", map.get("userid"));
		tipmap.put("tipid", map.get("id"));
		int result=tipDao.updateTipById(tipmap);//新增tip
		if(result>0){
			JSONArray fileArray=JSONObject.parseArray(String.valueOf(map.get("fileList")));
			for(int i=0;i<fileArray.size();i++){
				Map<String,Object> fileMap=fileArray.getJSONObject(i);
				if(String.valueOf(fileMap.get("id")).equals("")&&String.valueOf(fileMap.get("url")).equals("")){  //证明没有
					continue;
				}else if(String.valueOf(fileMap.get("id")).equals("")&&!String.valueOf(fileMap.get("url")).equals("")){  //证明没有
					Map<String, Object> room_map =new HashMap<>();
					room_map.put("url", fileMap.get("url"));
					room_map.put("sequence",fileMap.get("sequence"));
					room_map.put("title",fileMap.get("title"));
					room_map.put("tid", map.get("id"));
					switchingroomDao.insertSwitchingroom(room_map);
				}else{
					Map<String, Object> room_map =new HashMap<>();
					room_map.put("url", fileMap.get("url"));
					room_map.put("sequence",fileMap.get("sequence"));
					room_map.put("title",fileMap.get("title"));
					room_map.put("tid", map.get("id"));
					room_map.put("id", fileMap.get("id"));
				    switchingroomDao.updateSwitchingroomById(room_map);
				}
			}
		}
		
		return result;
		
	}

	
	
	
	@Override
	public PageUtil<TipInfo> findPage(PageUtil<TipInfo> page) {
		PageHelper.startPage(page.getPageindex(), page.getPagesize());   //分页
		List<TipInfo> list=tipDao.findPage(page);  //分页之后的集合
		PageUtil<TipInfo> pageInfo=new PageUtil<TipInfo>();  //存到page里
		pageInfo.setData(list);   //把查询结果存到page里
		pageInfo.setPagesize(page.getPagesize());
		pageInfo.setRecordCount(tipDao.findPage(page).size());   //总记录数
		return pageInfo;
	}

	@Override
	public TipInfo getTipById(Integer id) {
		// TODO Auto-generated method stub
		return tipDao.getTipById(id);
	}

	@Override
	public int getMaxIdTip() {
		// TODO Auto-generated method stub
		return tipDao.getMaxIdTip();
	}
	

}
