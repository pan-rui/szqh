package com.yjf.yrd.front.controller.sign.form;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import java.io.Serializable;

/**
 * Created by zjialin@yiji.com on 2014/4/14.
 */
public class SignFrom implements Serializable {
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 6977177231664783060L;

    /**
     * 用户ID
     */
    private String userBaseId;

    /**
     * 用户易极付ID
     */
    private String userId;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 证件号码
     */
    private String certNo;

    /**
     * 银行开户名
     */
    private String bankOpenName;

    /**
     * 银行卡号
     */
    private String bankCardNo;

    /**
     * 银行英文简称
     */
    private String bankType;

    /**
     * 联行号
     */
    private String bankKey;

    /**
     * 省份
     */
    private String bankProvince;

    /**
     * 城市
     */
    private String bankCity;

    /**
     * 详细地址
     */
    private String bankAddress;

    public String getUserBaseId() {
        return userBaseId;
    }

    public void setUserBaseId(String userBaseId) {
        this.userBaseId = userBaseId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getCertNo() {
        return certNo;
    }

    public void setCertNo(String certNo) {
        this.certNo = certNo;
    }

    public String getBankOpenName() {
        return bankOpenName;
    }

    public void setBankOpenName(String bankOpenName) {
        this.bankOpenName = bankOpenName;
    }

    public String getBankCardNo() {
        return bankCardNo;
    }

    public void setBankCardNo(String bankCardNo) {
        this.bankCardNo = bankCardNo;
    }

    public String getBankType() {
        return bankType;
    }

    public void setBankType(String bankType) {
        this.bankType = bankType;
    }

    public String getBankKey() {
        return bankKey;
    }

    public void setBankKey(String bankKey) {
        this.bankKey = bankKey;
    }

    public String getBankProvince() {
        return bankProvince;
    }

    public void setBankProvince(String bankProvince) {
        this.bankProvince = bankProvince;
    }

    public String getBankCity() {
        return bankCity;
    }

    public void setBankCity(String bankCity) {
        this.bankCity = bankCity;
    }

    public String getBankAddress() {
        return bankAddress;
    }

    public void setBankAddress(String bankAddress) {
        this.bankAddress = bankAddress;
    }

    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
