package com.udacity

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.core.content.withStyledAttributes
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        // Paint styles used for rendering are initialized here. This
        // is a performance optimization, since onDraw() is called
        // for every screen refresh.s
        isAntiAlias = true
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 55.0f
        typeface = Typeface.create("", Typeface.BOLD)
    }

    private var widthSize = 0
    private var heightSize = 0

    private var clickedColor = 0
    private var loadingColor = 0
    private var cirColor = 0

    private var bText = "DOWNLOAD"
    private var buttonWidthLoading = 0F
    private var cirAngleLoading = 0f

    private var recAnimator = ValueAnimator()
    private var cirAnimator = ValueAnimator()

    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->
        when (new) {
            ButtonState.Loading -> {
                recAnimator = ValueAnimator.ofFloat(0f, measuredWidth.toFloat()).apply {
                    duration = 8000
                    interpolator = DecelerateInterpolator()
                    addUpdateListener {
                        buttonWidthLoading = animatedValue as Float
                        this@LoadingButton.invalidate()
                    }
                    start()
                }
                cirAnimator = ValueAnimator.ofFloat(0f, 360f).apply {
                    duration = 8000
                    bText = "We Are Loading"

                    interpolator = AccelerateInterpolator(1f)
                    addUpdateListener {
                        cirAngleLoading = animatedValue as Float
                        this@LoadingButton.invalidate()
                    }
                    start()
                }

            }
            ButtonState.Completed -> {
                buttonWidthLoading = 0F
                cirAngleLoading = 0f
                bText = "COMPLETED"
                recAnimator.end()
                cirAnimator.end()
                setButState(ButtonState.Clicked)

            } else -> {
                ButtonState.Clicked
                buttonWidthLoading = 0f
                cirAngleLoading = 0f
                bText = "DOWNLOAD"
                recAnimator.cancel()
                cirAnimator.cancel()
            }

        }
    }

    fun setButState(state: ButtonState) {
        buttonState = state
    }

    init {
        isClickable = true
        context.withStyledAttributes(attrs, R.styleable.LoadingButton) {
            clickedColor = getColor(R.styleable.LoadingButton_buttColor1, 0)
            loadingColor = getColor(R.styleable.LoadingButton_buttColor2, 0)
            cirColor = getColor(R.styleable.LoadingButton_cirColor, 0)
        }
    }

    override fun performClick(): Boolean {
        super.performClick()

        invalidate()
        return true
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        // Set dial background color based on the selection.
        paint.color = clickedColor
        val rectDimens = Rect(0,0, measuredWidth, measuredHeight)
        val rectF = RectF(rectDimens)
        canvas?.drawRoundRect(rectF, 15F, 15F, paint)

        val rectDimens1 = Rect(0,0, buttonWidthLoading.toInt(), measuredHeight)
        val rectF1 = RectF(rectDimens1)
        paint.color = loadingColor
        canvas?.drawRoundRect(rectF1, 15F, 15F, paint)

        paint.color = Color.WHITE
        paint.textSize = 36F
        canvas?.drawText(bText, widthSize.toFloat()/2, heightSize.toFloat()/2+12, paint)

        paint.color = cirColor
        canvas?.drawArc(measuredWidth - 100f,
            (measuredHeight/2) - 30f,
            measuredWidth - 50f,
            (measuredHeight/2) + 30f,
        0f, cirAngleLoading, true, paint )
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }

}