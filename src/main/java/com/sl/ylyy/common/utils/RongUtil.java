package com.sl.ylyy.common.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.*;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sl.ylyy.common.Interceptor.MyException;
import com.sl.ylyy.common.config.UrlConfig;
import com.sl.ylyy.manager.entity.GroupNotificationMessage;
import com.sl.ylyy.manager.entity.SystemNoticeNoDisplayMessage;
import io.rong.RongCloud;
import io.rong.messages.TxtMessage;
import io.rong.methods.user.User;
import io.rong.models.Result;
import io.rong.models.group.GroupMember;
import io.rong.models.group.GroupModel;
import io.rong.models.message.GroupMessage;
import io.rong.models.response.BlockUserResult;
import io.rong.models.response.CheckOnlineResult;
import io.rong.models.response.ResponseResult;
import io.rong.models.response.TokenResult;
import io.rong.models.user.UserModel;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.stereotype.Service;


@Service
public class RongUtil {
	public static RongCloud rongCloud = RongCloud.getInstance(UrlConfig.RongAppKey, UrlConfig.RongAppSecret);
	private static final TxtMessage txtMessage = new TxtMessage("hello", "helloExtra");


		//返回信息 200返回Map 其他抛出错误
	 	public static Map<String, Object> is_code(String result){
			Map<String, Object> resultMap=JSONObject.parseObject(String.valueOf(result));
			if(String.valueOf(resultMap.get("code")).equals("200")){
				//数据正常
				return resultMap;
			}else{
				throw new MyException(Integer.parseInt(String.valueOf(resultMap.get("code"))),"融云服务器异常:"+String.valueOf(resultMap.get("errorMessage")),0);
			}
		}

//=================================================user 用户=======================================================//
	    /**
		 * 注册用户
		 */
		public static Map<String, Object> userRegister(Map<String, Object> map) {
			Map<String, Object> result=new HashMap<>();
			result.put("token","");
			result.put("token","");
			return  result;

			/*User user = rongCloud.user;
			  //注册用户，生成用户在融云的唯一身份标识 Token
			UserModel userModel = new UserModel()
			            .setId(map.get("rong_user_id").toString())
			            .setName(map.get("user_name").toString())
			            .setPortrait(map.get("portrait").toString());
			  TokenResult result = null;
			try {
				result = user.register(userModel);
			} catch (Exception e) {
				throw new MyException(2000,"融云服务器异常",0);
			}
			return is_code(String.valueOf(result));*/
		}


		/**
		 * 修改用户信息
		 */
		public static Map<String, Object> userUpdate(Map<String, Object> map) {

			return  new HashMap<>();
			/*UserModel user = new UserModel()
					.setId(map.get("rong_user_id").toString())
		            .setName(map.get("user_name").toString())
		            .setPortrait(map.get("portrait").toString());
			Result result = null;
			try {
				result = (ResponseResult)rongCloud.user.update(user);
			} catch (Exception e) {
				throw new MyException(2000,"融云服务器异常",0);
			}
			Map<String, Object> resultMap=JSONObject.parseObject(String.valueOf(result));
			return resultMap;*/
		}


		/**
		 * 检查用户在线状态 方法
		 */
		public static String userCheckOnline(Map<String, Object> map) {
			UserModel user = new UserModel();
			user.setId(map.get("id").toString());

			CheckOnlineResult result = null;
			try {
				result = rongCloud.user.onlineStatus.check(user);
			} catch (Exception e) {
				throw new MyException(2000,"融云服务器异常",0);
			}
			return result.toString();
		}


		/**
		 * 封禁用户（每秒钟限 100 次）
		 */
		public static String userAddBlock(Map<String, Object> map) {
			UserModel user = new UserModel()
					.setId(map.get("id").toString())   //封禁用户id
					.setMinute(1000);   //封禁时间
			Result result = null;
			try {
				result = (ResponseResult)rongCloud.user.block.add(user);
			} catch (Exception e) {
				throw new MyException(2000,"融云服务器异常",0);
			}
			return result.toString();
		}


