package org.im.service.user;

import org.im.model.User;


public interface UserService {

	public void register(User user) throws Exception;

	public User read(long id) throws Exception;
	
}
