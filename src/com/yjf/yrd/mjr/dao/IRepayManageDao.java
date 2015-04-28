package com.yjf.yrd.mjr.dao;

import java.util.List;
import java.util.Map;

/**
 * Created by panrui on 2015/4/27.
 */
public interface IRepayManageDao {

    List<Map<String, Object>> findRepayPlanForPage(Map<String, Object> param);
    List<Map<String, Object>> findRepayPlanTotalForPage(Map<String, Object> param);

}
