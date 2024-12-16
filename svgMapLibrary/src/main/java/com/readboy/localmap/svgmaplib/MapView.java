package com.readboy.localmap.svgmaplib;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapView extends View {
    /** 是否允许放大和移动 */
    private final boolean moveScaleEnable = false;
    /** 窗口放大比例 */
    private float scaleWin = 0.5f;
    /** 用户绘制时候的偏移量X */
    private float winOffsetX = 0.0f;
    /** 用户绘制时候的偏移量Y */
    private float winOffsetY = 0.0f;
    /** 显示的大小width */
    private float minWidth;
    /** 显示的大小height */
    private float minHeight;
    /** 矢量图片的大小width */
    private float svgWidth = 0f;
    /** 矢量图片的大小height */
    private float svgHeight = 0f;
    /** 矢量地图path数据 */
    private List<MapData> mapDatas = null;
    /** 地图名称 */
    private String mapName = null;
    /** 当前选中的地图对应的整个path区域 */
    private Path mMapPathFull = null;
    /** 区域大小 */
    private RectF mMapPathFullRectF = null;
    /** 用来绘制考区文字的paint */
    private Paint mPaint = null;
    /** 选中某个区域的回调 */
    private OnProvinceClickListener onProvinceClickListener;
    /** 返回上一级的回调 */
    private OnBlankClickListener onBlankClickListener;
    /** 地图里的省份数据 */
    private Map<String, String> mMapName = new HashMap<String, String>();
    /** 旋转角度动画用 */
    private float rotationAngle = 0.f;
    //=======================================
    /** 属性变量,暂时不支持手势滑动移动 */
    private float transX = 0f;
    private float transY = 0f;
    private float scaled = 1f; // 伸缩比例
    /** 0=未选择，1=拖动，2=缩放 */
    private int moveType = 0;    // 0=未选择，1=拖动，2=缩放
    /** 移动过程中临时变量,暂时不支持手势放大缩小 */
    private float actionX = 0f;
    private float actionY = 0f;
    private float spacing = 0f;
    private float degree = 0f;
    private long currentMS = 0L;
    //=======================================

    public MapView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MapView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        minWidth = dp2px(getContext(), 200f);//800f// 显示的最小大小
        minHeight = dp2px(getContext(), 160f);//640f// 显示的最小大小
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaint.setStrokeWidth(0.1f);
        mPaint.setColor(Color.RED);
        mPaint.setTextSize(15f);

        setData(R.raw.chinahigh_full, "中华人民共和国");    // 初始化svg地图数据

        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // 移除监听器以避免多次调用
                getViewTreeObserver().removeOnGlobalLayoutListener(this);
                // 获取视图的宽高
                minWidth = getWidth();
                minHeight = getHeight();
                updateMapViewOffsetWhenChange();// 更新放大倍数和偏移量并更新地图
//                Log.d("", "===divhee===Width: " + minWidth + ", Height: " + minHeight);
            }
        });

        startRotationAnimation();
    }

    /**
     * 大小尺寸的转换
     * @param context
     * @param dpValue
     * @return
     */
    private static float dp2px(Context context, float dpValue) {
        float fscale = context.getResources().getDisplayMetrics().density;
        return dpValue * fscale + 0.5f;
    }

    /**
     * 更新地图数据
     * @param mapResId
     * @param mapname
     */
    public void setData(int mapResId, String mapname) {
        // Assuming Dom2XmlUtils.dom2xml is a synchronous call for simplicity
        mapName = mapname;
        mapDatas = Dom2XmlUtils.dom2xml(getContext(), mapResId);
        // 地图数据做一个偏移，移动到0，0 start
        mMapPathFull = new Path();
        for (MapData city : mapDatas) {
            mMapPathFull.addPath(city.getPath());//获取整个地图的path路径
            if (!TextUtils.equals(mapName, "中华人民共和国")){
                city.setFillColor(0xffacd1fc);
                city.setStrokeColor(0xffffffff);
                city.setStrokeWidth(1.5f);// 设置基础地图的边线宽度
            } else {
                if (!TextUtils.equals(city.getName(), "争议") && !TextUtils.equals(city.getName(), "国界")
                && !TextUtils.equals(city.getName(), "海洋") && !TextUtils.equals(city.getName(), "省界")){
                    city.setFillColor(0xffacd1fc);
                    city.setStrokeColor(0xffffffff);
                    city.setStrokeWidth(1.0f);// 设置基础地图的边线宽度
                }
                city.setTextSize(7f);
            }
        }
        RectF rectF = new RectF();
        mMapPathFull.computeBounds(rectF, true);
        for (MapData city : mapDatas) {
            city.getPath().offset(-rectF.left, -rectF.top);
        }
        mMapPathFull.offset(-rectF.left, -rectF.top);
        mMapPathFullRectF = new RectF();
        mMapPathFull.computeBounds(mMapPathFullRectF, true);
        // 地图数据做一个偏移，移动到0，0  end

        svgWidth = Dom2XmlUtils.svgWidth;
        svgHeight = Dom2XmlUtils.svgHeight;
        scaleWin = minWidth * 1.0f / svgWidth;
        scaleWin = Math.min(scaleWin, minHeight * 1.0f / svgHeight);
//        Log.d("", "==divhee===============svgWidth==" + svgWidth + " , " + svgHeight);
        measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.AT_MOST), MeasureSpec.makeMeasureSpec(0, MeasureSpec.AT_MOST));
        postInvalidate();

        for (MapData city : mapDatas) {
            mMapPathFull.addPath(city.getPath());//获取整个地图的path路径
            if (TextUtils.equals(city.getName(), mapName)){
                mMapPathFull = new Path(city.getPath());
                mapDatas.remove(city);
                break;
            }
        }
        updateMapViewOffsetWhenChange();// 更新放大倍数和偏移量并更新地图

    }

    /**
     * 更新偏移量
     */
    public void updateMapViewOffsetWhenChange(){
        if (mMapPathFull != null){
            RectF rectF = new RectF();
            mMapPathFull.computeBounds(rectF, true);
            winOffsetX = (minWidth - rectF.width()*scaleWin)/2/scaleWin;
            winOffsetY = (minHeight - rectF.height()*scaleWin)/2/scaleWin;
//            Log.d("", "=1=divhee===============mMapPathFull==" + rectF + "=="+scaleWin);
        }
        postInvalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width = (int) minWidth;
        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            width = Math.min((int)minWidth, widthSize);
        } else {
            width = (int) minWidth;
        }
        width = (width == 0) ? (int)minWidth : width;

        int height = (int) minHeight;
        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            height = Math.min((int) minHeight, heightSize);
        } else {
            height = (int) minHeight;
        }
        height = (height == 0) ? (int)minHeight : height;

        // 更新地图的放大缩小倍数
        scaleWin = width * 1.0f / svgWidth;
        scaleWin = Math.min(scaleWin, height * 1.0f / svgHeight);

        setMeasuredDimension(width, height);