		/**
		 * 解除用户封禁（每秒钟限 100 次）
		 */
		public static String userUnBlock(Map<String, Object> map) {
			ResponseResult result = null;
			try {
				result = (ResponseResult)rongCloud.user.block.remove(map.get("id").toString());  //被封禁的用户id
			} catch (Exception e) {
				throw new MyException(2000,"融云服务器异常",0);
			}
			return result.toString();
		}


		/**
		 *  获取被封禁用户（每秒钟限 100 次）
		 */
		public static String userQueryBlock(Map<String, Object> map) {
			BlockUserResult result = null;
			try {
				result = (BlockUserResult)rongCloud.user.block.getList();
			} catch (Exception e) {
				throw new MyException(2000,"融云服务器异常",0);
			}
			return result.toString();
		}

//=================================================group 群组=======================================================//

	/**
	 * 创建群组方法（创建群组，并将用户加入该群组，用户将可以收到该群的消息，同一用户最多可加入 500 个群，
	 * 每个群最大至 3000 人，App 内的群组数量没有限制.注：其实本方法是加入群组方法 /group/join 的别名。）
	 */
	public static Map<String, Object>  groupCreate(Map<String, Object> map){
		return new HashMap<>();
		/*
		List<Map<String,Object>> userList=(List<Map<String,Object>>)map.get("userList");
		GroupMember[] members = new GroupMember[userList.size()];
		for(int i=0;i<userList.size();i++){
			members[i]=new GroupMember().setId(String.valueOf(userList.get(i).get("rong_user_id")));
		}
		GroupModel group = new GroupModel()
				.setId(String.valueOf(map.get("rong_group_id")))
				.setMembers(members)
				.setName(String.valueOf(map.get("group_name")));
		Result result = null;
		try {
			result = (Result)rongCloud.group.create(group);
		} catch (Exception e) {
			throw new MyException(2000,"融云服务器异常",0);
		}
		return is_code(String.valueOf(result));*/
	}


	/**
	 *  修改群组信息方法
	 */
	public static Map<String, Object> groupUpdate(Map<String, Object> map){
		return new HashMap<>();
		/*
		GroupModel group = new GroupModel()
				.setId(String.valueOf(map.get("rong_group_id")))
				.setMembers(null)
				.setName(String.valueOf(map.get("group_name")));
		Result result = null;
		try {
			result = (Result)rongCloud.group.update(group);
		} catch (Exception e) {
			throw new MyException(2000,"融云服务器异常",0);
		}
		return is_code(String.valueOf(result));*/
	}


	/**
	 * 将用户加入指定群组，用户将可以收到该群的消息，同一用户最多可加入 500 个群，每个群最大至 3000 人。
	 */
	public static Map<String, Object> groupJoin(Map<String, Object> map){
		return new HashMap<>();
		/*
		List<Map<String,Object>> userList=(List<Map<String,Object>>)map.get("userList");
		GroupMember[] members = new GroupMember[userList.size()];
		for(int i=0;i<userList.size();i++){
			members[i]=new GroupMember().setId(String.valueOf(userList.get(i).get("rong_user_id")));
		}
		GroupModel group = new GroupModel()
				.setId(String.valueOf(map.get("rong_group_id")))
				.setMembers(members)
				.setName(String.valueOf(map.get("group_name")));
		Result result = null;
		try {
			result = (Result)rongCloud.group.join(group);
		} catch (Exception e) {
			throw new MyException(2000,"融云服务器异常",0);
		}
		return is_code(String.valueOf(result));*/

	}

	/**
	 *  退出群组方法（将用户从群中移除，不再接收该群组的消息.）
	 */
	public static Map<String, Object> groupQuit(Map<String,Object> map){
		return new HashMap<>();
		/*
		List<Map<String,Object>> userList=(List<Map<String,Object>>)map.get("userList");
		GroupMember[] members = new GroupMember[userList.size()];
		for(int i=0;i<userList.size();i++){
			members[i]=new GroupMember().setId(String.valueOf(userList.get(i).get("rong_user_id")));
		}
		GroupModel group = new GroupModel()
				.setId(String.valueOf(map.get("rong_group_id")))
				.setMembers(members)
				.setName(null);
		Result result = null;
		try {
			result =(Result)rongCloud.group.quit(group);
		} catch (Exception e) {
			throw new MyException(2000,"融云服务器异常",0);
		}
		return is_code(String.valueOf(result));*/

	}

