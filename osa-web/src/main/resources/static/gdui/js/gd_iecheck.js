/**
 * 对IE9以下版本给出升级提示
 * 引用在gdui.css之后，所有js之前
 */

var gdIeCheckHtml =
    '<div class="gd-lowbrowser">' +
    '<div class="gd-lowbrowser-panel">' +
    '<h2 class="gd-lowbrowser-title">浏览器版本过低</h2>' +
    '<h5 class="gd-lowbrowser-info">建议您升级到IE10以上或者下载内置的Chrome浏览器安装使用</h5>' +
    '<div class="gd-lowbrowser-browbox">' +
    '<a href="/osa/client/download?packageType=2" class="gd-lowbrowser-item" style="background-image:url(/osa/images/compress.png);background-repeat:no-repeat;background-position:center 40px" target="_blank">浏览器安装包</a><span>' +
    '</div>' +
    '</div>' +
    '</div>';

function getBrowserInfo() {
    var ua = window.navigator.userAgent.toLowerCase();
    var match = '';
    var regMsie = /(msie\s|trident.*rv:)([\w.]+)/;
    match = regMsie.exec(ua);
    if (match != null) {
        return { browser: 'ie', version: parseInt(match[2]) || 0 };
    }
    return {};
}
var browserInfo = getBrowserInfo();
if (browserInfo.browser == 'ie' && browserInfo.version < 10) {
    document.write(gdIeCheckHtml);
    document.execCommand('Stop');
}
