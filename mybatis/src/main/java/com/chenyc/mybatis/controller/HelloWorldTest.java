package com.chenyc.mybatis.controller;


import com.chenyc.mybatis.dao.EmployeeMapper;
import com.chenyc.mybatis.po.Employee;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author chenyc
 * @create 2021-04-13 11:45
 */
public class HelloWorldTest {


    public SqlSessionFactory getSqlSessionFactory() throws IOException {
        String resource = "mybatis-config.xml";
        InputStream inputStream = Resources.getResourceAsStream(resource);
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        return sqlSessionFactory;
    }

    @Test
    public void test1() throws IOException {
        SqlSessionFactory sqlSessionFactory = getSqlSessionFactory();
        SqlSession sqlSession = sqlSessionFactory.openSession();
        Employee po = sqlSession.selectOne("abc.getEmpById", 1);
        System.out.println(po);
    }


    @Test
    public void  test2() throws IOException{
        SqlSessionFactory sqlSessionFactory = getSqlSessionFactory();
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            EmployeeMapper mapper = sqlSession.getMapper(EmployeeMapper.class);
            Employee po = mapper.getEmpById(1);
            System.out.println(mapper.getClass());
            System.out.println(po);
        } finally {
            sqlSession.close();
        }
    }


}
