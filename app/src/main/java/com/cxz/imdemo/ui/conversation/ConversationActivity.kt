package com.cxz.imdemo.ui.conversation

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import androidx.appcompat.app.AppCompatActivity
import com.cxz.imdemo.R
import com.cxz.imdemo.ui.plugin.MyShotPlugin
import io.rong.imkit.IMCenter
import io.rong.imkit.manager.SendImageManager
import io.rong.imkit.manager.SendMediaManager
import io.rong.imkit.picture.PictureMediaScannerConnection
import io.rong.imkit.picture.config.PictureMimeType
import io.rong.imkit.picture.entity.LocalMedia
import io.rong.imkit.picture.tools.MediaUtils
import io.rong.imkit.picture.tools.PictureFileUtils
import io.rong.imkit.picture.tools.SdkVersionUtils
import io.rong.imkit.utils.RouteUtils
import io.rong.imlib.RongIMClient
import io.rong.imlib.model.Conversation
import java.io.File

class ConversationActivity : AppCompatActivity() {

    // 用户昵称
    private var nickName = ""

    // 目标用户ID
    private var targetUserId: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_conversation)


        nickName = intent.extras?.getString(RouteUtils.TITLE) ?: ""
        targetUserId = (intent.getStringExtra(RouteUtils.TARGET_ID) ?: "0").toLong()

        val conversationFragment = CustomConversationFragment()
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, conversationFragment)
        transaction.commit()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (MyShotPlugin.cameraPath.isNotEmpty()) {
                requestCamera()
            }
        }
    }

    /**
     * 拍照后处理结果
     */
    private fun requestCamera() {
        val cameraPath = MyShotPlugin.cameraPath
        val config = MyShotPlugin.config
        // on take photo success
        var mimeType: String? = null
        var duration: Long = 0
        val isAndroidQ = SdkVersionUtils.checkedAndroid_Q()
        if (cameraPath.isEmpty()) {
            return
        }
        var size: Long = 0
        var newSize = IntArray(2)
        val file = File(cameraPath)
        if (!isAndroidQ) {
            PictureMediaScannerConnection(applicationContext, cameraPath) { }
        }
        val media = LocalMedia()
        // 图片视频处理规则
        if (isAndroidQ) {
            val path = PictureFileUtils.getPath(applicationContext, Uri.parse(cameraPath))
            val f = File(path)
            size = f.length()
            mimeType = PictureMimeType.fileToType(f)
            if (PictureMimeType.eqImage(mimeType)) {
                val degree = PictureFileUtils.readPictureDegree(this, cameraPath)
                val rotateImagePath = PictureFileUtils.rotateImageToAndroidQ(
                    this,
                    degree, cameraPath, config.cameraFileName
                )
                media.androidQToPath = rotateImagePath
                newSize = MediaUtils.getLocalImageSizeToAndroidQ(this, cameraPath)
            } else {
                newSize = MediaUtils.getLocalVideoSize(this, Uri.parse(cameraPath))
                duration = MediaUtils.extractDuration(this, true, cameraPath)
            }
        } else {
            mimeType = PictureMimeType.fileToType(file)
            size = File(cameraPath).length()
            if (PictureMimeType.eqImage(mimeType)) {
                val degree = PictureFileUtils.readPictureDegree(this, cameraPath)
                PictureFileUtils.rotateImage(degree, cameraPath)
                newSize = MediaUtils.getLocalImageWidthOrHeight(cameraPath)
            } else {
                newSize = MediaUtils.getLocalVideoSize(cameraPath)
                duration = MediaUtils.extractDuration(this, false, cameraPath)
            }
        }
        media.duration = duration
        media.width = newSize[0]
        media.height = newSize[1]
        media.path = cameraPath
        media.mimeType = mimeType
        media.size = size
        media.chooseModel = config.chooseMode

        val sendOrigin = media.isOriginal
        mimeType = media.mimeType
        val conversationType = Conversation.ConversationType.PRIVATE
        val targetId = targetUserId.toString()
        if (mimeType.startsWith("image")) {
            SendImageManager.getInstance().sendImage(conversationType, targetId, media, sendOrigin)
            if (conversationType == Conversation.ConversationType.PRIVATE) {
                RongIMClient.getInstance().sendTypingStatus(conversationType, targetId, "RC:ImgMsg")
            }
        } else if (mimeType.startsWith("video")) {
            var path = Uri.parse(media.path)
            if (TextUtils.isEmpty(path.scheme)) {
                path = Uri.parse("file://" + media.path)
            }
            SendMediaManager.getInstance()
                .sendMedia(IMCenter.getInstance().context, conversationType, targetId, path, media.duration)
            if (conversationType == Conversation.ConversationType.PRIVATE) {
                RongIMClient.getInstance().sendTypingStatus(conversationType, targetId, "RC:SightMsg")
            }
        }
        MyShotPlugin.cameraPath = ""
    }
}