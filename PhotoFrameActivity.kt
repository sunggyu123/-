package com.example.electronic_photo_frame

import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import java.time.Duration
import java.util.*
import kotlin.concurrent.timer

class PhotoFrameActivity : AppCompatActivity(){

    private val photoList = mutableListOf<Uri>()
    private var currentPosition = 0
    private  var timer: Timer? = null
    private val photoImage:ImageView by lazy{
        findViewById<ImageView>(R.id.PhotoImage)
    }
    private val backgroundphotoImage:ImageView by lazy{
        findViewById<ImageView>(R.id.backgroundPhotoImage)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photoframe)

        val size = intent.getIntExtra("photoListSize",0)
        for (i in 0..size){
            intent.getStringExtra("photo$i")?.let {
                photoList.add(Uri.parse(it))
            }
        }
        startTimer()
    }


    private fun startTimer(){
        timer = timer(period = 5*1000){
            runOnUiThread { //main thread로 바꿔준다.
                val current = currentPosition
                val next = if (photoList.size <= currentPosition + 1) 0 else currentPosition+1
                backgroundphotoImage.setImageURI(photoList[current])

                photoImage.alpha = 0f // 투명도 조정
                photoImage.setImageURI(photoList[next])
                photoImage.animate()
                    .alpha(1.0f)
                    .setDuration(1000)
                    .start()
                currentPosition = next

            }
        }
    }

    override fun onStop() {
        super.onStop()
        timer?.cancel()
    }

    override fun onStart() {
        super.onStart()
        startTimer()
    }

    override fun onDestroy() {
        super.onDestroy()
        timer?.cancel()
    }

}