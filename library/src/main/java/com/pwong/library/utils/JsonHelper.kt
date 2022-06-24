package com.pwong.library.utils

import org.json.JSONException
import org.json.JSONObject

object JsonHelper {

    fun getJsonString(json: String, key: String): String {
        val jo: JSONObject
        var ret = ""
        try {
            jo = JSONObject(json)
            ret = jo.getString(key)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return ret
    }

}