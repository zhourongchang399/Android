package com.example.helloworld.http;

import androidx.annotation.NonNull;

public class httpPathList {
//    String ip = "zc.tyu.wiki";
    String ip = "192.168.123.86";
    String port = "8088";
//    String url = "https://" + ip;
    String url = "http://" + ip + ":" + port;
    String[] path = new String[]{
            url+"/userAccount/login",
            url+"/userAccount/register",
            url+"/userInfo/searchUserInfo",//2查找用户信息
            url+"/userInfo/updateUserInfo",
            url+"/userAccount/searchUserAccountLike",//账号查找
            url+"/userInfo/searchFriendList",//5朋友列表
            url+"/friendManager/addFriend",//6添加好友
            url+"/friendManager/deleteFriend",//7删除好友
            url+"/messageManager/sendMessage",//8发送信息
            url+"/messageManager/historyMessage",//9历史记录
            url+"/messageManager/lastMessage",
            url+"/dialogsManager/searchDialogs",//11查找聊天对话
            url+"/dialogsManager/deleteDialog",//12删除聊天
            url+"/userAccount/offLineUserAccount",
            url+"/userAccount/onLineUserAccount",
            url+"/messageManager/unreadMessage",//15未读消息
            url+"/messageManager/resetUnreadMessage",//16重置未读消息
            url+"/userAccount/deleteOnlineList",//17退出登录
            url+"/messageManager/sendImageMessage",//18发送图片
            url+"/userAccount/updateFace",//19发送图片
            url+"/historyMsg/hisMsgBySearch",//20聊天记录
            url+"/historyMsg/hisMsgByType",//21聊天记录
            url+"/historyMsg/hisMsgByOwner",//22聊天记录
            url+"/historyMsg/hisMsgByDate",//23聊天记录
            url+"/messageManager/deleteMsg",//24删除聊天记录
            url+"/infoManager/sendInfo",//25发送添加
            url+"/infoManager/changeInfoStatus",//26添加状态
            url+"/infoManager/deleteInfo",//27删除消息
            url+"/infoManager/searchInfos",//28查找消息
            url+"/infoManager/deleteInfoForAll",//29删除所有消息
            url+"/blackListManager/insertBlackList",//30插入黑名单
            url+"/blackListManager/searchBlackList",//31查找黑名单
            url+"/blackListManager/deleteBlackList",//32删除黑名单
            url+"/userAccount/forgive",//33忘记密码
            url+"/userAccount/phoneLogin",//34手机号码登录
            url+"/userAccount/update",//35修改密码
            url+"/messageManager/historyMessageGroup",//36历史消息
            url+"/messageManager/resetUnreadMessageGroup",//37重置未读消息
            url+"/messageManager/sendMessageGroup",//38发送消息
            url+"/messageManager/sendImageMessageGroup",//39发送消息
            url+"/messageManager/deleteMsgAffiliationGroup",//40删除消息
            url+"/GroupChatManager/insertGroupChat",//41添加成员
            url+"/GroupChatManager/selectGroupChat",//42查找成员
            url+"/GroupChatManager/changeGroupChat",//43修改群聊名字
            url+"/dialogsManager/deleteDialogGroup",//44删除群聊
            url+"/GroupChatManager/deleteGroupChatSomeone",//45退出群聊
            url+"/historyMsg/searchHistoryByOwnerGroup",//46聊天记录
            url+"/historyMsg/searchHistoryByDateGroup",//47聊天记录
            url+"/historyMsg/searchHistoryByTypeGroup",//48聊天记录
            url+"/historyMsg/searchHistoryBySearchGroup",//49聊天记录
            url+"/GroupChatManager/selectHsimsgMenber"//50聊天记录
    };

    public String[] getPath() {
        return path;
    }

    @NonNull
    @Override
    public String toString() {
        return ip;
    }

    public String getUrl() {
        return url+"/"+"image";
    }
}
