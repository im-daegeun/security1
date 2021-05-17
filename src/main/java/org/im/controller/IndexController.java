package org.im.controller;

import java.util.Iterator;

import org.im.model.User;
import org.im.repository.UserRepository;
import org.im.service.PrincipalDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class IndexController {
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@GetMapping({ "", "/" })
	@ResponseBody 
	// Request시 authentication 객체를 통해 Session 정보 가져오기
	public String index(Authentication authentication) {
		
		if (authentication != null) {
			System.out.println("타입정보 : " + authentication.getClass());
			
			// 세션 정보 객체 반환
			WebAuthenticationDetails web = (WebAuthenticationDetails)authentication.getDetails();
			
			System.out.println("세션ID : " + web.getSessionId());

			// UsernamePasswordAuthenticationToken에 넣었던 UserDetails 객체 반환
			UserDetails userDetails = (UserDetails) authentication.getPrincipal();
			System.out.println("ID정보 : " + userDetails.getUsername());
		}
		
		// SecurityContextHolder를 통해 가져오기
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if(principal != null) {
			if("anonymousUser".equals(principal)) {
				// 
				System.out.println(" >> user ID : anonymousUser!!" );
			} else {
				PrincipalDetails  userDetails = (PrincipalDetails)principal;

				String username = userDetails.getUsername();  
				String password = userDetails.getPassword();
				String userId   = userDetails.getUserid();
				
				System.out.println(" >> user ID :" + userId);
				System.out.println(" >> user Nm :" + username);
				System.out.println(" >> user Pw :" + password);
			}
		}
		
		
		return "인덱스 페이지입니다.";
	}

	@GetMapping("/user")
	public @ResponseBody String user(@AuthenticationPrincipal PrincipalDetails principal) {
		System.out.println("Principal : " + principal);
		// iterator 순차 출력 해보기
		Iterator<? extends GrantedAuthority> iter = principal.getAuthorities().iterator();
		while (iter.hasNext()) {
			GrantedAuthority auth = iter.next();
			System.out.println(auth.getAuthority());
		}

		return "유저 페이지입니다.";
	}

	@GetMapping("/admin")
	public @ResponseBody String admin() {
		return "어드민 페이지입니다.";
	}
	
	//@PostAuthorize("hasRole('ROLE_MANAGER')")
	//@PreAuthorize("hasRole('ROLE_MANAGER')")
	@Secured("ROLE_MANAGER")
	@GetMapping("/manager")
	public @ResponseBody String manager() {
		return "매니저 페이지입니다.";
	}


	@PostMapping("/joinProc")
	public String joinProc(User user) {
		System.out.println("회원가입 진행 : " + user);
		String rawPassword = user.getPassword();
		String encPassword = bCryptPasswordEncoder.encode(rawPassword);
		user.setPassword(encPassword);
		user.setRole("ROLE_USER");
		userRepository.save(user);
		return "redirect:/";
	}
}
