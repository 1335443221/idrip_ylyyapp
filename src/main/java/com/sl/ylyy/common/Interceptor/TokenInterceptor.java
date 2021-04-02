package com.sl.ylyy.common.Interceptor;

import com.sl.ylyy.app_1.dao.UserMapper;
import com.sl.ylyy.common.utils.JwtToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import com.sl.ylyy.common.utils.CodeMsg;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Component;

import java.io.PrintWriter;
import java.util.Map;
	 
	/**
	 * 拦截器
	 * @ 用来判断用户的
	 *1. 当preHandle方法返回false时，从当前拦截器往回执行所有拦截器的afterCompletion方法，再退出拦截器链。也就是说，请求不继续往下传了，直接沿着来的链往回跑。
	 2.当preHandle方法全为true时，执行下一个拦截器,直到所有拦截器执行完。再运行被拦截的Controller。然后进入拦截器链，运
	 行所有拦截器的postHandle方法,完后从最后一个拦截器往回执行所有拦截器的afterCompletion方法.
	 */
	 
	//@component （把普通pojo实例化到spring容器中，相当于配置文件中的
	@Component
	public class TokenInterceptor implements HandlerInterceptor {

		@Autowired
		private UserMapper userMapper;

	    /**
	     * 在请求处理之前进行调用（Controller方法调用之前）
	     *
	     * @param request
	     * @param response
	     * @param handler
	     * @return
	     * @throws Exception
	     */
	    @Override
	    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
	    	response.setCharacterEncoding("UTF-8");  
	    	response.setContentType("application/json; charset=utf-8");
	    	String token = request.getParameter("token");
	        PrintWriter out = null ;
			JSONObject res = new JSONObject();
	        if(token==null){
				try {
						HttpServletRequest request2 = (HttpServletRequest) request;
						MyRequestWrapper requestWrapper = new MyRequestWrapper(request2);
						String body = requestWrapper.getBody();
						JSONObject j=JSONObject.parseObject(body);
						request.setAttribute("json", body);
						token=String.valueOf(j.get("token"));
					} catch (Exception e) {
						res.put("code", 1001);
						res.put("msg", "接口认证失败");
						out = response.getWriter();
						out.append(res.toString());
						return false;
				}
				if(token==null||token.equals("")){
					res.put("code", 1001);
					res.put("msg", "接口认证失败");
					out = response.getWriter();
					out.append(res.toString());
					return false;
				}else{
						res.put("code", 1002);
						res.put("msg", "授权过期");
						Map<String, Object> usermap= JwtToken.parseWebToken(token);
						if(usermap==null){
							out = response.getWriter();
							out.append(res.toString());
							return false;
						}else{
							Map<String, Object> user=userMapper.selectUserMap(Integer.parseInt(String.valueOf(usermap.get("user_id"))));
							if(!String.valueOf(user.get("company_id")).equals(String.valueOf(usermap.get("company_id")))||
									!String.valueOf(user.get("department_id")).equals(String.valueOf(usermap.get("department_id")))||
									!String.valueOf(user.get("group_id")).equals(String.valueOf(usermap.get("group_id")))||
									!String.valueOf(user.get("role_id")).equals(String.valueOf(usermap.get("role_id")))){
								out = response.getWriter();
								out.append(res.toString());
								return false;
							}
							if(!String.valueOf(user.get("user_name")).equals(String.valueOf(usermap.get("user_name")))||
									!String.valueOf(user.get("portrait")).equals(String.valueOf(usermap.get("portrait")))||
									!String.valueOf(user.get("department_name")).equals(String.valueOf(usermap.get("department_name")))||
									!String.valueOf(user.get("role_name")).equals(String.valueOf(usermap.get("role_name")))||
									!String.valueOf(user.get("group_name")).equals(String.valueOf(usermap.get("group_name")))||
									!String.valueOf(user.get("company_name")).equals(String.valueOf(usermap.get("company_name")))){
								res.put("code", 1027);
								res.put("msg", "用户信息过期");
								out = response.getWriter();
								out.append(res.toString());
								return false;
							}
						}
				}
	        }else{
					res.put("code", 1002);
					res.put("msg", "授权过期");
					Map<String, Object> usermap= JwtToken.parseWebToken(token);
					if(usermap==null){
						out = response.getWriter();
						out.append(res.toString());
						return false;
					}else{
						Map<String, Object> user=userMapper.selectUserMap(Integer.parseInt(String.valueOf(usermap.get("user_id"))));
						if(!String.valueOf(user.get("company_id")).equals(String.valueOf(usermap.get("company_id")))||
							!String.valueOf(user.get("department_id")).equals(String.valueOf(usermap.get("department_id")))||
							!String.valueOf(user.get("group_id")).equals(String.valueOf(usermap.get("group_id")))||
							!String.valueOf(user.get("role_id")).equals(String.valueOf(usermap.get("role_id")))){
							out = response.getWriter();
							out.append(res.toString());
							return false;
						}
						if(!String.valueOf(user.get("user_name")).equals(String.valueOf(usermap.get("user_name")))||
							!String.valueOf(user.get("portrait")).equals(String.valueOf(usermap.get("portrait")))||
							!String.valueOf(user.get("department_name")).equals(String.valueOf(usermap.get("department_name")))||
							!String.valueOf(user.get("group_name")).equals(String.valueOf(usermap.get("group_name")))||
								!String.valueOf(user.get("role_name")).equals(String.valueOf(usermap.get("role_name")))||
							!String.valueOf(user.get("company_name")).equals(String.valueOf(usermap.get("company_name")))){
							res.put("code", 1027);
							res.put("msg", "用户信息过期");
							out = response.getWriter();
							out.append(res.toString());
							return false;
						}
					}
	        }
	        return true;
	    }
	    private void error(CodeMsg aUTH_EXPIRES) {
			// TODO Auto-generated method stub

		}
		/**
	     * 请求处理之后进行调用，但是在视图被渲染之前（Controller方法调用之后）
	     * @param modelAndView
	     * @throws Exception
	     */
	    @Override
	    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
	      //  System.out.println("拦截器postHandle方法"+modelAndView);
//	        //就是为了能够在渲染之前所有的freemaker模板能够访问这个对象user，就是在所有的controller渲染之前将这个user加进去
//	        if(modelAndView != null){
//	            //这个其实就和model.addAttribute一样的功能，就是把这个变量与前端视图进行交互 //就是与header.html页面的user对应
//	            //modelAndView.addObject("user",hostHolder.getUserInfo());
//	        }
	    }
	 
	    @Override
	    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
	        //hostHolder.clear();   //当执行完成之后呢需要将变量清空
	    }
	 
	}
	


