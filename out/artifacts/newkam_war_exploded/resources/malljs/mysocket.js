// $(function(){


// alert("---");

var MSGS = "KAMMSGS";//所有消息的key


// localStorage.removeItem(MSGS);

// console.log(MSGS);

// //从本地址数据库查找
// function loadMessage(key) {
//     var array = localStorage.getItem(key);
//     if (array == null) {
//         array = [];
//     } else {
//         array = JSON.parse(array);
//     }
//     return array;
// }

//获取所有的消息队列
function getMsgsArray() {
    var array = localStorage.getItem(MSGS);
    if (array == null || array == "" || array == {}) {
        console.log("array为空");
        array = {};
        localStorage.setItem(MSGS, array);
        return array;
    }
    // console.log("本地存储的array是" + array);
    return JSON.parse(array);
}


//更新某人发过来的信息
function updateMessages(uid,messages) {
    var array = getMsgsArray();
    array[uid] = messages;
    localStorage.setItem(MSGS, JSON.stringify(array));
}

//获取与某人的聊天记录
function getMsgsArrayByUid(uid) {
    var msgs = getMsgsArray();
    var array;
    if (msgs.hasOwnProperty(uid)) {//存在
        // array = JSON.parse(msgs[uid]);
        array = msgs[uid];
    } else {
        array = [];
    }
    // console.log("UID:" + uid + "的消息记录是" + JSON.stringify(array));
    return array;
}

//存储与某人的聊天记录
function saveMsg(msg, uid) {
    // console.log("存储UID" + uid)
    var array = getMsgsArray();
    var msgs;
    if (array.hasOwnProperty(uid)) {//之前有存储过消息
        msgs = array[uid];
        msgs.push(msg);//保存当前消息
    } else {
        // console.log("新消息");
        msgs = [];
        msgs.push(msg);
        array[uid] = msgs;
    }
    localStorage.setItem(MSGS, JSON.stringify(array));
    // console.log(msg + "保存成功" + (JSON.stringify(array)));
}


// });