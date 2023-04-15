package com.firebasevapecounter.service

import android.R
import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.firebasevapecounter.MainActivity
import com.firebasevapecounter.OrdersActivity
import com.firebasevapecounter.model.OrderHistory
import com.firebasevapecounter.model.User
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.io.Serializable


class NotificationsService : Service(), ChildEventListener {

    private var manager: NotificationManager? = null
    private var channel: NotificationChannel? = null
    private var count: Long = 0

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.hasExtra("data") == true) {
            val userData = intent.serializable<User>("data")
            if (userData?.admin == true) {
                Firebase.database.reference.child("orders").limitToLast(1)
                    .addChildEventListener(this)
            }
        }
        return START_STICKY
    }

    inline fun <reified T : Serializable> Intent.serializable(key: String): T? = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> getSerializableExtra(
            key, T::class.java
        )
        else -> @Suppress("DEPRECATION") getSerializableExtra(key) as? T
    }

    override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
        val data = snapshot.getValue(OrderHistory::class.java)
        val msg = "Vape purchased by ${data?.name}. Count ${data?.currentCount}"
        val mBuilder = NotificationCompat.Builder(applicationContext, "notify_001")
        val ii = Intent(applicationContext, OrdersActivity::class.java)
        val pendingIntent =
            PendingIntent.getActivity(applicationContext, 0, ii, PendingIntent.FLAG_IMMUTABLE)

        val bigText = NotificationCompat.BigTextStyle()
        bigText.bigText(msg)
        bigText.setBigContentTitle("Purchased")
        bigText.setSummaryText("Counter")

        mBuilder.setContentIntent(pendingIntent)
        mBuilder.setSmallIcon(com.firebasevapecounter.R.mipmap.ic_launcher_round)
        mBuilder.setContentTitle(msg)
        mBuilder.setContentText(data?.currentCount.toString())
        mBuilder.priority = Notification.PRIORITY_MAX
        mBuilder.setStyle(bigText)

        manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channelId = "Count"
        val channel = NotificationChannel(
            channelId, "Count Notifications", NotificationManager.IMPORTANCE_HIGH
        )
        manager?.createNotificationChannel(channel)
        mBuilder.setChannelId(channelId)

        manager?.notify(0, mBuilder.build())
    }

    override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

    }

    override fun onChildRemoved(snapshot: DataSnapshot) {
    }


    override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
    }

    override fun onCancelled(error: DatabaseError) {

    }
}