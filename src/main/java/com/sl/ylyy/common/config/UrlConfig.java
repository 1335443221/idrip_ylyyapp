package com.sl.ylyy.common.config;

import org.springframework.stereotype.Service;

@Service
public class UrlConfig {
//	public static final String PROFILES_ACTIVE="dev";    //配置当前状态  dev测试  sale推广  prod生产
//	public static final String PROFILES_ACTIVE="sale";    //配置当前状态  dev测试  sale推广  prod生产
	public static final String PROFILES_ACTIVE="prod";    //配置当前状态  dev测试  sale推广  prod生产

	//融云
	public static final String Dev_RongAppKey = "k51hidwqk4nsb";
	public static final String Prod_RongAppKey = "tdrvipkstyq45";
	public static  String RongAppKey = "";

	public static final String Dev_RongAppSecret = "u7Ud2n4c9jsm";
	public static final String Prod_RongAppSecret = "oKvlHrYII8pky";
	public static  String RongAppSecret = "";

	//默认信息
	public static final String GroupPortrait = "http://123.57.209.56:21112/Portrait/groupPortrait.png";  //默认群头像
	public static final String UserPortrait = "http://123.57.209.56:21112/Portrait/userPortrait.png";  //默认用户头像
	public static final String DEFAULT_RONG_USER_Id = "rong_prod_27";  //默认用户的融云id 用来发送默认信息

	public static final String HuangLiUrl = "https://sp0.baidu.com/8aQDcjqpAAV3otqbppnN2DJv/api.php";  //黄历
//	public static final String WeatherUrl = "http://t.weather.sojson.com/api/weather/city/101010100";  //北京天气
	public static final String WeatherUrl = "http://www.tianqiapi.com/api";  //北京天气

	//七牛
	public static final String QiniuACCESS_KEY = "FKJE_OJ6hA9qzdzrn2sbgJon2eLTa3atfAwuvsPY";//AK
	public static final String QiniuSECRET_KEY = "a9I5Q0UOR5SjK83rftG-Wt7T8W7Ng4kbsmNwzCIt";//SK

	//下载地址
	public static final String DEV_DOWNURL="http://39.105.163.124:21112/";    //dev
	public static final String SALE_DOWNURL="http://39.105.163.124:21112/";    //推广版
	public static final String PROD_DOWNURL="http://39.105.163.124:21112/";   //线上环境图片存放地址
	public static  String DOWNURL="";


	//融云id前缀
	public static final String DEV_RONG_ID_PREFIX="rong_dev_";    //dev
	public static final String SALE_RONG_ID_PREFIX="rong_sale_";    //推广版
	public static final String PROD_RONG_ID_PREFIX="rong_prod_";   //线d上
	public static  String RONG_ID_PREFIX="";

	//图片上传绝对路径
	public static final String DEV_UPLOAD="/data/www/15javadev_app_v3/gt/download/ylyy/";    //dev
	public static final String SALE_UPLOAD="/data/www/15javadev_app_v3/gt/download/ylyy/";    //推广版
	public static final String PROD_UPLOAD="/data/www/15javadev_app_v3/gt/download/ylyy/";   //线上环境图片存放地址
	public static  String UPLOAD="";

	//index文件地址
	public static final String DEV_INDEX="/data/www/15javadev_app_v3/gt/download/ylyy/index.html";    //dev
	public static final String SALE_INDEX="/data/www/15javadev_app_v3/gt/download/ylyy/index.html";    //推广版
	public static final String PROD_INDEX="/data/www/15javadev_app_v3/gt/download/ylyy/index.html";   //线上环境图片存放地址
	public static  String INDEX_URL="";

	//二维码生成地址
	public static final String DEV_QRCODE="/data/www/15javadev_app_v3/gt/download/ylyy/QrCode/";    //dev
	public static final String SALE_QRCODE="/data/www/15javadev_app_v3/gt/download/QrCode/";    //推广版
	public static final String PROD_QRCODE="/data/www/15javadev_app_v3/gt/download/ylyy/QrCode/";   //线上环境图片存放地址
	public static  String QRCODE_URL="";

