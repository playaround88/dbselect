package com.ai.dbselectTest;

import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("userService")
public class UserService {
	private static Logger log=LoggerFactory.getLogger(UserService.class);
	private SqlSession sqlSession;
	@Autowired
	public void setSqlSession(SqlSession sqlSession) {
		this.sqlSession = sqlSession;
	}

	public long insert(User user){
		log.debug("插入一个新用户,"+user.toString());
		sqlSession.insert("user.insert", user);
		return user.getId();
	}

	public int del(long id){
		log.debug("删除一个用户："+id);
		return sqlSession.delete("user.del", id);
	}
	
	public int update(User user){
		log.debug("更新一个用户:"+user.toString());
		return sqlSession.update("user.update", user);
	}
	
	public User selectById(long id){
		log.debug("查询一个用户："+id);
		return sqlSession.selectOne("user.selectById", id);
	}
	
}
