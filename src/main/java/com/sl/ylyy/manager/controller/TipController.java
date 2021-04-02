package com.sl.ylyy.manager.controller;


import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.alibaba.fastjson.JSONObject;
import com.sl.ylyy.app_1.entity.Switchingroom;
import com.sl.ylyy.common.config.UrlConfig;
import com.sl.ylyy.manager.entity.TipInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sl.ylyy.manager.service.Operation_logDataService;
import com.sl.ylyy.manager.service.SwitchingroomDataService;
import com.sl.ylyy.manager.service.TipDataService;
import com.sl.ylyy.common.utils.PageUtil;
import com.sl.ylyy.common.utils.QiniuUpload;
import com.sl.ylyy.common.utils.DownQrCode;

@Controller
@RequestMapping("/gtgx")
public class TipController {
	@Autowired
	TipDataService tipDataImpl;
	@Autowired
	Operation_logDataService operation_logDataService;
	@Autowired
	SwitchingroomDataService SwitchingroomDataImpl;
	

	/**
	 * 获取所有的安全防护点
	 * @param stg
	 * @return
	 */
	@RequestMapping("/getAllTip")
	public String  getAllTip(Model model,int pageindex,String key){
		PageUtil<TipInfo> page=new PageUtil<TipInfo>();
		if(pageindex==0){
			pageindex=1;
		}
		page.setPageindex(pageindex); 
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("key", key);
		page.setParams(params);
		page.setPagesize(16);
		PageUtil<TipInfo> pageData=tipDataImpl.findPage(page);  //新的分页信息
		pageData.setPageindex(pageindex);  //返回当前页
		params.put("lastpage", pageData.getLastPage());  //上一页
		params.put("nextpage", pageData.getNextpage(pageData.getTotalPagecount())); //下一页
		pageData.setParams(params);  //返回模糊查询信息
		model.addAttribute("page",pageData); 
		return "ylyyPag/tipPag/showTip";
	}
	
	/**
	 * 删除安全防护点
	 * @param id
	 * @return
	 */
	@RequestMapping("/deleteTipById")
	public int deleteTipById(Integer id,HttpSession session){
		tipDataImpl.deleteTipById(id);
		
  	    Map<String,Object> omap= JSONObject.parseObject(String.valueOf(session.getAttribute("activeAdmin")));
		omap.put("operation", "删除安全防护点");
	    operation_logDataService.insertOperation_log(omap);
		return 1;
	}
	
	/**
	 * 去增加安全防护点
	 * @return
	 */
	@RequestMapping("/goAddTip")
	public String goAddTip(Model model){
		model.addAttribute("token",QiniuUpload.getUpToken2());
		model.addAttribute("prefix", UrlConfig.FILE_PREFIX);
		model.addAttribute("QiniuUrl", UrlConfig.QiniuUrl);
		return "ylyyPag/tipPag/addTip";
	}
	
