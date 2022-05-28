package com.pwong.uiframe.utils

import java.lang.reflect.ParameterizedType

object TUtil {
    fun <T> getT(any: Any, i: Int): T? {
        try {
            val supClass = any.javaClass.genericSuperclass as? ParameterizedType
            val arguments = supClass?.actualTypeArguments
            if (arguments != null && arguments.size >= i) {
                return (arguments[i] as Class<T>).newInstance()
            }
        } catch (e: InstantiationException) {
        } catch (e: IllegalAccessException) {
        } catch (e: ClassCastException) {
        }

        return null
    }
}