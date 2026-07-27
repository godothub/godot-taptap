extends Node

var singleton
var _initialized := false

signal onLoginResult(code, json)
signal onAntiAddictionCallback(code)
signal onTapMomentCallBack(code)
signal onUpdateCancelled
signal onLicenseValidated
signal onDLCQueryResult(code, states_json)
signal onDLCPurchaseResult(sku_id, status)
signal onShareResult(code)
signal onAchievementResult(code, result_json)
signal onAchievementError(achievement_id, code, message)
signal onRepResult(code, message)
signal onCloudSaveStatus(code)
signal onCloudSaveArchiveCreated(archive_json)
signal onCloudSaveArchiveUpdated(archive_json)
signal onCloudSaveArchiveDeleted(archive_json)
signal onCloudSaveArchiveList(archives_json)
signal onCloudSaveArchiveData(data_base64)
signal onCloudSaveArchiveCover(data_base64)
signal onCloudSaveError(code, message)
signal onRelationMessengerResult(code)
signal onRelationFansCountChanged(code, fans_count)
signal onRelationUnreadMessageCountChanged(code, unread_message_count)
signal onRelationGameInvite(open_id, union_id)
signal onRelationTeamInvite(open_id, union_id, team_id)
signal onLeaderboardStatus(code, message)
signal onLeaderboardShareSuccess(local_path)
signal onLeaderboardShareError(message)
signal onLeaderboardSubmitScores(result_json)
signal onLeaderboardScores(result_json)
signal onLeaderboardCurrentScore(result_json)
signal onLeaderboardPlayerCenteredScores(result_json)
signal onLeaderboardError(operation, code, message)


func _ready():
	if Engine.has_singleton("GodotTapTapSDK"):
		singleton = Engine.get_singleton("GodotTapTapSDK")
		singleton.onLoginResult.connect(_on_login_result)
		singleton.onAntiAddictionCallback.connect(_on_anti_addiction_callback)
		singleton.onTapMomentCallBack.connect(_on_tap_moment_callback)
		singleton.onUpdateCancelled.connect(_on_update_cancelled)
		singleton.onLicenseValidated.connect(_on_license_validated)
		singleton.onDLCQueryResult.connect(_on_dlc_query_result)
		singleton.onDLCPurchaseResult.connect(_on_dlc_purchase_result)
		singleton.onShareResult.connect(_on_share_result)
		singleton.onAchievementResult.connect(_on_achievement_result)
		singleton.onAchievementError.connect(_on_achievement_error)
		singleton.onRepResult.connect(_on_rep_result)
		singleton.onCloudSaveStatus.connect(_on_cloud_save_status)
		singleton.onCloudSaveArchiveCreated.connect(_on_cloud_save_archive_created)
		singleton.onCloudSaveArchiveUpdated.connect(_on_cloud_save_archive_updated)
		singleton.onCloudSaveArchiveDeleted.connect(_on_cloud_save_archive_deleted)
		singleton.onCloudSaveArchiveList.connect(_on_cloud_save_archive_list)
		singleton.onCloudSaveArchiveData.connect(_on_cloud_save_archive_data)
		singleton.onCloudSaveArchiveCover.connect(_on_cloud_save_archive_cover)
		singleton.onCloudSaveError.connect(_on_cloud_save_error)
		singleton.onRelationMessengerResult.connect(_on_relation_messenger_result)
		singleton.onRelationFansCountChanged.connect(_on_relation_fans_count_changed)
		singleton.onRelationUnreadMessageCountChanged.connect(_on_relation_unread_message_count_changed)
		singleton.onRelationGameInvite.connect(_on_relation_game_invite)
		singleton.onRelationTeamInvite.connect(_on_relation_team_invite)
		singleton.onLeaderboardStatus.connect(_on_leaderboard_status)
		singleton.onLeaderboardShareSuccess.connect(_on_leaderboard_share_success)
		singleton.onLeaderboardShareError.connect(_on_leaderboard_share_error)
		singleton.onLeaderboardSubmitScores.connect(_on_leaderboard_submit_scores)
		singleton.onLeaderboardScores.connect(_on_leaderboard_scores)
		singleton.onLeaderboardCurrentScore.connect(_on_leaderboard_current_score)
		singleton.onLeaderboardPlayerCenteredScores.connect(_on_leaderboard_player_centered_scores)
		singleton.onLeaderboardError.connect(_on_leaderboard_error)


