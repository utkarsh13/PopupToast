package com.utkarshr.popup_toast

import android.animation.Animator
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.*
import android.view.animation.OvershootInterpolator
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GestureDetectorCompat
import com.utkarshr.popup_toast.Utils.Companion.ifLet
import java.lang.ref.WeakReference
import kotlin.math.abs


class MainActivity : AppCompatActivity(), View.OnLayoutChangeListener, View.OnTouchListener {

    var mView: View? = null
    var mRootViewGroup: ViewGroup? = null

    private var gestureDetector: GestureDetectorCompat? = null

    private var mViewY = 0f
    private var mYDelta = 0f
    private var mPaddingFromBottom = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        gestureDetector = GestureDetectorCompat(this, FlingGestureListener(this))
        mRootViewGroup = window.decorView.rootView.findViewById(android.R.id.content) as ViewGroup
        mPaddingFromBottom = Utils.dpToPx(24).toFloat()

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

    fun removeView() {
        mView?.let {
            val finalY = mRootViewGroup!!.height + Utils.dpToPx(16).toFloat()
            val anim = ObjectAnimator.ofFloat(it, "translationY", finalY.toFloat())
            anim.duration = 400
            anim.interpolator = OvershootInterpolator(2f)
            anim.start()
            anim.addListener(object : Animator.AnimatorListener {
                override fun onAnimationEnd(p0: Animator?) {
                    mRootViewGroup?.removeView(it)
                    mView = null
                }

                override fun onAnimationRepeat(p0: Animator?) {
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
            val finalY = mRootViewGroup!!.height - viewHeight - mPaddingFromBottom
            val anim = ObjectAnimator.ofFloat(it, "translationY", finalY)
            anim.duration = 300
            anim.interpolator = OvershootInterpolator(2f)
            anim.start()
        }
    }

    fun overShootView() {
        mView?.let {
            val finalY = mRootViewGroup!!.height - it.height - mPaddingFromBottom
            val overShootY = finalY - Utils.dpToPx(12)

            it.y = overShootY
            val anim = ObjectAnimator.ofFloat(it, "translationY", finalY)
            anim.duration = 300
            anim.interpolator = OvershootInterpolator(2f)
            anim.start()

            //The above animation has a jerk. Need to fo it properly.
//            val anim = ObjectAnimator.ofFloat(it, "translationY", overShootY)
//            anim.duration = 150
//            anim.interpolator = AccelerateInterpolator()
//            anim.start()
//            anim.addListener(object : Animator.AnimatorListener {
//                override fun onAnimationEnd(p0: Animator?) {
//                    val anim1 = ObjectAnimator.ofFloat(it, "translationY", finalY)
//                    anim1.duration = 150
//                    anim1.interpolator = AccelerateInterpolator()
//                    anim1.start()
//                }
//
//                override fun onAnimationRepeat(p0: Animator?) {
//                }
//                override fun onAnimationCancel(p0: Animator?) {
//                }
//                override fun onAnimationStart(p0: Animator?) {
//                }
//            })
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
        val finalY = mRootViewGroup!!.height - viewHeight - mPaddingFromBottom
        view?.x = newX.toFloat()
        view?.y = startY.toFloat()

        mViewY = finalY.toFloat()

        val anim = ObjectAnimator.ofFloat(view, "translationY", finalY.toFloat())
        anim.duration = 300
        anim.interpolator = OvershootInterpolator(2f)
        anim.start()

        view?.removeOnLayoutChangeListener(this)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(view: View?, event: MotionEvent?): Boolean {
        ifLet(view, event, mRootViewGroup) {
            val touchY = event!!.rawY
            val currentViewY = view!!.y

            when (event.action and MotionEvent.ACTION_MASK) {
                MotionEvent.ACTION_DOWN -> {
                    mYDelta = touchY - currentViewY
                }
                MotionEvent.ACTION_UP -> {
                    if (currentViewY - mViewY > mPaddingFromBottom) {
                        removeView()
                    } else {
                        restoreViewPosition()
                    }
                }
                MotionEvent.ACTION_MOVE -> {
                    val newY = if ((touchY - mYDelta) < mViewY) mViewY else (touchY - mYDelta)
                    view.y = newY
                }
                else -> {
                }
            }
            mRootViewGroup!!.invalidate()
        }
        return !(gestureDetector?.onTouchEvent(event) ?: false)
    }

    private class FlingGestureListener(activity: MainActivity) : GestureDetector.SimpleOnGestureListener() {

        val weakActivity:  WeakReference<MainActivity> = WeakReference(activity)

        companion object {
            val flingThreshold = Utils.dpToPx(24)
            val velocityThreshold = 100
        }

        override fun onFling(
            event1: MotionEvent,
            event2: MotionEvent,
            velocityX: Float,
            velocityY: Float
        ): Boolean {

            val activity = weakActivity.get()

            activity?.let {
                val diffY: Float = event2.rawY - event1.rawY

                if (abs(diffY) > flingThreshold
                    && abs(velocityY) > velocityThreshold) {
                    if (diffY > 0) {
                        it.removeView()
                    } else {
                        it.overShootView()
                    }
                }
            }
            return true
        }
    }
}
