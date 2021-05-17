package com.cxz.imdemo.ui.im.conversationlist

import android.os.Bundle
import android.view.View
import io.rong.imkit.conversationlist.ConversationListAdapter
import io.rong.imkit.conversationlist.ConversationListFragment

/**
 * @author chenxz
 * @date 2021/5/17
 * @desc 自定义会话列表
 */
class CustomConversationListFragment : ConversationListFragment() {

    override fun onResolveAdapter(): ConversationListAdapter {
        return CustomConversationListAdapter()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

}