func initialize(client_id: String, client_token: String, region: String = "CN", enable_log: bool = false):
	if singleton == null:
		push_error("GodotTapTapSDK is only available in an Android export with the plugin enabled.")
		return
	if client_id.is_empty() or client_token.is_empty():
		push_error("TapTap Client ID and Client Token are required.")
		return
	singleton.initialize(client_id, client_token, region, enable_log)
	_initialized = true


func tap_login():
	if _require_initialization():
		singleton.login()


func isLogin() -> bool:
	return _require_initialization() and singleton.isLogin()


func getCurrentProfile():
	if not _require_initialization():
		return null
	return singleton.getCurrentProfile()


func logOut():
	if _require_initialization():
		singleton.logOut()


func quickCheck(user_identifier: String = ""):
	if _require_initialization():
		singleton.quickCheck(user_identifier)


func antiExit():
	if _require_initialization():
		singleton.antiExit()


func momentOpen():
	if _require_initialization():
		singleton.momentOpen()


func check_force_update():
	if _require_initialization():
		singleton.checkForceUpdate()


func update_game():
	if _require_initialization():
		singleton.updateGame()


func check_license(force_check: bool = false):
	if _require_initialization():
		singleton.checkLicense(force_check)


func query_dlc(sku_ids: PackedStringArray):
	if _require_initialization():
		singleton.queryDLC(JSON.stringify(Array(sku_ids)))


func purchase_dlc(sku_id: String):
	if _require_initialization():
		singleton.purchaseDLC(sku_id)


func share(
		title: String = "",
		contents: String = "",
		hashtag_ids: String = "",
		group_label_id: String = "",
		image_uris: PackedStringArray = PackedStringArray(),
		fail_url: String = ""
	) -> int:
	if not _require_initialization():
		return -1
	return singleton.share(
		title,
		contents,
		hashtag_ids,
		group_label_id,
		JSON.stringify(Array(image_uris)),
		fail_url
	)


func open_review():
	if _require_initialization():
		singleton.openReview()


func unlock_achievement(achievement_id: String):
	if _require_initialization():
		singleton.unlockAchievement(achievement_id)


func increment_achievement(achievement_id: String, steps: int):
	if _require_initialization():
		singleton.incrementAchievement(achievement_id, steps)


func show_achievements():
	if _require_initialization():
		singleton.showAchievements()


func set_achievement_toast_enabled(enabled: bool):
	if _require_initialization():
		singleton.setAchievementToastEnabled(enabled)


func open_rep(url: String):
	if _require_initialization():
		singleton.openRep(url)


func log_analytics_event(name: String, properties: Dictionary = {}):
	if _require_initialization():
		singleton.logAnalyticsEvent(name, JSON.stringify(properties))


func set_analytics_user_id(user_id: String, properties: Dictionary = {}):
	if _require_initialization():
		singleton.setAnalyticsUserId(user_id, JSON.stringify(properties))


func clear_analytics_user():
	if _require_initialization():
		singleton.clearAnalyticsUser()


func get_analytics_device_id() -> String:
	if not _require_initialization():
		return ""
	return singleton.getAnalyticsDeviceId()


func add_analytics_common(properties: Dictionary):
	if _require_initialization():
		singleton.addAnalyticsCommon(JSON.stringify(properties))


func clear_analytics_common(keys: PackedStringArray):
	if _require_initialization():
		singleton.clearAnalyticsCommon(JSON.stringify(Array(keys)))


