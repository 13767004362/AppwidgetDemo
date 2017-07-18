package com.xingen.appwidget

import android.app.IntentService
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import com.xingen.base.BaseApplication

/**
 * Created by ${xingen} on 2017/7/17.
 */

class ExampleAppWidgetService(name: String = defaultTag) : IntentService(name) {
   lateinit var receiver: NotifyStopBroadcast
     var continueFlag=true
    override fun onCreate() {
        super.onCreate()
        Log.i(defaultTag," service oncreate  ${BaseApplication.instance}")
        receiver= NotifyStopBroadcast()
        val intentFilter=IntentFilter(broadcast_action)
        BaseApplication.instance!!.registerReceiver(receiver,intentFilter)
    }
    /**
     * 沉睡时间
     */
    private val time: Long = 10 * 1000
    override fun onHandleIntent(intent: Intent) {
        Log.i(defaultTag," service onHandleIntent")
        var position:Long = 0
        while (continueFlag) {
            position++
            sendBroadcast(createIntent(position))
            Thread.sleep(time)
        }
    }
    override fun onDestroy() {
        BaseApplication.instance!!.unregisterReceiver(receiver)
        Log.i(defaultTag,"  service onDestroy ${BaseApplication.instance}")
        super.onDestroy()
    }

    /**
     * 创建开启广播的Intent
     */
    fun  createIntent(index:Long):Intent{
        var  intent=Intent(ExampleAppWidgetProvider.app_widget_action)
        var bundle=Bundle()
        bundle.putLong(defaultTag,index)
        intent.putExtras(bundle)
       application
        return intent
    }
    companion object {
        val defaultTag: String get() = ExampleAppWidgetService::class.java.simpleName
        val  service_action="com.xingen.appwidget.ExampleAppWidgetService"
        val broadcast_action="com.xingen.appwidget.ExampleAppWidgetService.NotifyStopBroadcast"

    }

    /**
     * 用于通知，停止服务
     */
   inner  class NotifyStopBroadcast :BroadcastReceiver(){
        override fun onReceive(context: Context, intent: Intent) {
            Log.i(defaultTag," NotifyStopBroadcast onReceive")
              this@ExampleAppWidgetService.continueFlag =false
              this@ExampleAppWidgetService.stopSelf()
        }
    }
}
