package com.goldencis.osa.common.entity;

/**
 * Created by limingchao on 2018/9/26.
 */
public class ResultLog {

    //删除成功
    private Integer successCount;

    // 返回消息
    private String resultData;


    public Integer getSuccessCount() {
        return successCount;
    }

    public void setSuccessCount(Integer successCount) {
        this.successCount = successCount;
    }

    public String getResultData() {
        return resultData;
    }

    public void setResultData(String resultData) {
        this.resultData = resultData;
    }

    public ResultLog() {

    }

    public ResultLog(Integer successCount, String resultData) {
        this.successCount = successCount;
        this.resultData = resultData;
    }
}
