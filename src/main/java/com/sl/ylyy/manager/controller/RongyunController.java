/*
package com.sl.ylyy.manager.controller;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.util.*;

public class rongyunController {
    private String Timestamp = String.valueOf(System.currentTimeMillis() / 1000);//时间戳，从 1970 年 1 月 1 日 0 点 0 分 0 秒开始到现在的秒数。
    private String Nonce = String.valueOf(Math.floor(Math.random() * 1000000));//随机数，无长度限制。
    */
/**
     * @return
     * @Description 获取融云token
     * @param userId  自定义id串，我们用的是员工id
     * @param userHead  图片的url
     * @param userName  名称
     *//*

    @RequestMapping(value = "/getToken", method = RequestMethod.POST)
    public Result<UserRespone> getToken(@RequestParam(required = true) String userId,
                                        @RequestParam(required = true) String userHead,
                                        @RequestParam(required = true) String userName) {
        StringBuffer res = new StringBuffer();
        String url = "http://api-cn.ronghub.com/user/getToken.json";
        String App_Key = "***********"; //开发者平台分配的 App Key。
        String App_Secret = "************";//开发者平台分配的App_Secret 。
        String Timestamp = String.valueOf(System.currentTimeMillis() / 1000);//时间戳，从 1970 年 1 月 1 日 0 点 0 分 0 秒开始到现在的秒数。
        String Nonce = String.valueOf(Math.floor(Math.random() * 1000000));//随机数，无长度限制。
        String Signature = sha1(App_Secret + Nonce + Timestamp);//数据签名。
        //Logger.i(Signature);
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader("App-Key", App_Key);
        httpPost.setHeader("Timestamp", Timestamp);
        httpPost.setHeader("Nonce", Nonce);
        httpPost.setHeader("Signature", Signature);
        httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
        List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(1);
        nameValuePair.add(new BasicNameValuePair("name", userName));//名称（例如使用这个功能的‘张三’）
        nameValuePair.add(new BasicNameValuePair("userId", userId));// 用户id（根据自己的项目，自己生成一个串就行，UUID就行）
        nameValuePair.add(new BasicNameValuePair("portraitUri", userHead));//头像(存储头像的路径)
        HttpResponse httpResponse = null;
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair, "utf-8"));
            httpResponse = httpClient.execute(httpPost);
            BufferedReader br = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()));
            String line = null;
            while ((line = br.readLine()) != null) {
                res.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("res=" + res.toString());
        UserRespone userRespone = JSON.parseObject(res.toString(), UserRespone.class);
        //Logger.i(userRespone.getCode()+"");
        return new ResultUtil<UserRespone>().setData(userRespone);
    }

    */
/**
     * 查询群组成员
     * @param groupId 群组id
     * @return
     *//*

    @ApiOperation(value = "查询群组成员")
    @RequestMapping(value = "/queryGroupMembers", method = RequestMethod.POST)
    public JSONObject queryGroupMembers(@RequestParam(required = true) String groupId){
        StringBuffer res = new StringBuffer();
        JSONObject jsonObject=new JSONObject();
        String url = "http://api-cn.ronghub.com/group/user/query.json";
        String Signature = sha1(App_Secret + Nonce + Timestamp);//数据签名。
        //Logger.i(Signature);
        //HttpClient httpClient = new DefaultHttpClient();
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader("App-Key", App_Key);
        httpPost.setHeader("Timestamp", Timestamp);
        httpPost.setHeader("Nonce", Nonce);
        httpPost.setHeader("Signature", Signature);
        httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
        List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(1);
        nameValuePair.add(new BasicNameValuePair("groupId", groupId));// 群组的Id
        HttpResponse httpResponse = null;
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair, "utf-8"));
            httpResponse = httpClient.execute(httpPost);
            BufferedReader br = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()));
            String line = null;
            while ((line = br.readLine()) != null) {
                res.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("res=" + res.toString());

        jsonObject=JSONObject.fromObject(res.toString());
        //Logger.i(userRespone.getCode()+"");
        return jsonObject;
    }

    */