//        Log.d("", "" + width + " , " + height + "=2=divhee===============svgWidth==" + svgWidth + " , " + svgHeight + "===" + scaleWin);
    }

    /**
     * 自动旋转动画
     */
    private void startRotationAnimation() {
        ValueAnimator animator = ValueAnimator.ofFloat(0f, 10f);// 动画从0到20像素
        animator.setDuration(2000);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setRepeatMode(ObjectAnimator.REVERSE);  // 到达终点时反向播放
        animator.addUpdateListener(animation -> {
            rotationAngle = (float) animation.getAnimatedValue();
            invalidate(); // 触发重绘
        });
        animator.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
//        // 背景立体感start
//        for (int inum = 0 ; inum < 10 ; inum++){
//            canvas.save();
//            canvas.scale(scaleWin, scaleWin);// 地图绘制的时候的放大缩小
//            canvas.translate(winOffsetX+inum/scaleWin, winOffsetY+inum/scaleWin);// 地图绘制的时候的偏移，让地图居中显示
//            if (mMapPathFull != null){
//                mPaint.setColor(0x0041BBFF+(0x3a000000-0x01000000*inum));
////                mPaint.setColor(0xffa0c1ff);// 多绘制几次，形成立体感
//                mPaint.setStrokeWidth(1f);
//                canvas.drawPath(mMapPathFull, mPaint);
//            }
//            canvas.restore();
//        }
//        // 背景立体感end
        // 背景阴影效果start
        if (!TextUtils.equals(mapName, "中华人民共和国")){
            if (mMapPathFull != null){
                canvas.save();
                canvas.scale(scaleWin, scaleWin);// 地图绘制的时候的放大缩小
                canvas.translate(winOffsetX, winOffsetY);// 地图绘制的时候的偏移，让地图居中显示
                mPaint.setStrokeWidth(5f);
                mPaint.setColor(0x8041BBFF); //设置黑色
                mPaint.setStyle(Paint.Style.FILL);
                // setShadowLayer 这将在主层下面绘制一个阴影层，并指定 偏移，颜色，模糊半径。如果半径是0，那么阴影删除层。
                mPaint.setShadowLayer(10f, 0f, 5f, 0x8041BBFF);//-0x100
                //直接绘制路径path
                canvas.drawPath(mMapPathFull, mPaint);
                //需要绘制两次  第一次绘制背景第二次绘制省份选中时，绘制省份
                mPaint.clearShadowLayer();
                // 背景阴影效果end
                canvas.restore();
            }
        }
        // 背景阴影效果end

        canvas.save();
        canvas.scale(scaleWin, scaleWin);// 地图绘制的时候的放大缩小
        canvas.translate(winOffsetX, winOffsetY);// 地图绘制的时候的偏移，让地图居中显示
        if (TextUtils.equals(mapName, "中华人民共和国")){
            for (MapData city : mapDatas) {
                city.onDraw(canvas);
            }
        } else {
            if (mMapPathFull != null){
                mPaint.setColor(0xffacd1fc);
                mPaint.setStrokeWidth(1f);
                canvas.drawPath(mMapPathFull, mPaint);
            }
            // 绘制考区名和定位
            mPaint.setColor(0xff103786);
            mPaint.setStrokeWidth(0.2f);
            for (int inum = mapDatas.size() - 1 ; inum >= 0 ; inum--){
                MapData city = mapDatas.get(inum);
                city.onDraw(canvas);
            }
            if (mMapPathFullRectF != null){
                mPaint.setColor(0xff103786);
                mPaint.setStrokeWidth(0.2f);
                String mapShowName = mapName;
                if (TextUtils.equals(mapName, "四川省") || TextUtils.equals(mapName, "青海省") || TextUtils.equals(mapName, "甘肃省")
                        || TextUtils.equals(mapName, "吉林省") || TextUtils.equals(mapName, "黑龙江省") || TextUtils.equals(mapName, "江苏省")) {
                    canvas.drawText(mapShowName, mMapPathFullRectF.left, mMapPathFullRectF.bottom - 30, mPaint);
                } else if (TextUtils.equals(mapName, "湖北省") || TextUtils.equals(mapName, "安徽省")){
                    canvas.drawText(mapShowName, mMapPathFullRectF.left, mMapPathFullRectF.top + 55, mPaint);
                } else {
                    canvas.drawText(mapShowName, mMapPathFullRectF.left, mMapPathFullRectF.top + 20, mPaint);
                }
            }
        }

        canvas.restore();
    }

