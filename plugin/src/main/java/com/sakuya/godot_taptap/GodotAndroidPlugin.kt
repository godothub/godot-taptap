package com.sakuya.godot_taptap

import android.net.Uri
import android.util.Base64
import android.util.Log
import com.sakuya.godot_taptap.taptap.GodotTapTap
import com.taptap.sdk.achievement.TapAchievementCallback
import com.taptap.sdk.achievement.TapTapAchievement
import com.taptap.sdk.achievement.TapTapAchievementResult
import com.taptap.sdk.compliance.TapTapCompliance
import com.taptap.sdk.compliance.TapTapComplianceCallback
import com.taptap.sdk.cloudsave.ArchiveData
import com.taptap.sdk.cloudsave.ArchiveMetadata
import com.taptap.sdk.cloudsave.TapTapCloudSave
import com.taptap.sdk.cloudsave.internal.TapCloudSaveCallback
import com.taptap.sdk.cloudsave.internal.TapCloudSaveRequestCallback
import com.taptap.sdk.core.TapTapEvent
import com.taptap.sdk.leaderboard.androidx.TapTapLeaderboard
import com.taptap.sdk.leaderboard.callback.ITapTapLeaderboardResponseCallback
import com.taptap.sdk.leaderboard.callback.ITapTapLeaderboardShareCallback
import com.taptap.sdk.leaderboard.callback.TapTapLeaderboardCallback
import com.taptap.sdk.leaderboard.data.request.LeaderboardCollection
import com.taptap.sdk.leaderboard.data.request.SubmitScoresRequest
import com.taptap.sdk.leaderboard.data.response.LeaderboardScoresResponse
import com.taptap.sdk.leaderboard.data.response.SubmitScoresResponse
import com.taptap.sdk.leaderboard.data.response.UserScoreResponse
import com.taptap.sdk.leaderboard.data.response.common.Leaderboard
import com.taptap.sdk.leaderboard.data.response.common.Score
import com.taptap.sdk.license.TapTapDLCCallback
import com.taptap.sdk.license.TapTapLicense
import com.taptap.sdk.license.TapTapLicenseCallback
import com.taptap.sdk.moment.TapTapMoment
import com.taptap.sdk.rep.TapTapRep
import com.taptap.sdk.rep.internal.TapRepResultCallback
import com.taptap.sdk.relation.TapTapRelation
import com.taptap.sdk.relation.TapTapRelationCallback
import com.taptap.sdk.relation.TapTapRelationInviteCallback
import com.taptap.sdk.review.TapTapReview
import com.taptap.sdk.share.TapTapShareBuilder
import com.taptap.sdk.update.TapTapUpdate
import com.taptap.sdk.update.TapTapUpdateCallback
import org.godotengine.godot.Godot
import org.godotengine.godot.plugin.GodotPlugin
import org.godotengine.godot.plugin.SignalInfo
import org.godotengine.godot.plugin.UsedByGodot
import org.json.JSONArray
import org.json.JSONObject

class GodotAndroidPlugin(godot: Godot) : GodotPlugin(godot) {

    private var godotTapTap: GodotTapTap? = null
    private var moduleCallbacksRegistered = false

    override fun getPluginName() = "GodotTapTapSDK"