/**
     * 创建群组并添加组员
     * @param createStaffId 创建群组的员工id
     * @param joinSta  多个群组成员的id用逗号隔开拼接的字符串
     * @param groupHead  群组头像
     * @param groupName  群组名称
     * @return
     *//*

    @ApiOperation(value = "创建群组并添加组员")
    @RequestMapping(value = "/groupCreateAndjoin", method = RequestMethod.POST)
    public Result<Map<String,Object>> groupCreateAndjoin(@RequestParam(required = true) String createStaffId,
                                                         @RequestParam(required = true) String joinSta,
                                                         @RequestParam(required = true) String groupHead,
                                                         @RequestParam(required = true) String groupName){
        String [] joinStaffId=joinSta.split(",");
        Map<String,Object> map=new HashMap<String, Object>();
        //创建人id不能存在与joinSta 参数中
        for (String join:joinStaffId){
            if(createStaffId.equals(join)){
                return new ResultUtil<Map<String,Object>>().setAppErrorMsg("添加群组失败，创建人不能存在与群组成员中");
            }
        };
        //创建群组的url
        String url = "http://api-cn.ronghub.com/group/create.json";
        String Signature = sha1(App_Secret + Nonce + Timestamp);//数据签名。
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader("App-Key", App_Key);
        httpPost.setHeader("Timestamp", Timestamp);
        httpPost.setHeader("Nonce", Nonce);
        httpPost.setHeader("Signature", Signature);
        httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
        List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(1);
        nameValuePair.add(new BasicNameValuePair("userId", createStaffId));//要加入群的用户Id（创建这个群组的员工id）
        String groupId= UUID.randomUUID().toString().replace("-", "");//自定义生成群id
        nameValuePair.add(new BasicNameValuePair("groupId",groupId));// 创建群组 Id（根据自己的项目，我们采用创建人的"sz"+员工id）
        //获取创建人的姓名
        */
/*String groupName=staffMapper.selectStaffDetail(createStaffId).getStaffName();
        for (int i=0;i<joinStaffId.length;i++){
            if (i==1){
                Staff staff1=staffMapper.selectStaffDetail(joinStaffId[i]);
                groupName=groupName+","+staff1.getStaffName()+"等";
                break;
            }else {
                Staff staff1=staffMapper.selectStaffDetail(joinStaffId[i]);
                groupName=groupName+","+staff1.getStaffName();
            }
        }*//*

        nameValuePair.add(new BasicNameValuePair("groupName", groupName));//群组Id对应的名称(我们采取创建人名字+两位成员名字+“等” 为群名称)
        HttpResponse httpResponse = null;
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair, "utf-8"));
            httpResponse = httpClient.execute(httpPost);
            BufferedReader br = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()));
            String line = null;
            String msg=null;
            String rongyunCode=null;
            while ((line = br.readLine()) != null) {
                System.out.println("line=" + line.toString());
                JSONObject jsonObject = JSONObject.fromObject(line); //将字符串类型的json格式串转换成json格式
                rongyunCode=jsonObject.get("code").toString();//获取融云返回的code码
                //创建分组成功后，直接添加分组成员
                if(rongyunCode.equals("200")){
                    //添加成员的url
                    String url1 = "http://api-cn.ronghub.com/group/join.json";
                    //Logger.i(Signature);
                    //HttpClient httpClient1 = new DefaultHttpClient(); 废弃的方法，用下面的
                    HttpClient httpClient1 = HttpClientBuilder.create().build();
                    HttpPost httpPost1 = new HttpPost(url1);
                    httpPost1.setHeader("App-Key", App_Key);
                    httpPost1.setHeader("Timestamp", Timestamp);
                    httpPost1.setHeader("Nonce", Nonce);
                    httpPost1.setHeader("Signature", Signature);
                    httpPost1.setHeader("Content-Type", "application/x-www-form-urlencoded");
                    List<NameValuePair> nameValuePair1 = new ArrayList<NameValuePair>(1);
                    for (String str:joinStaffId){
                        Staff staff=staffMapper.selectStaffDetail(str);
                        nameValuePair1.add(new BasicNameValuePair("userId", str));//要加入群的用户 Id，可提交多个，最多不超过 1000 个（员工id）
                        nameValuePair1.add(new BasicNameValuePair("groupId", groupId));// 要加入的群组 Id（上面创建的群组id）
                        nameValuePair1.add(new BasicNameValuePair("groupName", groupName));// 要加入的群 Id 对应的名称
                        httpPost1.setEntity(new UrlEncodedFormEntity(nameValuePair1, "utf-8"));
                        httpResponse = httpClient1.execute(httpPost1);
                        BufferedReader br1 = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()));
                        String line1 = null;
                        //String rongyunCode1=null;
                        while ((line1 = br1.readLine()) != null) {
                            System.out.println("line1=" + line1.toString());
                            JSONObject jsonObject1 = JSONObject.fromObject(line1);
                            rongyunCode=jsonObject1.get("code").toString();
                            if(rongyunCode.equals("200")){
                                msg="创建分组并添加成员成功";
                            }else {
                                msg=staff.getStaffName()+"添加群组失败，失败原因为："+codeMsg(rongyunCode);
                                map.put("rongyunCode",rongyunCode);//融云返回的code码
                                map.put("rongyunMsg",msg);//融云返回的msg信息
                                map.put("groupId",groupId);//群组id
                                map.put("groupName",groupName);//群组名称
                                return new ResultUtil<Map<String,Object>>().setData(map);
                            }
                        }
                    }
                }else { //如果失败直接返回code码和msg值，不在操作添加
                    msg=codeMsg(rongyunCode);
                }
                //res.append(line);
            }
            //群组创建成功后需要在融云群组信息表中添加一条数据
            if(rongyunCode.equals("200")){
                //创建分组成功后要发送群系统消息，不管此操作成不成功，不影响后续操作
                String types="0";
                String [] addStaffId=new String[0];//没用
                String [] addStaffName=new String[0];//没用groupPublish(createStaffId,addStaffId,addStaffName,groupId,types,groupName);
                //这里是我们自己的业务逻辑，主要是用来存储群主id和群头像的作用，不需要可忽略
                RongyunGroup rongyunGroup=new RongyunGroup();
                rongyunGroup.setGroupId(groupId); //群组id
                rongyunGroup.setGroupName(groupName);//群组名称
                rongyunGroup.setGroupHead(groupHead);//群组头像
                rongyunGroup.setGroupOwnerId(createStaffId);//群主id
                int res=rongyunGroupService.insertRongyunGroup(rongyunGroup);
            }
            map.put("rongyunCode",rongyunCode); //融云返回的code码
            map.put("rongyunMsg",msg);//融云返回的msg信息
            map.put("groupId",groupId);//群组id
            map.put("groupName",groupName);//群组名称
        } catch (IOException e) {
            e.printStackTrace();
        }
        //Logger.i(userRespone.getCode()+"");
        return new ResultUtil<Map<String,Object>>().setData(map);
    }

    */
