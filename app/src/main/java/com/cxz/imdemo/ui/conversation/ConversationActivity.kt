package com.cxz.imdemo.ui.conversation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.cxz.imdemo.R
import io.rong.imkit.conversation.ConversationFragment

class ConversationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_conversation)

        val conversationFragment = ConversationFragment()
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, conversationFragment)
        transaction.commit()
    }

}