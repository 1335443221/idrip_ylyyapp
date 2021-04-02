package com.sl.ylyy.app_1.service.impl;
import com.alibaba.fastjson.JSONObject;
import com.sl.ylyy.app_1.dao.*;
import com.sl.ylyy.app_1.entity.Company;
import com.sl.ylyy.app_1.entity.Department;
import com.sl.ylyy.app_1.entity.Group;
import com.sl.ylyy.app_1.entity.Switchingroom;
import com.sl.ylyy.app_1.entity.User;
import com.sl.ylyy.app_1.service.UserService_app1;
import com.sl.ylyy.common.config.UrlConfig;
import com.sl.ylyy.common.utils.Result1;
import com.sl.ylyy.common.utils.JwtToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.sl.ylyy.common.utils.JwtToken.*;

@Service("userServiceApp1")
public class UserService_app1Impl implements UserService_app1 {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private GroupMapper groupMapper;
    @Autowired
    private DepartmentMapper departmentMapper;
    @Autowired
    private CompanyMapper companyMapper;
    @Autowired
    private TipMapper tipMapper;
    @Autowired
    private SwitchingroomMapper switchingroomMapper;
    @Autowired
    private RoleMapper roleMapper;

    @Override
    public Map<String, Object> getUsersByIds(Map<String,Object> params) {
        //公司下全部子公司的部门、班组、员工级联树
        List<Map<String,Object>> companys = new ArrayList<>();
        //总公司下全部子公司
        List<Company> companyList = new ArrayList<>();

        Company pCompany = companyMapper.selectByPrimaryKey(1);
        companyList.add(pCompany);
        //company_id=1是总公司（当前项目）
        if (params.get("company_id") == null||Integer.parseInt(params.get("company_id").toString())==1) {
            companyList.addAll(companyMapper.selectChildrens(1));

        } else {
            companyList.add(companyMapper.selectByPrimaryKey(Integer.parseInt(params.get("company_id").toString())));
        }

        //遍历每个子公司，生成子公司下部门、班组、员工级联树
        for(Company company:companyList){
            Map<String,Object> companyMap = new HashMap<>();
            companyMap.put("cid",company.getId());
            companyMap.put("cname",company.getCname());
            companyMap.put("mlist",userMapper.selectUsersByRid(2));
            List<Department> departmentList = new ArrayList<>();
            //若参数中不包含部门id，则获取当前公司下全部部门
            if(params.get("department_id")==null){
                departmentList = departmentMapper.selectDepsByComId(company.getId());
            //若查询条件中指定部门，则根据部门id查询部门信息
            }else{
                Department department = new Department();
                Department departmentById = departmentMapper.selectByPrimaryKey(Integer.parseInt(params.get("department_id").toString()));
                department.setId(departmentById.getId());
                department.setDname(departmentById.getDname());
                departmentList.add(department);
            }
            List<Map<String,Object>> departments = new ArrayList<>();
            //遍历每个部门，生成部门下班组和员工级联树
            for(Department department:departmentList){
                Map<String,Object> departmentMap = new HashMap<>();

                if(company.getId()==1){
                    departmentMap.put("pdid",department.getId());
                    departmentMap.put("pdname",department.getDname());
                }else{
                    departmentMap.put("did",department.getId());
                    departmentMap.put("dname",department.getDname());
                }
                departmentMap.put("mlist",userMapper.selectDeparmentManagersByRid(3,department.getId()));

                List<Group> groupList = new ArrayList<>();
                //若参数中不包含班组id，则获取当前部门下全部班组
                if(params.get("group_id")==null){
                    groupList = groupMapper.selectGroupsByDepId(department.getId());
                }else{
                    Group group = new Group();
                    Group groupById = groupMapper.selectByPrimaryKey(Integer.parseInt(params.get("group_id").toString()));
                    group.setId(groupById.getId());
                    group.setName(groupById.getName());
                    groupList.add(groupById);
                }
                List<Map<String,Object>> groups = new ArrayList<>();
                //遍历每个班组，获取班组下全部员工
                for(Group group : groupList){
                    Map<String,Object> groupMap = new HashMap<>();
                    groupMap.put("gid", group.getId());
                    groupMap.put("gname", group.getName());
                    Integer groupManager = group.getGroupManager();

                    List<Map<String,Object>> userList = new ArrayList<>();
                    //若参数中不包含员工id，则获取当前班组下全部员工以及班组长的信息
                    if(params.get("u_id")==null){
                        userList = userMapper.selectUsersByGroupId(group.getId());
                        Map<String,Object> manager = userMapper.selectUserById(groupManager);
                        if(manager!=null&&!userList.contains(manager)){
                            userList.add(manager);
                        }
                    }else{
                        //若参数中含员工id，则根据id获取员工信息
                        userList.add(userMapper.selectUserById(Integer.parseInt(params.get("u_id").toString())));
                    }
                    groupMap.put("list",userList);
                    groups.add(groupMap);
                }
                departmentMap.put("list",groups);
                departments.add(departmentMap);
            }
            companyMap.put("list",departments);
            companys.add(companyMap);
        }

        Map<String,Object> allCompany = new HashMap<>();
        allCompany.put("pid",1);
        allCompany.put("pname","总公司");
        if(companys!=null&&companys.size()>0){
            allCompany.put("dlist",companys.get(0).get("list"));
            companys.remove(companys.get(0));
        }
        allCompany.put("mlist",userMapper.selectUsersByRid(1));
        allCompany.put("list",companys);
        return allCompany;
    }

