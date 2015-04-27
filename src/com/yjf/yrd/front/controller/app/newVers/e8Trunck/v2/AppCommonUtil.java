package com.yjf.yrd.front.controller.app.newVers.e8Trunck.v2;

import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpSession;

import org.springframework.cglib.beans.BeanMap;

import com.yjf.common.lang.util.DateUtil;
import com.yjf.common.lang.util.StringUtil;
import com.yjf.common.lang.util.money.Money;
import com.yjf.yrd.integration.web.server.impl.YjfEzmoneyPayPassUrlOrder;
import com.yjf.yrd.session.SessionLocal;
import com.yjf.yrd.session.SessionLocalManager;
import com.yjf.yrd.util.AppConstantsUtil;
import com.yjf.yrd.util.MD5Util;

/**
 * 
 * 
 * @Filename AppCommontil.java
 * 
 * @Description
 * 
 * @Version 1.0
 * 
 * @Author zhaohaibing
 * 
 * @Email abing@yiji.com
 * 
 * @Date: 2015-1-5
 * 
 * 
 */
public class AppCommonUtil {
	private final static String addMDString = "dsf2IHU158JI97bv6%^h";
	
	public static String getMD5(HttpSession session, String userBaseId) {
		String SMD5String = null;
		String token = UUID.randomUUID().toString();
		if (StringUtil.isNotEmpty(userBaseId)) {
			SMD5String = MD5Util.getMD5_32(addMDString + userBaseId + token);
			session.setAttribute("SMD5String", SMD5String);
		}
		return SMD5String;
		
	}
	
	public static boolean checkMD5(HttpSession session, String CMD5String) {
		boolean result = false;
		String userBaseId = SessionLocalManager.getSessionLocal().getUserBaseId();
		if (StringUtil.isNotEmpty(userBaseId) && StringUtil.isNotEmpty(CMD5String)) {
			String SMD5String = (String) session.getAttribute("SMD5String");
			if (StringUtil.isNotEmpty(SMD5String) && CMD5String.equals(SMD5String)) {
				result = true;
			}
			
		}
		return result;
		
	}
	
	/**
	 * 将查询的结果装入Map<String,String>
	 * 
	 * */
	public static Map<String, String> getResMap(Object obj) {
		Map<String, String> resMap = new HashMap<String, String>();
		@SuppressWarnings("unchecked")
		Map<String, String> map0 = BeanMap.create(obj);
		Object sMap[] = map0.keySet().toArray();
		for (int i = 0; i < map0.size(); i++) {
			String key = (String) sMap[i];
			Object value = map0.get(sMap[i]);
			if (value != null) {
				String class0 = String.valueOf(value.getClass());
				if (class0.indexOf("Date") > -1) {
					resMap.put(key, DateUtil.simpleFormat((Date) value));
				} else if (class0.indexOf("Money") > -1) {
					resMap.put(key, ((Money) value).toStandardString());
				} else {
					resMap.put(key, String.valueOf(value));
				}
				
			} else {
				resMap.put(key, "");
			}
			
		}
		return resMap;
		
	}
	
	/**
	 * 获取当前查询条件下的总页数
	 * 
	 * @param totalCount
	 * @param pageSize
	 * 
	 * @return totalPage
	 * */
	public static long getTotalPage(long totalCount, long pageSize) {
		long totalPage = 0;
		if (totalCount % pageSize == 0) {
			totalPage = totalCount / pageSize;
		} else {
			totalPage = totalCount / pageSize + 1;
		}
		
		return totalPage;
	}
	
	/**
	 * 判断是否登录
	 * 
	 * @return boolean
	 */
	public static boolean isLogin() {
		try {
			if (SessionLocalManager.getSessionLocal().getUserName().toString() != null) {
				return true;
			}
		} catch (NullPointerException e) {
			
		}
		return false;
	}
	
	/**
	 * 获取状态
	 * 
	 * 
	 * */
	public static String getAppStatus(int statues, double needMoey, boolean investAvlTimeReached,
										boolean limitTime) {
		String sta = "2";// 投资已满
		if (1 == statues && needMoey > 0 && investAvlTimeReached && limitTime) {
			sta = "1";// 正在投资
		} else if (!investAvlTimeReached) {
			sta = "3";// 未到时间
		} else if (1 == statues && needMoey > 0 && !limitTime) {
			sta = "6";// 已过投资时间
		} else if (7 == statues || 3 == statues) {
			sta = "4";// 交易完成
		} else if (8 == statues) {
			sta = "5";// 等待还款
		} else if (2 == statues) {
			sta = "7";// 已成立
		}
		return sta;
	}
	