/**
     * 在已有的分组中添加分组成员
     * @param groupId 群组id
     * @param groupName 群组名称
     * @param joinSta 需要添加的组员id字符串，多个用逗号隔开
     * @param groupHead 群组头像
     * @return
     *//*

    @RequestMapping(value = "/joinGroupMembers",method = RequestMethod.POST)
    @ApiOperation(value = "在已有的分组中添加分组成员")
    public Result<Map<String,Object>> joinGroupMembers(@RequestParam(required = true) String groupId,
                                                       @RequestParam(required = true) String groupName,
                                                       @RequestParam(required = true) String joinSta,
                                                       @RequestParam(required = true) String groupHead){
        Map<String,Object> map=new HashMap<>();
        String [] joinStaffId=joinSta.split(",");
        //添加成员的url
        String url = "http://api-cn.ronghub.com/group/join.json";
        String Signature = sha1(App_Secret + Nonce + Timestamp);//数据签名。
        //Logger.i(Signature);
        //HttpClient httpClient = new DefaultHttpClient();
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader("App-Key", App_Key);
        httpPost.setHeader("Timestamp", Timestamp);
        httpPost.setHeader("Nonce", Nonce);
        httpPost.setHeader("Signature", Signature);
        httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
        List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(1);
        for (String str:joinStaffId){
            Staff staff=staffMapper.selectStaffDetail(str);
            nameValuePair.add(new BasicNameValuePair("userId", str));//要加入群的用户 Id，可提交多个，最多不超过 1000 个（员工id）
            nameValuePair.add(new BasicNameValuePair("groupId", groupId));// 要加入的群组 Id（上面创建的群组id）
            nameValuePair.add(new BasicNameValuePair("groupName", groupName));// 要加入的群 Id 对应的名称
            HttpResponse httpResponse=null;
            try {
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair, "utf-8"));
                httpResponse = httpClient.execute(httpPost);
                BufferedReader br1 = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()));
                String line1 = null;
                String rongyunCode=null;
                String msg=null;
                //String rongyunCode1=null;
                while ((line1 = br1.readLine()) != null) {
                    System.out.println("line1=" + line1.toString());
                    JSONObject jsonObject1 = JSONObject.fromObject(line1);
                    rongyunCode=jsonObject1.get("code").toString();
                    if(rongyunCode.equals("200")){
                        msg="添加成员成功";
                        //添加成员成功后要发送群系统消息，不管此操作成不成功，不影响后续操作
                        String types="2";
                        String [] addStaffId=new String[]{str};//操作人id
                        String [] addStaffName=new String[]{staff.getStaffName()};
                        groupPublish(str,addStaffId,addStaffName,groupId,types,groupName);

                        //**********成功后更新头像，groupName在这里没用，充数放进去的  start
                        //获取创建人的姓名和成员名称拼新的名称，主要作用于一开始只有两个人的情况下，新增一个组员名称会变化
                        int res=updateRongyunGroup(groupHead,groupName,groupId);
                        if (res<=0){
                            return new ResultUtil<Map<String,Object>>().setAppErrorMsg("群组添加成员成功但修改头像和名称失败");
                        }
                        //**********成功后更新头像，groupName在这里没用，充数放进去的   end
                    }else {
                        msg=staff.getStaffName()+"添加群组成员失败，失败原因为："+codeMsg(rongyunCode);
                        map.put("rongyunCode",rongyunCode);//融云返回的code码
                        map.put("rongyunMsg",msg);//融云返回的msg信息
                        map.put("groupId",groupId);//群组id
                        map.put("groupName",groupName);//群组名称
                        return new ResultUtil<Map<String,Object>>().setData(map);
                    }
                }
                map.put("rongyunCode",rongyunCode);//融云返回的code码
                map.put("rongyunMsg",msg);//融云返回的msg信息
                map.put("groupId",groupId);//群组id
                map.put("groupName",groupName);//群组名称
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        return new ResultUtil<Map<String,Object>>().setData(map);
    }

    */
