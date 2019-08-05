# VoiceNote

## 介绍

> **VoiceNote** ———— 安卓语音记事本

**`科大讯飞语音合成`** + **`SQlite3`** + **`RichEditor for Android`** (Github 开源项目)

## 功能

## 支持功能

- [x] 富文本编辑
- [x] 图文混编
- [x] 语音阅读
- [ ] 语音输入(废弃)

### 功能说明

|  功能       |  操作方法             |
| ----------- | ---------------------|
| 新建笔记     | 点击 **`+`**         |
| 删除笔记     | 无内容笔记不会保存    |    
| 文本编辑     | 使用顶部工具栏        |
| 语音阅读     | 编辑页面的喇叭图标     |
| 复制到剪切板 | 边界页面的编写图标     |   

## 效果图

<table>
  <td><img src="images/image1.png" width = "200"></td>
  <td><img src="images/image2.png" width = "200"></td>
</table>

## 文本编辑

> 引用了开源项目 **`RichEditor for Android`** 开源项目([项目地址](https://github.com/wasabeef/richeditor-android))来支持富文本编辑

![支持的功能](https://github.com/wasabeef/richeditor-android/raw/master/art/demo.gif)

在原有的基础上，增加了一些额外的部分，让交互更人性化

1. **选中效果** 选中加粗之类的操作后，并不知道本身实际的所处状态，所以给每个按钮都加了监听，从红色和黑色之间切换，对于字体颜色的按钮与其选中的颜色对应
2. **图片添加** 增加了二级菜单，实现了三种照片的添加方式
    - 拍照添加
    - 从相册添加
    - URL 图片添加

## 语音效果

> 引用了 **科大讯飞的语音系统**，实现了将文本合成语音进行播报

将语音合成的操作封装为 `Speacker.java` 工具类，调用其内部的 `Speack # speak(String str)` 方法就可以将字符串进行语音合成

最开始也使用了 ~~语音识别~~ 作为输入，但是识别的效果不尽人意，再加上手机输入法的语音输入已经非常完善了，太过鸡肋就砍掉了

## 详细说明

项目的详细过程和部分重要实现，以及期间遇到的问题，都在 [Description.md](Description.md) 进行了描述
