package com.utkarshr.popup_toast

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.infoButton).setOnClickListener {
            PopupToast(this)
                .makeText("This is an info message.")
                .setStyle(ToastStyle.INFO)
                .show()
        }

        findViewById<Button>(R.id.successButton).setOnClickListener {
            PopupToast(this)
                .makeText("This is a success message.")
                .setStyle(ToastStyle.SUCCESS)
                .show()
        }

        findViewById<Button>(R.id.warningButton).setOnClickListener {
            PopupToast(this)
                .makeText("This is a warning message.")
                .setStyle(ToastStyle.WARNING)
                .show()
        }

        findViewById<Button>(R.id.errorButton).setOnClickListener {
            PopupToast(this)
                .makeText("This is a error message.")
                .setStyle(ToastStyle.ERROR)
                .show()
        }

        findViewById<Button>(R.id.customStyleButton).setOnClickListener {
            PopupToast(this)
                .makeText("This is a custom style message in two lines", Color.MAGENTA)
                .setIcon(R.drawable.ic_success_white, Color.RED)
                .setDuration(5000)
                .setThemeColor(Color.GREEN)
                .setBgColor(Color.BLACK)
                .show()
        }

        findViewById<Button>(R.id.customViewButton).setOnClickListener {
            val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val view = inflater.inflate(R.layout.view_custom, null)

            PopupToast(this)
                .setView(view)
                .setDuration(6000)
                .show()
        }

    }

}