/**
     * 退出群组 （自己的业务逻辑中不允许群主退出群组）
     * @param staffId 退出群的员工id,多个用逗号隔开
     * @param groupId 群组id
     * @param groupName  群组名称
     * @param groupHead  群组头像
     * @param type  1为踢人 其他为退群
     * @return
     *//*

    @RequestMapping(value = "/quitGroupMembers",method = RequestMethod.POST)
    @ApiOperation(value = "退出群组")
    public Result<Map<String,Object>> quitGroupMembers(@RequestParam(required = true) String staffId,
                                                       @RequestParam(required = true) String groupId,
                                                       @RequestParam(required = true) String groupName,
                                                       @RequestParam(required = true) String groupHead,
                                                       @RequestParam(required = true) String type){
        Map<String,Object> map=new HashMap<>();
        //组内成员退出的url
        String url = "http://api-cn.ronghub.com/group/quit.json";
        String Signature = sha1(App_Secret + Nonce + Timestamp);//数据签名。
        //Logger.i(Signature);
        //HttpClient httpClient = new DefaultHttpClient();
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader("App-Key", App_Key);
        httpPost.setHeader("Timestamp", Timestamp);
        httpPost.setHeader("Nonce", Nonce);
        httpPost.setHeader("Signature", Signature);
        httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
        List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(1);
        String [] sta=staffId.split(",");
        for (String str:sta){
            nameValuePair.add(new BasicNameValuePair("userId", str));//要退出群的用户 Id（员工id）
        }
        //nameValuePair.add(new BasicNameValuePair("userId", staffId));//要退出群的用户 Id（员工id）
        nameValuePair.add(new BasicNameValuePair("groupId", groupId));// 要退出的群 Id
        HttpResponse httpResponse=null;
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair, "utf-8"));
            httpResponse = httpClient.execute(httpPost);
            BufferedReader br1 = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()));
            String line = null;
            String rongyunCode=null;
            String msg=null;
            //String rongyunCode1=null;
            while ((line = br1.readLine()) != null) {
                System.out.println("line=" + line.toString());
                JSONObject jsonObject1 = JSONObject.fromObject(line);
                rongyunCode=jsonObject1.get("code").toString();
                if(rongyunCode.equals("200")){
                    msg="操作成功";
                    //踢人或退出成功后要发送群系统消息，不管此操作成不成功，不影响后续操作
                    String types="";
                    if (type.equals("1")){//踢出群组操作
                        types="3";
                    }else {
                        types="4";
                    }
                    String [] addStaffId=new String[]{staffId};//操作人id
                    Staff staff=staffMapper.selectStaffDetail(staffId);//获取员工名称
                    String [] addStaffName=new String[]{staff.getStaffName()};
                    groupPublish(staffId,addStaffId,addStaffName,groupId,types,groupName);

                    //**********成功后更新头像，groupName在这里没用，充数放进去的  start
                    //获取创建人的姓名和成员名称拼新的名称，主要作用于一开始只有两个人的情况下，新增一个组员名称会变化
                    int res=updateRongyunGroup(groupHead,groupName,groupId);
                    if (res<=0){
                        return new ResultUtil<Map<String,Object>>().setAppErrorMsg("群组添加成员成功但修改头像和名称失败");
                    }
                    //**********成功后更新头像，groupName在这里没用，充数放进去的   end
                }else {
                    msg="操作失败，失败原因为："+codeMsg(rongyunCode);
                    map.put("rongyunCode",rongyunCode);//融云返回的code码
                    map.put("rongyunMsg",msg);//融云返回的msg信息
                    map.put("groupId",groupId);//群组id
                    return new ResultUtil<Map<String,Object>>().setData(map);
                }
            }
            map.put("rongyunCode",rongyunCode);//融云返回的code码
            map.put("rongyunMsg",msg);//融云返回的msg信息
            map.put("groupId",groupId);//群组id
        }catch (IOException e){
            e.printStackTrace();
        }
        return new ResultUtil<Map<String,Object>>().setData(map);
    }

    */
