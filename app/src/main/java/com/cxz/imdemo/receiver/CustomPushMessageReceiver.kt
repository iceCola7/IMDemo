package com.cxz.imdemo.receiver

import android.content.Context
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