package com.cxz.imdemo.ui.message

import android.view.ViewGroup
import io.rong.imkit.conversationlist.model.BaseUiConversation
import io.rong.imkit.conversationlist.provider.BaseConversationProvider
import io.rong.imkit.widget.adapter.IViewProviderListener
import io.rong.imkit.widget.adapter.ViewHolder
import io.rong.imlib.model.Conversation

/**
 * @author chenxz
 * @date 2021/4/19
 * @desc 自定义会话模板
 */
class CustomConversationProvider : BaseConversationProvider() {

    override fun isItemViewType(item: BaseUiConversation?): Boolean {
        // 根据业务需要，判断 item 是该模板需要处理的会话时，返回 true, 否则返回 false
        // return super.isItemViewType(item)
        // 此处以自定义私聊会话模板为例
        return item!!.mCore.conversationType.equals(Conversation.ConversationType.PRIVATE)
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        return super.onCreateViewHolder(parent, viewType)
    }

    override fun bindViewHolder(
        holder: ViewHolder?,
        uiConversation: BaseUiConversation?,
        position: Int,
        list: MutableList<BaseUiConversation>?,
        listener: IViewProviderListener<BaseUiConversation>?
    ) {
        super.bindViewHolder(holder, uiConversation, position, list, listener)
    }

}