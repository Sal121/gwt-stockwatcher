package com.jsvest.crm.acl.server;

import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;
import java.util.List;

import com.ibatis.common.resources.Resources;
import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.client.SqlMapClientBuilder;

public class AclServiceImpl {
	private static SqlMapClient sqlMapper;

	static {
		try {
			Reader reader = Resources
					.getResourceAsReader("com/jsvest/crm/acl/server/SqlMapConfig.xml");
			sqlMapper = SqlMapClientBuilder.buildSqlMapClient(reader);
			reader.close();
		} catch (IOException e) {
			// Fail fast.
			throw new RuntimeException(
					"Something bad happened while building the SqlMapClient instance."
							+ e, e);
		}
	}


	public void listUser() throws SQLException {
		List result = sqlMapper.queryForList("selectAllUsers");
		System.out.println(result.size());
		System.out.println(result.get(0));
	}

	public User getUser(int id) throws SQLException {
		return (User)sqlMapper.queryForObject("selectUserById", id);
	}
	
	public void insertUser(User user) throws SQLException {
		sqlMapper.insert("insertUser", user);
	}
}
