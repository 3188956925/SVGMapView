package com.readboy.examdemo;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.appcompat.app.AppCompatActivity;

import com.readboy.localsvgmap.R;
import com.readboy.localmap.svgmaplib.MapView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class DemoActivity extends AppCompatActivity {
    private Map<String, String> mMap = new HashMap<String, String>() {
        {
            put("广西壮族自治区", "guangxi");
            put("福建省", "fujian");
            put("上海市", "shanghai");
            put("云南省", "yunnan");
            put("内蒙古自治区", "neimongo");
            put("北京市", "beijing");
            put("台湾省", "taiwan");
            put("吉林省", "jilin");
            put("四川省", "sichuan");
            put("天津市", "tianjin");
            put("宁夏回族自治区", "ningxia");
            put("安徽省", "anhui");
            put("山东省", "shandong");
            put("山西省", "shanxi");
            put("广东省", "guangdong");
            put("新疆维吾尔族自治区", "xinjiang");
            put("江苏省", "jiangsu");
            put("江西省", "jiangxi");
            put("河北省", "hebei");
            put("河南省", "henan");
            put("浙江省", "zhejiang");
            put("海南省", "hainan");
            put("湖南省", "hunan");
            put("湖北省", "hubei");
            put("澳门特别行政区", "macau");
            put("甘肃省", "gansu");
            put("西藏自治区", "xizang");
            put("贵州省", "guizhou");
            put("辽宁省", "liaoning");
            put("重庆市", "chongqing");
            put("陕西省", "shaanxi");
            put("青海省", "quinghai");
            put("香港特别行政区", "hongkong");
            put("黑龙江省", "heilongjiang");
        }
    };
    private MapView mapview;

    private WebViewClient client = new WebViewClient() {
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            // refreshLineChart(value);
        }
    };

    public String readAssetFile(Context context, String filePath) {
        InputStream inputStream = null;
        try {
            inputStream = context.getAssets().open(filePath);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder content = new StringBuilder();
            try {
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line);
                    content.append("\n");
                }
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return content.toString();
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            if (inputStream != null){
                try {
                    inputStream.close();
                } catch (Exception e){
                    e.printStackTrace();
                }
                inputStream = null;
            }
        }
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);

        mapview = findViewById(R.id.mapview);
        mapview.setMapName(mMap);
        mapview.setOnProvinceClickListener(new MapView.OnProvinceClickListener() {
            @Override
            public void onProvinceClick(String provinceName) {
                if (mMap.containsKey(provinceName)) {
                    String rawResourceName = mMap.get(provinceName);
                    int resourceId = getRawResourceId(DemoActivity.this, rawResourceName);
                    Log.d("", provinceName+"===divhee=============onProvinceClick==="+rawResourceName + "=="+resourceId);
                    if (resourceId != 0) {
                        mapview.setData(resourceId, provinceName);
//                        mapview.setUserCityName("中山市");
                    }
                }
            }
        });
        mapview.setOnBlankClickListener(new MapView.OnBlankClickListener() {
            @Override
            public void onBlankClick() {
                mapview.setData(R.raw.chinahigh_full, "中华人民共和国");
            }
        });
    }

    public int getRawResourceId(Context context, String resourceName) {
        return context.getResources().getIdentifier(resourceName, "raw", context.getPackageName());
    }
}