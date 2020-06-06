package com.utkarshr.popup_toast

import android.animation.Animator
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Message
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

    lateinit var mHandler: Handler

    private lateinit var gestureDetector: GestureDetectorCompat

    private var mViewY = 0f
    private var mYDelta = 0f
    private var mPaddingFromBottom = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        gestureDetector = GestureDetectorCompat(this, FlingGestureListener(this))
        mRootViewGroup = window.decorView.rootView.findViewById(android.R.id.content) as ViewGroup
        mPaddingFromBottom = Utils.dpToPx(24).toFloat()
        mHandler = @SuppressLint("HandlerLeak")
        object : Handler() {
            override fun handleMessage(msg: Message) {
                when (msg.what) {
                    ToastHandlerMessage.MSG_SHOW.value -> {
                        createView(this@MainActivity)
                    }
                    ToastHandlerMessage.MSG_HIDE.value -> {
                        mHandler.removeMessages(ToastHandlerMessage.MSG_HIDE.value)
                        removeView()
                    }
                    ToastHandlerMessage.MSG_REMOVE_TIMER.value -> {
                        mHandler.removeMessages(ToastHandlerMessage.MSG_HIDE.value)
                        mHandler.removeMessages(ToastHandlerMessage.MSG_REMOVE_TIMER.value)
                    }
                    ToastHandlerMessage.MSG_ADD_TIMER.value -> {
                        mHandler.sendEmptyMessageDelayed(ToastHandlerMessage.MSG_HIDE.value, 1000)
                    }
                }
            }
        }

        findViewById<Button>(R.id.createView).setOnClickListener {
            mHandler.sendEmptyMessage(ToastHandlerMessage.MSG_SHOW.value)
        }

        findViewById<Button>(R.id.removeView).setOnClickListener {
            mHandler.sendEmptyMessage(ToastHandlerMessage.MSG_HIDE.value)
        }
    }

    private fun createView(context: Context) {
        if (mView != null) {
            return
        }
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        mView = inflater.inflate(R.layout.view_popup_toast, null)
        mView?.findViewById<TextView>(R.id.popup_text)?.text = "This is some popup toast"
        //setting some initial position
        mView?.x = Utils.dpToPx(200).toFloat()
        mView?.y = Utils.dpToPx(200).toFloat()

        mView?.addOnLayoutChangeListener(this)
        mView?.setOnTouchListener(this)

        val params = getLayoutParams()

        setViewDrawable()

        mView?.visibility = View.INVISIBLE
        mRootViewGroup?.addView(
            mView,
            params
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

    private fun getLayoutParams(): LinearLayout.LayoutParams {
        val params = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        )
        params.marginStart = Utils.dpToPx(64)
        params.marginEnd = Utils.dpToPx(64)
        return params
    }

    private fun setViewDrawable() {
        val shape = GradientDrawable()
        shape.shape = GradientDrawable.RECTANGLE
        shape.setColor(Color.GRAY)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mView?.clipToOutline = true
            shape.cornerRadius = Utils.dpToPx(5).toFloat()
        }
        mView?.background = shape
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

        mHandler.sendEmptyMessageDelayed(ToastHandlerMessage.MSG_HIDE.value, 3000)

        view?.removeOnLayoutChangeListener(this)
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
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(view: View?, event: MotionEvent?): Boolean {
        ifLet(view, event, mRootViewGroup) {
            val touchY = event!!.rawY
            val currentViewY = view!!.y

            when (event.action and MotionEvent.ACTION_MASK) {
                MotionEvent.ACTION_DOWN -> {
                    mHandler.sendEmptyMessage(ToastHandlerMessage.MSG_REMOVE_TIMER.value)
                    mYDelta = touchY - currentViewY
                }
                MotionEvent.ACTION_UP -> {
                    mHandler.sendEmptyMessage(ToastHandlerMessage.MSG_ADD_TIMER.value)
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

enum class ToastHandlerMessage(val value: Int) {
    MSG_SHOW(1),
    MSG_HIDE(2),
    MSG_REMOVE_TIMER(3),
    MSG_ADD_TIMER(4)
}