/**
     * 踢出群组
     * @param memberId  被提出人的员工id,多个用逗号隔开
     * @param groupId 群组id
     * @param groupHead  群组头像
     * @param groupName  群组名称
     * @return
     *//*

    @RequestMapping(value = "/kickOutGroup",method = RequestMethod.POST)
    @ApiOperation(value = "踢出群组")
    public Result<Map<String, Object>> kickOutGroup(@RequestParam(required = true) String memberId,
                                                    @RequestParam(required = true) String groupId,
                                                    @RequestParam(required = false) String groupName,
                                                    @RequestParam(required = true) String groupHead){
        //融云没有踢出群组的接口，采用了退出群组接口，由我们这边通过逻辑去实现踢人功能
        String type="1";//用来区分踢人和退群
        Result<Map<String, Object>> result=quitGroupMembers(memberId,groupId,groupName,groupHead,type);
        System.out.println(result);

        return result;
    }


    */
/**
     * 解散群组
     * @param staffId 操作解散群的用户id
     * @param groupId 要解散的群组id
     * @return
     *//*

    @RequestMapping(value = "/dismissGroup",method = RequestMethod.POST)
    @ApiOperation(value = "解散群组")
    public Result<Map<String,Object>> dismissGroup(@RequestParam(required = true) String staffId,
                                                   @RequestParam(required = true) String groupId){
        Map<String,Object> map=new HashMap<>();
        //组内成员退出的url
        String url = "http://api-cn.ronghub.com/group/dismiss.json";
        String Signature = sha1(App_Secret + Nonce + Timestamp);//数据签名。
        //Logger.i(Signature);
        //HttpClient httpClient = new DefaultHttpClient();
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader("App-Key", App_Key);
        httpPost.setHeader("Timestamp", Timestamp);
        httpPost.setHeader("Nonce", Nonce);
        httpPost.setHeader("Signature", Signature);
        httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
        List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(1);
        nameValuePair.add(new BasicNameValuePair("userId", staffId));//操作解散群的用户 Id（员工id）
        nameValuePair.add(new BasicNameValuePair("groupId", groupId));// 要解散的群 Id
        HttpResponse httpResponse=null;
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair, "utf-8"));
            httpResponse = httpClient.execute(httpPost);
            BufferedReader br1 = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()));
            String line = null;
            String rongyunCode=null;
            String msg=null;
            //String rongyunCode1=null;
            while ((line = br1.readLine()) != null) {
                System.out.println("line=" + line.toString());
                JSONObject jsonObject1 = JSONObject.fromObject(line);
                rongyunCode=jsonObject1.get("code").toString();
                if(rongyunCode.equals("200")){
                    msg="解散群成功";
                    //解散成功后要发送群系统消息，不管此操作成不成功，不影响后续操作
                    String [] addStaffId=new String[0];//该字段没用
                    String [] addStaffName=new String[0];//该字段没用
                    String types="5";
                    String groupName="";//该字段没用
                    groupPublish(staffId,addStaffId,addStaffName,groupId,types,groupName);
                    //解散成功后要删除群组表中存储的群组数据
                    int res=rongyunGroupService.deleteRongyunGroup(groupId);
                    if (res<=0){
                        return new ResultUtil<Map<String,Object>>().setAppErrorMsg("删除群组信息失败");
                    }
                }else {
                    msg="解散群失败，失败原因为："+codeMsg(rongyunCode);
                    map.put("rongyunCode",rongyunCode);//融云返回的code码
                    map.put("rongyunMsg",msg);//融云返回的msg信息
                    map.put("groupId",groupId);//群组id
                    return new ResultUtil<Map<String,Object>>().setData(map);
                }
            }
            map.put("rongyunCode",rongyunCode);//融云返回的code码
            map.put("rongyunMsg",msg);//融云返回的msg信息
            map.put("groupId",groupId);//群组id
        }catch (IOException e){
            e.printStackTrace();
        }
        return new ResultUtil<Map<String,Object>>().setData(map);
    }


    */
