package com.ai.dbselect.datasource;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import com.ai.dbselect.balance.BalanceAlg;
import com.google.gson.Gson;

/**
 *
 <!-- 配置动态数据源 -->
	<bean id="dynamicDataSource" class="com.ai.dbselect.datasource.DynamicDataSource">
		<!-- 通过key-value的形式来关联数据源 -->
		<property name="targetDataSources">
			<map key-type="java.lang.String">
				<entry value-ref="dataSource" key="ds"></entry>
			</map>
		</property>
		<property name="defaultTargetDataSource" ref="dataSource" />
	</bean>
 * @author wu
 *
 */
public class DynamicDataSource extends AbstractRoutingDataSource{
	private static final Logger _LOG=LoggerFactory.getLogger(DynamicDataSource.class);
	private static final ThreadLocal<String> DSKEY=new ThreadLocal<String>();
	private String defaultDS="datasource";
	
	/*
	 * 负载均衡算法，支持如下四种算法
	 * rr,rrw,random,randomw
	 */
	private static String alg="rr";
	private static String writeDs="";
	private static List<String> readDs=null;
	static {
		//读取配置文件
		_LOG.info("###动态数据源加载配置###");
		Gson gson=new Gson();
		Reader reader=new InputStreamReader(DynamicDataSource.class.getResourceAsStream("/dbselection.ini"));
		HashMap<String,Object> conf=gson.fromJson(reader, HashMap.class);
		
		alg=(String)conf.get("alg");
		_LOG.debug("加载参数alg:"+alg);
		writeDs=(String)conf.get("writeDs");
		_LOG.debug("加载参数writeDs:"+writeDs);
		if(Pattern.matches(".*w$", alg)){
			_LOG.trace("加权类算法，加载readDs配置");
			readDs=new ArrayList<String>();
			List<Map> list=(List<Map>)conf.get("readDs");
			for(int i=0; i<list.size(); i++){
				Map db=list.get(i);
				double weight=(Double)db.get("weight");
				for(int k=0; k<weight; k++){
					readDs.add((String)db.get("ds"));
				}
			}
		}else{
			_LOG.trace("非加权类算法，加载readDs列表");
			readDs=(List<String>)conf.get("readDs");
		}
		_LOG.debug("###数据源读库列表(ds list): "+Arrays.toString(readDs.toArray()));
		_LOG.info("###动态数据源配置加载完毕列###");
	}
	
	public void setDefaultDS(String defaultDS) {
		this.defaultDS = defaultDS;
	}

	@Override
	protected Object determineCurrentLookupKey() {
		String ds=DSKEY.get();
		if(ds==null && "".equals(ds)){
			_LOG.debug("选择了数据(selected db): "+defaultDS);
			return defaultDS;
		}
		_LOG.debug("选择了数据(selected db): "+ds);
		return ds;
	}

	public static void selectWriteDS(){
		DynamicDataSource.DSKEY.set(writeDs);
	}
	
	public static void selectReadDS(){
		String ds="";
		if("rr".equals(alg) || "rrw".equals(alg)){
			//轮询算法和加权轮询算法
			ds=BalanceAlg.roundRobin(readDs);
			DynamicDataSource.DSKEY.set(ds);
		}else if("random".equals(alg) || "wrandom".equals(alg)){
			//随机算法和随机加权算法
			ds=BalanceAlg.random(readDs);
			DynamicDataSource.DSKEY.set(ds);
		}
		_LOG.debug("负载均衡计算读库："+ds);
	}
}
