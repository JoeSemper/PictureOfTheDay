package com.joesemper.pictureoftheday.util

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View

/**
 * Takes another view as a substance and draws its content.
 *
 * This is useful for copying an appearance of another view without spending the cost of full
 * instantiation.
 *
 * @see SharedFade
 */
class MirrorView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    init {
        setWillNotDraw(true)
    }

    private var _substance: View? = null
    var substance: View?
        get() = _substance
        set(value) {
            _substance = value
            setWillNotDraw(value == null)
        }

    override fun onDraw(canvas: Canvas?) {
        _substance?.draw(canvas)
    }
}