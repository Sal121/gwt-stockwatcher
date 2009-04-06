package com.jsvest.crm.acl.server;

import static org.junit.Assert.*;

import java.sql.SQLException;

import org.junit.Test;

public class AclServiceImplTest {

	@Test
	public void testListUser() throws SQLException {
		new AclServiceImpl().listUser();
	}

	@Test
	public void testGetUser() throws SQLException {
		User user = new AclServiceImpl().getUser(1);
		assertNotNull(user);
		System.out.println(user);
	}

	@Test
	public void testInsertUser() throws SQLException {
		User user = new User();
		//user.setId(3);
		user.setName("bbc");
		user.setPasswordHash("fafrwe3232323223");
		user.setEmail("aaa@bbb.com");
		new AclServiceImpl().insertUser(user);
		System.out.println(user);
	}

}
