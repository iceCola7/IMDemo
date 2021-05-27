package com.cxz.imdemo.receiver

import android.content.Context
import android.content.Intent
import com.cxz.imdemo.ui.MainActivity
import io.rong.push.PushType
import io.rong.push.notification.PushMessageReceiver
import io.rong.push.notification.PushNotificationMessage

/**
 * @author chenxz
 * @date 2021/4/10
 * @desc 自定义通知
 */
class CustomPushMessageReceiver : PushMessageReceiver() {

    override fun onNotificationMessageClicked(
        context: Context?,
        pushType: PushType?,
        notificationMessage: PushNotificationMessage?
    ): Boolean {
        if (notificationMessage?.sourceType != PushNotificationMessage.PushSourceType.FROM_ADMIN) {
            val targetId = notificationMessage?.targetId ?: ""
            //10000 为 Demo Server 加好友的 id，若 targetId 为 10000，则为加好友消息，默认跳转到 NewFriendListActivity
            if (targetId == "10000") {
                val intentMain = Intent(context, MainActivity::class.java)
                intentMain.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                val intentNewFriend = Intent(context, MainActivity::class.java)
                intentNewFriend.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                val intentArray = arrayOf(intentMain, intentNewFriend)
                context?.startActivities(intentArray)
                return true
            } else {
                val intentMain = Intent(context, MainActivity::class.java)
                intentMain.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context?.startActivity(intentMain)
            }
        }
        return super.onNotificationMessageClicked(context, pushType, notificationMessage)
    }

    override fun onNotificationMessageArrived(
        context: Context?,
        pushType: PushType?,
        notificationMessage: PushNotificationMessage?
    ): Boolean {
        return super.onNotificationMessageArrived(context, pushType, notificationMessage)
    }

    override fun onThirdPartyPushState(pushType: PushType?, action: String?, resultCode: Long) {
        super.onThirdPartyPushState(pushType, action, resultCode)
    }

}