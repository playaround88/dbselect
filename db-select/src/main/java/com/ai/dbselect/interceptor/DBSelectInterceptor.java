package com.ai.dbselect.interceptor;

import java.util.Properties;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ai.dbselect.common.SqlTypeUtil;
import com.ai.dbselect.datasource.DynamicDataSource;

@Intercepts({
		@Signature(type = Executor.class, method = "update", args = {
				MappedStatement.class, Object.class }),
		@Signature(type = Executor.class, method = "query", args = {
				MappedStatement.class, Object.class, RowBounds.class,
				ResultHandler.class }) })
public class DBSelectInterceptor implements Interceptor{
	private static final Logger _LOG=LoggerFactory.getLogger(DBSelectInterceptor.class);
	private Properties properties;

	public Object intercept(Invocation invocation) throws Throwable {
		MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
		Object parameter = null;
		if (invocation.getArgs().length > 1) {
			parameter = invocation.getArgs()[1];
		}
		final BoundSql boundSql = mappedStatement.getBoundSql(parameter);
		
		//拦截到的prepared sql
		String psql=boundSql.getSql();
		String sqlType=SqlTypeUtil.type(psql);
		_LOG.debug("intercept sql type: {}, sql: {}",sqlType,psql);
		
		if("select".equals(sqlType)){
			_LOG.trace("is select sql, select read ds!");
			DynamicDataSource.selectReadDS();
		}else{
			_LOG.trace("is update sql, select write ds!");
			DynamicDataSource.selectWriteDS();
		}
		
		return invocation.proceed();
	}

	public Object plugin(Object target) {
		return Plugin.wrap(target, this);
	}

	public void setProperties(Properties properties0) {
		this.properties = properties0;
	}
}
