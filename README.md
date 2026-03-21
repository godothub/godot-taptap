# Godot4 TapTap SDK 使用说明

此源码包括sdk与godot两部分的完整项目，下载后导入到4.2版本的引擎中便可使用。
* 支持引擎版本：Godot 4.2+
* TapTap SDK版本：3.27.0

### 包含以下功能：
* 一键登录
* 一键防沉迷认证
* 内嵌动态
_________________
## 安装方法
1. 复制项目本目录下plugin\demo\addons\GodotTapTapSDK文件内全部内容到你的add项目中或者自行编译整个项目。
   编译命令：./gradlew assemble，编译后的所有东西都在plugin\demo\addons\GodotTapTapSDK
2. 打开项目设置-插件-启用GodotTapTapSDK。
3. 项目导出时在**自定义构建** 中勾选启动。
_________________
## 使用方法
### 初始化插件：

修改addons/GodotTapTapSDK/GodotTapTap.gd中的三个参数，改成自己taptap游戏的，具体在tap开发者后台查看
```
singleton.init('client_id','client_token','server_url')
```

### 监听信号
```
来自登录的信号，code为200是登录成功，400为登录失败,json为登录成功后返回的信息字符串
signal onLoginResult(code,json)

来自防沉迷的信号
code == 500;   // 登录成功
code == 1000;  // 用户登出
code == 1001;  // 切换账号
code == 1030;  // 用户当前无法进行游戏
code == 1050;  // 时长限制
code == 9002;  // 实名过程中点击了关闭实名窗
signal onAntiAddictionCallback(code)

来自内嵌动态的信号
10000	动态发布成功
10100	动态发布失败
10200	关闭动态发布页面
20000	获取新消息成功
20100	获取新消息失败
30000	动态页面打开
30100	动态页面关闭
50000	取消关闭所有动态界面（弹框点击取消按钮）
50100	确认关闭所有动态界面（弹框点击确认按钮）
60000	动态页面内登录成功
70000	场景化入口回调
signal onTapMomentCallBack(code)

来自激励广告的信号
500 == 广告加载失败
200 == 广告加载成功 可以播放
201 == 激励广告已显示
202 == 激励广告已经关闭
203 == 视频播放结束
204 == 视频出错
205 == 激励任务已完成，游戏可以选择在此时进行玩家奖励
206 == 激励广告中玩家点击了跳过视频的按钮
207 == 激励广告点击事件
signal onRewardVideoAdCallBack(code)
```
## 函数说明
### 登录模块
```
func tap_login()
```
> #### tap登录功能
> - 调用后触发onLoginResult信号

```
func isLogin()
```
> #### 判断是否登录
> - 返回布尔值

```
func getCurrentProfile()
```
> #### 获取登录用户信息
> - 返回字符串，如果未登录返回null

```
func logOut()
```
> #### 退出登录
> - 无返回值
_________________
### 防沉迷模块
```
func quickCheck(id)
```
> #### 快速防沉迷认证
> - id 玩家的唯一ID，不传则默认为android_id
> - 调用后触发onAntiAddictionCallback信号

```
func antiExit()
```
> #### 退出当前用户的防沉迷认证
> - 无返回值

```
func setTestEnvironment(enable)
```
> #### 切换防沉迷模块是否为测试环境
> - enable bool值

_________________
### 动态模块
```
func setEntryVisible(enable)
```
> #### 是否打开悬浮窗
> - enable bool值

```
func momentOpen(ori)
```
> #### 打开内嵌动态
> - ori int类型，-1默认，0横屏，1竖屏，2随陀螺仪旋转
_________________
### 广告模块
```
func initAd(mediaId,mediaName,mediaKey)
```
> #### 初始化广告sdk
> - mediaId,mediaName,mediaKey 应用位信息

```
func initRewardVideoAd(spaceId,rewardName,extraInfo,userId)
```
> #### 加载激励广告
> - spaceId 广告位ID
> - rewardName 奖励名称
> - extraInfo 附加信息
> - userId 用户id

```
func showRewardVideoAd()
```
> #### 播放激励广告
点击加入Godot交流群：<a target="_blank" href="https://qm.qq.com/cgi-bin/qm/qr?k=W4HFsixrp21iVio3jhmalbDgyiuKuVZO&jump_from=webapi&authKey=1qoGS3eG8/2Tx2o4xqfuXERjwR5WuD3eGNPTykoPPeOF97xrkue62ly5utMvn9Aa"><img border="0" src="//pub.idqqimg.com/wpa/images/group.png" alt="Godot引擎交流群" title="Godot引擎交流群"></a>
或手动搜索：722737499
