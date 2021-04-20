package com.cxz.imdemo.ui.plugin

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.text.TextUtils
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.cxz.imdemo.R
import io.rong.imkit.IMCenter
import io.rong.imkit.config.RongConfigCenter
import io.rong.imkit.conversation.extension.RongExtension
import io.rong.imkit.conversation.extension.component.plugin.IPluginModule
import io.rong.imkit.conversation.extension.component.plugin.IPluginRequestPermissionResultCallback
import io.rong.imkit.conversation.extension.component.plugin.IPluginRequestPermissionResultCallback.REQUEST_CODE_PERMISSION_PLUGIN
import io.rong.imkit.manager.SendImageManager
import io.rong.imkit.manager.SendMediaManager
import io.rong.imkit.picture.PictureSelector
import io.rong.imkit.picture.config.PictureConfig
import io.rong.imkit.picture.config.PictureMimeType
import io.rong.imkit.utils.PermissionCheckUtil
import io.rong.imlib.RongIMClient
import io.rong.imlib.model.Conversation

/**
 * @author chenxz
 * @date 2021/4/20
 * @desc 自定义拍照插件
 */
class MyShotPlugin : IPluginModule, IPluginRequestPermissionResultCallback {

    var conversationType: Conversation.ConversationType? = null
    var targetId: String? = null
    private var mRequestCode = -1

    override fun obtainDrawable(context: Context): Drawable? {
        return ContextCompat.getDrawable(context, R.mipmap.icon_shot_plugin)
    }

    override fun obtainTitle(context: Context?): String {
        return "拍照"
    }

    override fun onClick(currentFragment: Fragment?, extension: RongExtension?, index: Int) {
        conversationType = extension!!.conversationType
        targetId = extension.targetId
        mRequestCode = (index + 1 shl 8) + (PictureConfig.CHOOSE_REQUEST and 0xff)
        val permissions = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
        )
        if (PermissionCheckUtil.checkPermissions(currentFragment!!.context, permissions)) {
            openCamera(currentFragment)
        } else {
            extension.requestPermissionForPluginResult(permissions, REQUEST_CODE_PERMISSION_PLUGIN, this)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            // 图片、视频、音频选择结果回调
            val selectList = PictureSelector.obtainMultipleResult(data)
            if (selectList != null && selectList.size > 0) {
                val sendOrigin = selectList[0].isOriginal
                for (item in selectList) {
                    val mimeType = item.mimeType
                    if (mimeType.startsWith("image")) {
                        SendImageManager.getInstance().sendImage(conversationType, targetId, item, sendOrigin)
                        if (conversationType == Conversation.ConversationType.PRIVATE) {
                            RongIMClient.getInstance().sendTypingStatus(conversationType, targetId, "RC:ImgMsg")
                        }
                    } else if (mimeType.startsWith("video")) {
                        var path = Uri.parse(item.path)
                        if (TextUtils.isEmpty(path.scheme)) {
                            path = Uri.parse("file://" + item.path)
                        }
                        SendMediaManager.getInstance()
                            .sendMedia(IMCenter.getInstance().context, conversationType, targetId, path, item.duration)
                        if (conversationType == Conversation.ConversationType.PRIVATE) {
                            RongIMClient.getInstance().sendTypingStatus(conversationType, targetId, "RC:SightMsg")
                        }
                    }
                }
            }
        }
    }

    override fun onRequestPermissionResult(
        fragment: Fragment,
        extension: RongExtension?,
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ): Boolean {
        if (PermissionCheckUtil.checkPermissions(fragment.activity, permissions)) {
            if (requestCode != -1) {
                openCamera(fragment)
            }
        } else {
            if (fragment.activity != null) {
                PermissionCheckUtil.showRequestPermissionFailedAlter(fragment.context, permissions, grantResults)
            }
        }
        return true
    }

    private fun openCamera(currentFragment: Fragment) {
        PictureSelector.create(currentFragment)
            .openCamera(PictureMimeType.ofImage())
            .loadImageEngine(RongConfigCenter.featureConfig().kitImageEngine)
            .forResult(PictureConfig.REQUEST_CAMERA)
    }
}