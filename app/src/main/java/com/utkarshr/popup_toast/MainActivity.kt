package com.utkarshr.popup_toast

import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.infoButton).setOnClickListener {
            PopupToast(this)
                .setStyle(ToastStyle.INFO)
                .setText("This is an info message.")
                .show()
        }

        findViewById<Button>(R.id.successButton).setOnClickListener {
            PopupToast(this)
                .setStyle(ToastStyle.SUCCESS)
                .setText("This is a success message.")
                .show()
        }

        findViewById<Button>(R.id.warningButton).setOnClickListener {
            PopupToast(this)
                .setStyle(ToastStyle.WARNING)
                .setText("This is a warning message.")
                .show()
        }

        findViewById<Button>(R.id.errorButton).setOnClickListener {
            PopupToast(this)
                .setStyle(ToastStyle.ERROR)
                .setText("This is a error message.")
                .show()
        }

        findViewById<Button>(R.id.customStyleButton).setOnClickListener {
            PopupToast(this)
                .setText("This is a custom style message", Color.MAGENTA)
                .setIcon(R.drawable.ic_success_white, Color.RED)
                .setDuration(5000)
                .setThemeColor(Color.GREEN)
                .setBgColor(Color.BLACK)
                .show()
        }

        findViewById<Button>(R.id.customViewButton).setOnClickListener {
            PopupToast(this)
                .setStyle(ToastStyle.INFO)
                .setText("Toast moved to class new abc")
                .show()
        }

    }

}

