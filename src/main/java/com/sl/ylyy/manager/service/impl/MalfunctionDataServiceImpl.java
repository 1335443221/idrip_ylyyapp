package com.sl.ylyy.manager.service.impl;

import static com.sl.ylyy.common.config.UrlConfig.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import com.alibaba.fastjson.JSONObject;
import com.sl.ylyy.manager.entity.Audio_Desc;
import com.sl.ylyy.manager.entity.Image_Desc;
import com.sl.ylyy.manager.entity.MalfunctionInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.sl.ylyy.manager.dao.MalfunctionDao;
import com.sl.ylyy.manager.dao.SystemDao;
import com.sl.ylyy.manager.dao.UserDao;
import com.sl.ylyy.manager.service.MalfunctionDataService;
import com.sl.ylyy.common.config.UrlConfig;
import com.sl.ylyy.common.utils.PageUtil;


@Service("MalfunctionDataImpl")
public class MalfunctionDataServiceImpl implements MalfunctionDataService {


	@Autowired
	private MalfunctionDao MalfunctionDao;
	@Autowired
	private UserDao userDao;
	@Autowired
	private SystemDao systemDao;

	@Override
	public ArrayList<MalfunctionInfo> getAllMalfunction(String rname) {
		// TODO Auto-generated method stub
		return MalfunctionDao.getAllMalfunction(rname);
	}

	public void deleteMalfunctionById(Integer id) {
		MalfunctionDao.deleteMalfunctionById(id);
	}

	@Override
	public void insertMalfunction(MalfunctionInfo MalfunctionInfo) {
		// TODO Auto-generated method stub
		MalfunctionDao.insertMalfunction(MalfunctionInfo);
		
	}

	@Override
	public void updateMalfunctionById(MalfunctionInfo MalfunctionInfo) {
		// TODO Auto-generated method stub
		MalfunctionDao.updateMalfunctionById(MalfunctionInfo);
		
	}

	
	@Override
	public PageUtil<MalfunctionInfo> findPage(PageUtil<MalfunctionInfo> page) {
		PageHelper.startPage(page.getPageindex(),page.getPagesize());    //分页
		List<MalfunctionInfo> list=MalfunctionDao.findPage(page);  //分页之后的集合
		
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
		
		PageUtil<MalfunctionInfo> pageInfo=new PageUtil<MalfunctionInfo>();  //存到page里
		pageInfo.setData(list);   //把查询结果存到page里
		pageInfo.setPagesize(page.getPagesize());
		pageInfo.setRecordCount(MalfunctionDao.findPage(page).size());   //总记录数
		return pageInfo;
	}

	@Override
	public MalfunctionInfo getMalfunctionDetail(Integer id) {
		// TODO Auto-generated method stub
		MalfunctionInfo m=MalfunctionDao.getMalfunctionDetail(id);
		List<String> list1=new ArrayList<>();
		List<String> list2=new ArrayList<>();
		List<String> list3=new ArrayList<>();
		if(m.getAudio_desc()!=null){
			list1 = Arrays.asList(m.getAudio_desc().split(","));
			List<Audio_Desc> audio_DescList = MalfunctionDao.getAudio_DescById(list1);
			for(int i=0;i<audio_DescList.size();i++){
				if(audio_DescList.get(i).getContent().contains(QiniuUrl)||audio_DescList.get(i).getContent().contains(DOWNURL)){
					audio_DescList.get(i).setContent(audio_DescList.get(i).getContent());
				}else{
					audio_DescList.get(i).setContent(UrlConfig.DOWNURL+audio_DescList.get(i).getContent());
				}
			}
			m.setAudioList(audio_DescList);
		}
		if(m.getImage_desc()!=null){
			list2 = Arrays.asList(m.getImage_desc().split(","));
			List<Image_Desc> Image_DescList = MalfunctionDao.getImage_DescById(list2);
			for(int i=0;i<Image_DescList.size();i++){
				if(Image_DescList.get(i).getContent().contains(QiniuUrl)||Image_DescList.get(i).getContent().contains(DOWNURL)){
					Image_DescList.get(i).setContent(Image_DescList.get(i).getContent());
				}else{
					Image_DescList.get(i).setContent(UrlConfig.DOWNURL+Image_DescList.get(i).getContent());
				}
			}
			m.setImageList(Image_DescList);
		}
		if(m.getText_desc()!=null){
			list3 = Arrays.asList(m.getText_desc().split(","));
			m.setTextList(MalfunctionDao.getText_DescById(list3));
		}
		
		return m;
	}
	