    /**
     * 设备管理  树形结构  user_id  为 center表的id
     * @param params
     * @return
     */
    @Override
    public Map<String, Object> getUsersByIdsElcm(Map<String,Object> params) {
        //公司下全部子公司的部门、班组、员工级联树
        List<Map<String,Object>> companys = new ArrayList<>();
        //总公司下全部子公司
        List<Company> companyList = new ArrayList<>();

        Company pCompany = companyMapper.selectByPrimaryKey(1);
        companyList.add(pCompany);
        //company_id=1是总公司（当前项目）
        if (params.get("company_id") == null||Integer.parseInt(params.get("company_id").toString())==1) {
            companyList.addAll(companyMapper.selectChildrens(1));

        } else {
            companyList.add(companyMapper.selectByPrimaryKey(Integer.parseInt(params.get("company_id").toString())));
        }

        //遍历每个子公司，生成子公司下部门、班组、员工级联树
        for(Company company:companyList){
            Map<String,Object> companyMap = new HashMap<>();
            companyMap.put("cid",company.getId());
            companyMap.put("cname",company.getCname());
            companyMap.put("mlist",userMapper.elcmSelectUsersByRid(2));
            List<Department> departmentList = new ArrayList<>();
            //若参数中不包含部门id，则获取当前公司下全部部门
            if(params.get("department_id")==null){
                departmentList = departmentMapper.selectDepsByComId(company.getId());
            //若查询条件中指定部门，则根据部门id查询部门信息
            }else{
                Department department = new Department();
                Department departmentById = departmentMapper.selectByPrimaryKey(Integer.parseInt(params.get("department_id").toString()));
                department.setId(departmentById.getId());
                department.setDname(departmentById.getDname());
                departmentList.add(department);
            }
            List<Map<String,Object>> departments = new ArrayList<>();
            //遍历每个部门，生成部门下班组和员工级联树
            for(Department department:departmentList){
                Map<String,Object> departmentMap = new HashMap<>();

                if(company.getId()==1){
                    departmentMap.put("pdid",department.getId());
                    departmentMap.put("pdname",department.getDname());
                }else{
                    departmentMap.put("did",department.getId());
                    departmentMap.put("dname",department.getDname());
                }
                departmentMap.put("mlist",userMapper.elcmSelectDeparmentManagersByRid(3,department.getId()));

                List<Group> groupList = new ArrayList<>();
                //若参数中不包含班组id，则获取当前部门下全部班组
                if(params.get("group_id")==null){
                    groupList = groupMapper.selectGroupsByDepId(department.getId());
                }else{
                    Group group = new Group();
                    Group groupById = groupMapper.selectByPrimaryKey(Integer.parseInt(params.get("group_id").toString()));
                    group.setId(groupById.getId());
                    group.setName(groupById.getName());
                    groupList.add(groupById);
                }
                List<Map<String,Object>> groups = new ArrayList<>();
                //遍历每个班组，获取班组下全部员工
                for(Group group : groupList){
                    Map<String,Object> groupMap = new HashMap<>();
                    groupMap.put("gid", group.getId());
                    groupMap.put("gname", group.getName());
                    Integer groupManager = group.getGroupManager();

                    List<Map<String,Object>> userList = new ArrayList<>();
                    //若参数中不包含员工id，则获取当前班组下全部员工以及班组长的信息
                    if(params.get("u_id")==null){
                        userList = userMapper.elcmSelectUsersByGroupId(group.getId());
                        Map<String,Object> manager = userMapper.elcmSelectUserById(groupManager);
                        if(manager!=null&&!userList.contains(manager)){
                            userList.add(manager);
                        }
                    }else{
                        //若参数中含员工id，则根据id获取员工信息
                        userList.add(userMapper.elcmSelectUserById(Integer.parseInt(params.get("u_id").toString())));
                    }
                    groupMap.put("list",userList);
                    groups.add(groupMap);
                }
                departmentMap.put("list",groups);
                departments.add(departmentMap);
            }
            companyMap.put("list",departments);
            companys.add(companyMap);
        }

        Map<String,Object> allCompany = new HashMap<>();
        allCompany.put("pid",1);
        allCompany.put("pname","总公司");
        if(companys!=null&&companys.size()>0){
            allCompany.put("dlist",companys.get(0).get("list"));
            companys.remove(companys.get(0));
        }
        allCompany.put("mlist",userMapper.elcmSelectUsersByRid(1));
        allCompany.put("list",companys);
        return allCompany;
    }




