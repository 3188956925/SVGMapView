package com.readboy.localmap.svgmaplib;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.Log;
import android.util.Xml;


import org.xmlpull.v1.XmlPullParser;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class Dom2XmlUtils {
    /** svg图的实际宽 */
    public static float svgWidth = 0f;
    /** svg图的实际高 */
    public static float svgHeight = 0f;

    /**
     * 解析svg转成vector后的地图数据
     * @param context
     * @param id
     * @return
     */
    public static List<MapData> dom2xml(Context context, int id) {
        List<MapData> data = new ArrayList<>();
        RectF rectF = new RectF();
        float maxRight = 0f;
        float maxBottom = 0f;
        float left = 0f;
        float top = 0f;
        InputStream inputStream = null;
        try {
            inputStream = context.getResources().openRawResource(id);
            XmlPullParser xmlPullParser = Xml.newPullParser();
            xmlPullParser.setInput(inputStream, "utf-8");
            int eventType = xmlPullParser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                String name = xmlPullParser.getName();
                if (!TextUtils.isEmpty(name)) {
                    if (eventType == XmlPullParser.START_TAG) {
                        if ("path".equals(name)) {
                            String pathData = xmlPullParser.getAttributeValue(null, "pathData");
                            Path path = PathParser.createPathFromPathData(pathData);
                            String cityName = xmlPullParser.getAttributeValue(null, "name");
                            int fillColor = Color.parseColor(xmlPullParser.getAttributeValue(null, "fillColor"));
                            int strokeColor = Color.parseColor(xmlPullParser.getAttributeValue(null, "strokeColor"));
                            float strokeWidth = Float.parseFloat(xmlPullParser.getAttributeValue(null, "strokeWidth"));
                            MapData city = new MapData(cityName, path, fillColor, strokeColor, strokeWidth);

                            float strokeAlpha = 1.f;
                            String strokeAlphaValue = xmlPullParser.getAttributeValue(null, "strokeAlpha");
                            if (strokeAlphaValue != null) {
                                try {
                                    strokeAlpha = Float.parseFloat(strokeAlphaValue);
                                } catch (Exception e) {
                                }
                            }
                            city.setStrokeAlpha(strokeAlpha);

                            float fillAlpha = 1.f;
                            String fillAlphaValue = xmlPullParser.getAttributeValue(null, "fillAlpha");
                            if (fillAlphaValue != null) {
                                try {
                                    fillAlpha = Float.parseFloat(fillAlphaValue);
                                } catch (Exception e) {
                                }
                            }
                            city.setFillAlpha(fillAlpha);

                            String strokeLineCapValue = xmlPullParser.getAttributeValue(null, "strokeLineCap");
                            if (strokeLineCapValue != null) {
                                city.setStrokeLineCap(strokeLineCapValue);
                            }
                            String strokeLineJoinValue = xmlPullParser.getAttributeValue(null, "strokeLineJoin");
                            if (strokeLineJoinValue != null) {
                                city.setStrokeLineJoin(strokeLineJoinValue);
                            }
                            String fillTypeValue = xmlPullParser.getAttributeValue(null, "fillType");
                            if (fillTypeValue != null) {
                                city.setFillType(fillTypeValue);
                            }


                            path.computeBounds(rectF, true);

                            maxRight = Math.max(maxRight, rectF.right);
                            maxBottom = Math.max(maxBottom, rectF.bottom);
                            left = Math.min(left, rectF.left);
                            top = Math.min(top, rectF.top);
                            data.add(city);
                        }
                    }
                }
                eventType = xmlPullParser.next();
            }
            svgWidth = maxRight - left;
            svgHeight = maxBottom - top;
//            Log.d("", "=1==divhee==============dom2xml===="+data);
            return data;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
//        Log.d("", "=2==divhee==============dom2xml===="+data);
        return new ArrayList<>();
    }
}