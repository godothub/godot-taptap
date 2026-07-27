package com.sakuya.godot_taptap.taptap

import android.app.Activity
import android.util.Log
import com.taptap.sdk.compliance.option.TapTapComplianceOptions
import com.taptap.sdk.core.TapTapRegion
import com.taptap.sdk.core.TapTapSdk
import com.taptap.sdk.core.TapTapSdkOptions
import com.taptap.sdk.kit.internal.callback.TapTapCallback
import com.taptap.sdk.kit.internal.exception.TapTapException
import com.taptap.sdk.login.Scopes
import com.taptap.sdk.login.TapTapAccount
import com.taptap.sdk.login.TapTapLogin
import org.json.JSONObject

class GodotTapTap(
    private val clientId: String,
    private val clientToken: String,
    private val region: String,
    private val enableLog: Boolean
) {

    companion object {
        private const val TAG = "GodotTapTapSDK"
        const val LOGIN_SUCCESS = 200
        const val LOGIN_FAILED = 400
        const val LOGIN_CANCELLED = 499

        inline fun build(block: Builder.() -> Unit) = Builder().apply(block).build()
    }

    class Builder {
        var clientId: String = ""
        var clientToken: String = ""
        var region: String = "CN"
        var enableLog: Boolean = false

        fun build() = GodotTapTap(clientId, clientToken, region, enableLog)
    }

    fun initialize(activity: Activity, onInitialized: () -> Unit) {
        if (clientId.isBlank() || clientToken.isBlank()) {
            Log.e(TAG, "Client ID and Client Token are required to initialize TapSDK")
            return
        }

        activity.runOnUiThread {
            TapTapSdk.init(
                context = activity,
                sdkOptions = TapTapSdkOptions(
                    clientId = clientId,
                    clientToken = clientToken,
                    region = parseRegion(region),
                    enableLog = enableLog
                ),
                options = arrayOf(
                    TapTapComplianceOptions(
                        showSwitchAccount = true,
                        useAgeRange = false
                    )
                )
            )
            onInitialized()
        }
    }

    fun isLogin(): Boolean = TapTapLogin.getCurrentTapAccount() != null

    fun login(activity: Activity, onResult: (Int, String) -> Unit) {
        val scopes = arrayOf(Scopes.SCOPE_PUBLIC_PROFILE)
        TapTapLogin.loginWithScopes(activity, scopes, object : TapTapCallback<TapTapAccount> {
            override fun onSuccess(result: TapTapAccount) {
                onResult(LOGIN_SUCCESS, accountToJson(result))
            }

            override fun onFail(exception: TapTapException) {
                Log.e(TAG, "TapTap login failed", exception)
                onResult(LOGIN_FAILED, "")
            }

            override fun onCancel() {
                onResult(LOGIN_CANCELLED, "")
            }
        })
    }

    fun getCurrentProfile(): String? {
        return TapTapLogin.getCurrentTapAccount()?.let(::accountToJson)
    }

    fun getCurrentOpenId(): String? = TapTapLogin.getCurrentTapAccount()?.openId

    fun logOut() {
        TapTapLogin.logout()
    }

    private fun accountToJson(account: TapTapAccount): String {
        return JSONObject()
            .put("openId", account.openId)
            .put("unionId", account.unionId)
            .toString()
    }

    private fun parseRegion(value: String): Int {
        return if (value.equals("GLOBAL", ignoreCase = true)) {
            TapTapRegion.GLOBAL
        } else {
            TapTapRegion.CN
        }
    }
}
