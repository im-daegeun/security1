package org.im.service.user;

import org.im.mapper.UserMapper;
import org.im.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserMapper mapper;
	
	@Autowired
	private BCryptPasswordEncoder pwEncoder;	

	@Override
	public void register(User user) throws Exception {
		user.setPassword(pwEncoder.encode(user.getPassword()));
		mapper.create(user);
	}

	@Override
	public User read(long id) throws Exception {
		return mapper.read(id);
	}
	
}

