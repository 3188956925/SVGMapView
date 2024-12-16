package com.readboy.localsvgmap

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*


class MainActivity : AppCompatActivity() {
    val mMap: HashMap<String, String> = object : HashMap<String, String>() {
        init {
            put("广西省", "guangxi")
            put("福建省", "fujian")
            put("上海市", "shanghai")
            put("云南省", "yunnan")
            put("内蒙古自治区", "neimongo")
            put("北京市", "beijing")
            put("台湾省", "taiwan")
            put("吉林省", "jilin")
            put("四川省", "sichuan")
            put("天津市", "tianjin")
            put("宁夏回族自治区", "ningxia")
            put("安徽省", "anhui")
            put("山东省", "shandong")
            put("山西省", "shanxi")
            put("广东省", "guangdong")
            put("新疆维吾尔族自治区", "xinjiang")
            put("江苏省", "jiangsu")
            put("江西省", "jiangxi")
            put("河北省", "hebei")
            put("河南省", "henan")
            put("浙江省", "zhejiang")
            put("海南省", "hainan")
            put("湖南省", "hunan")
            put("湖北省", "hubei")
            put("澳门特别行政区", "macau")
            put("甘肃省", "gansu")
            put("西藏自治区", "xizang")
            put("贵州省", "guizhou")
            put("辽宁省", "liaoning")
            put("重庆市", "chongqing")
            put("陕西省", "shaanxi")
            put("青海省", "quinghai")
            put("香港特别行政区", "hongkong")
            put("黑龙江省", "heilongjiang")
        }
    };
    private val client: WebViewClient?=object : WebViewClient() {
        override fun onPageFinished(view: WebView, url: String) {
            super.onPageFinished(view, url)
//            refreshLineChart(value)
        }
    }

    fun readAssetFile(context: Context, filePath: String): String {
        return context.assets.open(filePath).bufferedReader().use { it.readText() }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        webView.getSettings().setJavaScriptEnabled(true);
        var html = "<html>\n" +
                "<head>\n" +
                        "    <script src=\"" +
                "</head>\n" +
                "<body>\n" +
                "    <div id=\"chart\" style=\"width: 100%; height: 100%;\"></div>\n" +
                "    <script type=\"text/javascript\">\n" +
                "        var chart = echarts.init(document.getElementById('chart'));\n" +
                "        var option = {\n" +
                "            title: {\n" +
                "                text: 'ECharts Pie Chart'\n" +
                "            },\n" +
                "            series: [\n" +
                "                {\n" +
                "                    name: 'Data',\n" +
                "                    type: 'pie',\n" +
                "                    data: [\n" +
                "                        {value: 335, name: 'A'},\n" +
                "                        {value: 310, name: 'B'},\n" +
                "                        {value: 234, name: 'C'},\n" +
                "                        {value: 135, name: 'D'},\n" +
                "                        {value: 1548, name: 'E'}\n" +
                "                    ]\n" +
                "                }\n" +
                "            ]\n" +
                "        };\n" +
                "        chart.setOption(option);\n" +
                "    </script>\n" +
                "</body>\n" +
                "</html>";
//        webView.loadData(html, "text/html", "UTF-8");
//        webView.loadUrl("file:///android_asset/echart/chinamap1.html");
////        webView!!.webViewClient =client
//        webView!!.init("file:///android_asset/echart/chinamap1.html")
//        webView.refreshEchartsWithOption(1)
        val data1 =
            "{\"title\":{\"show\":false,\"text\":\"订单状态百分比\",\"subtext\":\"\"},\"tooltip\":{\"trigger\":\"item\",\"formatter\":\"{a} <br/>{b} : {c} ({d}%)\"},\"series\":[{\"center\":[\"50%\",\"60%\"],\"radius\":[\"50%\",\"80%\"],\"type\":\"pie\",\"name\":\"订单笔数\",\"data\":[{\"name\":\"待付款\",\"value\":\"0\"},{\"name\":\"待发货\",\"value\":\"0\"},{\"name\":\"待收货\",\"value\":\"16\"},{\"name\":\"已完成\",\"value\":\"27\"}]}],\"legend\":{\"show\":false,\"data\":[\"待付款\",\"待发货\",\"待收货\",\"已完成\"]}}"
        val data2 =
            "{\"color\":[\"#3398DB\",\"#e43c59\"],\"title\":{\"show\":false,\"text\":\"订单销售走势统计图\",\"subtext\":\"订单销售走势统计图\"},\"tooltip\":{\"trigger\":\"axis\",\"axisPointer\":{\"type\":\"shadow\"}},\"grid\":{\"zlevel\":0,\"z\":0,\"borderWidth\":0,\"containLable\":true},\"xAxis\":[{\"type\":\"category\",\"axisTick\":{\"show\":true,\"splitNumber\":0},\"data\":[\"2018-09-22\",\"2018-09-23\",\"2018-09-24\",\"2018-09-25\",\"2018-09-26\",\"2018-09-27\",\"2018-09-28\"]}],\"yAxis\":[{\"type\":\"value\",\"axisLabel\":{\"formatter\":\"{value}\"}}],\"series\":[{\"smooth\":true,\"type\":\"line\",\"name\":\"待发货订单\",\"data\":[\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\"]},{\"smooth\":true,\"type\":\"line\",\"name\":\"已完成订单\",\"data\":[\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"1\"]}],\"legend\":{\"show\":false,\"data\":[\"待发货订单\",\"已完成订单\"]}}"
//实例化EChart
//        ht_echart_id1.loadUrl("file:///android_asset/echart/echarts.html");
//        val data3 = readAssetFile(this, "echart/sichuan.json")
//        Log.d("", "==divhee===data3="+data3)
//        ht_echart_id1.setData(data1);
        mapview.setOnProvinceClickLisener({
//            if (it.contains("内蒙古", true)) {
//                mapview.setData(R.raw.neimeng)
//            } else if (it.contains("台湾", true)) {
//                mapview.setData(R.raw.taiwanhigh)
//            } else if (it.contains("安徽", true)) {
//                mapview.setData(R.raw.anhui)
//            } else if (it.contains("北京", true)) {
//                mapview.setData(R.raw.beijing)
//            }
            if (mMap.contains(it)){
                var dataid = getRawResourceId(this, mMap.get(it))
                if (dataid != 0){
                    mapview.setData(dataid)
                }
            }
        }, {
            mapview.setData(R.raw.chinahigh)
        })
    }

    fun getRawResourceId(context: Context, resourceName: String?): Int {
        return context.getResources().getIdentifier(resourceName, "raw", context.getPackageName())
    }
}