    @Override
    public Set<Map<String, Object>> getStorekeepers(Map<String, Object> params) {
        Map<String,Object> user = getUserByWebToken((String)params.get("token"));
        Integer companyId = (Integer)user.get("company_id");
        Set<Map<String,Object>> storekeepers = new LinkedHashSet<>();
        storekeepers.addAll(userMapper.selectStorekeepers(companyId));
        /*List<Department> departments = departmentMapper.selectDepsByComId(companyId);
        for(Department department:departments){
            List<Group> groups = groupMapper.selectGroupsByDepId(department.getId());
            for(Group group:groups){
                User keeper = userMapper.selectByPrimaryKey(group.getGroupManager());
                Map<String,Object> storekeeper =  new HashMap<>();
                storekeeper.put("id",keeper.getId());
                storekeeper.put("name",keeper.getUserName());
                storekeepers.add(storekeeper);
            }
        }*/
        return storekeepers;
    }

    @Override
    public Result1 login(Map<String,Object> params) {
        String account = (String) params.get("account");
        if(!account.startsWith("1")||!(account.length()==11)){
            return new Result1("1005","账号不是手机号");
        }

        String md5_password = (String) params.get("password");
//        String md5_password = DigestUtils.md5DigestAsHex(password.getBytes());
        User user = userMapper.selectUserByCellPhone(account);
        if(user ==null){
            return new Result1("1006","账号不存在");
        }else{
            if(user.getPassword()!=null&& user.getPassword()!=""&& user.getPassword().equals(md5_password)){
                Map<String,Object> userResult = userMapper.selectUserMap(user.getId());
                if(userResult.get("department_id")!=null){
                    String dname =
                            departmentMapper.selectByPrimaryKey((Integer)userResult.get("department_id")).getDname();
                    userResult.put("department_name",dname);
                }
                if(userResult.get("group_id")!=null){
                    String gname = groupMapper.selectByPrimaryKey(
                            (Integer)userResult.get("group_id")).getName();
                    userResult.put("group_name",gname);
                }

                //token内容
                JSONObject tokenData = new JSONObject();
                tokenData.put("user_id",userResult.get("user_id"));
                tokenData.put("rong_user_id",userResult.get("rong_user_id"));
                tokenData.put("role_id",userResult.get("role_id"));
                tokenData.put("role_name",userResult.get("role_name"));
                tokenData.put("company_id",userResult.get("company_id"));
                tokenData.put("department_id",userResult.get("department_id"));
                tokenData.put("group_id",userResult.get("group_id"));
                tokenData.put("portrait",userResult.get("portrait"));
                tokenData.put("user_name",userResult.get("user_name"));
                tokenData.put("cellphone",userResult.get("cellphone"));
                tokenData.put("notice_rong_group_id",userResult.get("notice_rong_group_id"));
                tokenData.put("company_rong_group_id",userResult.get("company_rong_group_id"));
                tokenData.put("rong_token",userResult.get("rong_token"));
                tokenData.put("department_name",userResult.get("department_name"));
                tokenData.put("group_name",userResult.get("group_name"));
                tokenData.put("role_name",userResult.get("role_name"));
                tokenData.put("company_name",userResult.get("company_name"));

                //为了调用center接口 设备管理模块
                tokenData.put("project_id",userResult.get("center_project_id"));   //center库 项目id
                //tokenData.put("center_app_user_id",userResult.get("center_app_user_id"));   //center库 app user 表
                tokenData.put("uid",userResult.get("center_project_user_id"));   //center库 user表
                tokenData.put("center_role_id",userResult.get("center_role_id")); //center库 role表

                String token = JwtToken.getWebToken(tokenData);
                userResult.put("token",token);
                return new Result1(userResult);
            }else{
                return new Result1("1007","账号或密码错误");
            }
        }
    }

