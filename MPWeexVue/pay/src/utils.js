import MD5 from "./md5.js";

const Utils = {
    apiUrl(path, obj) {
        const host = "https://zqmp.lmspay.com/pay-demo/api";
        let url = host + path;
        if(obj !== undefined) {
            url += '?';
            url += this.formDataEncode(obj);
        }
        return url;
    },
    syncUrl() {
        // 支付同步回调地址，用于拦截
        return 'https://zqmp.lmspay.com/pay-demo/redirect_notify';
    },
    formDataEncode(obj) {
        let d = "";
        for(var key in obj) {
            if(d.length > 1) {
                d += '&';
            }
            d += key + '=';
            d += encodeURI(obj[key]);
        }
        return d;
    },
    getSign(method, path, obj) {
        var signStr = "";
        var ret = {};
        if(method == 'POST') {
            signStr += "entity=";
            signStr += JSON.stringify(obj);
        }else {
            var keySet = [];
            for(var k in obj) {
                keySet.push(k);
            }
            keySet.sort();
            for(var i in keySet) {
                var v = obj[keySet[i]];
                if(signStr.length > 1) {
                    signStr += '&';
                }
                signStr += keySet[i];
                signStr += '=';
                signStr += v;
            }
        }
        if(signStr.length > 1) {
            signStr += '&';
        }
        ret["timestamp"] = Math.round(new Date().getTime());

        signStr += "path=";
        signStr += path;
        signStr += "&timestamp="
        signStr += ret["timestamp"];
        signStr += "&key=HSQBN2lr25vT3TWUAHKrgJ9MAEnq5ZIJ";

        // console.log("create sign: " + signStr);

        ret["acctoken"] = MD5.hex_md5(signStr);

        return ret;
    }
}

export default Utils;