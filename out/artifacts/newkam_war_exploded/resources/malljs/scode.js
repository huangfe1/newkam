console.log("scode.js正在执行！");

//验证码的正确性
function validateUrl(url) {
    if (url.indexOf("code=") < 0 && url.indexOf("boxCode=") < 0) {
        alertError("码规则有误，请检查");
        return false;
    }
    return true;
}

//是否是大箱
function isBox(code) {
    if (code.indexOf("boxCode") > -1) {
        return true;
    }
    return false;
}

//判断大小码是否有重复
function validateRepeat(os, code) {
    var result = false;
    if(os.length>1){
        os.each(function (k,v) {
            if ($(this).html() == code) {
                result = true;
            }
        })
    }else {
        var tem = os.html();
        // alert(tem==code);
        if(tem==code){
            result = true;
        }else {
        }
    }
    return result;
}

//从防伪码链接中获取编号
function getCodeFromCodeStr(code) {
    var sc = code.substr(code.lastIndexOf("=") + 1, code.length);
    return sc;
}

//是否是小箱
function isCode(code) {
    if (code.indexOf("code") > -1) {
        return true;
    }
    return false;
}
