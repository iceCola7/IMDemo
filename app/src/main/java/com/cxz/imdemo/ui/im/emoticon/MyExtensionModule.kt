package com.cxz.imdemo.ui.im.emoticon

import android.content.Context
import androidx.fragment.app.Fragment
import io.rong.imkit.conversation.extension.IExtensionModule
import io.rong.imkit.conversation.extension.RongExtension
import io.rong.imkit.conversation.extension.component.emoticon.IEmoticonTab
import io.rong.imkit.conversation.extension.component.plugin.IPluginModule
import io.rong.imlib.model.Conversation
import io.rong.imlib.model.Message

/**
 * @author chenxz
 * @date 2021/4/19
 * @desc 自定义表情
 */
class MyExtensionModule : IExtensionModule {

    override fun onInit(context: Context?, appKey: String?) {
    }

    override fun onAttachedToExtension(fragment: Fragment?, extension: RongExtension?) {
    }

    override fun onDetachedFromExtension() {
    }

    override fun onReceivedMessage(message: Message?) {
    }

    override fun getPluginModules(conversationType: Conversation.ConversationType?): MutableList<IPluginModule> {
        return mutableListOf()
    }

    override fun getEmoticonTabs(): MutableList<IEmoticonTab> {
        val list = mutableListOf<IEmoticonTab>()
        list.add(MyEmoticon())
        return list
    }

    override fun onDisconnect() {
    }
}