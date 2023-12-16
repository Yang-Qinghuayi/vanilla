package com.example.m5.util

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView


@SuppressLint("AppCompatCustomView")
class BlurBGImageView : ImageView {
    private var overlay: Bitmap? = null
    private var scaleFactor = 2
    private var radius = 8

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    fun setScaleFactor(scaleFactor: Int) {
        this.scaleFactor = scaleFactor
    }

    fun refreshBG(bgView: View) {
        bgView.isDrawingCacheEnabled = true
        bgView.buildDrawingCache()
        var bitmap1: Bitmap? = null
        try {
            bitmap1 = view2Bitmap(bgView)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        if (bitmap1 != null) {
            blur(bitmap1, this, radius.toFloat()) //模糊处理
            bitmap1.recycle()
        }
        bgView.isDrawingCacheEnabled = false //清除缓存
    }

    private fun blur(bkg: Bitmap, view: ImageView, radius: Float) {
        if (overlay != null) {
            overlay!!.recycle()
        }
        overlay =
            Bitmap.createScaledBitmap(bkg, bkg.width / scaleFactor, bkg.height / scaleFactor, false)
        overlay = blur(context, overlay, radius) //高斯模糊
        view.setImageBitmap(overlay)
    }

    private fun blur(context: Context, image: Bitmap?, radius: Float): Bitmap {
        val rs = RenderScript.create(context)
        val outputBitmap = Bitmap.createBitmap(image!!.width, image.height, Bitmap.Config.ARGB_8888)
        val `in` = Allocation.createFromBitmap(rs, image)
        val out = Allocation.createFromBitmap(rs, outputBitmap)
        val intrinsicBlur = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs))
        intrinsicBlur.setRadius(radius)
        intrinsicBlur.setInput(`in`)
        intrinsicBlur.forEach(out)
        out.copyTo(outputBitmap)
        image.recycle()
        rs.destroy()
        return outputBitmap
    }

    private fun view2Bitmap(v: View): Bitmap? {
        val w = v.width
        val h = v.height
        val bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val c = Canvas(bmp)
        c.drawColor(Color.WHITE) //如果不设置颜色，默认是透明背景
        v.layout(0, 0, w, h)
        v.draw(c)
        return bmp
    }

}


