package com.readboy.localsvgmap

import android.graphics.*
import androidx.annotation.ColorInt

class MapData(var name: String, var path: Path, @param:ColorInt private var fillColor: Int, @param:ColorInt private val strokeColor: Int, private val strokeWidth: Float) {

    private val paint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    var isSelected = false

    fun onDraw(canvas: Canvas) {
        if (isSelected){
            //绘制省份背景 因为是被选中的省份需要绘制有阴影
            paint.strokeWidth = 5f
            paint.color = Color.BLACK //设置黑色
            paint.style = Paint.Style.FILL
            // setShadowLayer 这将在主层下面绘制一个阴影层，并指定 偏移，颜色，模糊半径。如果半径是0，那么阴影删除层。
            paint.setShadowLayer(8f, 0f, 0f, -0x100)
            //直接绘制路径path
            canvas.drawPath(path, paint)
            //需要绘制两次  第一次绘制背景第二次绘制省份选中时，绘制省份
            paint.clearShadowLayer()
        }

        //背景
        if (isSelected) {
            paint.color = Color.RED
        } else {
            paint.color = fillColor
        }

        paint.style = Paint.Style.FILL
        paint.strokeWidth = 1f
        canvas.drawPath(path, paint)

        //边线
        paint.strokeWidth = strokeWidth
        paint.color = strokeColor
        paint.style = Paint.Style.STROKE
        canvas.drawPath(path, paint)

        //文字
        paint.style = Paint.Style.FILL_AND_STROKE
        paint.strokeWidth = 0.1f
        paint.color = Color.BLACK
        paint.textSize = 15f

        val rectF = RectF()
        path.computeBounds(rectF, true)
        canvas.drawText((if (name.isEmpty()) "" else name), rectF.centerX() - paint.measureText(name) / 2, rectF.centerY(), paint)
    }

    fun isContainXY(x: Float, y: Float): Boolean {
        val rectF = RectF()
        path.computeBounds(rectF, true)
        val region = Region()
        region.setPath(path, Region(rectF.left.toInt(), rectF.top.toInt(), rectF.right.toInt(), rectF.bottom.toInt()))
        return region.contains(x.toInt(), y.toInt())
    }
}