package com.yjf.yrd.mjr.service;

import com.yjf.yrd.base.BaseAutowiredDAOService;
import com.yjf.yrd.mjr.dao.impl.RepayManageDaonImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Created by panrui on 2015/4/27.
 */
@Service
public class RepayManageService extends BaseAutowiredDAOService{
    @Autowired
    private RepayManageDaonImpl repayManageDao;
@Transactional(rollbackFor = Exception.class,propagation = Propagation.REQUIRED)
    public List<Map<String, Object>> findRepayPlanByCondition(Map<String,Object> params) {
        List<Map<String, Object>> result=repayManageDao.findRepayPlanForPage(params);
        return result == null ? new ArrayList<Map<String, Object>>() : result;
    }

    @Transactional(rollbackFor = Exception.class,propagation = Propagation.REQUIRED)
    public List<Map<String, Object>> findRepayPlanByConditionTotal(Map<String,Object> params) {
        List<Map<String, Object>> result=repayManageDao.findRepayPlanTotalForPage(params);
        return result == null ? new ArrayList<Map<String, Object>>() : result;
    }
}
