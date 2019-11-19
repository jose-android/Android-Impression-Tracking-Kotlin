package com.example.impressiontutorialstepthrough

import android.app.Activity
import android.graphics.Rect
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import java.util.*

class VisibilityTracker(activity: Activity) {
    private val mTrackedViews = WeakHashMap<View, TrackingInfo>()
    private var mOnPreDrawListener: ViewTreeObserver.OnPreDrawListener? = null
    private var mVisibilityTrackerListener: VisibilityTrackerListener? = null
    private var mIsVisibilityCheckScheduled: Boolean = false
    private val mVisibilityChecker: VisibilityChecker
    private val mVisibilityHandler: Handler
    private val mVisibilityRunnable: Runnable


    interface VisibilityTrackerListener {
        fun onVisibilityChanged(visibleViews: List<View>, invisibleViews: List<View>)
    }

    internal class TrackingInfo {
        var mRootView: View? = null
        var mMinVisiblePercent: Int = 100
    }

    init {
        val rootView = activity.window.decorView
        val viewTreeObserver = rootView.viewTreeObserver

        mVisibilityHandler = Handler()
        mVisibilityChecker = VisibilityChecker()
        mVisibilityRunnable = VisibilityRunnable()

        if (viewTreeObserver.isAlive) {
            mOnPreDrawListener = ViewTreeObserver.OnPreDrawListener {
                scheduleVisibilityCheck()
                true
            }
            viewTreeObserver.addOnPreDrawListener(mOnPreDrawListener)
        } else {
            Log.d(VisibilityTracker::class.java.simpleName, "Visibility tracker root view is not alive")
        }
    }

    fun addView(view: View, minVisiblePercentageViewed: Int) {

        var trackingInfo: TrackingInfo? = mTrackedViews[view]
        if (trackingInfo == null) {
            trackingInfo = TrackingInfo()
            mTrackedViews[view] = trackingInfo
            scheduleVisibilityCheck()
        }


        trackingInfo.mRootView = view
        trackingInfo.mMinVisiblePercent = minVisiblePercentageViewed
    }

    fun setVisibilityTrackerListener(listener: VisibilityTrackerListener) {
        mVisibilityTrackerListener = listener
    }

    fun removeVisibilityTrackerListener() {
        mVisibilityTrackerListener = null
    }

    private fun scheduleVisibilityCheck() {
        if (mIsVisibilityCheckScheduled) {
            return
        }
        mIsVisibilityCheckScheduled = true
        mVisibilityHandler.postDelayed(mVisibilityRunnable, VISIBILITY_CHECK_DELAY_MILLIS)
    }


    internal class VisibilityChecker {
        private val mClipRect = Rect()

        fun isVisible(view: View?, minPercentageViewed: Int): Boolean {
            if (view == null || view.visibility != View.VISIBLE || view.parent == null) {
                return false
            }

            if (!view.getGlobalVisibleRect(mClipRect)) {
                return false
            }

            val visibleArea = mClipRect.height().toLong() * mClipRect.width()
            val totalViewArea = view.height.toLong() * view.width

            return totalViewArea > 0 && 100 * visibleArea >= minPercentageViewed * totalViewArea
        }
    }

    internal inner class VisibilityRunnable : Runnable {
        private val mVisibleViews: MutableList<View>
        private val mInvisibleViews: MutableList<View>

        init {
            mVisibleViews = ArrayList()
            mInvisibleViews = ArrayList()
        }

        override fun run() {
            mIsVisibilityCheckScheduled = false
            for ((view, value) in mTrackedViews) {
                val minPercentageViewed = value.mMinVisiblePercent

                if (mVisibilityChecker.isVisible(view, minPercentageViewed)) {
                    mVisibleViews.add(view)
                } else
                    mInvisibleViews.add(view)
            }

            if (mVisibilityTrackerListener != null) {
                mVisibilityTrackerListener!!.onVisibilityChanged(mVisibleViews, mInvisibleViews)
            }

            mVisibleViews.clear()
            mInvisibleViews.clear()
        }
    }

    companion object {
        const val VISIBILITY_CHECK_DELAY_MILLIS: Long = 1000
    }
}