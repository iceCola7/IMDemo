package com.cxz.imdemo

import android.app.Application
import com.cxz.imdemo.manager.RYManager
import io.rong.imkit.RongIM

/**
 * @author chenxz
 * @date 2021/4/9
 * @desc
 */
class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        initRY()
    }

    private fun initRY() {
        val appKey = "3argexb63f7he"
        RongIM.init(this, appKey, true)
        RYManager.instance.init()
    }
}