package com.cxz.imdemo.ui.im.conversation

import io.rong.imkit.conversation.MessageListAdapter
import io.rong.imkit.model.UiMessage
import io.rong.imkit.widget.adapter.IViewProviderListener
import io.rong.imkit.widget.adapter.ViewHolder

/**
 * @author chenxz
 * @date 2021/5/17
 * @desc 自定义会话界面的Adapter
 */
class CustomMessageListAdapter(listener: IViewProviderListener<UiMessage>) : MessageListAdapter(listener) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
    }

}