    override fun getPluginSignals(): MutableSet<SignalInfo> {
        return mutableSetOf(
            SignalInfo("onLoginResult", Integer::class.java, String::class.java),
            SignalInfo("onAntiAddictionCallback", Integer::class.java),
            SignalInfo("onTapMomentCallBack", Integer::class.java),
            SignalInfo("onUpdateCancelled"),
            SignalInfo("onLicenseValidated"),
            SignalInfo("onDLCQueryResult", Integer::class.java, String::class.java),
            SignalInfo("onDLCPurchaseResult", String::class.java, Integer::class.java),
            SignalInfo("onShareResult", Integer::class.java),
            SignalInfo("onAchievementResult", Integer::class.java, String::class.java),
            SignalInfo("onAchievementError", String::class.java, Integer::class.java, String::class.java),
            SignalInfo("onRepResult", Integer::class.java, String::class.java),
            SignalInfo("onCloudSaveStatus", Integer::class.java),
            SignalInfo("onCloudSaveArchiveCreated", String::class.java),
            SignalInfo("onCloudSaveArchiveUpdated", String::class.java),
            SignalInfo("onCloudSaveArchiveDeleted", String::class.java),
            SignalInfo("onCloudSaveArchiveList", String::class.java),
            SignalInfo("onCloudSaveArchiveData", String::class.java),
            SignalInfo("onCloudSaveArchiveCover", String::class.java),
            SignalInfo("onCloudSaveError", Integer::class.java, String::class.java),
            SignalInfo("onRelationMessengerResult", Integer::class.java),
            SignalInfo("onRelationFansCountChanged", Integer::class.java, Integer::class.java),
            SignalInfo("onRelationUnreadMessageCountChanged", Integer::class.java, Integer::class.java),
            SignalInfo("onRelationGameInvite", String::class.java, String::class.java),
            SignalInfo("onRelationTeamInvite", String::class.java, String::class.java, String::class.java),
            SignalInfo("onLeaderboardStatus", Integer::class.java, String::class.java),
            SignalInfo("onLeaderboardShareSuccess", String::class.java),
            SignalInfo("onLeaderboardShareError", String::class.java),
            SignalInfo("onLeaderboardSubmitScores", String::class.java),
            SignalInfo("onLeaderboardScores", String::class.java),
            SignalInfo("onLeaderboardCurrentScore", String::class.java),
            SignalInfo("onLeaderboardPlayerCenteredScores", String::class.java),
            SignalInfo("onLeaderboardError", String::class.java, Integer::class.java, String::class.java)
        )
    }

    @UsedByGodot
    private fun initialize(
        clientId: String,
        clientToken: String,
        region: String,
        enableLog: Boolean
    ) {
        val currentActivity = activity
        if (currentActivity == null) {
            Log.e(TAG, "TapSDK initialization failed because the activity is unavailable")
            return
        }

        godotTapTap = GodotTapTap.build {
            this.clientId = clientId
            this.clientToken = clientToken
            this.region = region
            this.enableLog = enableLog
        }
        godotTapTap?.initialize(currentActivity) {
            registerCallbacks()
        }
    }

    @UsedByGodot
    private fun isLogin(): Boolean = godotTapTap?.isLogin() ?: false

    @UsedByGodot
    private fun login() {
        val currentActivity = activity
        val tapTap = godotTapTap
        if (currentActivity == null || tapTap == null) {
            emitSignal("onLoginResult", LOGIN_FAILED, "")
            return
        }

        tapTap.login(currentActivity) { code, accountJson ->
            emitSignal("onLoginResult", code, accountJson)
        }
    }

    @UsedByGodot
    private fun getCurrentProfile(): String? = godotTapTap?.getCurrentProfile()

    @UsedByGodot
    private fun logOut() {
        TapTapCompliance.exit()
        godotTapTap?.logOut()
    }

    @UsedByGodot
    private fun quickCheck(userIdentifier: String) {
        val currentActivity = activity
        val resolvedIdentifier = userIdentifier.ifBlank { godotTapTap?.getCurrentOpenId().orEmpty() }
        if (currentActivity == null || resolvedIdentifier.isBlank()) {
            Log.w(TAG, "Compliance requires a logged-in TapTap account or an explicit user identifier")
            emitSignal("onAntiAddictionCallback", COMPLIANCE_CONFIGURATION_ERROR)
            return
        }
        TapTapCompliance.startup(currentActivity, resolvedIdentifier)
    }

    @UsedByGodot
    private fun antiExit() {
        TapTapCompliance.exit()
    }

    @UsedByGodot
    private fun momentOpen() {
        if (godotTapTap == null) {
            Log.w(TAG, "TapSDK must be initialized before opening TapTap Moment")
            return
        }
        TapTapMoment.open()
    }

    @UsedByGodot
    private fun checkForceUpdate() {
        TapTapUpdate.checkForceUpdate()
    }

    @UsedByGodot
    private fun updateGame() {
        val currentActivity = activity ?: return
        TapTapUpdate.updateGame(currentActivity, object : TapTapUpdateCallback {
            override fun onCancel() {
                emitSignal("onUpdateCancelled")
            }
        })
    }

    @UsedByGodot
    private fun checkLicense(forceCheck: Boolean) {
        activity?.let { TapTapLicense.checkLicense(it, forceCheck) }
    }

