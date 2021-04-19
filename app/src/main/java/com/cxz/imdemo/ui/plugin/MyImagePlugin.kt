package com.cxz.imdemo.ui.plugin

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.fragment.app.Fragment
import io.rong.imkit.conversation.extension.RongExtension
import io.rong.imkit.conversation.extension.component.plugin.ImagePlugin

/**
 * @author chenxz
 * @date 2021/4/19
 * @desc 自定义图片插件
 */
class MyImagePlugin : ImagePlugin() {

    override fun obtainDrawable(context: Context?): Drawable {
        return super.obtainDrawable(context)
    }

    override fun obtainTitle(context: Context?): String {
        return super.obtainTitle(context)
    }

    override fun onClick(currentFragment: Fragment?, extension: RongExtension?, index: Int) {
        super.onClick(currentFragment, extension, index)
    }

}