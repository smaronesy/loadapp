package com.udacity

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.messaging.FirebaseMessaging
import com.udacity.databinding.ActivityMainBinding
import com.udacity.util.sendNotification
import com.udacity.viewmodels.MainViewModel
import com.udacity.viewmodels.MainViewModelFactory
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0
    private val TOPIC = "download"

    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action
    lateinit var mainViewModel: MainViewModel
    lateinit var message: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        var binding: ActivityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this

        val viewModelFactory = MainViewModelFactory(application)
        mainViewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)

        createChannel(CHANNEL_ID, "ChannelName")

        // TODO: Step 3.1 create a new channel for FCM
        createChannel(
            getString(R.string.download_notification_channel_id),
            getString(R.string.download_notification_channel_name)
        )

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        // Step 3.4 call subscribe topics on start
        subscribeTopic()

        custom_button.setOnClickListener {
            when(radioGroup.checkedRadioButtonId) {
                R.id.glide -> {
                    download(URL_GLIDE)
                    message = "GLID Repository"
                    custom_button.setButState(ButtonState.Loading)
                }
                R.id.loadApp -> {
                    download(URL_LOAD_APP)
                    message = "Load App Repository"
                    custom_button.setButState(ButtonState.Loading)
                }
                R.id.retrofit -> {
                    download(URL_RETROFIT)
                    message = "Retrofit Repository"
                    custom_button.setButState(ButtonState.Loading)
                }
                else -> {
                    Toast.makeText(this,
                        "Please Choose one of the repositories",
                        Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            val query = id?.let { DownloadManager.Query().setFilterById(it) }
            val cursor = downloadManager.query(query)

            var downloadStatus = 0
            if(cursor.moveToFirst()){
                downloadStatus = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
            }

            var result = ""
            if(downloadStatus == DownloadManager.STATUS_SUCCESSFUL){
                result = "Successful"
            } else if (downloadStatus == DownloadManager.STATUS_FAILED) {
                result = "Failed"
            }

            notificationManager = ContextCompat.getSystemService(
                application,
                NotificationManager::class.java
            ) as NotificationManager

            notificationManager.sendNotification(message, result, applicationContext)

            custom_button.setButState(ButtonState.Completed)
        }
    }

    private fun download(url: String) {
        println("Testing DOWNLOAD Button")
        val request =
            DownloadManager.Request(Uri.parse(url))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID =
            downloadManager.enqueue(request)// enqueue puts the download request in the queue.
    }

    private fun createChannel(channelID: String, channelName: String) {
        // START create a channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelID,
                channelName,
                // change importance
                NotificationManager.IMPORTANCE_HIGH
            )
            //disable badges for this channel
                .apply {
                    setShowBadge(false)
                }
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = "Download Completee"

            val notificationManager = this.getSystemService(
                NotificationManager::class.java
            )

            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    private fun subscribeTopic() {
        // [START subscribe_topics]
        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC)
            .addOnCompleteListener { task ->
                var msg = getString(R.string.message_subscribed)
                if (!task.isSuccessful) {
                    msg = getString(R.string.message_subscribe_failed)
                }
//                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
            }
        // [END subscribe_topics]
    }

    companion object {

        private const val URL_GLIDE =
            "https://github.com/bumptech/glide/archive/master.zip"
        private const val URL_LOAD_APP =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val URL_RETROFIT =
            "https://github.com/square/retrofit/archive/master.zip"
        const val CHANNEL_ID = "channelId"
    }

}