    @UsedByGodot
    private fun queryDLC(skuIdsJson: String) {
        activity?.let { TapTapLicense.queryDLC(it, parseStringList(skuIdsJson)) }
    }

    @UsedByGodot
    private fun purchaseDLC(skuId: String) {
        activity?.let { TapTapLicense.purchaseDLC(it, skuId) }
    }

    @UsedByGodot
    private fun share(
        title: String,
        contents: String,
        hashtagIds: String,
        groupLabelId: String,
        imageUrisJson: String,
        failUrl: String
    ): Int {
        val currentActivity = activity ?: return SHARE_ACTIVITY_UNAVAILABLE
        val builder = TapTapShareBuilder()
            .addTitle(title)
            .addContents(contents)
            .addHashtagIds(hashtagIds)
            .addGroupLabelId(groupLabelId)
            .addFailUrl(failUrl)
        val imageUris = parseStringList(imageUrisJson).mapTo(ArrayList()) { Uri.parse(it) }
        if (imageUris.isNotEmpty()) {
            builder.addFooterImageUrls(imageUris)
        }
        val result = builder.build().share(currentActivity)
        emitSignal("onShareResult", result)
        return result
    }

    @UsedByGodot
    private fun openReview() {
        TapTapReview.openReview()
    }

    @UsedByGodot
    private fun unlockAchievement(achievementId: String) {
        TapTapAchievement.unlock(achievementId)
    }

    @UsedByGodot
    private fun incrementAchievement(achievementId: String, steps: Int) {
        TapTapAchievement.increment(achievementId, steps)
    }

    @UsedByGodot
    private fun showAchievements() {
        TapTapAchievement.showAchievements()
    }

    @UsedByGodot
    private fun setAchievementToastEnabled(enabled: Boolean) {
        TapTapAchievement.setToastEnable(enabled)
    }

    @UsedByGodot
    private fun openRep(url: String) {
        TapTapRep.open(url, object : TapRepResultCallback {
            override fun onRepSuccess() {
                emitSignal("onRepResult", REP_SUCCESS, "")
            }

            override fun onRepFail(errorCode: Int, errorMessage: String) {
                emitSignal("onRepResult", errorCode, errorMessage)
            }
        })
    }

    @UsedByGodot
    private fun logAnalyticsEvent(name: String, propertiesJson: String) {
        TapTapEvent.logEvent(name, parseJsonObject(propertiesJson))
    }

    @UsedByGodot
    private fun setAnalyticsUserId(userId: String, propertiesJson: String) {
        TapTapEvent.setUserId(userId, parseJsonObject(propertiesJson))
    }

    @UsedByGodot
    private fun clearAnalyticsUser() {
        TapTapEvent.clearUser()
    }

    @UsedByGodot
    private fun getAnalyticsDeviceId(): String = TapTapEvent.getDeviceId().orEmpty()

    @UsedByGodot
    private fun addAnalyticsCommon(propertiesJson: String) {
        TapTapEvent.addCommon(parseJsonObject(propertiesJson))
    }

    @UsedByGodot
    private fun clearAnalyticsCommon(keysJson: String) {
        TapTapEvent.clearCommonProperties(*parseStringList(keysJson).toTypedArray())
    }

    @UsedByGodot
    private fun clearAllAnalyticsCommon() {
        TapTapEvent.clearAllCommonProperties()
    }

    @UsedByGodot
    private fun deviceInitialize(propertiesJson: String) {
        TapTapEvent.deviceInitialize(parseJsonObject(propertiesJson))
    }

    @UsedByGodot
    private fun deviceUpdate(propertiesJson: String) {
        TapTapEvent.deviceUpdate(parseJsonObject(propertiesJson))
    }

    @UsedByGodot
    private fun deviceAdd(propertiesJson: String) {
        TapTapEvent.deviceAdd(parseJsonObject(propertiesJson))
    }

    @UsedByGodot
    private fun userInitialize(propertiesJson: String) {
        TapTapEvent.userInitialize(parseJsonObject(propertiesJson))
    }

    @UsedByGodot
    private fun userUpdate(propertiesJson: String) {
        TapTapEvent.userUpdate(parseJsonObject(propertiesJson))
    }

    @UsedByGodot
    private fun userAdd(propertiesJson: String) {
        TapTapEvent.userAdd(parseJsonObject(propertiesJson))
    }

