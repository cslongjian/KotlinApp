package com.cslong.kotlinapp

import android.os.Bundle
import android.os.Handler
import android.os.Message
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val handler: Handler = object : Handler() {
            private var degrees = 0f
            override fun handleMessage(msg: Message) {
                super.handleMessage(msg)
                taiji_view.setRotate(5.let { degrees += it; degrees })
                this.sendEmptyMessageDelayed(0, 80)
            }
        }

        handler.sendEmptyMessageDelayed(0, 20)
    }
}
