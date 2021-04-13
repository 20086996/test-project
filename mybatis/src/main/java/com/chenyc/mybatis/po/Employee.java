package com.chenyc.mybatis.po;

import org.apache.ibatis.type.Alias;

@Alias("employee")
public class Employee {
	
	private Integer id;
	private String lastName;
	private String email;
	private String gender;

	//getter and setter and toString()
}
