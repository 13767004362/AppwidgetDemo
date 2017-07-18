package com.xingen.appwidget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.RemoteViews
import android.widget.Toast
import com.xingen.R
import com.xingen.base.BaseApplication


/**
 * create by ${新根} on 2017/7/17
 *  定义AppWidgetProvider子类，用来响应widget的添加、删除、更新等操作。
 *
 *  在androidManifest.xml中注册，需要添加  <action android:name="android.appwidget.action.APPWIDGET_UPDATE"></action>
 *
 */
class ExampleAppWidgetProvider : AppWidgetProvider() {
    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        Log.i(tag," onReceive ")
        val action = intent.action
        var  appwidgetManager= AppWidgetManager.getInstance(context)
        if (app_widget_action.equals(action)) {//更新数据
            val changeIndex = intent.extras.getLong(ExampleAppWidgetService.defaultTag)
            Log.i(tag," onReceive  appwidget update $changeIndex")
            updateAppWidgets(context,appwidgetManager,  changeIndex)
        }else if (Intent.CATEGORY_ALTERNATIVE.equals(action)){//按钮的点击
            Log.i(tag," onReceive  appwidget onclick ")
            var remoteViewId=intent.data.schemeSpecificPart.toInt()
            Toast.makeText(context,"点击关闭AppWidget",Toast.LENGTH_SHORT).show()
        }
    }
    /**
     * 用于开启更新数据的后台服务的action
     */
    fun createIntent():Intent=Intent(ExampleAppWidgetService.service_action).setPackage(package_name)
    /**
     * 第一个widget被创建时调用，进行开启服务
     */
    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        Log.i(tag, " onEnabled ，进行开启服务的操作 ")
        BaseApplication.instance!!.startService(createIntent())
    }

    /**
     * onUpdate() 在更新 widget 时，被执行
     */
    override fun onUpdate(context: Context?, appWidgetManager: AppWidgetManager?, appWidgetIds: IntArray) {
        var list=appWidgetIds.toList()
        // 每次 widget 被创建时，对应的将widget的id添加到set中
        for (i in list.indices){
            Log.i(tag, " onUpdate appWidgetIds的个数  ${list.size} id为 ${list[i]} ")
            idsSet.add(list[i])
        }
        Log.i(tag, " onUpdate set集合中个数 ${idsSet.size}")
    }

    /**
     *  当 widget 被初次添加 或者 当 widget 的大小被改变时，被调用
     */
    override fun onAppWidgetOptionsChanged(context: Context?, appWidgetManager: AppWidgetManager?, appWidgetId: Int, newOptions: Bundle?) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions)
        Log.i(tag, " onAppWidgetOptionsChanged ")
    }

    /**
     * widget被删除时调用
     */
    override fun onDeleted(context: Context?, appWidgetIds: IntArray) {
        Log.i(tag, " onDeleted ")
        idsSet.removeAll(appWidgetIds.toList())
        super.onDeleted(context, appWidgetIds)
    }

    /**
     *  最后一个widget被删除时调用,进行关闭服务
     */
    override fun onDisabled(context: Context) {
        super.onDisabled(context)
        Log.i(tag, " onDisabled ，进行关闭后台服务的操作${  BaseApplication.instance}")
       BaseApplication.instance!!.sendBroadcast(Intent(ExampleAppWidgetService.broadcast_action))

    }

    /**
     * 更新全部的widget
     */
    fun updateAppWidgets(context: Context, appWidgetManager: AppWidgetManager, changIndex: Long) {
        Log.i(tag," updateAppWidgets size ${idsSet.size}")
        var index = changIndex
        var iterator=idsSet.iterator()
        while (iterator.hasNext()) {
            var remoteView = RemoteViews(context.packageName, R.layout.example_app_widget)
            remoteView.setTextViewText(R.id.appwidget_content_tv, "第$index 个AppWidget")
            remoteView.setOnClickPendingIntent(R.id.appwidget_test_btn,createPendingIntent(context,0))
            appWidgetManager.updateAppWidget(iterator.next(), remoteView)
            index--
        }
    }

    fun  createPendingIntent(context: Context,remoteViewId:Int):PendingIntent{
        var  intent=Intent(context,ExampleAppWidgetProvider::class.java)
        intent.action=Intent.CATEGORY_ALTERNATIVE
        intent.data= Uri.parse("custom:$remoteViewId")
        return  PendingIntent.getBroadcast(context,0,intent,0)
    }

    companion object {
        val tag:String = ExampleAppWidgetProvider::class.java.simpleName
        val package_name = "com.xingen"
        val app_widget_action = "com.xingen.appwidget.ExampleAppWidgetProvider"
        /**
         * 用于存储widget的id,每次新建一个widget,都会分配一个id。这里用静态常量的方式。
         */
        val idsSet = hashSetOf<Int>()
    }
}