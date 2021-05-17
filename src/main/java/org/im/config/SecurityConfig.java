package org.im.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration // IoC 빈(bean)을 등록
@EnableWebSecurity // 필터 체인 관리 시작 어노테이션
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true) // 특정 주소 접근시 권한 및 인증을 위한 어노테이션 활성화
public class SecurityConfig  extends WebSecurityConfigurerAdapter {
		
	@Autowired
	AuthProvider authProvider;
		
	@Bean
	public BCryptPasswordEncoder encodePwd() {
		return new BCryptPasswordEncoder();
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		
		http.csrf().disable();
		http.authorizeRequests()
			.antMatchers("/user/**").authenticated()
			//.antMatchers("/admin/**").access("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
			//.antMatchers("/admin/**").access("hasRole('ROLE_ADMIN') and hasRole('ROLE_USER')")
			.antMatchers("/admin/**").access("hasRole('ROLE_ADMIN')")
			.anyRequest().permitAll()
		.and()
			.formLogin()
			.loginPage("/login")
			.loginProcessingUrl("/loginAction")
			.defaultSuccessUrl("/")
			.successHandler(new LoginSuccessHandler("/"))
			.failureUrl("/loginFail")
		    .usernameParameter("userid")
            .passwordParameter("password")
            .permitAll()
         .and()
         	.logout()
         	.logoutUrl("/logout")
         	.logoutSuccessUrl("/")
         	.invalidateHttpSession(true)        // HttpSession의 정보를 무효화
         	.deleteCookies("JSESSIONID")
         	.permitAll()
         .and()
         	.authenticationProvider(authProvider);
	}	
}