	/**
	 *  解散群组方法
	 */
	public static Map<String, Object> groupDismiss(Map<String, Object> map){

		return new HashMap<>();
		/*
		GroupMember[] members = new GroupMember[]{new GroupMember().setId(String.valueOf(map.get("dismiss_by")))};
		GroupModel group = new GroupModel()
				.setId(String.valueOf(map.get("rong_group_id")))
				.setMembers(members);

		Result result = null;
		try {
			result = (Result)rongCloud.group.dismiss(group);
		} catch (Exception e) {
			throw new MyException(2000,"融云服务器异常",0);
		}
		return is_code(String.valueOf(result));*/
	}
	//=================================================发送消息 ======================================================//

	/**
	 * 群组发送定向消息 群通知等等
	 * @throws Exception
	 */
	public static Map<String, Object> sendGroupDirection(Map<String, Object> map){
		String[] toUserIds= {String.valueOf(map.get("rong_user_id"))};
		String[] targetIds= {String.valueOf(map.get("rong_group_id"))};
		GroupMessage groupMessage = new GroupMessage()
				.setSenderId(UrlConfig.DEFAULT_RONG_USER_Id)
				.setTargetId(targetIds)
				.setToUserId(toUserIds)
				.setObjectName(SystemNoticeNoDisplayMessage.TYPE)
				.setContent(new SystemNoticeNoDisplayMessage())
				.setPushContent(null)
				.setPushData(null)
				.setIsPersisted(1)
				.setIsIncludeSender(0)
				.setContentAvailable(0);
		ResponseResult result = null;
		try {
			result = rongCloud.message.group.sendDirection(groupMessage);
		} catch (Exception e) {
			throw new MyException(CodeMsg.RONG_ERRAY,0);
		}
		return is_code(String.valueOf(result));
	}


	/**
	 * 群组发送消息 退出群组等等
	 * @throws Exception
	 */
	public static Map<String, Object> sendGroup(String rong_group_id, GroupNotificationMessage groupNoticeNoDisplayMessage,String jsonMessage){
		//群组消息
		String[] targetIds= {rong_group_id};
		GroupMessage groupMessage = new GroupMessage()
				.setSenderId(UrlConfig.DEFAULT_RONG_USER_Id)
				.setTargetId(targetIds)
				.setObjectName(GroupNotificationMessage.TYPE)
				.setContent(groupNoticeNoDisplayMessage)
				.setPushContent(jsonMessage)
				.setPushData(jsonMessage)
				.setIsPersisted(0)
				.setIsIncludeSender(1)
				.setContentAvailable(0);
		ResponseResult result = null;
		try {
			result = rongCloud.message.group.send(groupMessage);
		} catch (Exception e) {
			throw new MyException(CodeMsg.RONG_ERRAY,0);
		}
		return is_code(String.valueOf(result));
	}