	//极光推送
	private static final String ENV_DEV = "env=ylyydev";//测试环境
	private static final String ENV_SALE = "env=sale";//销售推广环境
	private static final String ENV_PROD ="env=ylyy"; //生产环境
	public static  String ENV_TEST = "";

	//存储空间
	public static final String DEV_QiniuBucketName = "ovelec_app";
	public static final String SALE_QiniuBucketName = "ovelec_app";
	public static final String PROD_QiniuBucketName = "ovelec_app";
	public static  String QiniuBucketName="";

	//七牛url
	public static final String DEV_QiniuUrl = "http://seal.ovelec.com/";
	public static final String SALE_QiniuUrl = "http://seal.ovelec.com/";
	public static final String PROD_QiniuUrl = "http://seal.ovelec.com/";
	public static  String QiniuUrl ="";

	//七牛云文件前缀
	public static final String DEV_PREFIX = "dev/";
	public static final String SALE_PREFIX = "spread/";
	public static final String PROD_PREFIX = "ylyy/";
	public static  String FILE_PREFIX ="";

	//访问idrip url
	public static final String DEV_PMP_IDRIP_URL = "http://39.106.89.166:8085/";  //8085 高新v3分支
	public static final String SALE_PMP_IDRIP_URL = "http://39.105.163.124:9083/";
	public static final String PROD_PMP_IDRIP_URL = "http://39.105.163.124:9083/";   //高新 171环境
	public static  String PMP_IDRIP_URL ="";

	static{
		switch (PROFILES_ACTIVE){
				case "dev":
					DOWNURL=DEV_DOWNURL;
					UPLOAD=DEV_UPLOAD;
					INDEX_URL=DEV_INDEX;
					QRCODE_URL=DEV_QRCODE;
					ENV_TEST = ENV_DEV;
					QiniuBucketName=DEV_QiniuBucketName;
					QiniuUrl =DEV_QiniuUrl;
					FILE_PREFIX =DEV_PREFIX;
					RONG_ID_PREFIX=DEV_RONG_ID_PREFIX;
					RongAppKey=Dev_RongAppKey;
					RongAppSecret=Dev_RongAppSecret;
					PMP_IDRIP_URL=DEV_PMP_IDRIP_URL;
					break;
				case "sale":
					DOWNURL=SALE_DOWNURL;
					UPLOAD=SALE_UPLOAD;
					INDEX_URL=SALE_INDEX;
					QRCODE_URL=SALE_QRCODE;
					ENV_TEST = ENV_SALE;
					QiniuBucketName=SALE_QiniuBucketName;
					QiniuUrl =SALE_QiniuUrl;
					FILE_PREFIX =SALE_PREFIX;
					RONG_ID_PREFIX=SALE_RONG_ID_PREFIX;
					RongAppKey=Dev_RongAppKey;
					RongAppSecret=Dev_RongAppSecret;
					PMP_IDRIP_URL=SALE_PMP_IDRIP_URL;
					break;
				case "prod":
					DOWNURL=PROD_DOWNURL;
					UPLOAD=PROD_UPLOAD;
					INDEX_URL=PROD_INDEX;
					QRCODE_URL=PROD_QRCODE;
					ENV_TEST = ENV_PROD;
					QiniuBucketName=PROD_QiniuBucketName;
					QiniuUrl =PROD_QiniuUrl;
					FILE_PREFIX =PROD_PREFIX;
					RONG_ID_PREFIX=PROD_RONG_ID_PREFIX;
					RongAppKey=Prod_RongAppKey;
					RongAppSecret=Prod_RongAppSecret;
					PMP_IDRIP_URL=PROD_PMP_IDRIP_URL;
					break;
		}
	}

}
