package com.chenyc.mybatis.dao;

import com.chenyc.mybatis.po.Employee;

public interface EmployeeMapper {
	
	public Employee getEmpById(Integer id);

}