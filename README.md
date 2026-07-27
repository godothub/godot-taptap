# Godot 4 TapTap SDK

Godot 4 的 Android TapSDK v4 插件集成与使用说明。

## 集成环境

* Godot `4.2+`
* Android 自定义构建
* TapSDK `4.10.7`
* 已在 TapTap 开发者中心创建应用，并取得 Client ID 和 Client Token

## 安装插件

### 直接使用已构建插件

将 [`plugin/demo/addons/GodotTapTapSDK`](plugin/demo/addons/GodotTapTapSDK) 整个目录复制到游戏项目的 `addons` 目录。

然后在 Godot 中完成以下操作：

1. 打开 **项目设置 -> 插件**，启用 `GodotTapTapSDK`。
2. 打开 Android 导出预设，启用 **自定义构建**。
3. 执行一次 Android 导出。

导出插件会自动解析 TapSDK 所需的 Maven 依赖，无需手动向 Godot 项目添加 TapSDK AAR 文件。

### 从源码构建

在仓库根目录执行：

```powershell
.\gradlew.bat assemble
```

构建完成后，插件目录为 `plugin/demo/addons/GodotTapTapSDK`。

## 初始化与通用约定

启用插件后，项目中会自动注册 `GodotTapTap` 自动加载节点。调用任意接口前必须初始化一次：

```gdscript
func _ready() -> void:
    GodotTapTap.initialize(
        "your_client_id",
        "your_client_token",
        "CN",
        OS.is_debug_build()
    )
```

| 参数 | 类型 | 默认值 | 说明 |
| --- | --- | --- | --- |
| `client_id` | `String` | 无 | TapTap 开发者中心的 Client ID |
| `client_token` | `String` | 无 | TapTap 开发者中心的 Client Token |
| `region` | `String` | `"CN"` | `CN` 或 `GLOBAL`，必须与应用配置的地区一致 |
| `enable_log` | `bool` | `false` | 是否输出 TapSDK 调试日志，发布包建议关闭 |

所有异步结果通过 `GodotTapTap` 信号返回。账户、成就、云存档和排行榜等结构化结果使用 JSON 字符串；统一解析方式如下：

```gdscript
func parse_taptap_json(value: String):
    var result = JSON.parse_string(value)
    if result == null:
        push_error("TapTap 返回了无效 JSON")
    return result
```

## 目录

