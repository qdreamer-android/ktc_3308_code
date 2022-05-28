package com.pwong.library.utils

import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import org.json.JSONException
import org.json.JSONObject

object JsonHelper {

    private val mGson by lazy {
        GsonBuilder().create()
    }

    fun toJson(any: Any?): String {
        return mGson.toJson(any)
    }

    fun <T> fromJson(jsonStr: String?, cls: Class<T>): T {
        return mGson.fromJson(jsonStr ?: "", cls)
    }

    fun <T> fromJson(jsonStr: String, typeToken: TypeToken<T>): T? {
        return mGson.fromJson(jsonStr, typeToken.type)
    }

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