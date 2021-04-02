package com.sl.ylyy.common.Interceptor;

import com.sl.ylyy.common.config.UrlConfig;
import com.sl.ylyy.manager.service.impl.CommonDataServiceImpl;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 */
public class SessionInterceptor implements HandlerInterceptor
{
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object o) throws Exception {
        //登录不做拦截
        if(request.getRequestURI().equals("/ylyy/login") || request.getRequestURI().equals("/ylyy/login")||
        request.getRequestURI().startsWith("/gaoxin_app/api/v1"))
        {
            return true;
        }
        Cookie[] cookies = request.getCookies();
        String sessionID = "";
        if (cookies != null && cookies.length > 0) {
            for(int i=0;i<cookies.length;i++){
                if("gtgx_session".equals(cookies[i].getName())) {
                    sessionID = cookies[i].getValue();
                }
            }
        }
        if (sessionID == null || "".equals(sessionID)){
            if(UrlConfig.PROFILES_ACTIVE.equals("sale")){
                response.sendRedirect("http://appgt.ove-ipark.com/ylyy/login");
            }else{
                response.sendRedirect("/ylyy/login");
            }

            return false;
        }

        //验证session是否存在
        Map<String,Object> parmeMap=new HashMap<>();
        parmeMap.put("gtgx_session",sessionID);
        Map<String,Object> sessionMap=CommonDataServiceImpl.getSession(parmeMap);
        if (sessionMap==null){
            if(UrlConfig.PROFILES_ACTIVE.equals("sale")){
                response.sendRedirect("http://appgt.ove-ipark.com/ylyy/login");
            }else{
                response.sendRedirect("/ylyy/login");
            }
            return false;
        }else{
            request.getSession().setAttribute("activeAdmin",sessionMap);
            return true;
        }
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }
}


