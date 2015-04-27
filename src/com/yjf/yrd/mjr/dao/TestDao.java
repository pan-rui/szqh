package com.yjf.yrd.mjr.dao;

import com.ibatis.sqlmap.client.SqlMapClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Objects;

/**
 * Created by panrui on 2015/4/24.
 */
//@Repository
public class TestDao extends SqlMapClientDaoSupport {
/*    @Autowired
    private SqlMapClient sqlMapClient;*/
    public long getTest(Map<String, Object> params) {
        return (long)this.getSqlMapClientTemplate().queryForObject("RM-AMOUNT-STATISTICS-COUNTBYYEAR-TEST", params);
    }
}