    @UsedByGodot
    private fun setAnalyticsOAID(oaid: String) {
        TapTapEvent.setOAID(oaid)
    }

    @UsedByGodot
    private fun logDeviceLoginEvent() {
        TapTapEvent.logDeviceLoginEvent()
    }

    @UsedByGodot
    private fun createCloudArchive(metadataJson: String, dataFilePath: String, coverFilePath: String) {
        TapTapCloudSave.createArchive(
            parseArchiveMetadata(metadataJson),
            dataFilePath,
            coverFilePath,
            cloudSaveRequestCallback()
        )
    }

    @UsedByGodot
    private fun updateCloudArchive(
        archiveUuid: String,
        metadataJson: String,
        dataFilePath: String,
        coverFilePath: String
    ) {
        TapTapCloudSave.updateArchive(
            archiveUuid,
            parseArchiveMetadata(metadataJson),
            dataFilePath,
            coverFilePath,
            cloudSaveRequestCallback()
        )
    }

    @UsedByGodot
    private fun deleteCloudArchive(archiveUuid: String) {
        TapTapCloudSave.deleteArchive(archiveUuid, cloudSaveRequestCallback())
    }

    @UsedByGodot
    private fun getCloudArchiveList() {
        TapTapCloudSave.getArchiveList(cloudSaveRequestCallback())
    }

    @UsedByGodot
    private fun getCloudArchiveData(archiveUuid: String, archiveFileId: String) {
        TapTapCloudSave.getArchiveData(archiveUuid, archiveFileId, cloudSaveRequestCallback())
    }

    @UsedByGodot
    private fun getCloudArchiveCover(archiveUuid: String, archiveFileId: String) {
        TapTapCloudSave.getArchiveCover(archiveUuid, archiveFileId, cloudSaveRequestCallback())
    }

    @UsedByGodot
    private fun prepareRelation() {
        TapTapRelation.prepare()
    }

    @UsedByGodot
    private fun startRelationMessenger() {
        TapTapRelation.startMessenger()
    }

    @UsedByGodot
    private fun inviteRelationGame() {
        TapTapRelation.inviteGame()
    }

    @UsedByGodot
    private fun inviteRelationTeam(teamId: String) {
        TapTapRelation.inviteTeam(teamId)
    }

    @UsedByGodot
    private fun showRelationUserProfile(openId: String, unionId: String) {
        TapTapRelation.showTapUserProfile(openId, unionId)
    }

    @UsedByGodot
    private fun getRelationNewFansCount(): Int = TapTapRelation.getNewFansCount()

    @UsedByGodot
    private fun getRelationUnreadMessageCount(): Int = TapTapRelation.getUnreadMessageCount()

    @UsedByGodot
    private fun destroyRelation() {
        TapTapRelation.destroy()
    }

    @UsedByGodot
    private fun openLeaderboard(leaderboardId: String, collection: String) {
        activity?.let { TapTapLeaderboard.openLeaderboard(it, leaderboardId, collection) }
    }

    @UsedByGodot
    private fun showLeaderboardUserProfile(openId: String, unionId: String) {
        activity?.let { TapTapLeaderboard.showTapUserProfile(it, openId, unionId) }
    }

    @UsedByGodot
    private fun submitLeaderboardScores(scoresJson: String) {
        val scores = parseLeaderboardScoreItems(scoresJson)
        if (scores.isEmpty()) {
            emitSignal("onLeaderboardError", "submitScores", INVALID_ARGUMENT, "At least one score is required")
            return
        }
        TapTapLeaderboard.submitScores(scores, object : ITapTapLeaderboardResponseCallback<SubmitScoresResponse> {
            override fun onSuccess(data: SubmitScoresResponse) {
                emitSignal("onLeaderboardSubmitScores", submitScoresToJson(data))
            }

            override fun onFailure(code: Int, message: String) {
                emitSignal("onLeaderboardError", "submitScores", code, message)
            }
        })
    }