    public static Map<String, Object>  groupPublish(String rong_group_id,String jsonMessage) {

		return new HashMap<>();
		/*
        Map<String, Object> map = new HashMap<>();
        String Timestamp = String.valueOf(System.currentTimeMillis() / 1000);
        String Nonce = String.valueOf(Math.floor(Math.random() * 1000000));
        //组内成员退出的url
        String url = "http://api-cn.ronghub.com/message/group/publish.json";
        String Signature = sha1(UrlConfig.RongAppSecret + Nonce + Timestamp);//数据签名。
        //Logger.i(Signature);
        //HttpClient httpClient = new DefaultHttpClient();
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader("App-Key", UrlConfig.RongAppKey);
        httpPost.setHeader("Timestamp", Timestamp);
        httpPost.setHeader("Nonce", Nonce);
        httpPost.setHeader("Signature", Signature);
        httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
        List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(1);
        nameValuePair.add(new BasicNameValuePair("fromUserId", UrlConfig.DEFAULT_RONG_USER_Id));//发送人员工 Id
        nameValuePair.add(new BasicNameValuePair("toGroupId", rong_group_id));//接收群 Id
        nameValuePair.add(new BasicNameValuePair("objectName", "RC:GrpNtf"));//消息类型 本类型为群组系统消息
        nameValuePair.add(new BasicNameValuePair("content", jsonMessage));  // 发送消息内容
        nameValuePair.add(new BasicNameValuePair("isIncludeSender", "1"));  // 发送消息内容
        HttpResponse httpResponse = null;
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair, "utf-8"));
            httpResponse = httpClient.execute(httpPost);
            BufferedReader br1 = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()));
            String line = null;
            String rongyunCode = null;
            String msg = null;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;*/
    }

     public static String sha1(String data){
        StringBuffer buf = new StringBuffer();
        try {
            MessageDigest md = MessageDigest.getInstance("SHA1");
            md.update(data.getBytes());
            byte[] bits = md.digest();
            for (int i = 0; i < bits.length; i++) {
                int a = bits[i];
                if (a < 0) a += 256;
                if (a < 16) buf.append("0");
                buf.append(Integer.toHexString(a));
            }
        } catch (Exception e) {

        }
        return buf.toString();
    }



	//=================================================main 测试======================================================//

		public static void main(String[] args) {
		//user测试
			/*Map<String, Object> map=new HashMap<>();
			map.put("id",100);
			map.put("user_name", "2");
			map.put("portrait", "http://123.57.209.56:21112/work/Tomcat/localhost/ROOT/Portrait/16434f9b009346d6899c0e9bc3880982.png");
			System.out.println(RongUtil.userRegister(map));
			System.out.println(RongUtil.userUpdate(map));*/
		//group 测试
			/*List<Map<String,Object>> userList=new ArrayList<>();
			Map<String,Object> userMap=new HashMap<String, Object>();
			userMap.put("user_id","1");
			userList.add(userMap);
			userMap=new HashMap<String, Object>();
			userMap.put("user_id","2");
			/*userList.add(userMap);
			userMap=new HashMap<String, Object>();
			userMap.put("user_id","5");*/
			//System.out.println(RongUtil.groupCreate(groupMap));
			//System.out.println(RongUtil.groupUpdate(groupMap));

			//System.out.println(RongUtil.groupJoin(groupMap));
			//	groupMap.put("userId",1);
			//System.out.println(RongUtil.groupDismiss(groupMap));

			//接受
			/*String input = "2019-09-19";

			Date date = new Date();
			SimpleDateFormat dateFm = new SimpleDateFormat("EEEE");
			String currSun = dateFm.format(date);
			System.out.println(currSun);*/

			Map<String, Object> map=new HashMap<>();
			map.put("rong_user_id","rong_dev_98");
			map.put("rong_group_id","rong_dev_19");
			System.out.println(sendGroupDirection(map));
		}



	/**
	 * 判断当前日期是星期几
	 *
	 * @param date 修要判断的时间
	 * @return dayForWeek 判断结果
	 * @Exception 发生异常
	 */
	public static int dayForWeek(String date){
		SimpleDateFormat sDateFormat=new SimpleDateFormat("yyyy-MM-dd");
		Date date2=null;
		try {
			date2=sDateFormat.parse(date);
		} catch(Exception px) {
			px.printStackTrace();
		}
		if (date2==null){
			return 0;
		}
		int[] weekDays = {7,1, 2, 3, 4, 5, 6};
		Calendar cal = Calendar.getInstance();
		cal.setTime(date2);
		int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
		if (w < 0){
			w = 0;
		}
		return weekDays[w];
	}




