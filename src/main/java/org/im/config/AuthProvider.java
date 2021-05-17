/**
 * git push test
 */
package org.im.config;

import org.im.service.PrincipalDetails;
import org.im.service.PrincipalDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import lombok.extern.java.Log;

@Log
@Component
public class AuthProvider implements AuthenticationProvider{

	@Autowired
	PrincipalDetailsService principalDetailsService;
	
	@Autowired
	BCryptPasswordEncoder pwEncoder;
	
	
	public Authentication authenticate(Authentication authentication)
			throws AuthenticationException {

		// 사용자 입력값 정보 조회 
		// config : username => getName
		//          password => getCredentials
		
		String userId = authentication.getName();
		String userPw = (String) authentication.getCredentials();

		System.out.println(" --> SEC : authenticate : user : "+ userId + ", pw:" +userPw); 
		// UserDetailsService를 통해 user정보 조회 
		//  - userId : 키여야 함
		// * UserDetailsService 를 사용하지 않고, mybatis를 통해  사용자 정보 가져오기로 바꿀 수 있음.
		PrincipalDetails userDetails= (PrincipalDetails) principalDetailsService.loadUserByUsername(userId);
				
		
		// 인증 진행 
		if (userDetails == null || 
			!userId.equals(userDetails.getUserid()) || 
			!pwEncoder.matches(userPw, userDetails.getPassword())) {
			
			// DB에 정보가 없는 경우 예외 발생 (아이디/패스워드 잘못됐을 때와 동일한 것이 좋음)
			// ID 및 PW 체크해서 안맞을 경우 (matches를 이용한 암호화 체크를 해야함)
			
			throw new BadCredentialsException(userId);
			
		} else if (!userDetails.isAccountNonLocked()) {
			
			throw new LockedException(userId);
			

		} else if (!userDetails.isEnabled()) {
			// 비활성화된 계정일 경우
			throw new DisabledException(userId);

		
		} else if (!userDetails.isAccountNonExpired()) {
			// 만료된 계정일 경우
			throw new AccountExpiredException(userId);

		} else if (!userDetails.isCredentialsNonExpired()) {
			// 비밀번호가 만료된 경우
			throw new CredentialsExpiredException(userId);
		}

		// 다 썼으면 패스워드 정보는 지워줌 (객체를 계속 사용해야 하므로)
		// userDetails.setPassword(null);
		
		/* 최종 리턴 시킬 새로만든 Authentication 객체 */
		Authentication newAuth = new UsernamePasswordAuthenticationToken(
				userDetails, null, userDetails.getAuthorities());

		return newAuth;		
	}


	@Override
	public boolean supports(Class<?> authentication) {
		// TODO Auto-generated method stub
		return authentication.equals(UsernamePasswordAuthenticationToken.class);
	}
	
}
