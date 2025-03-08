package com.fadlurahmanfdev.pushly.service

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.Build
import android.os.IBinder
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import com.fadlurahmanfdev.pushly.constant.PushlyConstant

abstract class PushlyCallService : Service() {
    lateinit var audioManager: AudioManager
    var mediaPlayer: MediaPlayer? = null
    private lateinit var vibrator: Vibrator

    private var isRingerModeListenerRegistered: Boolean = false
    private var currentAudioRingerMode:Int = AudioManager.RINGER_MODE_VIBRATE

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val manager = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibrator = manager.defaultVibrator
        } else {
            vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(this::class.java.simpleName, "Pushly-LOG %%% - on received action ${intent?.action}")
        when (intent?.action) {
            PushlyConstant.ACTION_INCOMING_CALL -> {
                onIncomingCall(intent)
            }

            PushlyConstant.ACTION_STOP_INCOMING_CALL -> {
                onStopIncomingCall(intent)
            }
        }

        return START_STICKY
    }

    open fun onIncomingCall(intent: Intent) {
        stopRinging()
        startRinging()
        listenRingerMode()
    }

    open fun onStopIncomingCall(intent: Intent) {
        stopListenRingerMode()
        stopRinging()
        stopService()
    }

    private fun startRinging() {
        currentAudioRingerMode = audioManager.ringerMode
        when (audioManager.ringerMode) {
            AudioManager.RINGER_MODE_NORMAL -> {
                startRingingNormalMode()
            }

            AudioManager.RINGER_MODE_VIBRATE -> {
                playVibrateEffect()
            }

            AudioManager.RINGER_MODE_SILENT -> {
                cancelVibrator()
            }
        }
        listenRingerMode()
    }

    private fun stopRinging(){
        stopListenRingerMode()
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        cancelVibrator()
    }

    private fun stopListenRingerMode() {
        if (isRingerModeListenerRegistered) {
            unregisterReceiver(ringerModeReceiver)
        }
        isRingerModeListenerRegistered = false
    }

    private fun cancelVibrator() {
        vibrator.cancel()
    }

    private fun startRingingNormalMode() {
        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
        mediaPlayer = MediaPlayer()
        mediaPlayer?.setDataSource(applicationContext, soundUri)

        mediaPlayer?.setAudioAttributes(
            AudioAttributes.Builder()
                .setLegacyStreamType(AudioManager.STREAM_NOTIFICATION)
                .build()
        )

        mediaPlayer?.prepare()
        mediaPlayer?.isLooping = true
        mediaPlayer?.start()

        playVibrateEffect()
    }

    private fun playVibrateEffect() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createWaveform(longArrayOf(0L, 2000L, 2000L), 0))
        } else {
            vibrator.vibrate(longArrayOf(0L, 2000L, 2000L), 0)
        }
    }

    private fun stopService() {
        stopRinging()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            stopForeground(STOP_FOREGROUND_REMOVE)
        } else {
            stopForeground(true)
        }
    }

    private fun listenRingerMode() {
        if (!isRingerModeListenerRegistered) {
            registerReceiver(
                ringerModeReceiver,
                IntentFilter(AudioManager.RINGER_MODE_CHANGED_ACTION)
            )
        }
        isRingerModeListenerRegistered = true
    }

    private val ringerModeReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.d(
                this@PushlyCallService::class.java.simpleName,
                "Pushly-LOG %%% on ringer receiver mode ${intent?.action}"
            )
            when (intent?.action) {
                AudioManager.RINGER_MODE_CHANGED_ACTION -> {
                    if(currentAudioRingerMode == audioManager.ringerMode){
                        return
                    }

                    Log.d(
                        this@PushlyCallService::class.java.simpleName,
                        "Pushly-LOG %%% ringer mode changed into ${audioManager.ringerMode}"
                    )

                    startRinging()
                }
            }
        }
    }
}