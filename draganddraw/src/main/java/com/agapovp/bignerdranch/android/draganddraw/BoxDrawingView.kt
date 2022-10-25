package com.agapovp.bignerdranch.android.draganddraw

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import kotlinx.parcelize.Parcelize
import kotlin.math.atan2
import kotlin.math.max
import kotlin.math.min

class BoxDrawingView(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {

    private val boxen = mutableListOf<Box>()
    private val boxPaint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.dark_blue)
    }
    private val backgroundPaint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.pale_yellow)
    }

    private var currentBox: Box? = null
    private var pointerID1 = MotionEvent.INVALID_POINTER_ID
    private var pointerID2 = MotionEvent.INVALID_POINTER_ID
    private var pointerX1 = 0f
    private var pointerY1 = 0f
    private var pointerX2 = 0f
    private var pointerY2 = 0f
    private var angle = 0F
    private var currentAngle = 0.0F

    init {
        contentDescription = resources.getQuantityString(R.plurals.rectangles, 0, 0)
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        (state as? Bundle)?.let { bundle ->
            super.onRestoreInstanceState(bundle.getParcelable(KEY_PARENT_STATE))
            angle = bundle.getFloat(KEY_VIEW_STATE_ANGLE)
            bundle.getParcelableArrayList<Box.ParcelableBox>(KEY_VIEW_STATE_BOXEN)?.let { list ->
                boxen.addAll(list.map(Box.ParcelableBox::toBox))
            }
        }

        contentDescription =
            resources.getQuantityString(R.plurals.rectangles, boxen.size, boxen.size)
    }

    override fun onSaveInstanceState(): Parcelable =
        bundleOf(
            KEY_VIEW_STATE_BOXEN to boxen.map(Box::toParcelableBox),
            KEY_VIEW_STATE_ANGLE to angle,
            KEY_PARENT_STATE to super.onSaveInstanceState()
        )

    override fun onDraw(canvas: Canvas) {

        canvas.drawPaint(backgroundPaint)
        canvas.rotate(angle + currentAngle, (width / 2).toFloat(), (height / 2).toFloat())

        boxen.forEach { box ->
            canvas.drawRect(box.left, box.top, box.right, box.bottom, boxPaint)
        }
        currentBox?.let { canvas.drawRect(it.left, it.top, it.right, it.bottom, boxPaint) }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {

        val current = PointF(event.x, event.y)
        val action: String = when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                pointerID1 = event.getPointerId(event.actionIndex)
                currentBox = Box(current)
                "ACTION_DOWN"
            }
            MotionEvent.ACTION_POINTER_DOWN -> {
                pointerID2 = event.getPointerId(event.actionIndex)
                pointerX1 = event.getX(event.findPointerIndex(pointerID1))
                pointerY1 = event.getY(event.findPointerIndex(pointerID1))
                pointerX2 = event.getX(event.findPointerIndex(pointerID2))
                pointerY2 = event.getY(event.findPointerIndex(pointerID2))
                currentBox = null
                "ACTION_POINTER_DOWN"
            }
            MotionEvent.ACTION_MOVE -> {
                if (pointerID1 != MotionEvent.INVALID_POINTER_ID
                    && pointerID2 != MotionEvent.INVALID_POINTER_ID
                ) {
                    val newPointerX1 = event.getX(event.findPointerIndex(pointerID1))
                    val newPointerY1 = event.getY(event.findPointerIndex(pointerID1))
                    val newPointerX2 = event.getX(event.findPointerIndex(pointerID2))
                    val newPointerY2 = event.getY(event.findPointerIndex(pointerID2))

                    currentAngle = getAngle(newPointerX1, newPointerY1, newPointerX2, newPointerY2)
                    invalidate()
                } else {
                    updateCurrentBox(current)
                }
                "ACTION_MOVE"
            }
            MotionEvent.ACTION_UP -> {
                updateCurrentBox(current)
                currentBox?.let { boxen.add(it) }
                currentBox = null
                contentDescription =
                    resources.getQuantityString(R.plurals.rectangles, boxen.size, boxen.size)
                "ACTION_UP"
            }
            MotionEvent.ACTION_POINTER_UP -> {
                pointerID2 = MotionEvent.INVALID_POINTER_ID
                angle += currentAngle
                currentAngle = 0F
                "ACTION_POINTER_UP"
            }
            MotionEvent.ACTION_CANCEL -> {
                pointerID1 = MotionEvent.INVALID_POINTER_ID
                pointerID2 = MotionEvent.INVALID_POINTER_ID
                currentBox = null
                currentAngle = 0F
                "ACTION_CANCEL"
            }
            else -> event.actionMasked.toString()
        }
        Log.i(TAG, "$action at x = ${current.x}, y = ${current.y}")

        return true
    }

    private fun updateCurrentBox(current: PointF) {
        currentBox?.let { newBox ->
            newBox.end = current
            invalidate()
        }
    }

    /**
     * [RotationGestureDetector.java](https://stackoverflow.com/questions/10682019/android-two-finger-rotation)
     */
    private fun getAngle(firstX: Float, firstY: Float, secondX: Float, secondY: Float): Float {

        val angle1 = atan2(pointerY1 - pointerY2, pointerX1 - pointerX2)
        val angle2 = atan2(firstY - secondY, firstX - secondX)

        var angle = (Math.toDegrees((angle2 - angle1).toDouble()) % 360).toFloat()
        if (angle < -180F) angle += 360F
        if (angle > 180F) angle -= 360F

        return angle
    }

    private class Box(val start: PointF) {

        var end: PointF = start

        val left: Float
            get() = min(start.x, end.x)

        val right: Float
            get() = max(start.x, end.x)

        val top: Float
            get() = min(start.y, end.y)

        val bottom: Float
            get() = max(start.y, end.y)

        fun toParcelableBox(): ParcelableBox = ParcelableBox(start, end)

        @Parcelize
        class ParcelableBox(
            val start: PointF,
            val end: PointF
        ) : Parcelable {
            fun toBox(): Box = Box(start).apply { end = this@ParcelableBox.end }
        }
    }

    companion object {
        private const val TAG = "BoxDrawingView"

        private const val KEY_PARENT_STATE = "KEY_PARENT_STATE"
        private const val KEY_VIEW_STATE_BOXEN = "KEY_VIEW_STATE_BOXEN"
        private const val KEY_VIEW_STATE_ANGLE = "KEY_VIEW_STATE_ANGLE"
    }
}