/**
     * 刷新群组信息
     * @param groupName  群组名称
     * @param groupId 群组id
     * @return
     *//*

    */
/*@RequestMapping(value = "/refreshGroup",method = RequestMethod.POST)
    @ApiOperation(value = "刷新群组信息")*//*

    public JSONObject refreshGroup(@RequestParam(required = true) String groupId,
                                   @RequestParam(required = true) String groupName){
        JSONObject jsonObject=new JSONObject();
        //组内成员退出的url
        String url = "http://api-cn.ronghub.com/group/refresh.json";
        String Signature = sha1(App_Secret + Nonce + Timestamp);//数据签名。
        //Logger.i(Signature);
        //HttpClient httpClient = new DefaultHttpClient();
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader("App-Key", App_Key);
        httpPost.setHeader("Timestamp", Timestamp);
        httpPost.setHeader("Nonce", Nonce);
        httpPost.setHeader("Signature", Signature);
        httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
        List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(1);
        nameValuePair.add(new BasicNameValuePair("groupId", groupId));//群组 Id
        nameValuePair.add(new BasicNameValuePair("groupName", groupName));// 群组名称
        HttpResponse httpResponse=null;
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair, "utf-8"));
            httpResponse = httpClient.execute(httpPost);
            BufferedReader br1 = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()));
            String line = null;
            String rongyunCode=null;
            String msg=null;
            //String rongyunCode1=null;
            while ((line = br1.readLine()) != null) {
                System.out.println("line=" + line.toString());
                JSONObject jsonObject1 = JSONObject.fromObject(line);
                rongyunCode=jsonObject1.get("code").toString();
                if(rongyunCode.equals("200")){
                    msg="刷新群组信息成功";
                }else {
                    msg="刷新群组信息失败，失败原因为："+codeMsg(rongyunCode);
                    jsonObject.put("rongyunCode",rongyunCode);//融云返回的code码
                    jsonObject.put("rongyunMsg",msg);//融云返回的msg信息
                    jsonObject.put("groupId",groupId);//群组id
                    return jsonObject;
                }
            }
            jsonObject.put("rongyunCode",rongyunCode);//融云返回的code码
            jsonObject.put("rongyunMsg",msg);//融云返回的msg信息
            jsonObject.put("groupId",groupId);//群组id
        }catch (IOException e){
            e.printStackTrace();
        }
        return jsonObject;
    }

    */
