package com.goldencis.osa.common.entity;

/**
 * Created by limingchao on 2018/11/20.
 */
public class ResultPlay {

    public static final Integer RESPONSE_TRUE = 0;

    public static final Integer RESPONSE_FALSE = 1;

    public static final Integer RESPONSE_ERROR = 500;

    // 响应业务状态
    private Integer resultCode;

    // 响应消息
    private String resultMsg;

    //视频播放列表的开始时间
    private Integer endTime;

    //视频播放列表的结束时间
    private Integer startTime;

    //视频播放列表的路径
    private String playListPath;

    public ResultPlay() {
    }

    public ResultPlay(Integer resultCode, String resultMsg, Integer startTime, Integer endTime, String playListPath) {
        this.resultCode = resultCode;
        this.resultMsg = resultMsg;
        this.startTime = startTime;
        this.endTime = endTime;
        this.playListPath = playListPath;
    }

    public ResultPlay(Integer resultCode, String resultMsg) {
        this.resultCode = resultCode;
        this.resultMsg = resultMsg;
    }

    public static ResultPlay build(Integer resultCode, String resultMsg) {
        return new ResultPlay(resultCode, resultMsg);
    }

    public static ResultPlay build(Integer resultCode, String resultMsg, Integer startTime, Integer endTime, String playListPath) {
        return new ResultPlay(resultCode, resultMsg, startTime, endTime, playListPath);
    }

    public static ResultPlay ok(Integer start, Integer end, String playListPath) {
        return build(RESPONSE_TRUE, null, start, end, playListPath);
    }

    public static ResultPlay False(String resultMsg) {
        return build(RESPONSE_FALSE, resultMsg);
    }

    public static ResultPlay error(String resultMsg) {
        return build(RESPONSE_ERROR, resultMsg);
    }

    public Integer getStartTime() {
        return startTime;
    }

    public void setStartTime(Integer startTime) {
        this.startTime = startTime;
    }

    public Integer getEndTime() {
        return endTime;
    }

    public void setEndTime(Integer endTime) {
        this.endTime = endTime;
    }

    public String getPlayListPath() {
        return playListPath;
    }

    public void setPlayListPath(String playListPath) {
        this.playListPath = playListPath;
    }

    public Integer getResultCode() {
        return resultCode;
    }

    public void setResultCode(Integer resultCode) {
        this.resultCode = resultCode;
    }

    public String getResultMsg() {
        return resultMsg;
    }

    public void setResultMsg(String resultMsg) {
        this.resultMsg = resultMsg;
    }
}
