export default class Cookies {

    static getCookie(name) {
        var v = document.cookie.match('(^|;) ?' + name + '=([^;]*)(;|$)');
        return v ? v[2] : null;
    }

    static setCookie(name, value, seconds) {
        var date = new Date;
        date.setTime(date.getTime() + 1000*seconds);
        Cookies.setCookieWithDate(name, value, date);
    }

    static setCookieWithDate(name, value, date) {
        document.cookie = name + "=" + value + ";path=/;expires=" + date.toGMTString();
    }

    static deleteCookie(name) { setCookie(name, '', -1); }
}