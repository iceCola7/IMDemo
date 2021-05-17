package com.cxz.imdemo.ui.im.message

import io.rong.imkit.conversationlist.model.BaseUiConversation
import io.rong.imkit.conversationlist.provider.PrivateConversationProvider
import io.rong.imkit.widget.adapter.IViewProviderListener
import io.rong.imkit.widget.adapter.ViewHolder

/**
 * @author chenxz
 * @date 2021/5/17
 * @desc 自定义私聊列表Item
 */
class CustomPrivateConversationProvider : PrivateConversationProvider() {

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