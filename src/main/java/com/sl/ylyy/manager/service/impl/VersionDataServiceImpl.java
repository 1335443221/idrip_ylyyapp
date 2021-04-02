package com.sl.ylyy.manager.service.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sl.ylyy.manager.entity.VersionInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.github.pagehelper.PageHelper;
import com.sl.ylyy.manager.dao.VersionDao;
import com.sl.ylyy.manager.service.VersionDataService;
import com.sl.ylyy.common.config.UrlConfig;
import com.sl.ylyy.common.utils.PageUtil;
import com.sl.ylyy.common.utils.QRCodeUtil;
import com.sl.ylyy.common.utils.Result;
import com.sl.ylyy.manager.dao.Operation_logDao;


@Service("VersionDataImpl")
public class VersionDataServiceImpl implements VersionDataService {


	@Autowired
	private VersionDao VersionDao;
	
	@Autowired
	private Operation_logDao Operation_logDao;
	
	
	@Override
	public VersionInfo getMaxIdVersion() {
		// TODO Auto-generated method stub
		return VersionDao.getMaxIdVersion();
	}
	
	
	
	
	

	@Override
	public ArrayList<VersionInfo> getAllVersion(String gname) {
		// TODO Auto-generated method stub
		return VersionDao.getAllVersion(gname);
	}

	/**
	 * 删除版本
	 */
	public void deleteVersionById(Integer id) {
		VersionInfo versionInfo = VersionDao.getMaxIdVersion();  //获取最新版本
		VersionDao.deleteVersionById(id);
		if(versionInfo.getId()==id){   //如果删除的是最新版本
			VersionInfo versionInfo2 = VersionDao.getMaxIdVersion();  //当前最新版本
			String url=versionInfo2.getDownload_url();   //下载地址
			
			try {
				FileOutputStream testfile = new FileOutputStream(UrlConfig.INDEX_URL);
				testfile.write(new String("").getBytes());
				File file1 =new File(UrlConfig.INDEX_URL);
				Writer out =new FileWriter(file1);
				String data="<head><meta http-equiv='refresh' content='0; url="+url+"'>" +
						"<meta http-equiv=\"Expires\" content=\"0\">\n" +
						"<meta http-equiv=\"Pragma\" content=\"no-cache\">\n" +
						"<meta http-equiv=\"Cache-control\" content=\"no-cache\">\n" +
						"<meta http-equiv=\"Cache\" content=\"no-cache\">\n" +
						"</head>";
				out.write(data);
				testfile.close();
				out.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}

	/**
	 * 上传版本
	 */
	@Override
	public Object insertVersion(VersionInfo versionInfo) {
		
		try {
			FileOutputStream testfile = new FileOutputStream(UrlConfig.INDEX_URL);
			testfile.write(new String("").getBytes());
			File file1 =new File(UrlConfig.INDEX_URL);
			Writer out =new FileWriter(file1);
			String data="<head><meta http-equiv='refresh' content='0; url="+versionInfo.getDownload_url()+"'>" +
					"<meta http-equiv=\"Expires\" content=\"0\">\n" +
					"<meta http-equiv=\"Pragma\" content=\"no-cache\">\n" +
					"<meta http-equiv=\"Cache-control\" content=\"no-cache\">\n" +
					"<meta http-equiv=\"Cache\" content=\"no-cache\">" +
					"</head>";
			out.write(data);
			testfile.close();
			out.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		int i=VersionDao.insertVersion(versionInfo);
		
		return i;
	}

	/**
	 * 修改app版本
	 */
	@Override
	public Object updateVersionById(VersionInfo VersionInfo) {
		if(VersionInfo.getDownload_url()!=null&&VersionInfo.getDownload_url()!=""){
			try {
				FileOutputStream testfile = new FileOutputStream(UrlConfig.INDEX_URL);
				testfile.write(new String("").getBytes());
				File file1 =new File(UrlConfig.INDEX_URL);
				Writer out =new FileWriter(file1);
				String data="<head><meta http-equiv='refresh' content='0; url="+VersionInfo.getDownload_url()+"'>" +
						"<meta http-equiv=\"Expires\" content=\"0\">\n" +
						"<meta http-equiv=\"Pragma\" content=\"no-cache\">\n" +
						"<meta http-equiv=\"Cache-control\" content=\"no-cache\">\n" +
						"<meta http-equiv=\"Cache\" content=\"no-cache\">" +
						"</head>";
				out.write(data);
				testfile.close();
				out.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
			VersionInfo.setDownload_url(null);
		}
		
		return VersionDao.updateVersionById(VersionInfo);
		
	}
	
	@Override
	public PageUtil<VersionInfo> findPage(PageUtil<VersionInfo> page) {
		PageHelper.startPage(page.getPageindex(), page.getPagesize());   //分页
		List<VersionInfo> list=VersionDao.findPage(page);  //分页之后的集合
		PageUtil<VersionInfo> pageInfo=new PageUtil<VersionInfo>();  //存到page里
		pageInfo.setData(list);   //把查询结果存到page里
		pageInfo.setPagesize(page.getPagesize());
		pageInfo.setRecordCount(VersionDao.findPage(page).size());   //总记录数
		return pageInfo;
	}
	

	@Override
	public VersionInfo getVersionById(Integer id) {
		// TODO Auto-generated method stub
		return VersionDao.getVersionById(id);
	}






	@Override
	public ArrayList<VersionInfo> checkVersion(Map<String, Object> map) {
		// TODO Auto-generated method stub
		return VersionDao.checkVersion(map);
	}




	/**
	 * 获取当前下载二维码
	 */
	@Override
	public Map<String, Object> getMaxIdQrCode() {
		return VersionDao.getMaxIdQrCode();
	}


	/**
	 * 上传二维码url
	 */
	@Override
	public int insertQrCode(Map<String, Object> map) {
		String a=QRCodeUtil.cQrCode(map.get("url").toString());  //生成并上传二维码
		map.put("img_url", a);
		int b=VersionDao.insertQrCode(map);
		if(b>0){
		return 1;
		}else{
		return 0;
		}
		
	}

	
	/**
	 * 获取二维码url
	 */
	@Override
	public Object getQrCodeUrl(Map<String, Object> map) {
		Map<String, Object> map2=new HashMap<>();
		Map<String, Object> map3=VersionDao.getMaxIdQrCode();
		if(map3!=null&&map3.get("img_url")!=null){
			map2.put("img_url", map3.get("img_url"));
		}
		return Result.success(map2);
	}

	
	

}
