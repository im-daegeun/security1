package org.im.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.im.model.User;
import org.im.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import lombok.extern.java.Log;

@Log
@Controller
public class LoginController {
	@Autowired
	private UserService service;

	@GetMapping("/login")
	public String login() {
		return "login";
	}

	@GetMapping("/join")
	public String join() {
		return "join";
	}
	
    @GetMapping("/loginFail")
    public String loginFail(){
        return "loginFail";
    }
    
    @PostMapping("/saveUser")
    public String saveUser(User user) throws Exception{
    	System.out.println(" -- > username :" + user.getUsername());
    	service.register(user);
        return "loginFail";
    }
    
	
    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response
            , @AuthenticationPrincipal User user){
    	System.out.println("--> logout in");
    	
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }

        // (remember-me 토큰 삭제) persistent_logins 테이블에 저장되어있는 Token 삭제.
        // memberMapper.deletePersistentLogins(userCustom);
        return "redirect:/";
    }
	
}