/**
     * 发送群组通知消息
     * @param staffId  //员工id
     * @param addStaffId //被加入群的用户 ID
     * @param addStaffName //被加入群的用户 名称
     * @param addStaffName //主用于退出群组，如果退出的是群主则为新群主id，不是群主就为null
     * @param types  //0 创建群组
     * @param groupId  群组id
     * @param groupName  群组名称
     * @return
     *//*

    //@RequestMapping(value = "/groupPublish",method = RequestMethod.POST)
    //@ApiOperation(value = "发送群组通知消息")
    public Result<Map<String,Object>> groupPublish(@RequestParam(required = true) String staffId,
                                                   @RequestParam(required = false) String [] addStaffId,
                                                   @RequestParam(required = false) String [] addStaffName,
                                                   @RequestParam(required = true) String groupId,
                                                   @RequestParam(required = true) String types,
                                                   @RequestParam(required = true) String groupName){
        Map<String,Object> map=new HashMap<>();
        //组内成员退出的url
        String url = "http://api-cn.ronghub.com/message/group/publish.json";
        String Signature = sha1(App_Secret + Nonce + Timestamp);//数据签名。
        //Logger.i(Signature);
        //HttpClient httpClient = new DefaultHttpClient();
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader("App-Key", App_Key);
        httpPost.setHeader("Timestamp", Timestamp);
        httpPost.setHeader("Nonce", Nonce);
        httpPost.setHeader("Signature", Signature);
        httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
        List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(1);
        nameValuePair.add(new BasicNameValuePair("fromUserId", staffId));//发送人员工 Id
        nameValuePair.add(new BasicNameValuePair("toGroupId", groupId));//接收群 Id
        nameValuePair.add(new BasicNameValuePair("objectName", "RC:GrpNtf"));//消息类型 本类型为群组系统消息
        Staff staff=staffMapper.selectStaffDetail(staffId);
        String content="";
        //content逻辑开始*************
        JSONObject jsonObject=new JSONObject();
        JSONObject data=new JSONObject();
        if (types.equals("0")){//创建群组消息
            //content="{\"operatorNickname\":\""+staffId+"\",\"operation\":\"Rename\",\"data\":{\"operatorNickname\":\""+staff.getStaffName()+"\",\"targetGroupName\":\""+groupName+"\"},\"message\":\"创建群组\",\"extra\":\"\"}";
            jsonObject.put("operatorUserId",staffId);//操作人用户 Id
            jsonObject.put("operation","Create");//操作名 创建
            data.put("operatorNickname",staff.getStaffName());//data 数据说明：operatorNickname 为操作者
            data.put("targetGroupName",groupName);//data 数据说明：群组名称
            jsonObject.put("data",data); //data
            jsonObject.put("message","创建群组");
            jsonObject.put("extra","");
            content=jsonObject.toString();
        } else if (types.equals("1")){//修改群名称
            jsonObject.put("operatorUserId",staffId);//操作人用户 Id
            jsonObject.put("operation","Rename");//操作名 修改群名称
            data.put("operatorNickname",staff.getStaffName());//data 数据说明：operatorNickname 为操作者
            data.put("targetGroupName",groupName);//data 数据说明：群组名称
            jsonObject.put("data",data); //data
            jsonObject.put("message","修改群名称");
            jsonObject.put("extra","");
            content=jsonObject.toString();
        } else if (types.equals("2")){//添加群成员
            jsonObject.put("operatorUserId",staffId);//操作人用户 Id
            jsonObject.put("operation","Add");//操作名 添加群成员
            data.put("operatorNickname",staff.getStaffName());//data 数据说明：operatorNickname 为操作者
            data.put("targetUserIds",addStaffId);// 被加入群的用户 ID
            data.put("targetUserDisplayNames",addStaffName);// 被加入群的用户 名称
            data.put("targetGroupName",groupName);//data 数据说明：群组名称
            jsonObject.put("data",data); //data
            jsonObject.put("message","添加群成员");
            jsonObject.put("extra","");
            content=jsonObject.toString();
        } else if (types.equals("3")){//移出群成员
            jsonObject.put("operatorUserId",staffId);//操作人用户 Id
            jsonObject.put("operation","Kicked");//操作名 移出群成员
            data.put("operatorNickname",staff.getStaffName());//data 数据说明：operatorNickname 为操作者
            data.put("targetUserIds",addStaffId);// 被移出群成员的用户 ID
            data.put("targetUserDisplayNames",addStaffName);// 被移出群成员的用户 名称
            jsonObject.put("data",data); //data
            jsonObject.put("message","移出群成员");
            jsonObject.put("extra","");
            content=jsonObject.toString();
        }else if (types.equals("4")){//退出群组
            jsonObject.put("operatorUserId",staffId);//操作人用户 Id
            jsonObject.put("operation","Quit");//操作名 退出群组
            data.put("operatorNickname",staff.getStaffName());//data 数据说明：operatorNickname 为操作者
            data.put("targetUserIds",addStaffId);// 退出群组的用户 ID
            data.put("targetUserDisplayNames",addStaffName);// 退出群组的用户 名称
            //data.put("newCreatorId",newCreatorId);//data 数据说明：如退出群的用户为群创建者则 newCreatorId 为新的群创建者 ID ，否则为 null(自己的业务逻辑中不允许群主退出群组)
            jsonObject.put("data",data); //data
            jsonObject.put("message","退出群组");
            jsonObject.put("extra","");
            content=jsonObject.toString();
        }else if (types.equals("5")){//解散群组
            jsonObject.put("operatorUserId",staffId);//操作人用户 Id
            jsonObject.put("operation","Dismiss");//操作名 解散群组
            data.put("operatorNickname",staff.getStaffName());//data 数据说明：operatorNickname 为操作者
            jsonObject.put("data",data); //data
            jsonObject.put("message","解散群组");
            jsonObject.put("extra","");
            content=jsonObject.toString();
        }
        //content逻辑结束*************

        System.out.println(content);
        nameValuePair.add(new BasicNameValuePair("content", content));// 发送消息内容
        nameValuePair.add(new BasicNameValuePair("isIncludeSender", "1"));// 发送消息内容
        HttpResponse httpResponse=null;
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair, "utf-8"));
            httpResponse = httpClient.execute(httpPost);
            BufferedReader br1 = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()));
            String line = null;
            String rongyunCode=null;
            String msg=null;
            //String rongyunCode1=null;
            while ((line = br1.readLine()) != null) {
                System.out.println("line=" + line.toString());
                JSONObject jsonObject1 = JSONObject.fromObject(line);
                rongyunCode=jsonObject1.get("code").toString();
                if(rongyunCode.equals("200")){
                    msg="系统消息发送成功";
                }else {
                    msg="系统消息发送失败，失败原因为："+codeMsg(rongyunCode);
                    map.put("rongyunCode",rongyunCode);//融云返回的code码
                    map.put("rongyunMsg",msg);//融云返回的msg信息
                    //map.put("groupId",groupId);//群组id
                    return new ResultUtil<Map<String,Object>>().setData(map);
                }
            }
            map.put("rongyunCode",rongyunCode);//融云返回的code码
            map.put("rongyunMsg",msg);//融云返回的msg信息
            //map.put("groupId",groupId);//群组id
        }catch (IOException e){
            e.printStackTrace();
        }
        return new ResultUtil<Map<String,Object>>().setData(map);
    }

    //SHA1加密//http://www.rongcloud.cn/docs/server.html#通用_API_接口签名规则
    private static String sha1(String data) {
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

    */
