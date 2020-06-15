package com.utkarshr.popup_toast

import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.createView).setOnClickListener {
            PopupToast(this)
                .setStyle(ToastStyle.ERROR)
                .setText("Toast moved to class")
                .show()
        }

    }

}