	/**
	 * 故障上报列表
	 */
	@Override
	public PageUtil<Map<String, Object>> getAllMalfunctionReport(Map<String, Object> map,HttpSession session){
		
		PageUtil<Map<String, Object>> pageInfo=new PageUtil<Map<String, Object>>();
		int pageindex =Integer.parseInt(String.valueOf(map.get("pageindex")));  //页码
		String reportTime="";
		if(map.get("reportTime")!=null&&!String.valueOf(map.get("reportTime")).equals("")){   //上报时间
			reportTime=String.valueOf(map.get("reportTime"));
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
			 Date date = null;
			try {
				date = simpleDateFormat.parse(String.valueOf(map.get("reportTime")));
			} catch (ParseException e) {
				e.printStackTrace();
			}
			if(date!=null){
				map.put("reportTime", Math.round(date.getTime()/1000));  //上报时间
				map.put("endTime", Math.round(date.getTime()/1000)+84600);  //上报时间
			}
		}
		
		PageHelper.startPage(pageindex,pageInfo.getPagesize());  //分页
		List<Map<String,Object>> list=MalfunctionDao.getAllMalfunctionReport(map);  //分页后的数据
		for(int i=0;i<list.size();i++){
			 SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");   //把时间转换成年月日
			 long a =Integer.parseInt(String.valueOf(list.get(i).get("create_at")));
			 String create_at=sdf.format(a*1000);
			 list.get(i).put("create_at",create_at);
			 list.get(i).put("textList",  MalfunctionDao.getText_DescById(Arrays.asList(String.valueOf(list.get(i).get("text_desc")).split(","))));
		}

		Map<String,Object> adminMap= JSONObject.parseObject(String.valueOf(session.getAttribute("activeAdmin")));
		Map<String,Object> dMap=new HashMap<>();
		dMap.put("company_id", adminMap.get("company_id"));
		map.put("reportTime", reportTime);
		map.put("departmentList", systemDao.getAllDepartment(dMap)); //工程列表
		map.put("typeList", MalfunctionDao.getAllMalfunctionType(dMap));//分类列表
		pageInfo.setData(list);  //把数据存到page中
		pageInfo.setRecordCount(MalfunctionDao.getAllMalfunctionReport(map).size());  //总记录数
		pageInfo.setPageindex(pageindex);  //返回当前页
		map.put("lastpage", pageInfo.getLastPage());  //上一页
		map.put("nextpage", pageInfo.getNextpage(pageInfo.getTotalPagecount()));  //下一页
		pageInfo.setParams(map);  //返回模糊查询信息
		return pageInfo;
	}

	@Override
	public List<Map<String,Object>> getAllMalfunctionType(Map<String, Object> map){
		return MalfunctionDao.getAllMalfunctionType(map);
	}
	
	@Override
	public int insertMalfunctionReport(Map<String, Object> map) {
		
		map.put("create_at", Math.round(new Date().getTime()/1000));  //上报时间
		map.put("source",2);  //上报方式
		if(map.get("text_desc")!=null&&map.get("text_desc")!=""){
			Map<String,Object> textMap=new HashMap<>();
			textMap.put("content",map.get("text_desc"));
			textMap.put("create_at",map.get("create_at"));
			textMap.put("create_by",map.get("create_by"));
			MalfunctionDao.insertTextDesc(textMap);
			map.put("text_desc", textMap.get("text_id"));
		}else{
			map.put("text_desc", null);
		}
		if(map.get("image_desc")!=null&&map.get("image_desc")!=""){
			List<String> list=Arrays.asList(map.get("image_desc").toString().split(","));
			String image_id="";
			for (int i=0;i<list.size();i++){
				Map<String,Object> textMap=new HashMap<>();
				textMap.put("content",map.get("image_desc"));
				textMap.put("create_at",map.get("create_at"));
				textMap.put("create_by",map.get("create_by"));
				MalfunctionDao.insertImageDesc(textMap);
				if (image_id.equals("")){
					image_id=textMap.get("image_id").toString();
				}else{
					image_id=image_id+","+textMap.get("image_id");
				}
			}
			map.put("image_desc",image_id);
		}else{
			map.put("image_desc", null);
		}

		return MalfunctionDao.insertMalfunctionReport(map);
	}
}