	/**
	 * str 页面展示
	 * 
	 * @param str
	 * @param type mail /mobile/idCard
	 * */
	
	public static String viewStr(String str, String type) {
		if (StringUtil.isEmpty(str)) {
			return "";
		}
		if (StringUtil.isEmpty(type)) {
			return str;
		}
		if ("mail".equals(type) && str.length() > 4) {
			str = str.substring(0, 2) + "***" + str.subSequence(str.indexOf("@"), str.length());
		} else if ("mobile".equals(type) && str.length() == 11) {
			str = str.substring(0, 3) + "****" + str.substring(7, 11);
		} else if ("idCard".equals(type) && str.length() > 5) {
			str = str.substring(0, 4) + "**********"
					+ str.subSequence(str.length() - 4, str.length());
		}
		return str;
		
	}
	
	/**
	 * 生成app端验证支付密码用 Order
	 * 
	 * **/
	
	public static YjfEzmoneyPayPassUrlOrder getPayUrlOrder() {
		YjfEzmoneyPayPassUrlOrder ezmoneyPayPassUrlOrder = new YjfEzmoneyPayPassUrlOrder();
		SessionLocal sessionLocal = SessionLocalManager.getSessionLocal();
		ezmoneyPayPassUrlOrder.setUserId(sessionLocal.getAccountId());
		ezmoneyPayPassUrlOrder.setIsPhone("1");
		ezmoneyPayPassUrlOrder.setPlatformName(AppConstantsUtil.getYrdPrefixion());
		ezmoneyPayPassUrlOrder.setBtnColor(AppConstantsUtil.getYjfModifyPwdBtnColor());
		return ezmoneyPayPassUrlOrder;
	}
	
	/**
	 * 将返回数据中的 null 换成 ""
	 * */
	public static Map<String, String> cleanNull(Map<String, String> map) {
		Object sMap[] = map.keySet().toArray();
		for (int i = 0; i < map.size(); i++) {
			if (StringUtil.isEmpty(map.get(sMap[i])) || "null".equalsIgnoreCase(map.get(sMap[i]))) {
				map.put((String) sMap[i], "");
			}
		}
		return map;
	}
	
	/**
	 * APP用激活支付账号
	 * */
	
	public static String appAccountActiveUrl(String userId) {
		String yjfUrl = AppConstantsUtil.getYjfUrl();
		String url = yjfUrl + "/anon/app/register/paymentActivation.htm?btnColor="
						+ AppConstantsUtil.getYjfModifyPwdBtnColor() + "&userId=" + userId
						+ "&redirect=" + AppConstantsUtil.getHostHttpUrl() + "/app/login.htm";
		
		return url;
	}
	
	/**
	 * 解密
	 * */
	public static String decode(byte[] data) {
		int len = data.length;
		ByteArrayOutputStream buf = new ByteArrayOutputStream(len);
		int i = 0;
		int b1, b2, b3, b4;
		while (i < len) {
			do {
				b1 = base64DecodeChars[data[i++]];
			} while (i < len && b1 == -1);
			if (b1 == -1) {
				break;
			}
			do {
				b2 = base64DecodeChars[data[i++]];
			} while (i < len && b2 == -1);
			if (b2 == -1) {
				break;
			}
			buf.write(((b1 << 2) | ((b2 & 0x30) >>> 4)));
			do {
				b3 = data[i++];
				if (b3 == 61) {
					return buf.toString();
				}
				b3 = base64DecodeChars[b3];
			} while (i < len && b3 == -1);
			if (b3 == -1) {
				break;
			}
			buf.write((((b2 & 0x0f) << 4) | ((b3 & 0x3c) >>> 2)));
			do {
				b4 = data[i++];
				if (b4 == 61) {
					return buf.toString();
				}
				b4 = base64DecodeChars[b4];
			} while (i < len && b4 == -1);
			if (b4 == -1) {
				break;
			}
			buf.write((((b3 & 0x03) << 6) | b4));
		}
		return buf.toString();
	}
	
	private static byte[] base64DecodeChars = new byte[] { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
															-1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
															-1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
															-1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
															-1, -1, -1, 62, -1, -1, -1, 63, 52, 53,
															54, 55, 56, 57, 58, 59, 60, 61, -1, -1,
															-1, -1, -1, -1, -1, 0, 1, 2, 3, 4, 5,
															6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16,
															17, 18, 19, 20, 21, 22, 23, 24, 25, -1,
															-1, -1, -1, -1, -1, 26, 27, 28, 29, 30,
															31, 32, 33, 34, 35, 36, 37, 38, 39, 40,
															41, 42, 43, 44, 45, 46, 47, 48, 49, 50,
															51, -1, -1, -1, -1, -1 };
	
}
