package com.utkarshr.popup_toast

import android.animation.Animator
import android.animation.ObjectAnimator
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity(), View.OnLayoutChangeListener {

    var mView: View? = null

    var mRootViewGroup: ViewGroup? = null

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mRootViewGroup = window.decorView.rootView.findViewById(android.R.id.content) as ViewGroup

        findViewById<Button>(R.id.createView).setOnClickListener {
            createView(this)
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

        mView?.addOnLayoutChangeListener(this)

        mView?.visibility = View.INVISIBLE
        mRootViewGroup?.addView(mView, LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT))
    }

    private fun removeView() {
        mView?.let {
            val finalY = mRootViewGroup!!.height
            val anim = ObjectAnimator.ofFloat(it, "translationY", finalY.toFloat())
            anim.duration = 300
            anim.interpolator = OvershootInterpolator(2f)
            anim.start()
            anim.addListener(object : Animator.AnimatorListener {
                override fun onAnimationRepeat(p0: Animator?) {
                }
                override fun onAnimationEnd(p0: Animator?) {
                    mRootViewGroup?.removeView(it)
                    mView = null
                }
                override fun onAnimationCancel(p0: Animator?) {
                }
                override fun onAnimationStart(p0: Animator?) {
                }
            })
        }
    }

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
        val startY = mRootViewGroup!!.height
        val finalY = mRootViewGroup!!.height - viewHeight - Utils.dpToPx(24)
        view.x = newX.toFloat()
        view.y = startY.toFloat()

        val anim = ObjectAnimator.ofFloat(view, "translationY", finalY.toFloat())
        anim.duration = 300
        anim.interpolator = OvershootInterpolator(2f)
        anim.start()

        view.removeOnLayoutChangeListener(this)
    }

}