/**
     * 如果失败返回的各种msg信息
     * @param code
     * @return
     *//*

    private static String codeMsg(String code){
        String msg=null;
        if(code.equals("400")){
            msg="请求参数错误";
        }else if(code.equals("401")){
            msg="未授权";
        }else if(code.equals("403")){
            msg="服务器拒绝请求";
        }else if(code.equals("404")){
            msg="服务器找不到请求的地址";
        }else if(code.equals("405")){
            msg="群容量超出上限，禁止请求此方法";
        }else if(code.equals("429")){
            msg="请求频率超限";
        }else if(code.equals("500")){
            msg="服务器内部错误";
        }else if(code.equals("504")){
            msg="网关超时";
        }else if(code.equals("1000")){
            msg="服务器端内部逻辑错误,请稍后重试";
        }else if(code.equals("1001")){
            msg="App Secret 错误";
        }else if(code.equals("1002")){
            msg="参数错误";
        }else if(code.equals("1003")){
            msg="无 POST 数据";
        }else if(code.equals("1004")){
            msg="验证签名错误";
        }else if(code.equals("1005")){
            msg="参数长度超限";
        }else if(code.equals("1006")){
            msg="App 被锁定或删除";
        }else if(code.equals("1007")){
            msg="被限制调用";
        }else if(code.equals("1008")){
            msg="调用频率超限";
        }else if(code.equals("1009")){
            msg="服务未开通";
        }else if(code.equals("1015")){
            msg="要删除的保活聊天室 ID 不存在";
        }else if(code.equals("1016")){
            msg="设置的保活聊天室个数超限";
        }else if(code.equals("1017")){
            msg="实时音视频 SDK 版本不支持";
        }else if(code.equals("1018")){
            msg="实时音视频服务未开启";
        }else if(code.equals("1050")){
            msg="内部服务响应超时";
        }else if(code.equals("2007")){
            msg="注册用户数量超限";
        }
        return msg;
    }
}*/
