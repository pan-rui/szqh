package com.yjf.yrd.mjr.dao.impl;

import com.yjf.yrd.mjr.dao.IRepayManageDao;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import java.util.List;
import java.util.Map;

/**
 * Created by panrui on 2015/4/27.
 */
public class RepayManageDaonImpl extends SqlMapClientDaoSupport implements IRepayManageDao{
    @Override
    public List<Map<String, Object>> findRepayPlanForPage(Map<String, Object> param) {
        return this.getSqlMapClientTemplate().queryForList("FIND-REPAY-PLAN",param);
    }

    @Override
    public List<Map<String, Object>> findRepayPlanTotalForPage(Map<String, Object> param) {
        return this.getSqlMapClientTemplate().queryForList("FIND-REPAY-PLAN",param);
    }
}
