package com.box.f2j_circle_progress

import android.content.Context
import android.graphics.*
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import kotlin.math.*



class CircleProgress(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private val mouthPath = Path()
    private val progressPath = Path()

    private val mPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    // Some colors for the face background, eyes and mouth.
    private var progressColor = Color.YELLOW
    private var thumbColor = Color.BLUE
    private var mouthColor = Color.RED
    private var bgPathColor = Color.BLACK
    private var strokeWidth = 100f
    private val progressStroke = 10f

    // Face border width in pixels
    private var borderWidth = 4.0f

    // View size in pixels
    private var size = 500
    private val radius: Float
        get() = size / 2 - (mPaint.strokeWidth / 2)
    private val centerX: Float
        get() = (size / 2).toFloat()
    private val centerY: Float
        get() = (size / 2).toFloat()


    private var centerOfPathRadius = 0f

    private var name: String? = "unKnown"

    private var isInit = true


    private var dY: Float = 0f
    private var dX: Float = 0f
    private var angle: Double = 0.0
    private var newCX: Double = 0.0
    private var newCY: Double = 0.0

    //    private var progressStartX  = 0.0
//    private var progressStartY  = 0.0
//
//    private var progressAngle = 0.0
    private var sweepingAngle = 0f

    init {
        mPaint.isAntiAlias = true
        setInit(attrs)
    }


    private fun setInit(attrs: AttributeSet) {

        context.theme.obtainStyledAttributes(attrs, R.styleable.CircleProgress, 0, 0).apply {
            try {

                thumbColor = getColor(R.styleable.CircleProgress_thumb_color,Color.YELLOW)
                bgPathColor= getColor(R.styleable.CircleProgress_bg_path_color,Color.LTGRAY)
                strokeWidth = getFloat(R.styleable.CircleProgress_strock_width,100f)
                progressColor = getColor(R.styleable.CircleProgress_progress_color,Color.YELLOW)

            } finally {
                recycle()
            }
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        Log.e("CustomView0", "onDraw: ")

        canvas?.let {
            drawFace(it)
//            drawEyes(it)
//            drawMouth(it)
        }
    }

    private fun drawFace(canvas: Canvas) {

        mPaint.apply {
            color = bgPathColor
            style = Paint.Style.STROKE
            strokeWidth = this@CircleProgress.strokeWidth
        }
        canvas.drawCircle(centerX, centerY, radius, mPaint)
        centerOfPathRadius = radius

        if (isInit) {

            mPaint.apply {
                color = thumbColor
                style = Paint.Style.FILL
            }
            canvas.drawCircle(centerX, centerY - centerOfPathRadius, strokeWidth / 2, mPaint)
            isInit = false
        } else {

            mPaint.apply {
                color = progressColor
                style = Paint.Style.STROKE
                strokeWidth = progressStroke
            }
            setProgressRectF()
            canvas.drawPath(progressPath, mPaint)
            mPaint.apply {
                color = thumbColor
                style = Paint.Style.FILL
            }
            canvas.drawCircle(
                newCX.toFloat(),
                newCY.toFloat(),
                this@CircleProgress.strokeWidth / 2,
                mPaint
            )

        }
    }
    private fun setProgressRectF() {
        sweepingAngle = when {
            90 > angle && angle > 0 -> {
                90 - angle
            }
            angle > 90 && angle < 180 -> {
                450 - angle
            }
            angle < 0 && angle > -180 -> {
                90 + (-angle)
            }
            else -> {
                0
            }
        }.toFloat()
        progressPath.reset()
        progressPath.addArc(
            centerX - centerOfPathRadius,
            centerY - centerOfPathRadius,
            centerX + centerOfPathRadius,
            centerY + centerOfPathRadius,
            270f,
            sweepingAngle
        )
    }

    fun setPercent(percentFloat: Float){
        if(percentFloat < 1.0 && percentFloat > 0.0){
            angle = when{
                percentFloat < 0.25-> (360 * (0.25 - percentFloat))
                percentFloat < 0.75 ->(-360 *(0.25 - percentFloat))
                else->(1.25 - percentFloat) * 360
            }
            Log.d("CustomView0", ">>  setPercent:  ####  angle:${angle}  ####")

            newCX = centerX + (centerOfPathRadius * cos(Math.toRadians(angle)))
            newCY = centerY - (centerOfPathRadius * sin(Math.toRadians(angle)))

            invalidate()
        }
    }

    fun getPercent():Float{
        Log.d("CustomView0", ">>  getPercent:  ####  sweepingAngle:$sweepingAngle  ####")
        return (sweepingAngle /  360)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        Log.e("CustomView0", "onMeasure: ")
        size = min(measuredHeight, measuredWidth)

        setMeasuredDimension(size, size)
    }



    private fun centerTouchCheck(x: Float, y: Float) {
        val touchRadius = sqrt((x - centerX).pow(2) + (y - centerY).pow(2))

        /**
         * very important point
         * */
        val radiusLenCondition = touchRadius > radius - strokeWidth && touchRadius < radius
        if (radiusLenCondition) {
            Log.d("CustomView0", ">>  centerTouchCheck:  ####  yes right area  ####")
            calculatePathCenterCoordinate(x, y)

        }
    }

    private fun calculatePathCenterCoordinate(touchX: Float, touchY: Float) {
        mPaint.apply {
            color = progressColor
            style = Paint.Style.FILL
        }
//        val dX = touchX - centerX
        dY = touchY - centerY/*if(touchY > centerY) touchY - centerY else centerY -  touchY*/
        dX = touchX - centerX/*if(touchX > centerX) touchX - centerX else centerX - touchX*/
        angle = -Math.toDegrees(atan2(dY, dX).toDouble())

        newCX = centerX + (centerOfPathRadius * cos(Math.toRadians(angle)))
        newCY = centerY - (centerOfPathRadius * sin(Math.toRadians(angle)))

//        progressAngle = -Math.toDegrees(atan2())
        Log.d(
            "CustomView0",
            ">>  calculatePathCenterCoordinate:  ####  cos(angle):${cos(angle)}  ####"
        )
        Log.d(
            "CustomView0",
            ">>  calculatePathCenterCoordinate:  ####  sin(angle):${sin(angle)}  ####"
        )
        Log.d("CustomView0", ">>  calculatePathCenterCoordinate:  ####  angle:$angle  ####")
        Log.d("CustomView0", ">>  calculatePathCenterCoordinate:  ####  newCX:$newCX  ####")
        Log.d("CustomView0", ">>  calculatePathCenterCoordinate:  ####  newCY:$newCY  ####")


        invalidate()
//        requestLayout()
    }


    private var originEmotion = 0

    override fun onTouchEvent(event: MotionEvent?): Boolean {

        return when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                Log.d("CustomView0", ">>  onTouchEvent:  ####  action_down  ####")
                true
            }
            MotionEvent.ACTION_UP -> {
                Log.d("CustomView0", ">>  onTouchEvent:  ####  action_up  ####")
                true
            }
            MotionEvent.ACTION_MOVE -> {
                centerTouchCheck(event.x, event.y)
                true
            }
            else -> true
        }
    }

    override fun onSaveInstanceState(): Parcelable? {
        val b = Bundle()
//        b.putInt("emotion", emotion)
        b.putParcelable("super", super.onSaveInstanceState())
        return b
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        var viewState = state
        if (viewState is Bundle) {
//            emotion = viewState.getInt("emotion")
            viewState = viewState.getParcelable("super")
        }
        super.onRestoreInstanceState(viewState)
    }


    private fun drawEyes(canvas: Canvas) {
        mPaint.apply {
            style = Paint.Style.FILL
//            color = eyesColor
        }
        val leftRect = RectF(size * 0.2f, size * 0.2f, size * 0.4f, size * 0.4f)
        val rightRect = RectF(size * 0.6f, size * 0.2f, size * 0.8f, size * 0.4f)
        canvas.apply {
            drawOval(leftRect, mPaint)
            drawOval(rightRect, mPaint)
        }
    }


    private fun drawMouth(canvas: Canvas) {
        mouthPath.reset()
        mouthPath.moveTo(size * 0.22f, size * 0.7f)
//        when (emotion) {
//            Emotion.HAPPY.which -> {
//                mouthPath.quadTo(size * 0.5f, size * 0.80f, size * 0.78f, size * 0.7f)
//                mouthPath.quadTo(size * 0.5f, size * 0.90f, size * 0.22f, size * 0.7f)
//            }
//            Emotion.SAD.which -> {
//                mouthPath.quadTo(size * 0.5f, size * 0.50f, size * 0.78f, size * 0.7f)
//                mouthPath.quadTo(size * 0.5f, size * 0.60f, size * 0.22f, size * 0.7f)
//            }
//            Emotion.NORM.which -> {
//                mouthPath.quadTo(size * 0.5f, size * 0.70f, size * 0.78f, size * 0.7f)
//                mouthPath.quadTo(size * 0.78f, size * 0.80f, size * 0.5f, size * 0.8f)
//                mouthPath.quadTo(size * 0.22f, size * 0.80f, size * 0.22f, size * 0.7f)
//            }
//        }
//        mPaint.apply {
//            color = mouthColor
//            style = Paint.Style.FILL
//        }
//        canvas.drawPath(mouthPath, mPaint)
    }
}