	public static Map<String,Object> HuangLi(String date){
		SimpleDateFormat sDateFormat=new SimpleDateFormat("yyyy-M-d");
		Date date2=null;
		String da=date;
		try {
			date2=sDateFormat.parse(date);
			da=sDateFormat.format(date2);
		} catch(Exception px) {
			px.printStackTrace();
		}


		Map<String,String> map=new HashMap<>();
		Map<String,Object> result=new HashMap<>();
		map.put("query",date);
		map.put("resource_id","6018");
		map.put("format","json");

		HttpClientService hc=new HttpClientService();
		String json=hc.post(UrlConfig.HuangLiUrl,map);
		Map<String,Object> jsonMap=JSONObject.parseObject(json);
		if (String.valueOf(jsonMap.get("status")).equals("0")){
			JSONArray jsonDataList=JSONObject.parseArray(String.valueOf(jsonMap.get("data")));
			Map<String,Object> jsonData=JSONObject.parseObject(String.valueOf(jsonDataList.get(0)));
			JSONArray list=JSONObject.parseArray(String.valueOf(jsonData.get("almanac")));
			for (int i=0;i<list.size();i++){
				Map<String,Object> j=JSONObject.parseObject(String.valueOf(list.get(i)));
				if (j.get("date").equals(da)){
					result.put("huangli_ji",j.get("avoid"));
					result.put("huangli_yi",j.get("suit"));
				}
			}
			return result;
		}else{
			throw new MyException(CodeMsg.HUANGLI_ERRAY,0);
		}

	}

	//http://t.weather.sojson.com/api/weather/city/101010100";  //北京天气
	/*public static Map<String,Object> Weather(String date){
		Map<String,Object> map=new HashMap<>();
		HttpClientService hc=new HttpClientService();
		String weatherData=hc.get(UrlConfig.WeatherUrl,new HashMap<>());
		Map<String,Object> jsonMap=JSONObject.parseObject(weatherData);

		if (Integer.parseInt(String.valueOf(jsonMap.get("status")))==200){
			Map<String,Object> jsonData=JSONObject.parseObject(String.valueOf(jsonMap.get("data")));
			JSONArray list=JSONObject.parseArray(String.valueOf(jsonData.get("forecast")));
			for (int i=0;i<list.size();i++){
				Map<String,Object> j=JSONObject.parseObject(String.valueOf(list.get(i)));
				if (j.get("ymd").equals(date)){
					String high =String.valueOf(j.get("high"));
					String low =String.valueOf(j.get("low"));
					String high2 = high.substring(high.indexOf(" "));
					String low2 = low.substring(low.indexOf(" "));
					map.put("weather_type",j.get("type"));
					map.put("weather_temperature",low2+"-"+high2);
					map.put("weather_wind",String.valueOf(j.get("fx"))+String.valueOf(j.get("fl")));
				}
			}
			return map;
		}else{
			throw new MyException(CodeMsg.WEATHER_ERRAY,0);
		}

	}*/
	public static Map<String,Object> Weather(String date){
		Map<String,Object> map=new HashMap<>();
		HttpClientService hc=new HttpClientService();
		Map<String,Object> params=new HashMap<>();
		params.put("version","v9");
		params.put("appid","23035354");
		params.put("appsecret","8YvlPNrz");
		String weatherData=hc.get(UrlConfig.WeatherUrl,params);
		Map<String,Object> jsonMap=JSONObject.parseObject(weatherData);

		JSONArray list=JSONObject.parseArray(String.valueOf(jsonMap.get("data")));
		for (int i=0;i<list.size();i++){
				Map<String,Object> j=JSONObject.parseObject(String.valueOf(list.get(i)));
				if (j.get("date").equals(date)){
					String high =j.get("tem1")+"℃";
					String low =j.get("tem2")+"℃";
					map.put("weather_type",j.get("wea"));
					map.put("weather_temperature",low+"-"+high);

					JSONArray winList=JSONObject.parseArray(String.valueOf(j.get("win")));
					map.put("weather_wind",String.valueOf(winList.get(0))+String.valueOf(j.get("win_speed")));
				}
		}
			return map;

		}


}
