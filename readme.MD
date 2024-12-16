## 使用svg实现自定义考区示意图显示
1.svgMapLibrary为主要显示控件包。考区展示请使用com.readboy.localmap.svgmaplib.MapView展示
2.activity_demo里是例子，可以自定义大小。
3.ExamMapData是考区地图数据在MapView里有初始化。
4.MapData是普通地图数据。
5.mapview.setUserCityName("中山市");//设置用户所在城市
6.可以根据省份通过DemoActivity的mMap获取对应的地图id。设置地图后展示对应的考区效果。 mapview.setData(resourceId, provinceName);//这个是设置地图id和省份名的
7.test_region_string2json.py是转考区数据工具，注意省份合并在一起的要拆开一下，有省会考区的，放第1位，进去地图后默认选中第1个。
8.test_svg2vector.py是转svg成vector的工具
9.地图可以绘制的有立体感一点，也可以只是背景阴影，也可以增加动画等，具体看MapView里边。
10.app里边是kotlin代码版本，无用，暂时不使用了。
11.高德中国地图svg网址https://datav.aliyun.com/portal/school/atlas/area_selector#&lat=31.886295751669298&lng=106.720060693723&zoom=4.5

## 使用svg实现自定义map,结构清晰,逻辑简单
1. 采用android studio将svg转换成xml,如raw/neimeng.xml
2. 利用Xml解析svg的代码 封装成javaBean 得到最重要的Path
3. 重写OnDraw方法 利用Path绘制台湾地图
4. 重写OnTouchEvent方法，记录手指触摸位置，判断这个位置是否坐落在某个省份上
5. 支持全国所有省份地图显示,svg资料见svg地图数据文件夹
6. 支持城市显示
7. 支持放大缩小
8. 支持点击选中效果

##地图示例

##### 中国地图
![](https://github.com/luxiao0314/SVGMapView/blob/master/photos/china.png)

##### 内蒙古
![](https://github.com/luxiao0314/SVGMapView/blob/master/photos/neimeng.png)

##### 台湾
![](https://github.com/luxiao0314/SVGMapView/blob/master/photos/taiwan.png)