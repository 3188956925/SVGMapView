# -*- coding: utf-8 -*-
"""
Created on 2023.07.13 20.31.20

@author: divhee
"""
#地图svg来源start
#https://datav.aliyun.com/portal/school/atlas/area_selector#&lat=31.886295751669298&lng=106.720060693723&zoom=4.5
#地图svg来源end

import os
from xml.etree import ElementTree as ET

def prettify(xml_string):
    """Return a pretty-printed XML string for the Element."""
    dom = minidom.parseString(xml_string)
    return dom.toprettyxml(indent="  ")

def contains_china(filename):
    return "中国" in filename or "世界地图" in filename

def svg_to_android_vector(path_to_svg_folder, output_folder):
    # 确保输出文件夹存在
    if not os.path.exists(output_folder):
        os.makedirs(output_folder)

    province_to_pinyin = {
        '广西省': 'guangxi',
        '广西壮族自治区': 'guangxi',
        '福建省': 'fujian',
        '上海市': 'shanghai',
        '云南省': 'yunnan',
        '内蒙古自治区': 'neimongo',
        '北京市': 'beijing',
        '台湾省': 'taiwan',
        '吉林省': 'jilin',
        '四川省': 'sichuan',
        '天津市': 'tianjin',
        '宁夏回族自治区': 'ningxia',
        '安徽省': 'anhui',
        '山东省': 'shandong',
        '山西省': 'shanxi',
        '广东省': 'guangdong',
        '新疆维吾尔族自治区': 'xinjiang',
        '新疆维吾尔自治区': 'xinjiang',
        '江苏省': 'jiangsu',
        '江西省': 'jiangxi',
        '河北省': 'hebei',
        '河南省': 'henan',
        '浙江省': 'zhejiang',
        '海南省': 'hainan',
        '湖南省': 'hunan',
        '湖北省': 'hubei',
        '澳门特别行政区': 'macau',
        '甘肃省': 'gansu',
        '西藏自治区': 'xizang',
        '贵州省': 'guizhou',
        '辽宁省': 'liaoning',
        '重庆市': 'chongqing',
        '陕西省': 'shaanxi',
        '青海省': 'quinghai',
        '香港特别行政区': 'hongkong',
        '黑龙江省': 'heilongjiang',
        '世界地图': '世界地图',
        '中国': 'chinahigh',
        '中华人民共和国': 'chinahigh',
    }


    # 遍历文件夹中的所有SVG文件
    for filename in os.listdir(path_to_svg_folder):
        if filename.endswith('.svg'):
            svg_path = os.path.join(path_to_svg_folder, filename)
            ######################################
            # 读取文件内容
            with open(svg_path, 'r', encoding='utf-8') as file:
                tag_content = file.read()
            # 移除<g>和</g>
            tag_content = tag_content.replace('<g>', '').replace('</g>', '')
            # 将更新后的内容写回文件
            with open(svg_path, 'w', encoding='utf-8') as file:
                file.write(tag_content)
            ######################################

            #print(f'Converted {filename} and {svg_path}')
            tree = ET.parse(svg_path)
            root = tree.getroot()

            # 创建Vector Drawable的根元素
            if contains_china(filename):
                vector = ET.Element('vector', {
                    'xmlns:android': 'http://schemas.android.com/apk/res/android',
                    'android:mapName': root.attrib.get('mapName', filename.replace('.svg', '')),
                    'android:width': '-1dp',
                    'android:height': '-1dp',
                    'android:viewportWidth': '-1',
                    'android:viewportHeight': '-1'
                })
            else :
                vector = ET.Element('vector', {
                    'xmlns:android': 'http://schemas.android.com/apk/res/android',
                    'android:mapName': root.attrib.get('mapName', filename.replace('.svg', '')),
                    'android:width': '800dp',
                    'android:height': '600dp',
                    'android:viewportWidth': '800',
                    'android:viewportHeight': '600'
                })

            # 遍历SVG中的所有元素并转换
            for element in root:
                if element.tag.endswith('path'):
                    # 基础的 path 元素属性
                    path_attributes = {
                        'android:name': element.attrib.get('name', element.attrib.get('id', 'default_name')),
                        'android:fillColor': '#D3D3D3',  # 默认填充颜色
                        'android:strokeColor': '#FFFFFF',
                        'android:pathData': element.attrib['d'],
                    }

                    # 可选的属性
                    optional_attributes = {
                        'android:strokeWidth': element.attrib.get('stroke-width'),
                        'android:strokeLineCap': element.attrib.get('stroke-linecap'),
                        'android:strokeLineJoin': element.attrib.get('stroke-linejoin'),
                        'android:fillType': element.attrib.get('fill-rule'),
                        'android:fillAlpha': element.attrib.get('fill-opacity'),
                        'android:strokeAlpha': element.attrib.get('stroke-opacity'),
                    }

                    # 更新字典，不添加值为 None 的键（即忽略不存在的属性）
                    path_attributes.update({k: v for k, v in optional_attributes.items() if v is not None})

                    # 创建子元素
                    path_element = ET.SubElement(vector, 'path', path_attributes)

                    #path_element = ET.SubElement(vector, 'path', {
					#	'android:name': element.attrib.get('name', element.attrib.get('id', 'default_name')),
					#    'android:fillColor': '#D3D3D3',  # 默认黑色填充
					#	'android:strokeColor': '#FFFFFF',
                    #    'android:pathData': element.attrib['d'],
                    #    'android:strokeWidth': '0.5'
                    #})
                    #print(f'Converted {element.attrib} and {element}')

            # 创建新的XML文件并写入转换后的内容
            #vector_path = os.path.join(output_folder, filename.replace('.svg', '.xml'))
            vector_path = os.path.join(output_folder, province_to_pinyin[filename.replace('.svg', '')]+'.xml')
            
            tree = ET.ElementTree(vector)
            tree.write(vector_path, encoding='utf-8', xml_declaration=True)


            # 读取文件内容
            with open(vector_path, 'r', encoding='utf-8') as file:
                file_content = file.read()

            # 替换<path为\r\n<path
            updated_content = file_content.replace('<path', '\r\n    <path')
            updated_content = updated_content.replace('android:name', '\r\n        android:name')
            updated_content = updated_content.replace('android:fillColor', '\r\n        android:fillColor')
            updated_content = updated_content.replace('android:strokeColor', '\r\n        android:strokeColor')
            updated_content = updated_content.replace('android:pathData', '\r\n        android:pathData')
            updated_content = updated_content.replace('android:strokeWidth', '\r\n        android:strokeWidth')
            updated_content = updated_content.replace('android:mapName', '\r\n    android:mapName')
            updated_content = updated_content.replace('android:width', '\r\n    android:width')
            updated_content = updated_content.replace('android:height', '\r\n    android:height')
            updated_content = updated_content.replace('android:viewportWidth', '\r\n    android:viewportWidth')
            updated_content = updated_content.replace('android:viewportHeight', '\r\n    android:viewportHeight')
            updated_content = updated_content.replace('xmlns:android', '\r\n    xmlns:android')
            updated_content = updated_content.replace('android:mapName', 'android:name')
            updated_content = updated_content.replace('/>', '\r\n        />')
            updated_content = updated_content.replace('</vector>', '\r\n</vector>')
            

            # 将更新后的内容写回文件
            with open(vector_path, 'w', encoding='utf-8') as file:
                file.write(updated_content)

            print(f'Converted {filename} to {vector_path}')

# 使用示例
svg_folder = './svgs'
output_folder = './svgs/vector'
svg_to_android_vector(svg_folder, output_folder)
 





