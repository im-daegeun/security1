package org.im.mapper;

import org.im.model.User;

public interface UserMapper {

	public void create(User user) throws Exception;
	public User read(long boardNo) throws Exception;

	/*
	public void update(User user) throws Exception;

	public void delete(long id) throws Exception;

	public List<User> list() throws Exception;
	*/
}
