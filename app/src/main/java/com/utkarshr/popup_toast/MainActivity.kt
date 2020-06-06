package com.utkarshr.popup_toast

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.createView).setOnClickListener {
            val toast = PopupToast(this)
            toast.setText("Toast moved to different class")
            toast.show()
        }

    }

}