* [登录](#登录)
* [合规认证](#合规认证)
* [内嵌动态](#内嵌动态)
* [更新唤起](#更新唤起)
* [正版验证与 DLC](#正版验证与-dlc)
* [分享、评价与 REP](#分享评价与-rep)
* [成就](#成就)
* [数据分析](#数据分析)
* [云存档](#云存档)
* [好友与聊天](#好友与聊天)
* [排行榜](#排行榜)

## 登录

### 接口

| 方法 | 参数 | 返回值 | 说明 |
| --- | --- | --- | --- |
| `tap_login()` | 无 | 无 | 打开 TapTap 登录流程 |
| `isLogin()` | 无 | `bool` | 当前是否存在登录账户 |
| `getCurrentProfile()` | 无 | `String` 或 `null` | 当前账户 JSON；未登录时为 `null` |
| `logOut()` | 无 | 无 | 退出登录，同时退出合规认证 |

### 回调

```gdscript
signal onLoginResult(code, account_json)
```

| 参数 | 类型 | 说明 |
| --- | --- | --- |
| `code` | `int` | `200` 成功，`400` 失败，`499` 用户取消 |
| `account_json` | `String` | 成功时为账户 JSON；失败或取消时为空字符串 |

账户 JSON 字段：

```json
{
  "openId": "TapTap OpenID",
  "unionId": "TapTap UnionID"
}
```

### 示例

```gdscript
func _ready() -> void:
    GodotTapTap.onLoginResult.connect(_on_login_result)


func login() -> void:
    GodotTapTap.tap_login()


func _on_login_result(code: int, account_json: String) -> void:
    if code != 200:
        return
    var account = JSON.parse_string(account_json)
    print("openId: ", account.openId)
```

## 合规认证

### 接口

| 方法 | 参数 | 返回值 | 说明 |
| --- | --- | --- | --- |
| `quickCheck(user_identifier := "")` | `user_identifier: String` | 无 | 开始合规认证；为空时使用当前登录账户的 `openId` |
| `antiExit()` | 无 | 无 | 退出当前合规认证状态 |

### 回调

```gdscript
signal onAntiAddictionCallback(code)
```

| 参数 | 类型 | 说明 |
| --- | --- | --- |
| `code` | `int` | 合规认证状态码；游戏可开始时为 `500` |

### 示例

```gdscript
func _ready() -> void:
    GodotTapTap.onAntiAddictionCallback.connect(_on_compliance_result)


func begin_game() -> void:
    GodotTapTap.quickCheck()
    # 使用游戏自己的账号体系时：GodotTapTap.quickCheck(player_id)


func _on_compliance_result(code: int) -> void:
    if code == 500:
        start_game()
```

## 内嵌动态

### 接口

| 方法 | 参数 | 返回值 | 说明 |
| --- | --- | --- | --- |
| `momentOpen()` | 无 | 无 | 打开 TapTap 内嵌动态页面 |

### 回调

```gdscript
signal onTapMomentCallBack(code)
```

| 参数 | 类型 | 说明 |
| --- | --- | --- |
| `code` | `int` | 内嵌动态模块返回的状态码 |

### 示例

```gdscript
func _ready() -> void:
    GodotTapTap.onTapMomentCallBack.connect(_on_moment_result)


func open_moment() -> void:
    GodotTapTap.momentOpen()


func _on_moment_result(code: int) -> void:
    print("动态状态: ", code)
```

## 更新唤起

### 接口

| 方法 | 参数 | 返回值 | 说明 |
| --- | --- | --- | --- |
| `check_force_update()` | 无 | 无 | 使用开发者中心配置检查并处理强制更新 |
| `update_game()` | 无 | 无 | 游戏自行判断更新后，打开更新下载和安装流程 |

### 回调

```gdscript
signal onUpdateCancelled()
```

用户在 `update_game()` 的更新流程中取消时触发，无参数。

### 示例

```gdscript
func _ready() -> void:
    GodotTapTap.onUpdateCancelled.connect(_on_update_cancelled)
    GodotTapTap.check_force_update()


func _on_update_cancelled() -> void:
    print("用户取消更新")
```

## 正版验证与 DLC

### 接口

| 方法 | 参数 | 返回值 | 说明 |
| --- | --- | --- | --- |
| `check_license(force_check := false)` | `force_check: bool` | 无 | 执行正版验证；`true` 表示强制检查 |
| `query_dlc(sku_ids)` | `sku_ids: PackedStringArray` | 无 | 查询多个 DLC SKU 的状态 |
| `purchase_dlc(sku_id)` | `sku_id: String` | 无 | 发起指定 DLC 的购买流程 |

### 回调

```gdscript
signal onLicenseValidated()
signal onDLCQueryResult(code, states_json)
signal onDLCPurchaseResult(sku_id, status)
```

| 信号 | 参数 | 说明 |
| --- | --- | --- |
| `onLicenseValidated` | 无 | 正版验证成功 |
| `onDLCQueryResult` | `code: int`、`states_json: String` | 查询状态码和 SKU 状态 JSON |
| `onDLCPurchaseResult` | `sku_id: String`、`status: int` | 被购买 SKU 和购买状态码 |

`states_json` 的格式为 SKU 到状态码的映射：

```json
{
  "soundtrack": 1,
  "expansion_1": 0
}
```

### 示例

```gdscript
func _ready() -> void:
    GodotTapTap.onLicenseValidated.connect(_on_license_validated)
    GodotTapTap.onDLCQueryResult.connect(_on_dlc_query)
    GodotTapTap.onDLCPurchaseResult.connect(_on_dlc_purchase)

    GodotTapTap.check_license()
    GodotTapTap.query_dlc(PackedStringArray(["soundtrack", "expansion_1"]))


func _on_license_validated() -> void:
    print("正版验证成功")


func _on_dlc_query(code: int, states_json: String) -> void:
    var states = JSON.parse_string(states_json)


func _on_dlc_purchase(sku_id: String, status: int) -> void:
    print("DLC: ", sku_id, ", 状态: ", status)
```

## 分享、评价与 REP

### 接口

```gdscript
share(
    title: String = "",
    contents: String = "",
    hashtag_ids: String = "",
    group_label_id: String = "",
    image_uris: PackedStringArray = PackedStringArray(),
    fail_url: String = ""
) -> int
```

| 参数 | 说明 |
| --- | --- |
| `title` | 分享标题 |
| `contents` | 分享正文 |
| `hashtag_ids` | TapTap 话题 ID |
| `group_label_id` | TapTap 分组标签 ID |
| `image_uris` | Android 图片 URI 数组，例如 `content://...` |
| `fail_url` | 分享失败时打开的网页地址 |

其他接口：

| 方法 | 参数 | 返回值 | 说明 |
| --- | --- | --- | --- |
| `open_review()` | 无 | 无 | 打开游戏评价页 |
| `open_rep(url)` | `url: String` | 无 | 打开 REP 页面 |

### 回调

```gdscript
signal onShareResult(code)
signal onRepResult(code, message)
```

| 信号 | 参数 | 说明 |
| --- | --- | --- |
| `onShareResult` | `code: int` | 与 `share()` 的返回值一致 |
| `onRepResult` | `code: int`、`message: String` | REP 结果；`code == 0` 表示成功 |

### 示例

```gdscript
func share_result() -> void:
    var code = GodotTapTap.share(
        "我的游戏",
        "完成了一次挑战",
        "",
        "",
        PackedStringArray(),
        "https://www.taptap.cn/"
    )
    print("分享调用结果: ", code)


func open_review() -> void:
    GodotTapTap.open_review()
```

## 成就

### 接口

| 方法 | 参数 | 返回值 | 说明 |
| --- | --- | --- | --- |
| `unlock_achievement(achievement_id)` | `achievement_id: String` | 无 | 解锁指定成就 |
| `increment_achievement(achievement_id, steps)` | `achievement_id: String`、`steps: int` | 无 | 增加分步成就进度 |
| `show_achievements()` | 无 | 无 | 打开成就展示页 |
| `set_achievement_toast_enabled(enabled)` | `enabled: bool` | 无 | 是否显示 SDK 成就冒泡 |

### 回调

```gdscript
signal onAchievementResult(code, result_json)
signal onAchievementError(achievement_id, code, message)
```

成功结果 `result_json`：

```json
{
  "achievementId": "first_win",
  "achievementName": "首胜",
  "achievementType": "NORMAL",
  "currentSteps": 1
}
```

| 信号 | 参数 | 说明 |
| --- | --- | --- |
| `onAchievementResult` | `code: int`、`result_json: String` | 成就解锁或进度更新成功 |
| `onAchievementError` | `achievement_id: String`、`code: int`、`message: String` | 成就操作失败 |

### 示例

```gdscript
func _ready() -> void:
    GodotTapTap.onAchievementResult.connect(_on_achievement_result)
    GodotTapTap.onAchievementError.connect(_on_achievement_error)


func report_win() -> void:
    GodotTapTap.unlock_achievement("first_win")
    GodotTapTap.increment_achievement("wins", 1)


func _on_achievement_result(code: int, result_json: String) -> void:
    var result = JSON.parse_string(result_json)


func _on_achievement_error(achievement_id: String, code: int, message: String) -> void:
    push_error("成就失败: %s %s" % [code, message])
```

## 数据分析

数据分析接口没有 Godot 回调信号。属性参数均为 `Dictionary`，插件会转换为 JSON 后传给 TapSDK。

### 事件与用户

| 方法 | 参数 | 说明 |
| --- | --- | --- |
| `log_analytics_event(name, properties := {})` | `name: String`、`properties: Dictionary` | 上报自定义事件 |
| `set_analytics_user_id(user_id, properties := {})` | `user_id: String`、`properties: Dictionary` | 设置分析用户 ID 与用户属性 |
| `clear_analytics_user()` | 无 | 清除当前分析用户 |
| `get_analytics_device_id()` | 无 | 返回 TapSDK 设备 ID |

### 公共属性与实体属性

| 方法 | 参数 | 说明 |
| --- | --- | --- |
| `add_analytics_common(properties)` | `properties: Dictionary` | 添加所有后续事件的公共属性 |
| `clear_analytics_common(keys)` | `keys: PackedStringArray` | 清除指定公共属性 |
| `clear_all_analytics_common()` | 无 | 清除全部公共属性 |
| `analytics_device_initialize(properties)` | `properties: Dictionary` | 初始化设备属性 |
| `analytics_device_update(properties)` | `properties: Dictionary` | 更新设备属性 |
| `analytics_device_add(properties)` | `properties: Dictionary` | 增加设备属性 |
| `analytics_user_initialize(properties)` | `properties: Dictionary` | 初始化用户属性 |
| `analytics_user_update(properties)` | `properties: Dictionary` | 更新用户属性 |
| `analytics_user_add(properties)` | `properties: Dictionary` | 增加用户属性 |
| `set_analytics_oaid(oaid)` | `oaid: String` | 设置 OAID |
| `log_device_login_event()` | 无 | 上报设备登录事件 |

### 示例

```gdscript
GodotTapTap.set_analytics_user_id("player_42", {"server": "asia"})
GodotTapTap.add_analytics_common({"game_mode": "ranked"})
GodotTapTap.log_analytics_event("match_finished", {
    "duration_seconds": 178,
    "won": true
})
```

## 云存档

### 接口

```gdscript
create_cloud_archive(
    metadata: Dictionary,
    data_file_path: String,
    cover_file_path: String = ""
)

update_cloud_archive(
    archive_uuid: String,
    metadata: Dictionary,
    data_file_path: String,
    cover_file_path: String = ""
)
```

| 元数据字段 | 类型 | 说明 |
| --- | --- | --- |
| `name` | `String` | 存档名称 |
| `summary` | `String` | 存档摘要 |
| `extra` | `String` | 自定义扩展字符串 |
| `playtime` | `int` | 游戏时长，单位由游戏自行约定 |

`data_file_path` 和 `cover_file_path` 为游戏可读取的本地文件路径，例如 `user://saves/slot_1.dat`。封面路径可以为空字符串。

| 方法 | 参数 | 返回值 | 说明 |
| --- | --- | --- | --- |
| `delete_cloud_archive(archive_uuid)` | `archive_uuid: String` | 无 | 删除存档 |
| `get_cloud_archive_list()` | 无 | 无 | 获取全部存档列表 |
| `get_cloud_archive_data(archive_uuid, archive_file_id)` | 两个 `String` | 无 | 下载存档数据 |
| `get_cloud_archive_cover(archive_uuid, archive_file_id)` | 两个 `String` | 无 | 下载存档封面 |

### 回调

```gdscript
signal onCloudSaveStatus(code)
signal onCloudSaveArchiveCreated(archive_json)
signal onCloudSaveArchiveUpdated(archive_json)
signal onCloudSaveArchiveDeleted(archive_json)
signal onCloudSaveArchiveList(archives_json)
signal onCloudSaveArchiveData(data_base64)
signal onCloudSaveArchiveCover(data_base64)
signal onCloudSaveError(code, message)
```

单个存档 JSON 的字段：

```json
{
  "uuid": "存档 UUID",
  "fileId": "存档文件 ID",
  "name": "存档名称",
  "summary": "存档摘要",
  "extra": "扩展信息",
  "playtime": 7200,
  "saveSize": 1024,
  "coverSize": 128,
  "createdTime": 0,
  "modifiedTime": 0
}
```

| 信号 | 参数 | 说明 |
| --- | --- | --- |
| `onCloudSaveStatus` | `code: int` | 云存档模块通用状态 |
| `onCloudSaveArchiveCreated` | `archive_json: String` | 创建成功的存档 |
| `onCloudSaveArchiveUpdated` | `archive_json: String` | 更新成功的存档 |
| `onCloudSaveArchiveDeleted` | `archive_json: String` | 删除成功的存档 |
| `onCloudSaveArchiveList` | `archives_json: String` | 存档 JSON 数组 |
| `onCloudSaveArchiveData` | `data_base64: String` | 下载的存档数据，Base64 编码 |
| `onCloudSaveArchiveCover` | `data_base64: String` | 下载的封面数据，Base64 编码 |
| `onCloudSaveError` | `code: int`、`message: String` | 请求失败 |

### 示例

```gdscript
func _ready() -> void:
    GodotTapTap.onCloudSaveArchiveCreated.connect(_on_archive_created)
    GodotTapTap.onCloudSaveArchiveData.connect(_on_archive_data)
    GodotTapTap.onCloudSaveError.connect(_on_cloud_save_error)


func upload_save() -> void:
    GodotTapTap.create_cloud_archive(
        {
            "name": "第一章",
            "summary": "已完成关卡",
            "extra": "{\"difficulty\":\"hard\"}",
            "playtime": 7200
        },
        "user://saves/chapter_1.dat",
        "user://saves/chapter_1.png"
    )


func _on_archive_created(archive_json: String) -> void:
    var archive = JSON.parse_string(archive_json)
    GodotTapTap.get_cloud_archive_data(archive.uuid, archive.fileId)


func _on_archive_data(data_base64: String) -> void:
    var data: PackedByteArray = Marshalls.base64_to_raw(data_base64)


func _on_cloud_save_error(code: int, message: String) -> void:
    push_error("云存档失败: %s %s" % [code, message])
```

## 好友与聊天

### 接口

| 方法 | 参数 | 返回值 | 说明 |
| --- | --- | --- | --- |
| `prepare_relation()` | 无 | 无 | 初始化好友实时消息、未读数和新增粉丝能力；使用这些能力前调用 |
| `start_relation_messenger()` | 无 | 无 | 打开聊天页面 |
| `invite_relation_game()` | 无 | 无 | 打开邀请好友上线页面 |
| `invite_relation_team(team_id)` | `team_id: String` | 无 | 打开邀请好友组队页面 |
| `show_relation_user_profile(open_id := "", union_id := "")` | 两个 `String` | 无 | 打开 TapTap 用户名片；传入其中一个 ID |
| `get_relation_new_fans_count()` | 无 | `int` | 返回新增粉丝数量 |
| `get_relation_unread_message_count()` | 无 | `int` | 返回新增未读消息数量 |
| `destroy_relation()` | 无 | 无 | 销毁好友模块资源 |

### 回调

```gdscript
signal onRelationMessengerResult(code)
signal onRelationFansCountChanged(code, fans_count)
signal onRelationUnreadMessageCountChanged(code, unread_message_count)
signal onRelationGameInvite(open_id, union_id)
signal onRelationTeamInvite(open_id, union_id, team_id)
```

| 信号 | 参数 | 说明 |
| --- | --- | --- |
| `onRelationMessengerResult` | `code: int` | 聊天模块状态码 |
| `onRelationFansCountChanged` | `code: int`、`fans_count: int` | 新增粉丝数变化 |
| `onRelationUnreadMessageCountChanged` | `code: int`、`unread_message_count: int` | 新增未读消息数变化 |
| `onRelationGameInvite` | `open_id: String`、`union_id: String` | 收到上线邀请 |
| `onRelationTeamInvite` | `open_id: String`、`union_id: String`、`team_id: String` | 收到组队邀请 |

### 示例

```gdscript
func _ready() -> void:
    GodotTapTap.onRelationGameInvite.connect(_on_game_invite)
    GodotTapTap.onRelationTeamInvite.connect(_on_team_invite)
    GodotTapTap.prepare_relation()


func invite_team() -> void:
    GodotTapTap.invite_relation_team("room_987")


func _on_game_invite(open_id: String, union_id: String) -> void:
    print("收到上线邀请: ", open_id)


func _on_team_invite(open_id: String, union_id: String, team_id: String) -> void:
    print("收到组队邀请: ", team_id)
```

## 排行榜

### 接口

| 方法 | 参数 | 返回值 | 说明 |
| --- | --- | --- | --- |
| `open_leaderboard(leaderboard_id, collection := "public")` | `leaderboard_id: String`、`collection: String` | 无 | 打开排行榜页面 |
| `show_leaderboard_user_profile(open_id := "", union_id := "")` | 两个 `String` | 无 | 打开排行榜用户资料；传入其中一个 ID |
| `submit_leaderboard_scores(scores)` | `scores: Array` | 无 | 批量提交分数 |
| `load_leaderboard_scores(leaderboard_id, collection := "public", next_page := "", period_token := "")` | 四个参数 | 无 | 获取排行榜分页数据 |
| `load_current_leaderboard_score(leaderboard_id, collection := "public", period_token := "")` | 三个参数 | 无 | 获取当前登录用户分数 |
| `load_player_centered_leaderboard_scores(leaderboard_id, collection := "public", period_token := "", limit := 0)` | 四个参数 | 无 | 获取当前用户附近的分数 |

`collection` 只支持 `"public"`（总榜）和 `"friends"`（好友榜）。`next_page` 传入上一次 `onLeaderboardScores` 返回 JSON 中的 `nextPage`；首次查询传空字符串。`period_token` 为空时使用当前周期。`limit` 为 `0` 时由 SDK 使用默认值。

提交分数的 `scores` 格式：

```gdscript
[
    {"leaderboardId": "weekly_score", "score": 12800},
    {"leaderboardId": "weekly_time", "score": 360}
]
```

### 回调

```gdscript
signal onLeaderboardStatus(code, message)
signal onLeaderboardShareSuccess(local_path)
signal onLeaderboardShareError(message)
signal onLeaderboardSubmitScores(result_json)
signal onLeaderboardScores(result_json)
signal onLeaderboardCurrentScore(result_json)
signal onLeaderboardPlayerCenteredScores(result_json)
signal onLeaderboardError(operation, code, message)
```

| 信号 | 参数 | 说明 |
| --- | --- | --- |
| `onLeaderboardStatus` | `code: int`、`message: String` | 排行榜模块通用状态 |
| `onLeaderboardShareSuccess` | `local_path: String` | 排行榜分享图片保存后的本地路径 |
| `onLeaderboardShareError` | `message: String` | 排行榜分享图片生成失败 |
| `onLeaderboardSubmitScores` | `result_json: String` | 提交分数成功 |
| `onLeaderboardScores` | `result_json: String` | 分页分数列表 |
| `onLeaderboardCurrentScore` | `result_json: String` | 当前用户分数 |
| `onLeaderboardPlayerCenteredScores` | `result_json: String` | 当前用户附近的分数列表 |
| `onLeaderboardError` | `operation: String`、`code: int`、`message: String` | 请求失败；`operation` 为 `submitScores`、`loadScores`、`loadCurrentScore` 或 `loadPlayerCenteredScores` |

`onLeaderboardScores` 与 `onLeaderboardPlayerCenteredScores` 的结果格式：

```json
{
  "leaderboard": {
    "id": "weekly_score",
    "name": "周榜",
    "availablePeriods": [
      {"display": "本周", "periodToken": "weekly"}
    ],
    "period": {"display": "本周", "periodToken": "weekly"},
    "score": {}
  },
  "scores": [
    {
      "rank": 1,
      "rankDisplay": "1",
      "score": 12800,
      "scoreDisplay": "12,800",
      "scoreSubmittedTime": 0,
      "user": {
        "avatar": "",
        "name": "玩家名称",
        "openId": "",
        "unionId": ""
      }
    }
  ],
  "nextPage": "下一页令牌"
}
```

`onLeaderboardCurrentScore` 的结果为：

```json
{
  "currentUserScore": {},
  "leaderboard": {}
}
```

### 示例

```gdscript
func _ready() -> void:
    GodotTapTap.onLeaderboardScores.connect(_on_leaderboard_scores)
    GodotTapTap.onLeaderboardError.connect(_on_leaderboard_error)


func submit_score() -> void:
    GodotTapTap.submit_leaderboard_scores([
        {"leaderboardId": "weekly_score", "score": 12800}
    ])


func load_scores() -> void:
    GodotTapTap.load_leaderboard_scores("weekly_score", "public")


func _on_leaderboard_scores(result_json: String) -> void:
    var result = JSON.parse_string(result_json)
    var next_page = result.nextPage


func _on_leaderboard_error(operation: String, code: int, message: String) -> void:
    push_error("排行榜 %s 失败: %s %s" % [operation, code, message])
```

## Android 导出

插件会在 Android 导出时自动添加 TapSDK Maven 依赖及所需 Manifest 配置。项目无需额外添加 TapSDK 依赖；只需确保 Android 导出预设开启了 **自定义构建**。

云存档、成就、排行榜、好友、正版验证和 DLC 等服务，需要先在 TapTap 开发者中心完成对应服务配置后再进行真机测试。

## 验证构建

```powershell
.\gradlew.bat :plugin:assemble
```

构建后的 Debug 和 Release AAR 位于 `plugin/demo/addons/GodotTapTapSDK/bin`。
