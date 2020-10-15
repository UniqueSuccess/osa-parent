$(function() {
    gd.get(baseUrl + '/replay/getCommandReplayBySessionId?sessionId=' + gd.query('sessionId'), function(msg) {
        if (msg.resultCode == 0) {
            gd.get(baseUrl + msg.data.filepath, function(json) {
                var width = (window.innerWidth + 12) / $('#ch').width();
                var height = ((window.innerHeight - 12) * 0.75) / 12;
                var data = {
                    version: 1,
                    width: width,
                    height: height,
                    duration: 0,
                    command: '',
                    title: '',
                    env: {
                        TERM: 'xterm-256color',
                        SHELL: ''
                    },
                    stdout: []
                };
                var prevTime = 0;
                data.stdout.push([0, '']);
                $.each(json, function(curTime, m, i) {
                    data.duration = Math.max(data.duration, Number(curTime));
                    var command = [curTime - prevTime, m];
                    data.stdout.push(command);
                    prevTime = curTime;
                });

                var blob = new Blob([JSON.stringify(data)], { type: 'application/json' });
                asciinema.player.js.CreatePlayer('asciinema_player', URL.createObjectURL(blob), {
                    width: width,
                    height: height,
                    autoPlay: true
                });
            });
        } else {
            gd.showError(msg.resultMsg);
        }
    });
});
