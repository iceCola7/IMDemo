package com.cxz.imdemo.ui.conversationlist

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.cxz.imdemo.R
import io.rong.imkit.conversationlist.ConversationListFragment

class ConversationListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_conversation_list)

        val conversationListFragment = ConversationListFragment()
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, conversationListFragment)
        transaction.commit()
    }
}