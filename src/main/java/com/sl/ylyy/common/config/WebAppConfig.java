package com.sl.ylyy.common.config;
import com.sl.ylyy.common.Interceptor.TokenInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import com.sl.ylyy.common.Interceptor.SessionInterceptor;

@Configuration
public class WebAppConfig extends WebMvcConfigurerAdapter {

	@Bean
	public TokenInterceptor tokenInterceptor() {
		return new TokenInterceptor();
	}
	/**
	 * 配置拦截器，阻止普通用户和游客进入管理员界面，阻止游客进行个人信息有关操作
	 */
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		
		registry.addInterceptor(new SessionInterceptor())
			.addPathPatterns("/ylyy/**")
			.addPathPatterns("/gtgx/**")
			.addPathPatterns("/manage/**")
			.addPathPatterns("/supervision/**")
			.excludePathPatterns("/gtgx/getQrCodeUrl")
			.excludePathPatterns("/gtgx/applogin")
			.excludePathPatterns("/gtgx/recordRongUserGroupData")
			.excludePathPatterns("/gtgx/recordRongUserToken")
			.excludePathPatterns("/gtgx/recordRongGroup")
			.excludePathPatterns("/gtgx/checkNoticeAuth")
			.excludePathPatterns("/gaoxin_app/api/v1/**")
			.excludePathPatterns("/gtgx/getUploadConfig")
			;
		
		registry.addInterceptor(tokenInterceptor())
		.addPathPatterns("/gaoxin_app/api/v1/**")
		.addPathPatterns("/app/supervision/**")
		.addPathPatterns("/app/manage/**")
		.addPathPatterns("/app/qiniu/singleUpload")
		.excludePathPatterns("/gaoxin_app/api/v1/user/signin")
		.excludePathPatterns("/gaoxin_app/api/v1/app/update")
		;
	}

}
