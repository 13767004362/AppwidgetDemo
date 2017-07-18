package com.xingen.base

import android.app.Application

/**
 * Created by ${xingen} on 2017/7/18.
 */
class BaseApplication:Application(){
    override fun onCreate() {
        super.onCreate()
        instance=this
    }
    companion object {
        var  instance:BaseApplication?=null
    }
}