//    /**
//     * 绘制标题
//     * @param canvas
//     * @param title
//     */
//    public void onDrawTitle(Canvas canvas, String title) {
//        if (mMapPathFull != null && canvas != null && !TextUtils.isEmpty(title)){
//            RectF rectF = new RectF();
//            mMapPathFull.computeBounds(rectF, true);
//            mPaint.setColor(Color.RED);
//            canvas.drawText(title, rectF.left, rectF.top, mPaint);
//
////            Log.d("", "=2=divhee===============mMapPathFull==" + rectF);
////            canvas.drawPath(mMapPathFull, mPaint);
////            drawExamPoint(canvas, 50, 80, false);
//        }
//    }

    /**
     * 绘制考区定点
     * @param canvas
     * @param centerx
     * @param centery
     * @param bSelecte
     */
    public Point drawExamPoint(Canvas canvas, int centerx, int centery, boolean bSelecte){
        Drawable drawable = ContextCompat.getDrawable(getContext(), bSelecte ? R.drawable.ic_star_sel : R.drawable.ic_star_nor);
        if (drawable instanceof VectorDrawable) {
            VectorDrawable vectorDrawable = (VectorDrawable) drawable;
            int width = (bSelecte ? 29 : 29*60/100);
            int height = (bSelecte ? 34 : 29*60/100);//(int)(29 - 10 *(rotationAngle/10.0f))
            int startx = centerx - width/2;
            int starty = centery - height;
            int endx = startx + width;
            int endy = starty + height;
            vectorDrawable.setBounds(startx, starty, endx, endy);

            if (bSelecte){
                canvas.translate(0, rotationAngle); // 沿Y轴平移
            }
            vectorDrawable.draw(canvas);  // 在 Canvas 上绘制
            if (bSelecte){
                canvas.translate(0, -rotationAngle); // 沿Y轴平移回去
            }
            return new Point(startx, starty);
        }
        return new Point(centerx, centery);
    }

    /**
     * 获取图形的中心区域
     * @param path
     * @return
     */
    public PointF getGeometricCenter(Path path) {
        PathMeasure pathMeasure = new PathMeasure(path, false);
        float length = pathMeasure.getLength();

        // 初始化坐标和点的数量
        float[] coords = new float[2];
        float sumX = 0;
        float sumY = 0;
        int numPoints = 100; // 取样点的数量

        // 计算Path上的多个点
        for (int i = 0; i <= numPoints; i++) {
            float position = (length * i) / numPoints;
            pathMeasure.getPosTan(position, coords, null);
            sumX += coords[0];
            sumY += coords[1];
        }

        // 计算几何中心
        return new PointF(sumX / (numPoints + 1), sumY / (numPoints + 1)); // 返回几何中心坐标
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction() & MotionEvent.ACTION_MASK;
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                moveType = 1;
                actionX = event.getRawX();
                actionY = event.getRawY();
                currentMS = System.currentTimeMillis(); // 获取系统时间
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                moveType = 2;
                spacing = getSpacing(event);
                degree = getDegree(event);
                break;
            case MotionEvent.ACTION_MOVE:
                if (moveScaleEnable){
                    if (moveType == 1) {
                        transX += event.getRawX() - actionX;
                        transY += event.getRawY() - actionY;
                        setTranslationX(transX);
                        setTranslationY(transY);
                        actionX = event.getRawX();
                        actionY = event.getRawY();
                    } else if (moveType == 2) {
                        scaled *= getSpacing(event) / spacing;
                        setScaleX(scaled);
                        setScaleY(scaled);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                moveType = 0;
                // 判断是否继续传递信号,移动时间
                if (System.currentTimeMillis() - currentMS > 200) {
                    return true;
                } else {
                    handlerTouch(event);
                }
                break;
            case MotionEvent.ACTION_POINTER_UP:
                moveType = 0;
                break;
        }
        return true;
    }

    /**
     * 放大缩小的时候，两指区域
     * @param event
     * @return
     */
    private float getSpacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    /**
     * 放大缩小，偏移的时候使用
     * @param event
     * @return
     */
    private float getDegree(MotionEvent event) {
        float deltaX = event.getX(0) - event.getX(1);
        float deltaY = event.getY(0) - event.getY(1);
        double radians = Math.atan2(deltaY, deltaX);
        return (float) Math.toDegrees(radians);
    }

    /**
     * 考区的点击选择
     * @param event
     */
    private void handlerTouch(MotionEvent event) {
        MapData selectedMapData = null;
        float x = event.getX();
        float y = event.getY();
        x /= scaleWin;
        y /= scaleWin;
        x -= winOffsetX;// 显示有一个偏移量
        y -= winOffsetY;// 显示有一个偏移量
        for (MapData city : mapDatas) {
            boolean isContain = city.isContainXY(x, y);
            city.setSelected(isContain);// 设置为选中的城市
//            Log.d("", city.getName()+"====divhee==========setSelected==3=="+isContain + "==="+scaleWin);
//            city.setUserCity(TextUtils.equals(city.getName(), mUserCityName));// 设置为用户所在城市

            if (isContain) {
                selectedMapData = city;
            }
        }
        // 更新选中地区的处理
        if (selectedMapData != null){
//            for (ExamMapData city : mExamMapDatas) {
//                boolean isContain = city.isContainXY(x, y);
//                city.setSelected(isContain);// 设置为选中的城市
////            Log.d("", city.getName()+"====divhee==========setSelected==3=="+isContain + "==="+scaleWin);
//                city.setUserCity(TextUtils.equals(city.getName(), mUserCityName));// 设置为用户所在城市
//
//                if (isContain) {
//                    selectedMapData = city;
//                }
//            }
            if (onProvinceClickListener != null){
                if (selectedMapData != null && !(selectedMapData.getName().equals("台湾省")||selectedMapData.getName().equals("香港特别行政区")||selectedMapData.getName().equals("澳门特别行政区"))) {
                    onProvinceClickListener.onProvinceClick(selectedMapData.getName());
                }
            }
        }

        invalidate();
        if (selectedMapData == null && onBlankClickListener != null && mapDatas.stream().noneMatch(MapData::isSelected)) {
            onBlankClickListener.onBlankClick();
        }
    }

    /** 选中了某个区域的回调 */
    public interface OnProvinceClickListener {
        void onProvinceClick(String provinceName);
    }
    public void setOnProvinceClickListener(OnProvinceClickListener listener) {
        this.onProvinceClickListener = listener;
    }

    /** 未选择任何区域，返回上一级的回调 */
    public interface OnBlankClickListener {
        void onBlankClick();
    }
    public void setOnBlankClickListener(OnBlankClickListener listener) {
        this.onBlankClickListener = listener;
    }

    /** 获取用于控制全国地图里的一些显示数据 */
    public Map<String, String> getMapName() {
        return mMapName;
    }
    /** 用于控制全国地图里的一些显示数据 */
    public void setMapName(Map<String, String> maps) {
        this.mMapName.clear();
        if (maps != null){
            this.mMapName.putAll(maps);
        }
    }
}