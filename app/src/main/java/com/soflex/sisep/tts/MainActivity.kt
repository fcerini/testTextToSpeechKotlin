package com.soflex.sisep.tts

import android.media.AudioManager
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import kotlin.concurrent.schedule

class MainActivity : AppCompatActivity() {

    lateinit var mTTS: TextToSpeech
    lateinit var notification: Uri
    lateinit var ring: Ringtone

    lateinit var audioManager: AudioManager
    var maxVolume: Int = 0
    var originalVolume = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        ring = RingtoneManager.getRingtone(this, notification)

        audioManager = getSystemService(AUDIO_SERVICE) as AudioManager
        maxVolume = audioManager.getStreamMaxVolume( AudioManager.STREAM_MUSIC)
        originalVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)


        mTTS = TextToSpeech(applicationContext, TextToSpeech.OnInitListener { status ->
            if (status != TextToSpeech.ERROR){
                //if there is no error then set language
                mTTS.language = Locale("ES") // or Locale.US ...
            }
        })

        //speak button click
        speakBtn.setOnClickListener {

            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, maxVolume, 0)

            ring.play()

            val toSpeak:CharSequence = textEt.text.toString()
            if (toSpeak != ""){

                Timer("say it", false).schedule(1000) {
                    mTTS.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null, "noIdea")
                }

                Timer("quiet", false).schedule(20000) {
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, originalVolume, 0)
                }

            }
        }

        //stop speaking button click
        stopBtn.setOnClickListener {
            if (mTTS.isSpeaking){
                //if speaking then stop
                mTTS.stop()
                //mTTS.shutdown()
            }
            else{
                //if not speaking
                Toast.makeText(this, "Not speaking", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onPause() {
        if (mTTS.isSpeaking){
            //if speaking then stop
            mTTS.stop()
            //mTTS.shutdown()
        }
        super.onPause()
    }
}