package com.utkarshr.popup_toast

import android.animation.Animator
import android.animation.ObjectAnimator
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.utkarshr.popup_toast.Utils.Companion.dpToPx
import com.utkarshr.popup_toast.Utils.Companion.ifLet


class MainActivity : AppCompatActivity(), View.OnLayoutChangeListener, View.OnTouchListener {

    var mView: View? = null

    var mRootViewGroup: ViewGroup? = null

    private var mTopMargin = 0
    private var mViewY = 0f
    private var mYDelta = 0f

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
        mView?.setOnTouchListener(this)

        mView?.visibility = View.INVISIBLE
        mRootViewGroup?.addView(
            mView,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )
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

    private fun restoreViewPosition() {
        mView?.let {

            val viewHeight = it.height
            val finalY = mRootViewGroup!!.height - viewHeight - dpToPx(24)
            val anim = ObjectAnimator.ofFloat(it, "translationY", finalY.toFloat())
            anim.duration = 300
            anim.interpolator = OvershootInterpolator(2f)
            anim.start()

        }
    }



    override fun onLayoutChange(
        view: View?,
        left: Int,
        top: Int,
        right: Int,
        bottom: Int,
        oldLeft: Int,
        oldTop: Int,
        oldRight: Int,
        oldBottom: Int
    ) {
        view?.visibility = View.VISIBLE
        val viewWidth = view?.width ?: 0
        val viewHeight = view?.height ?: 0
        val newX = (Utils.screenWidth - viewWidth) / 2
        val startY = mRootViewGroup!!.height
        val finalY = mRootViewGroup!!.height - viewHeight - Utils.dpToPx(24)
        view?.x = newX.toFloat()
        view?.y = startY.toFloat()

        mTopMargin = (view?.layoutParams as FrameLayout.LayoutParams).topMargin
        mViewY = finalY.toFloat()

        val anim = ObjectAnimator.ofFloat(view, "translationY", finalY.toFloat())
        anim.duration = 300
        anim.interpolator = OvershootInterpolator(2f)
        anim.start()

        view.removeOnLayoutChangeListener(this)
    }

    override fun onTouch(view: View?, event: MotionEvent?): Boolean {
        ifLet(view, event, mRootViewGroup) {

            val y = event!!.rawY
            val layoutParams = view!!.layoutParams as FrameLayout.LayoutParams
            when (event.action and MotionEvent.ACTION_MASK) {
                MotionEvent.ACTION_DOWN -> {
                    mYDelta = y - view.y
                }
                MotionEvent.ACTION_UP -> {
                    if (view.y - mViewY > Utils.dpToPx(32)) {
                        removeView()
                    } else {
                        restoreViewPosition()
                    }
                }
                MotionEvent.ACTION_MOVE -> {
                    val newY = if ((y - mYDelta) < mViewY) mViewY else (y - mYDelta)
                    view.y = newY
                }
                else -> {
                }
            }
            mRootViewGroup!!.invalidate()
        }
        return true
    }

}
