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
