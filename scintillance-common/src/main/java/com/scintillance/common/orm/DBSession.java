package com.scintillance.common.orm;

import com.scintillance.common.util.SpringUtil;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by Administrator on 2017/10/29 0029.
 */
@Component
public class DBSession {

    private SqlSessionFactory sqlSessionFactory;

    public SqlSession getSqlSession() {
        return getSqlSessionFactory().openSession(true);
    }

    private SqlSessionFactory getSqlSessionFactory() {
        if(sqlSessionFactory == null) {
            synchronized (this) {
                if(sqlSessionFactory == null) {
                    sqlSessionFactory = SpringUtil.getBean(SqlSessionFactory.class);
                }
            }
        }
        return sqlSessionFactory;
    }



    public <T> T selectOne(String statementId) {
        return selectOne(statementId,null);
    }

    public <T> T selectOne(String statementId, Object parameterObject) {
        return getSqlSession().selectOne(statementId,parameterObject);
    }

    public <E> List<E> selectList(String statementId) {
        return selectList(statementId,null);
    }

    public <E> List<E> selectList(String statementId, Object parameterObject) {
        return getSqlSession().selectList(statementId,parameterObject);
    }

    public int insert(String statementId) {
        return insert(statementId,null);
    }

    public int insert(String statementId, Object parameterObject) {
        return getSqlSession().insert(statementId,parameterObject);
    }

    public int update(String statementId) {
        return update(statementId,null);
    }

    public int update(String statementId, Object parameterObject) {
        return getSqlSession().update(statementId,parameterObject);
    }

    public int delete(String statementId) {
        return delete(statementId,null);
    }

    public int delete(String statementId, Object parameterObject) {
        return getSqlSession().delete(statementId,parameterObject);
    }
}
