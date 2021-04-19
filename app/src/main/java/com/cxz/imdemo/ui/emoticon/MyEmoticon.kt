package com.cxz.imdemo.ui.emoticon

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import io.rong.imkit.conversation.extension.component.emoticon.IEmoticonTab

/**
 * @author chenxz
 * @date 2021/4/19
 * @desc 自定义表情
 */
class MyEmoticon : IEmoticonTab {

    override fun obtainTabDrawable(context: Context?): Drawable {
        TODO("Not yet implemented")
    }

    override fun obtainTabPager(context: Context?, parent: ViewGroup?): View {
        TODO("Not yet implemented")
    }

    override fun onTableSelected(position: Int) {
    }

    override fun getEditInfo(): LiveData<String> {
        TODO("Not yet implemented")
    }
}