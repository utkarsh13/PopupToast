package com.utkarshr.popup_toast

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {

    var mView: View? = null

    var create: Boolean = false

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        findViewById<Button>(R.id.createView).setOnClickListener {


            if (create) {
//                val animation2: ObjectAnimator = ObjectAnimator.ofFloat(mView, "translationY", 2020F, 1000F)
//                animation2.duration = 1000
//                animation2.target = mView
//                animation2.start()
            } else {
                createView(this)
//                val animation2: ObjectAnimator = ObjectAnimator.ofFloat(mView, "translationY", 1000F, 2020F)
//                animation2.duration = 1000
//                animation2.target = mView
//                animation2.start()
            }
            create = !create
        }

        findViewById<Button>(R.id.removeView).setOnClickListener {
            removeView()
        }


    }

    private fun createView(context: Context) {
        if (mView != null) {
            return
        }
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        mView = inflater.inflate(R.layout.view_popup_toast, null)
        mView?.findViewById<TextView>(R.id.popup_text)?.text = "Popup text"
        //setting some initial position
        mView?.x = Utils.dpToPx(200).toFloat()
        mView?.y = Utils.dpToPx(200).toFloat()

        //can be later used to adjust margins if needed
        mView?.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(v: View?) {

            }

            override fun onViewDetachedFromWindow(v: View?) {

            }
        })

        val listener = object: View.OnLayoutChangeListener {
            override fun onLayoutChange(
                view: View,
                left: Int,
                top: Int,
                right: Int,
                bottom: Int,
                oldLeft: Int,
                oldTop: Int,
                oldRight: Int,
                oldBottom: Int
            ) {
                view.visibility = View.VISIBLE
                val viewWidth = view.width
                val viewHeight = view.height
                val newX = (Utils.screenWidth - viewWidth)/2
                val newY = Utils.screenHeight - viewHeight - Utils.dpToPx(16)
                view.x = newX.toFloat()
                view.y = newY.toFloat()
                view.removeOnLayoutChangeListener(this)
            }

        }
        mView?.addOnLayoutChangeListener(listener)

        mView?.visibility = View.INVISIBLE
        (window.decorView.rootView as ViewGroup).addView(mView, LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT))
    }

    private fun removeView() {
        mView?.let {
            (window.decorView.rootView as ViewGroup).removeView(it)
            mView = null
        }
    }

}