    @UsedByGodot
    private fun loadLeaderboardScores(
        leaderboardId: String,
        collection: String,
        nextPage: String,
        periodToken: String
    ) {
        TapTapLeaderboard.loadLeaderboardScores(
            leaderboardId,
            parseLeaderboardCollection(collection),
            nextPage.ifBlank { null },
            periodToken.ifBlank { null },
            object : ITapTapLeaderboardResponseCallback<LeaderboardScoresResponse> {
                override fun onSuccess(data: LeaderboardScoresResponse) {
                    emitSignal("onLeaderboardScores", leaderboardScoresToJson(data))
                }

                override fun onFailure(code: Int, message: String) {
                    emitSignal("onLeaderboardError", "loadScores", code, message)
                }
            }
        )
    }

    @UsedByGodot
    private fun loadCurrentLeaderboardScore(leaderboardId: String, collection: String, periodToken: String) {
        TapTapLeaderboard.loadCurrentPlayerLeaderboardScore(
            leaderboardId,
            parseLeaderboardCollection(collection),
            periodToken.ifBlank { null },
            object : ITapTapLeaderboardResponseCallback<UserScoreResponse> {
                override fun onSuccess(data: UserScoreResponse) {
                    emitSignal("onLeaderboardCurrentScore", userScoreToJson(data))
                }

                override fun onFailure(code: Int, message: String) {
                    emitSignal("onLeaderboardError", "loadCurrentScore", code, message)
                }
            }
        )
    }

    @UsedByGodot
    private fun loadPlayerCenteredLeaderboardScores(
        leaderboardId: String,
        collection: String,
        periodToken: String,
        limit: Int
    ) {
        TapTapLeaderboard.loadPlayerCenteredScores(
            leaderboardId,
            parseLeaderboardCollection(collection),
            periodToken.ifBlank { null },
            limit.takeIf { it > 0 },
            object : ITapTapLeaderboardResponseCallback<LeaderboardScoresResponse> {
                override fun onSuccess(data: LeaderboardScoresResponse) {
                    emitSignal("onLeaderboardPlayerCenteredScores", leaderboardScoresToJson(data))
                }

                override fun onFailure(code: Int, message: String) {
                    emitSignal("onLeaderboardError", "loadPlayerCenteredScores", code, message)
                }
            }
        )
    }

    private fun registerCallbacks() {
        if (moduleCallbacksRegistered) {
            return
        }
        moduleCallbacksRegistered = true

        TapTapCompliance.registerComplianceCallback(
            object : TapTapComplianceCallback {
                override fun onComplianceResult(code: Int, extra: Map<String, Any>?) {
                    emitSignal("onAntiAddictionCallback", code)
                }
            }
        )

        TapTapMoment.setCallback(
            object : TapTapMoment.TapTapMomentCallback {
                override fun onCallback(code: Int, msg: String?) {
                    emitSignal("onTapMomentCallBack", code)
                }
            }
        )

        TapTapLicense.registerLicenseCallback(
            object : TapTapLicenseCallback {
                override fun onLicenseSuccess() {
                    emitSignal("onLicenseValidated")
                }
            }
        )

        TapTapLicense.registerDLCCallback(
            object : TapTapDLCCallback {
                override fun onQueryResult(resultCode: Int, resultList: HashMap<String, Int>?) {
                    val result = JSONObject()
                    resultList?.forEach { (skuId, status) -> result.put(skuId, status) }
                    emitSignal("onDLCQueryResult", resultCode, result.toString())
                }

                override fun onPurchaseResult(skuId: String, status: Int) {
                    emitSignal("onDLCPurchaseResult", skuId, status)
                }
            }
        )

        TapTapAchievement.registerCallback(
            object : TapAchievementCallback {
                override fun onAchievementSuccess(code: Int, result: TapTapAchievementResult?) {
                    emitSignal(
                        "onAchievementResult",
                        code,
                        result?.let(::achievementResultToJson) ?: JSONObject().toString()
                    )
                }

                override fun onAchievementFailure(achievementId: String, errorCode: Int, errorMessage: String) {
                    emitSignal("onAchievementError", achievementId, errorCode, errorMessage)
                }
            }
        )

        TapTapCloudSave.registerCloudSaveCallback(
            object : TapCloudSaveCallback {
                override fun onResult(resultCode: Int) {
                    emitSignal("onCloudSaveStatus", resultCode)
                }
            }
        )

        TapTapRelation.registerRelationCallback(
            object : TapTapRelationCallback {
                override fun onMessengerCodeResult(code: Int) {
                    emitSignal("onRelationMessengerResult", code)
                }

                override fun onNewFansCountChanged(code: Int, fansCount: Int) {
                    emitSignal("onRelationFansCountChanged", code, fansCount)
                }

                override fun onUnreadMessageCountChanged(code: Int, unreadMsgCount: Int) {
                    emitSignal("onRelationUnreadMessageCountChanged", code, unreadMsgCount)
                }
            }
        )

        TapTapRelation.registerRelationInviteCallback(
            object : TapTapRelationInviteCallback {
                override fun onGameInviteReceived(openId: String, unionId: String) {
                    emitSignal("onRelationGameInvite", openId, unionId)
                }

                override fun onTeamInviteReceived(openId: String, unionId: String, teamId: String) {
                    emitSignal("onRelationTeamInvite", openId, unionId, teamId)
                }
            }
        )

        TapTapLeaderboard.registerLeaderboardCallback(
            object : TapTapLeaderboardCallback {
                override fun onLeaderboardResult(code: Int, message: String) {
                    emitSignal("onLeaderboardStatus", code, message)
                }
            }
        )

        TapTapLeaderboard.setShareCallback(
            object : ITapTapLeaderboardShareCallback {
                override fun onShareSuccess(localPath: String) {
                    emitSignal("onLeaderboardShareSuccess", localPath)
                }

                override fun onShareFailed(error: Throwable) {
                    emitSignal("onLeaderboardShareError", error.message.orEmpty())
                }
            }
        )
    }