func clear_all_analytics_common():
	if _require_initialization():
		singleton.clearAllAnalyticsCommon()


func analytics_device_initialize(properties: Dictionary):
	if _require_initialization():
		singleton.deviceInitialize(JSON.stringify(properties))


func analytics_device_update(properties: Dictionary):
	if _require_initialization():
		singleton.deviceUpdate(JSON.stringify(properties))


func analytics_device_add(properties: Dictionary):
	if _require_initialization():
		singleton.deviceAdd(JSON.stringify(properties))


func analytics_user_initialize(properties: Dictionary):
	if _require_initialization():
		singleton.userInitialize(JSON.stringify(properties))


func analytics_user_update(properties: Dictionary):
	if _require_initialization():
		singleton.userUpdate(JSON.stringify(properties))


func analytics_user_add(properties: Dictionary):
	if _require_initialization():
		singleton.userAdd(JSON.stringify(properties))


func set_analytics_oaid(oaid: String):
	if _require_initialization():
		singleton.setAnalyticsOAID(oaid)


func log_device_login_event():
	if _require_initialization():
		singleton.logDeviceLoginEvent()


func create_cloud_archive(metadata: Dictionary, data_file_path: String, cover_file_path: String = ""):
	if _require_initialization():
		singleton.createCloudArchive(JSON.stringify(metadata), data_file_path, cover_file_path)


func update_cloud_archive(
		archive_uuid: String,
		metadata: Dictionary,
		data_file_path: String,
		cover_file_path: String = ""
	):
	if _require_initialization():
		singleton.updateCloudArchive(
			archive_uuid,
			JSON.stringify(metadata),
			data_file_path,
			cover_file_path
		)


func delete_cloud_archive(archive_uuid: String):
	if _require_initialization():
		singleton.deleteCloudArchive(archive_uuid)


func get_cloud_archive_list():
	if _require_initialization():
		singleton.getCloudArchiveList()


func get_cloud_archive_data(archive_uuid: String, archive_file_id: String):
	if _require_initialization():
		singleton.getCloudArchiveData(archive_uuid, archive_file_id)


func get_cloud_archive_cover(archive_uuid: String, archive_file_id: String):
	if _require_initialization():
		singleton.getCloudArchiveCover(archive_uuid, archive_file_id)


func prepare_relation():
	if _require_initialization():
		singleton.prepareRelation()


func start_relation_messenger():
	if _require_initialization():
		singleton.startRelationMessenger()


func invite_relation_game():
	if _require_initialization():
		singleton.inviteRelationGame()


func invite_relation_team(team_id: String):
	if _require_initialization():
		singleton.inviteRelationTeam(team_id)


func show_relation_user_profile(open_id: String = "", union_id: String = ""):
	if _require_initialization():
		singleton.showRelationUserProfile(open_id, union_id)


func get_relation_new_fans_count() -> int:
	if not _require_initialization():
		return 0
	return singleton.getRelationNewFansCount()


func get_relation_unread_message_count() -> int:
	if not _require_initialization():
		return 0
	return singleton.getRelationUnreadMessageCount()


func destroy_relation():
	if _require_initialization():
		singleton.destroyRelation()


func open_leaderboard(leaderboard_id: String, collection: String = "public"):
	if _require_initialization():
		singleton.openLeaderboard(leaderboard_id, collection)


func show_leaderboard_user_profile(open_id: String = "", union_id: String = ""):
	if _require_initialization():
		singleton.showLeaderboardUserProfile(open_id, union_id)


func submit_leaderboard_scores(scores: Array):
	if _require_initialization():
		singleton.submitLeaderboardScores(JSON.stringify(scores))


func load_leaderboard_scores(
		leaderboard_id: String,
		collection: String = "public",
		next_page: String = "",
		period_token: String = ""
	):
	if _require_initialization():
		singleton.loadLeaderboardScores(leaderboard_id, collection, next_page, period_token)


