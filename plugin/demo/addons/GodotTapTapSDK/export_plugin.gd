@tool
extends EditorPlugin

# A class member to hold the editor export plugin during its lifecycle.
var export_plugin : AndroidExportPlugin

func _enter_tree():
	# Initialization of the plugin goes here.
	export_plugin = AndroidExportPlugin.new()
	add_export_plugin(export_plugin)
	add_autoload_singleton("GodotTapTap","res://addons/GodotTapTapSDK/GodotTapTap.gd")


func _exit_tree():
	# Clean-up of the plugin goes here.
	remove_export_plugin(export_plugin)
	export_plugin = null


class AndroidExportPlugin extends EditorExportPlugin:
	var _plugin_name = "GodotTapTapSDK"

	func _supports_platform(platform):
		if platform is EditorExportPlatformAndroid:
			return true
		return false

	func _get_android_libraries(platform, debug):
		var array = []
		if debug:
			array.append(_plugin_name + "/bin/debug/" + _plugin_name + "-debug.aar",)
		else:
			array.append(_plugin_name + "/bin/release/" + _plugin_name + "-release.aar")
		return PackedStringArray(array)
		
	func _get_android_dependencies(platform: EditorExportPlatform, debug: bool) -> PackedStringArray:
		return PackedStringArray([
			"com.taptap.sdk:tap-core:4.10.7",
			"com.taptap.sdk:tap-login:4.10.7",
			"com.taptap.sdk:tap-compliance:4.10.7",
			"com.taptap.sdk:tap-moment:4.10.7",
			"com.taptap.sdk:tap-update:4.10.7",
			"com.taptap.sdk:tap-license:4.10.7",
			"com.taptap.sdk:tap-share:4.10.7",
			"com.taptap.sdk:tap-review:4.10.7",
			"com.taptap.sdk:tap-achievement:4.10.7",
			"com.taptap.sdk:tap-rep:4.10.7",
			"com.taptap.sdk:tap-cloudsave:4.10.7",
			"com.taptap.sdk:tap-relation:4.10.7",
			"com.taptap.sdk:tap-leaderboard-androidx:4.10.7"
		])
		
	func _get_name():
		return _plugin_name

	# 隐私合规：关闭 TapTap SDK 的 ContentProvider 自动初始化。
	# 这两个 Provider 会在 App 进程启动时（隐私弹窗显示之前）自动初始化 SDK 并采集设备标识（OAID 等），
	# 属于「未经用户同意私自收集个人信息」。移除后，SDK 只在用户同意隐私政策、进入 title 页面后，
	# 由 GodotTapTap.initialize() 手动初始化。tools:node="remove" 由 Android manifest merger 处理。
	# 注意：Godot 4.x 调用这个虚方法时只传 platform（没有 debug），
	# 参数多一个必填参数会导致虚方法不匹配、整段 XML 根本没被插入。debug 给默认值才能两边都兼容。
	func _get_android_manifest_application_element_contents(platform: EditorExportPlatform, debug: bool = false) -> String:
		return '<provider android:name="com.taptap.sdk.kit.internal.TapTapKitInitProvider" android:authorities="${applicationId}.TapTapKitInitProvider" tools:node="remove" /><provider android:name="com.taptap.sdk.themis.lite.ThemisProvider" android:authorities="${applicationId}.themisLite" tools:node="remove" />'
