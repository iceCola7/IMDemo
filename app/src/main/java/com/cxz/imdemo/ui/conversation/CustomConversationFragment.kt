package com.cxz.imdemo.ui.conversation

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import com.cxz.imdemo.R
import io.rong.imkit.conversation.ConversationFragment
import io.rong.imkit.conversation.MessageListAdapter

/**
 * @author chenxz
 * @date 2021/4/19
 * @desc 自定义私聊界面
 */
class CustomConversationFragment : ConversationFragment() {

    override fun onResolveAdapter(): MessageListAdapter {
        return super.onResolveAdapter()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    private fun handleBanStatus(isBan: Boolean) {
        val panelView = mRongExtension.inputPanel.rootView
        panelView.apply {
            val voiceToggleBtn = findViewById<ImageView>(R.id.input_panel_voice_toggle)
        }
    }

}