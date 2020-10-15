$(function() {
    var display = document.getElementById('display');
    var param = parseQueryString(location.search);
    if (!param.resolution || param.resolution == 'fullScreen') {
        param.resolution = window.innerWidth + 'x' + window.innerHeight;
    }
    var url = '';
    if (param.osauser) {
        url = location.origin + '/guacamole/tunnel';
    } else {
        url = location.origin + '/guacamole/monitor';
    }
    var guac = new Guacamole.Client(new Guacamole.HTTPTunnel(url));
    display.appendChild(guac.getDisplay().getElement());
    guac.onerror = function(error) {
        console.log(error);
    };
    guac.connect($.param(param));
    window.onunload = function() {
        guac.disconnect();
    };
    var mouse = new Guacamole.Mouse(guac.getDisplay().getElement());
    mouse.onmousedown = mouse.onmouseup = mouse.onmousemove = function(mouseState) {
        guac.sendMouseState(mouseState);
    };
    var keyboard = new Guacamole.Keyboard(document);
    keyboard.onkeydown = function(keysym) {
        guac.sendKeyEvent(1, keysym);
    };
    keyboard.onkeyup = function(keysym) {
        guac.sendKeyEvent(0, keysym);
    };
});
function parseQueryString(url) {
    try {
        url = url.match(/\?([^#]+)/)[1];
        var obj = {},
            arr = url.split('&');
        for (var i = 0; i < arr.length; i++) {
            var subArr = arr[i].split('=');
            obj[subArr[0]] = subArr[1];
        }
        return obj;
    } catch (err) {
        return {};
    }
}
