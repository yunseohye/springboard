package com.util.sqlMap;

import java.io.Reader;

import com.ibatis.common.resources.Resources;
import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.client.SqlMapClientBuilder;

public class sqlMapConfig {
	
	private static final SqlMapClient sqlMap;
	
	static {
		
		try {
			
			String source="com/util/sqlMap/sqlMapConfig.xml";
			Reader reader = Resources.getResourceAsReader(source);
			
			sqlMap = SqlMapClientBuilder.buildSqlMapClient(reader);
			
		} catch (Exception e) {
			
			e.printStackTrace();
			//사용자 에러를 발생시키는 것임
			throw new RuntimeException("Error");
		}
		
	}
	
	public static SqlMapClient getSqlMapInstance() {
		return sqlMap;
	}

}
