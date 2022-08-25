package com.util.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface CommonDAO {

	//데이터 추가
	public void insertData(String id, Object value) throws SQLException;
	
	//데이터 수정 (종류가 2가지)
	public int updateData(String id, Object value) throws SQLException;
	public int updateData(String id, Map<String, Object> map) throws SQLException;

	//데이터 삭제 (종류가 3가지)
	public int deleteData(String id, Object value) throws SQLException;
	public int deleteData(String id, Map<String, Object> map) throws SQLException;
	public int deleteAllData(String id) throws SQLException;
	
	//데이터 1개 가져오기(수정창에 데이터를 띄우는것)
	public Object getReadData(String id);
	public Object getReadData(String id, Object value);
	public Object getReadData(String id, Map<String, Object> map);
	
	//숫자형태의 데이터를 1개 가져오기 (num의 최대값을 가져올때)
	public int getIntValue(String id);
	public int getIntValue(String id, Object value);
	public int getIntValue(String id, Map<String, Object> map);
	
	//전체 데이터를 가져오기
	public List<Object> getListData(String id);
	public List<Object> getListData(String id, Object value);
	public List<Object> getListData(String id, Map<String, Object> map);
	
	
	
}
