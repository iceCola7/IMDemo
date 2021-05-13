package com.cxz.imdemo

import android.app.Application
import com.cxz.imdemo.manager.IMManager

/**
 * @author chenxz
 * @date 2021/4/9
 * @desc
 */
class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // 初始化融云
        initRY()
    }

    /**
     * 初始化融云
     */
    private fun initRY() {
        IMManager.instance.init(this)
    }
}