func load_current_leaderboard_score(
		leaderboard_id: String,
		collection: String = "public",
		period_token: String = ""
	):
	if _require_initialization():
		singleton.loadCurrentLeaderboardScore(leaderboard_id, collection, period_token)


func load_player_centered_leaderboard_scores(
		leaderboard_id: String,
		collection: String = "public",
		period_token: String = "",
		limit: int = 0
	):
	if _require_initialization():
		singleton.loadPlayerCenteredLeaderboardScores(leaderboard_id, collection, period_token, limit)


func _require_initialization() -> bool:
	if singleton == null:
		push_error("GodotTapTapSDK is unavailable on this platform.")
		return false
	if not _initialized:
		push_error("Call GodotTapTap.initialize() before using TapTap SDK features.")
		return false
	return true


func _on_login_result(code, json):
	onLoginResult.emit(code, json)


func _on_anti_addiction_callback(code):
	onAntiAddictionCallback.emit(code)


func _on_tap_moment_callback(code):
	onTapMomentCallBack.emit(code)


func _on_update_cancelled():
	onUpdateCancelled.emit()


func _on_license_validated():
	onLicenseValidated.emit()


func _on_dlc_query_result(code, states_json):
	onDLCQueryResult.emit(code, states_json)


func _on_dlc_purchase_result(sku_id, status):
	onDLCPurchaseResult.emit(sku_id, status)


func _on_share_result(code):
	onShareResult.emit(code)


func _on_achievement_result(code, result_json):
	onAchievementResult.emit(code, result_json)


func _on_achievement_error(achievement_id, code, message):
	onAchievementError.emit(achievement_id, code, message)


func _on_rep_result(code, message):
	onRepResult.emit(code, message)


func _on_cloud_save_status(code):
	onCloudSaveStatus.emit(code)


func _on_cloud_save_archive_created(archive_json):
	onCloudSaveArchiveCreated.emit(archive_json)


func _on_cloud_save_archive_updated(archive_json):
	onCloudSaveArchiveUpdated.emit(archive_json)


func _on_cloud_save_archive_deleted(archive_json):
	onCloudSaveArchiveDeleted.emit(archive_json)


func _on_cloud_save_archive_list(archives_json):
	onCloudSaveArchiveList.emit(archives_json)


func _on_cloud_save_archive_data(data_base64):
	onCloudSaveArchiveData.emit(data_base64)


func _on_cloud_save_archive_cover(data_base64):
	onCloudSaveArchiveCover.emit(data_base64)


func _on_cloud_save_error(code, message):
	onCloudSaveError.emit(code, message)


func _on_relation_messenger_result(code):
	onRelationMessengerResult.emit(code)


func _on_relation_fans_count_changed(code, fans_count):
	onRelationFansCountChanged.emit(code, fans_count)


func _on_relation_unread_message_count_changed(code, unread_message_count):
	onRelationUnreadMessageCountChanged.emit(code, unread_message_count)


func _on_relation_game_invite(open_id, union_id):
	onRelationGameInvite.emit(open_id, union_id)


func _on_relation_team_invite(open_id, union_id, team_id):
	onRelationTeamInvite.emit(open_id, union_id, team_id)


func _on_leaderboard_status(code, message):
	onLeaderboardStatus.emit(code, message)


func _on_leaderboard_share_success(local_path):
	onLeaderboardShareSuccess.emit(local_path)


func _on_leaderboard_share_error(message):
	onLeaderboardShareError.emit(message)


func _on_leaderboard_submit_scores(result_json):
	onLeaderboardSubmitScores.emit(result_json)


func _on_leaderboard_scores(result_json):
	onLeaderboardScores.emit(result_json)


func _on_leaderboard_current_score(result_json):
	onLeaderboardCurrentScore.emit(result_json)


func _on_leaderboard_player_centered_scores(result_json):
	onLeaderboardPlayerCenteredScores.emit(result_json)


func _on_leaderboard_error(operation, code, message):
	onLeaderboardError.emit(operation, code, message)
