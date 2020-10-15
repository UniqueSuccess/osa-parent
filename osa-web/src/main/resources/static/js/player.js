$(function() {
    getReplayListBySessionId(gd.query('id'));
});
//获取回放列表
function getReplayListBySessionId(sessionId) {
    gd.get(baseUrl + '/replay/getReplayListBySessionId', { sessionId: sessionId }, function(msg) {
        if (msg.resultCode == 0) {
            playVideo(baseUrl + msg.playListPath, msg.startTime, msg.endTime);
        } else {
            gd.showError('视频加载失败！' + (msg.resultMsg || ''));
        }
    });
}

//播放视频
function playVideo(playListPath, startTime, endTime) {
    var video = document.getElementById('video');
    video.src = playListPath;
    video.currentTime = startTime;
    video.play();
}
