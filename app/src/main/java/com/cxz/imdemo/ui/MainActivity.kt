package com.cxz.imdemo.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.cxz.imdemo.R
import io.rong.imkit.utils.RouteUtils

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        RouteUtils.routeToConversationListActivity(this,"1111")

    }
}