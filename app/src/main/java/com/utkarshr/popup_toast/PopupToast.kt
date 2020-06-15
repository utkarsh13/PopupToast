package com.utkarshr.popup_toast

import android.animation.Animator
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.media.Image
import android.os.Build
import android.os.Handler
import android.os.Message
import android.view.*
import android.view.animation.OvershootInterpolator
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.GestureDetectorCompat
import java.lang.ref.WeakReference
import kotlin.math.abs

class PopupToast(private val mContext: Context): View.OnLayoutChangeListener, View.OnTouchListener {

    var mView: View? = null
    var mRootViewGroup: ViewGroup? = null

    private lateinit var mHandler: Handler

    private lateinit var mGestureDetector: GestureDetectorCompat

    private var mViewY = 0f
    private var mYDelta = 0f
    private val mPaddingFromBottom = Utils.dpToPx(24).toFloat()
    private var mToastDuration = 3000   //time in ms
    private var mPostHoldDuration = 1000   //time in ms

    init {
        val activityFromContext = Utils.getActivity(mContext)

        activityFromContext?.let {activity ->
            mGestureDetector = GestureDetectorCompat(mContext, FlingGestureListener(this))
            mRootViewGroup = activity.window.decorView.rootView.findViewById(android.R.id.content) as ViewGroup
            mHandler = ToastHandler(this)

            createView(mContext)
        }
    }

    @SuppressLint("InflateParams")
    private fun createView(context: Context) {
        if (mView != null) {
            return
        }
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        mView = inflater.inflate(R.layout.view_popup_toast, null)

        //setting some initial position
        mView?.x = Utils.dpToPx(200).toFloat()
        mView?.y = Utils.dpToPx(200).toFloat()

        mView?.addOnLayoutChangeListener(this)
        mView?.setOnTouchListener(this)

        setViewDrawable()
    }

    private fun addViewToSuperview() {
        val params = getLayoutParams()

        mView?.visibility = View.INVISIBLE
        mRootViewGroup?.addView(
                mView,
                params
        )
    }

    private fun removeView() {
        mView?.let {
            val finalY = mRootViewGroup!!.height + Utils.dpToPx(16).toFloat()
            val anim = ObjectAnimator.ofFloat(it, "translationY", finalY)
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

    private fun getLayoutParams(): LinearLayout.LayoutParams {
        val params = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        )
        params.marginStart = Utils.dpToPx(64)
        params.marginEnd = Utils.dpToPx(64)
        return params
    }

    private fun setViewDrawable(color: Int = Color.WHITE) {
        val shape = GradientDrawable()
        shape.shape = GradientDrawable.RECTANGLE
        shape.setColor(color)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mView?.clipToOutline = true
            shape.cornerRadius = Utils.dpToPx(5).toFloat()
        }
        mView?.background = shape
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

        mViewY = finalY

        val anim = ObjectAnimator.ofFloat(view, "translationY", finalY)
        anim.duration = 300
        anim.interpolator = OvershootInterpolator(2f)
        anim.start()

        mHandler.sendEmptyMessageDelayed(ToastHandlerMessage.MSG_HIDE.value, mToastDuration.toLong())

        view?.removeOnLayoutChangeListener(this)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(view: View?, event: MotionEvent?): Boolean {
        Utils.ifLet(view, event, mRootViewGroup) {
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
        return !mGestureDetector.onTouchEvent(event)
    }

    fun setText(text: String, color: Int = Color.WHITE): PopupToast {
        mView?.findViewById<TextView>(R.id.popup_text)?.let {
            it.text = text
            it.setTextColor(color)
        }
        return this
    }

    fun setDuration(duration: Int): PopupToast {
        mToastDuration = duration
        return this
    }

    fun setThemeColor(color: Int): PopupToast {
        mView?.findViewById<View>(R.id.left_view)?.setBackgroundColor(color)
        return this
    }

    fun setBgColor(color: Int): PopupToast {
        setViewDrawable(color)
        return this
    }

    fun setIcon(resId: Int, color: Int = Color.WHITE): PopupToast {
        mView?.findViewById<ImageView>(R.id.image_view)?.let {
            it.setImageResource(resId)
            it.setColorFilter(color)
        }
        return this
    }

    fun show() {
        mHandler.sendEmptyMessage(ToastHandlerMessage.MSG_SHOW.value)
    }

    private class FlingGestureListener(popupToast: PopupToast) : GestureDetector.SimpleOnGestureListener() {

        private val weakToast: WeakReference<PopupToast> = WeakReference(popupToast)

        companion object {
            val flingThreshold = Utils.dpToPx(24)
            const val velocityThreshold = 100
        }

        override fun onFling(
            event1: MotionEvent,
            event2: MotionEvent,
            velocityX: Float,
            velocityY: Float
        ): Boolean {

            val popupToast = weakToast.get()

            popupToast?.let {
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

    private class ToastHandler(popupToast: PopupToast) : Handler() {
        private val weakToast: WeakReference<PopupToast> = WeakReference(popupToast)

        override fun handleMessage(msg: Message) {
            val popupToast = weakToast.get()
            popupToast?.let {
                when (msg.what) {
                    ToastHandlerMessage.MSG_SHOW.value -> {
                        it.addViewToSuperview()
                    }
                    ToastHandlerMessage.MSG_HIDE.value -> {
                        it.mHandler.removeMessages(ToastHandlerMessage.MSG_HIDE.value)
                        it.removeView()
                    }
                    ToastHandlerMessage.MSG_REMOVE_TIMER.value -> {
                        it.mHandler.removeMessages(ToastHandlerMessage.MSG_HIDE.value)
                        it.mHandler.removeMessages(ToastHandlerMessage.MSG_REMOVE_TIMER.value)
                    }
                    ToastHandlerMessage.MSG_ADD_TIMER.value -> {
                        it.mHandler.sendEmptyMessageDelayed(ToastHandlerMessage.MSG_HIDE.value, it.mPostHoldDuration.toLong())
                    }
                    else -> {
                    }
                }
            }

        }

    }
}