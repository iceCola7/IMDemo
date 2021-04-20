package com.cxz.imdemo.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.cxz.imdemo.R
import io.rong.imkit.utils.RouteUtils
import io.rong.imlib.model.Conversation
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button.setOnClickListener {
            RouteUtils.routeToConversationListActivity(this, "会话列表")
        }
        button2.setOnClickListener {
            val conversationType = Conversation.ConversationType.PRIVATE
            val targetId = "123456"
            val title = "会话页面标题"
            val bundle = Bundle()
            bundle.putString(RouteUtils.TITLE, title) //会话页面标题
            RouteUtils.routeToConversationActivity(this, conversationType, targetId, bundle)
        }
    }
}