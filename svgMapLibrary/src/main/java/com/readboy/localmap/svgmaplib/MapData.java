package com.readboy.localmap.svgmaplib;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.ColorInt;

import com.google.gson.Gson;

import org.w3c.dom.Text;

public class MapData {
    /** 名称 */
    private String name;
    /** 地图边缘路径 */
    private Path path;
    /** 填充颜色 */
    @ColorInt
    private int fillColor;
    /** 线颜色 */
    @ColorInt
    private int strokeColor;
    /** 线宽 */
    private float strokeWidth;
    /** 线alpha */
    private float strokeAlpha;
    /** 填充alpha */
    private float fillAlpha;
    /** cap类型 */
    private String strokeLineCap;
    /** join类型 */
    private String strokeLineJoin;
    /** 填充类型 */
    private String fillType;
    /** 文字大小 */
    private float mTextSize = 15f;

    /** 画刷 */
    private Paint paint;
    /** 被选中的城市 */
    private boolean isSelected;
    /** 用户所在城市 */
    private boolean isUserCity;

    public MapData(String name, Path path, @ColorInt int fillColor, @ColorInt int strokeColor, float strokeWidth) {
        this.name = name;
        this.path = path;
        this.fillColor = fillColor;
        this.strokeColor = strokeColor;
        this.strokeWidth = strokeWidth;
        this.paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        this.isSelected = false;
    }

    public void onDraw(Canvas canvas) {
        if (isUserCity) {
            //绘制省份背景 因为是被选中的省份需要绘制有阴影
            paint.setStrokeWidth(5f);
            paint.setColor(Color.BLACK); //设置黑色
            paint.setStyle(Paint.Style.FILL);
            // setShadowLayer 这将在主层下面绘制一个阴影层，并指定 偏移，颜色，模糊半径。如果半径是0，那么阴影删除层。
            paint.setShadowLayer(8f, 0f, 0f, -0x100);
            //直接绘制路径path
            canvas.drawPath(path, paint);
            //需要绘制两次  第一次绘制背景第二次绘制省份选中时，绘制省份
            paint.clearShadowLayer();
        }

        //背景
        if (isSelected) {
            paint.setColor(0xff78a5ff);
        } else if (isUserCity){
            paint.setColor(0xff9dbbf9);
        } else {
            paint.setColor(fillColor);
        }
        if (TextUtils.isEmpty(strokeLineCap)){
            paint.setStrokeCap(Paint.Cap.BUTT);
        } else if ("round".equals(strokeLineCap)) {
            paint.setStrokeCap(Paint.Cap.ROUND);
        } else if ("square".equals(strokeLineCap)) {
            paint.setStrokeCap(Paint.Cap.SQUARE);
        } else {
            // 默认为 butt，可以不设置
            paint.setStrokeCap(Paint.Cap.BUTT);
        }

        if (TextUtils.isEmpty(strokeLineJoin)){
            paint.setStrokeJoin(Paint.Join.MITER);
        } else if ("round".equals(strokeLineJoin)) {
            paint.setStrokeJoin(Paint.Join.ROUND);
        } else if ("bevel".equals(strokeLineJoin)) {
            paint.setStrokeJoin(Paint.Join.BEVEL);
        } else {
            // 默认为 MITER，可以不设置
            paint.setStrokeJoin(Paint.Join.MITER);
        }

        if (TextUtils.isEmpty(fillType)){
            path.setFillType(Path.FillType.WINDING);
        } else if ("evenodd".equals(fillType)) {
            path.setFillType(Path.FillType.EVEN_ODD);
        } else {
            // 默认为 MITER，可以不设置
            path.setFillType(Path.FillType.WINDING);
        }
        paint.setStrokeWidth(strokeWidth);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawPath(path, paint);

        //边线
        paint.setStrokeWidth(strokeWidth);
        paint.setColor(strokeColor);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawPath(path, paint);

        onDrawText(canvas);

    }



    public void onDrawText(Canvas canvas) {
        //文字
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setStrokeWidth(0.1f);
        paint.setColor(0xff103786);
        paint.setTextSize(mTextSize);

        RectF rectF = new RectF();
        path.computeBounds(rectF, true);
        String strText = "";
        if (isSelected && !isUserCity){
            strText = TextUtils.isEmpty(name) ? "" : name;
        } else if (isUserCity){
            strText = name+"(我的)";
        } else {
            strText = name;
        }
        canvas.drawText(strText, rectF.centerX() - paint.measureText(strText) / 2, rectF.centerY(), paint);
    }


    public boolean isContainXY(float x, float y) {
        RectF rectF = new RectF();
        path.computeBounds(rectF, true);
        Region region = new Region();
        region.setPath(path, new Region((int) rectF.left, (int) rectF.top, (int) rectF.right, (int) rectF.bottom));
        return region.contains((int) x, (int) y);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    @ColorInt
    public int getFillColor() {
        return fillColor;
    }

    public void setFillColor(@ColorInt int fillColor) {
        this.fillColor = fillColor;
    }

    @ColorInt
    public int getStrokeColor() {
        return strokeColor;
    }

    public void setStrokeColor(@ColorInt int strokeColor) {
        this.strokeColor = strokeColor;
    }

    public float getStrokeWidth() {
        return strokeWidth;
    }

    public void setStrokeWidth(float strokeWidth) {
        this.strokeWidth = strokeWidth;
    }

    public float getStrokeAlpha() {
        return strokeAlpha;
    }

    public void setStrokeAlpha(float strokeAlpha) {
        this.strokeAlpha = strokeAlpha;
    }

    public float getFillAlpha() {
        return fillAlpha;
    }

    public void setFillAlpha(float fillAlpha) {
        this.fillAlpha = fillAlpha;
    }

    public String getStrokeLineCap() {
        return strokeLineCap;
    }

    public void setStrokeLineCap(String strokeLineCap) {
        this.strokeLineCap = strokeLineCap;
    }

    public String getStrokeLineJoin() {
        return strokeLineJoin;
    }

    public void setStrokeLineJoin(String strokeLineJoin) {
        this.strokeLineJoin = strokeLineJoin;
    }

    public String getFillType() {
        return fillType;
    }

    public void setFillType(String fillType) {
        this.fillType = fillType;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public float getTextSize() {
        return mTextSize;
    }

    public void setTextSize(float mTextSize) {
        this.mTextSize = mTextSize;
    }

    public boolean isUserCity() {
        return isUserCity;
    }

    public void setUserCity(boolean userCity) {
        isUserCity = userCity;
    }

    @Override
    public String toString(){
        return new Gson().toJson(this);
    }
}