    private fun achievementResultToJson(result: TapTapAchievementResult): String {
        return JSONObject()
            .put("achievementId", result.achievementId)
            .put("achievementName", result.achievementName)
            .put("achievementType", result.achievementType.toString())
            .put("currentSteps", result.currentSteps)
            .toString()
    }

    private fun parseJsonObject(value: String): JSONObject {
        if (value.isBlank()) {
            return JSONObject()
        }
        return try {
            JSONObject(value)
        } catch (error: Exception) {
            Log.w(TAG, "Invalid JSON object passed to TapTap SDK", error)
            JSONObject()
        }
    }

    private fun parseStringList(value: String): List<String> {
        if (value.isBlank()) {
            return emptyList()
        }
        return try {
            val values = JSONArray(value)
            List(values.length()) { index -> values.getString(index) }
        } catch (error: Exception) {
            Log.w(TAG, "Invalid JSON string array passed to TapTap SDK", error)
            emptyList()
        }
    }

    private fun cloudSaveRequestCallback() = object : TapCloudSaveRequestCallback {
        override fun onArchiveCreated(archive: ArchiveData) {
            emitSignal("onCloudSaveArchiveCreated", archiveToJson(archive))
        }

        override fun onArchiveUpdated(archive: ArchiveData) {
            emitSignal("onCloudSaveArchiveUpdated", archiveToJson(archive))
        }

        override fun onArchiveDeleted(archive: ArchiveData) {
            emitSignal("onCloudSaveArchiveDeleted", archiveToJson(archive))
        }

        override fun onArchiveListResult(archiveList: List<ArchiveData>) {
            val archives = JSONArray()
            archiveList.forEach { archives.put(JSONObject(archiveToJson(it))) }
            emitSignal("onCloudSaveArchiveList", archives.toString())
        }

        override fun onArchiveDataResult(archiveData: ByteArray) {
            emitSignal("onCloudSaveArchiveData", Base64.encodeToString(archiveData, Base64.NO_WRAP))
        }

        override fun onArchiveCoverResult(coverData: ByteArray) {
            emitSignal("onCloudSaveArchiveCover", Base64.encodeToString(coverData, Base64.NO_WRAP))
        }

        override fun onRequestError(errorCode: Int, errorMessage: String) {
            emitSignal("onCloudSaveError", errorCode, errorMessage)
        }
    }

    private fun parseArchiveMetadata(metadataJson: String): ArchiveMetadata {
        val metadata = parseJsonObject(metadataJson)
        return ArchiveMetadata.Builder()
            .setName(metadata.optString("name"))
            .setSummary(metadata.optString("summary"))
            .setExtra(metadata.optString("extra"))
            .setPlaytime(metadata.optInt("playtime"))
            .build()
    }

