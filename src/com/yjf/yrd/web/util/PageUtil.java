package com.yjf.yrd.web.util;

import com.yjf.yrd.page.Page;
import com.yjf.yrd.ws.service.query.result.QueryBaseBatchResult;

public class PageUtil {
	public static <T> Page<T> getCovertPage(QueryBaseBatchResult<T> batchResult) {
		long start = (batchResult.getPageNumber() - 1) * batchResult.getPageSize();
		Page<T> newPage = new Page<T>(start, batchResult.getTotalCount(),
			(int) batchResult.getPageSize(), batchResult.getPageList());
		return newPage;
	}
}
