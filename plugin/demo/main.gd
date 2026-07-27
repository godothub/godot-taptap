extends Node2D

@export var tap_client_id := ""
@export var tap_client_token := ""
@export_enum("CN", "GLOBAL") var tap_region := "CN"
@export var enable_tap_log := true


func _ready():
	GodotTapTap.onLoginResult.connect(_on_login_result)
	GodotTapTap.onAntiAddictionCallback.connect(_on_anti_addiction_callback)
	GodotTapTap.onTapMomentCallBack.connect(_on_tap_moment_callback)
	GodotTapTap.initialize(tap_client_id, tap_client_token, tap_region, enable_tap_log)


func _on_Button_pressed():
	GodotTapTap.tap_login()


func _on_button_2_pressed() -> void:
	GodotTapTap.quickCheck()


func _on_button_3_pressed() -> void:
	GodotTapTap.momentOpen()


func _on_login_result(code, account_json):
	$Label.text = "登录: %s\n%s" % [code, account_json]


func _on_anti_addiction_callback(code):
	$Label.text = "合规认证: %s" % code


func _on_tap_moment_callback(code):
	$Label.text = "内嵌动态: %s" % code