    @Override
    public Result1 getUserByLogin(Map<String,Object> params) {
        String token = (String)params.get("token");
        Map<String,Object> user = getUserByWebToken(token);
        user.replace("portrait",UrlConfig.DOWNURL + user.get("portrait"));
//        user.replace("portrait",System.getProperty("user.dir").replaceAll("\\\\","/")+
//        		"/resource/"+user.get("portrait"));
        return new Result1(user);
    }

    @Override
    public Result1 getTip(Map<String, Object> params) {
        Integer rid = roleMapper.selectRidByUid(getUserIdByToken((String)params.get("token")));
       /* String url1 = Thread.currentThread().getContextClassLoader().getResource("").getPath();
        System.out.println("url1:"+url1);
        String url2 = url1.replaceAll("\\\\", "/");
        System.out.println("url2:"+url2);
        String url3 = DOWNURL.replaceAll("\\\\","/");
        System.out.println("url3:"+url3);*/
        int tid = Integer.parseInt(params.get("key").toString());
        Map<String,Object> tipResult = tipMapper.selectTipById(tid);
        if(tipResult==null){
            return new Result1("1005","二维码验证失败", rid);
        }
        List<Switchingroom> switchingrooms = switchingroomMapper.selectByTid(tid);
        /*if(switchingrooms!=null&&switchingrooms.size()>0){
            for(Switchingroom switchingroom:switchingrooms){
                if(switchingroom.getUrl()!=null){
                	if (switchingroom.getUrl().startsWith("http")) {
                		switchingroom.setUrl(switchingroom.getUrl());
					}else{
						//不是绝对路径, 就拼接图片url
						switchingroom.setUrl(UrlConfig.DOWNURL + switchingroom.getUrl());
					}
                    switchingroom.setUrl(DOWNURL.replaceAll("\\\\","/")+
                            "resource/"+switchingroom.getUrl());
                }
            }
        }*/
        tipResult.put("switchingroom",switchingrooms);
        return new Result1(tipResult,rid);
    }
}