	/**
	 * 获取一个安全防护点
	 * @param id
	 * @return
	 */
	@RequestMapping("/getTipById")
	public String getTipById(Integer id,Model model){
		List<Switchingroom> list=SwitchingroomDataImpl.getSwitchingroombyTip(id);
		
		for(int i=0;i<6;i++){
			if(list.size()<6){
				list.add(new Switchingroom());
			}
			
		}
		
		TipInfo tipInfo =tipDataImpl.getTipById(id);
		model.addAttribute("tip", tipInfo);
		model.addAttribute("Switchingroomlist", list);
		model.addAttribute("token",QiniuUpload.getUpToken2());
		model.addAttribute("prefix", UrlConfig.FILE_PREFIX);
		model.addAttribute("QiniuUrl", UrlConfig.QiniuUrl);
		return "ylyyPag/tipPag/updateTip";
	}
	
	
	/**
	 * 新增安全防护点
	 * @param
	 * @return
	 */
	@RequestMapping(value="/insertTip",method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public int insertTip(@RequestBody String json,HttpSession session) {
		int a=tipDataImpl.insertTip(json);
		if(a>0){
			Map<String,Object> omap= JSONObject.parseObject(String.valueOf(session.getAttribute("activeAdmin")));
			 omap.put("operation", "增加安全防护点");
		     operation_logDataService.insertOperation_log(omap);
		}
	    return a;
	}
	
	/**
	 * 修改安全防护点
	 * @param
	 * @return
	 */
	@RequestMapping(value="/updateTip",method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public int updateTip(@RequestBody String json,HttpSession session) {
		int result=tipDataImpl.updateTipById(json);
		if(result>0){
			Map<String,Object> omap= JSONObject.parseObject(String.valueOf(session.getAttribute("activeAdmin")));
			 omap.put("operation", "修改安全防护点信息");
		     operation_logDataService.insertOperation_log(omap);
		}
	    return result;
	}
	
	
	
	@RequestMapping("/downQrCode")
    public void downQrCode(String url,HttpServletRequest request,HttpServletResponse response){
		 String uuid = UUID.randomUUID().toString().replaceAll("-","");     //随机文件名   防止重名
         //得到 文件名  
		 String  filename=uuid+".jpg";   
		
        InputStream in = null;
        try{
            //1.设置文件ContentType类型，这样设置，会自动判断下载文件类型  
            response.setContentType("multipart/form-data");  
            //2.设置文件头：最后一个参数是设置下载文件名
            response.setHeader("Content-Disposition", "attachment;fileName="+filename); 

            byte[] buffer= DownQrCode.down(url);

            //3.通过response获取ServletOutputStream对象(out)  
           
            response.getOutputStream().write(buffer);//4.写到输出流(out)中   

        } catch (Exception e) {  
        }finally{
            try {
                if (in != null) {
                    in.close();
                }
                response.getOutputStream().flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

	
	/*--------------------------配电室---------------------------------------*/
	
	
	/**
	 * 获取所有的安全防护点
	 * @param stg
	 * @return
	 */
	@RequestMapping("/getAllSwitchingroom")
	public String  getAllSwitchingroom(Model model,int pageindex,String key){
		PageUtil<Switchingroom> page=new PageUtil<Switchingroom>();
		if(pageindex==0){
			pageindex=1;
		}
		page.setPageindex(pageindex); 
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("key", key);
		page.setParams(params);
		page.setPagesize(6);
		PageUtil<Switchingroom> pageData=SwitchingroomDataImpl.findPage(page);  //新的分页信息
		pageData.setPageindex(pageindex);  //返回当前页
		params.put("lastpage", pageData.getLastPage());  //上一页
		params.put("nextpage", pageData.getNextpage(pageData.getTotalPagecount())); //下一页
		pageData.setParams(params);  //返回模糊查询信息
		model.addAttribute("page",pageData); 
		return "ylyyPag/switchingroomPag/showSwitchingroom";
	}
	
	/**
	 * 删除安全防护点
	 * @param id
	 * @return
	 */
	@RequestMapping("/deleteSwitchingroomById")
	public int deleteSwitchingroomById(Integer id,HttpSession session){
		
		Switchingroom s=SwitchingroomDataImpl.getSwitchingroomById(id);
		int tid=s.getTid();
		SwitchingroomDataImpl.deleteSwitchingroomById(id);
		
  	  return tid;
	}
	
	/**
	 * 去增加安全防护点
	 * @return
	 */
	@RequestMapping("/goAddSwitchingroom")
	public String goAddSwitchingroom(String tid,Model model){
		model.addAttribute("tid", tid);
		return "ylyyPag/switchingroomPag/addOrUpdateSwitchingroom";
	}
	
	/**
	 * 获取一个安全防护点
	 * @param id
	 * @return
	 */
	@RequestMapping("/getSwitchingroomById")
	public String getSwitchingroomById(Integer id,Model model){
		Switchingroom Switchingroom=SwitchingroomDataImpl.getSwitchingroomById(id);
		model.addAttribute("Switchingroom", Switchingroom);
		return "ylyyPag/switchingroomPag/addOrUpdateSwitchingroom";
	}
	

	
	/**
	 * 去增加安全防护点
	 * @return
	 */
	@RequestMapping("/goQrCode")
	public String goQrCode(){
		return "ylyyPag/switchingroomPag/qrCode";
	}
	
	
	
	@RequestMapping("/downQrCodeSwitchingroom")
    public void downQrCodeSwitchingroom(String url,HttpServletRequest request,HttpServletResponse response){
		 String uuid = UUID.randomUUID().toString().replaceAll("-","");     //随机文件名   防止重名
         //得到 文件名  
		 String  filename=uuid+".jpg";   
        InputStream in = null;
        try{
            //1.设置文件ContentType类型，这样设置，会自动判断下载文件类型  
            response.setContentType("mulSwitchingroomart/form-data");  
            //2.设置文件头：最后一个参数是设置下载文件名
            response.setHeader("Content-Disposition", "attachment;fileName="+filename); 

            byte[] buffer= DownQrCode.down(url);

            //3.通过response获取ServletOutputStream对象(out)  
           
            response.getOutputStream().write(buffer);//4.写到输出流(out)中   

        } catch (Exception e) {  
        }finally{
            try {
                if (in != null) {
                    in.close();
                }
                response.getOutputStream().flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

	
	
	
}
	