    private fun archiveToJson(data: ArchiveData): String {
        return JSONObject()
            .put("uuid", data.uuid)
            .put("fileId", data.fileId)
            .put("name", data.name)
            .put("summary", data.summary)
            .put("extra", data.extra)
            .put("playtime", data.playtime)
            .put("saveSize", data.saveSize)
            .put("coverSize", data.coverSize)
            .put("createdTime", data.createdTime)
            .put("modifiedTime", data.modifiedTime)
            .toString()
    }

    private fun parseLeaderboardScoreItems(scoresJson: String): List<SubmitScoresRequest.ScoreItem> {
        return try {
            val values = JSONArray(scoresJson)
            buildList {
                for (index in 0 until values.length()) {
                    val item = values.getJSONObject(index)
                    val leaderboardId = item.optString("leaderboardId")
                    if (leaderboardId.isNotBlank()) {
                        add(SubmitScoresRequest.ScoreItem(leaderboardId, item.optLong("score")))
                    }
                }
            }
        } catch (error: Exception) {
            Log.w(TAG, "Invalid leaderboard score array", error)
            emptyList()
        }
    }

    private fun parseLeaderboardCollection(value: String): LeaderboardCollection {
        return if (value.equals("friends", ignoreCase = true)) {
            LeaderboardCollection.FRIENDS
        } else {
            LeaderboardCollection.PUBLIC
        }
    }

    private fun submitScoresToJson(data: SubmitScoresResponse): String {
        val items = JSONArray()
        data.items.forEach { item ->
            val score = item.score
            items.put(
                JSONObject()
                    .put("leaderboardId", item.leaderboardId)
                    .put("openId", item.openId)
                    .put("unionId", item.unionId)
                    .put("periodToken", item.periodToken)
                    .put(
                        "score",
                        JSONObject()
                            .put("newBest", score?.newBest)
                            .put("rawScore", score?.rawScore)
                            .put("scoreDisplay", score?.scoreDisplay)
                    )
            )
        }
        return JSONObject().put("items", items).toString()
    }

    private fun leaderboardScoresToJson(data: LeaderboardScoresResponse): String {
        val scores = JSONArray()
        data.scores.forEach { scores.put(scoreToJson(it)) }
        return JSONObject()
            .put("leaderboard", leaderboardToJson(data.leaderboard))
            .put("scores", scores)
            .put("nextPage", data.nextPage)
            .toString()
    }

    private fun userScoreToJson(data: UserScoreResponse): String {
        return JSONObject()
            .put("currentUserScore", scoreToJson(data.currentUserScore))
            .put("leaderboard", leaderboardToJson(data.leaderboard))
            .toString()
    }

    private fun leaderboardToJson(leaderboard: Leaderboard?): JSONObject {
        if (leaderboard == null) {
            return JSONObject()
        }
        val periods = JSONArray()
        leaderboard.availablePeriods.forEach { period ->
            periods.put(
                JSONObject()
                    .put("display", period.display)
                    .put("periodToken", period.periodToken)
            )
        }
        return JSONObject()
            .put("id", leaderboard.id)
            .put("name", leaderboard.name)
            .put("availablePeriods", periods)
            .put(
                "period",
                leaderboard.period?.let {
                    JSONObject()
                        .put("display", it.display)
                        .put("periodToken", it.periodToken)
                }
            )
            .put("score", scoreToJson(leaderboard.score))
    }

    private fun scoreToJson(score: Score?): JSONObject {
        if (score == null) {
            return JSONObject()
        }
        return JSONObject()
            .put("rank", score.rank)
            .put("rankDisplay", score.rankDisplay)
            .put("score", score.score)
            .put("scoreDisplay", score.scoreDisplay)
            .put("scoreSubmittedTime", score.scoreSubmittedTime)
            .put(
                "user",
                score.user?.let { user ->
                    JSONObject()
                        .put("avatar", user.avatar)
                        .put("name", user.name)
                        .put("openId", user.openid)
                        .put("unionId", user.unionid)
                }
            )
    }

    private companion object {
        const val TAG = "GodotTapTapSDK"
        const val LOGIN_FAILED = 400
        const val COMPLIANCE_CONFIGURATION_ERROR = 1200
        const val REP_SUCCESS = 0
        const val SHARE_ACTIVITY_UNAVAILABLE = -1
        const val INVALID_ARGUMENT = -2
    }
}
