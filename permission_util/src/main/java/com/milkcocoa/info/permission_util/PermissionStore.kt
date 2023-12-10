package com.milkcocoa.info.permission_util

import android.content.Context
import androidx.core.content.edit

/**
 * PermissionStore
 * @author keita
 * @since 2023/12/10 21:09
 */

/**
 *
 */
object PermissionStore {
    private const val PREFERENCE = "PermissionStore"
    private const val TAG = "PermissionStore"

    private fun createKey(permission: String) = "${TAG}_$permission"

    @Synchronized
    fun storeState(context: Context, permission: String): Boolean{
        return runCatching {
            val preferences = context.getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE) ?: return@runCatching false
            preferences.edit(commit = true){
                putBoolean(createKey(permission), true)
            }
            true
        }.getOrDefault(false)
    }

    @Synchronized
    fun loadState(context: Context, permission: String): Boolean{
        return runCatching {
            val preferences = context.getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE) ?: return@runCatching false
            preferences.getBoolean(createKey(permission), false)
        }.getOrDefault(false)
    }
}
