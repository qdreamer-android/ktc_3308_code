package com.pwong.uiframe.expand

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.Settings

/**
 * @author android
 * @date 2020/6/23
 * @instruction fucking bugs
 */
fun Activity.jumpToAppPermission() {
    val intent = Intent()
    intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
    intent.data = Uri.fromParts("package", packageName, null)
    startActivity(intent)
}

fun Activity.jumpToLocation() {
    startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
}