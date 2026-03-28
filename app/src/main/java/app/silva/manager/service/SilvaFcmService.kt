/*
 * Copyright 2026 Silva.
 * https://github.com/SilvaApp/silva-manager
 */

package app.silva.manager.service

import android.annotation.SuppressLint
import android.util.Log
import app.silva.manager.util.UpdateNotificationManager
import app.silva.manager.util.tag
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.koin.android.ext.android.inject

/**
 * Firebase Cloud Messaging service for Silva Manager.
 *
 * High-priority FCM messages (sent with `"android": { "priority": "high" }`) are
 * delivered by Google Play Services even when the device is in Doze mode or the
 * app process is dead — unlike WorkManager, which can be suppressed by aggressive
 * vendor battery optimisations (Xiaomi, Huawei, Samsung, OnePlus, etc.).
 *
 * ## Four-topic model
 *
 * Manager and patches topics are independent — each has its own stable/dev pair:
 *
 * | Topic                        | Audience                                              |
 * |------------------------------|-------------------------------------------------------|
 * | `morphe_updates`             | Manager: stable build AND prereleases OFF             |
 * | `morphe_updates_dev`         | Manager: dev build OR prereleases ON                  |
 * | `morphe_patches_updates`     | Patches: prereleases OFF                              |
 * | `morphe_patches_updates_dev` | Patches: prereleases ON                               |
 * | *(none)*                     | Notifications OFF                                     |
 *
 * A device with a dev manager build and prereleases OFF subscribes to **both**
 * `morphe_updates` and `morphe_updates_dev` — a stable release (e.g. `1.5.0`) is
 * a valid upgrade from a dev build (e.g. `1.5.0-dev.1`).
 *
 * The patches topic is determined solely by the "Use prereleases" preference,
 * independent of the installed manager build variant.
 *
 * Subscription is managed by [app.silva.manager.util.syncFcmTopics], which is called
 * whenever the user toggles "Background notifications" or "Use prereleases" in Settings,
 * and on every cold start in [app.silva.manager.ManagerApplication].
 *
 * ## Message contract
 *
 * Every FCM message must include a `type` key in its `data` map:
 *
 * | type             | extra keys       | action                                                          |
 * |------------------|------------------|-----------------------------------------------------------------|
 * | `manager_update` | `version` (opt.) | Calls [UpdateNotificationManager.showManagerUpdateNotification] |
 * | `bundle_update`  | `version` (opt.) | Calls [UpdateNotificationManager.showBundleUpdateNotification]  |
 *
 * `version` is optional in both types for compatibility.
 * Unknown types are silently ignored for forward-compatibility with future message types.
 */
@SuppressLint("MissingFirebaseInstanceTokenRefresh")
class SilvaFcmService : FirebaseMessagingService() {

    private val notificationManager: UpdateNotificationManager by inject()

    override fun onMessageReceived(message: RemoteMessage) {
        val data = message.data

        if (data.isEmpty()) {
            Log.w(tag, "SilvaFcmService: received empty data payload - ignoring")
            return
        }

        val type = data[KEY_TYPE]
        val version = data[KEY_VERSION]
        Log.d(tag, "SilvaFcmService: message received, type=$type, topic=${message.from}")

        when (type) {
            TYPE_MANAGER_UPDATE -> notificationManager.showManagerUpdateNotification(version)
            TYPE_BUNDLE_UPDATE  -> notificationManager.showBundleUpdateNotification(version)
            else -> Log.d(tag, "SilvaFcmService: unknown type '$type' - ignoring")
        }
    }

    companion object {
        private const val KEY_TYPE    = "type"
        private const val KEY_VERSION = "version"

        private const val TYPE_MANAGER_UPDATE = "manager_update"
        private const val TYPE_BUNDLE_UPDATE  = "bundle_update"
    }
}
