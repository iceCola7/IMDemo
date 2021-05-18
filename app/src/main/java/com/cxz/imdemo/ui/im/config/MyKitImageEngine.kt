package com.cxz.imdemo.ui.im.config

import android.content.Context
import android.widget.ImageView
import io.rong.imkit.GlideKitImageEngine

/**
 * @author chenxz
 * @date 2021/5/6
 * @desc 自定义im_kit加载图片
 */
class MyKitImageEngine : GlideKitImageEngine() {

    override fun loadConversationListPortrait(context: Context, url: String, imageView: ImageView) {
        super.loadConversationListPortrait(context, url, imageView)
    }

    override fun loadConversationPortrait(context: Context, url: String, imageView: ImageView) {
        super.loadConversationPortrait(context, url, imageView)
    }
}