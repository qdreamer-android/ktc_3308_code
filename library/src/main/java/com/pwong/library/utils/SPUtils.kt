package com.pwong.library.utils

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences

import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method

object SPUtils {

    private val FILE_NAME = "AndroidP"

    fun put(context: Context?, key: String, value: Any) {
        if (context == null || ((context is Activity) && context.isDestroyed)) return
        val sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
        val editor = sp.edit()
        when (value) {
            is String -> editor.putString(key, value)
            is Int -> editor.putInt(key, value)
            is Boolean -> editor.putBoolean(key, value)
            is Float -> editor.putFloat(key, value)
            is Long -> editor.putLong(key, value)
            else -> editor.putString(key, value.toString())
        }
        SharedPreferencesCompat.apply(editor)
    }

    operator fun get(context: Context?, key: String, defaultObject: Any): Any? {
        if (context == null || ((context is Activity) && context.isDestroyed)) return null
        val sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
        return when (defaultObject) {
            is String -> sp.getString(key, defaultObject)
            is Int -> sp.getInt(key, defaultObject)
            is Boolean -> sp.getBoolean(key, defaultObject)
            is Float -> sp.getFloat(key, defaultObject)
            is Long -> sp.getLong(key, defaultObject)
            else -> null
        }
    }

    fun remove(context: Context?, key: String) {
        if (context == null || ((context is Activity) && context.isDestroyed)) return
        val sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
        val editor = sp.edit()
        editor.remove(key)
        SharedPreferencesCompat.apply(editor)
    }

    fun clear(context: Context?) {
        if (context == null || ((context is Activity) && context.isDestroyed)) return
        val sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
        val editor = sp.edit()
        editor.clear()
        SharedPreferencesCompat.apply(editor)
    }

    fun contains(context: Context?, key: String): Boolean {
        if (context == null || ((context is Activity) && context.isDestroyed)) return false
        val sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
        return sp.contains(key)
    }

    fun getAll(context: Context?): Map<String, *>? {
        if (context == null || ((context is Activity) && context.isDestroyed)) return null
        val sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
        return sp.all
    }

    private object SharedPreferencesCompat {
        private val sApplyMethod = findApplyMethod()

        private fun findApplyMethod(): Method? {
            try {
                val clz = SharedPreferences.Editor::class.java
                return clz.getMethod("apply")
            } catch (e: NoSuchMethodException) {
                e.printStackTrace()
            }
            return null
        }

        fun apply(editor: SharedPreferences.Editor) {
            try {
                if (sApplyMethod != null) {
                    sApplyMethod.invoke(editor)
                    return
                }
            } catch (e: IllegalArgumentException) {
                e.printStackTrace()
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            } catch (e: InvocationTargetException) {
                e.printStackTrace()
            }
            editor.commit()
        }
    }

}  