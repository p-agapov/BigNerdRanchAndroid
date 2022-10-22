package com.agapovp.bignerdranch.android.photogallery

import android.content.Context
import androidx.core.content.edit
import androidx.preference.PreferenceManager

private const val PREF_SEARCH_QUERY = "searchQuery"
private const val PREF_LAST_RESULT_ID = "lastResultId"
private const val PREF_IS_POLLING = "isPolling"

object QueryPreferences {

    fun getStoredQuery(context: Context): String =
        PreferenceManager.getDefaultSharedPreferences(context).getString(PREF_SEARCH_QUERY, "")!!

    fun setStoredQuery(context: Context, query: String) =
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit {
                putString(PREF_SEARCH_QUERY, query)
            }

    fun getLastResultId(context: Context): String =
        PreferenceManager.getDefaultSharedPreferences(context).getString(PREF_LAST_RESULT_ID, "")!!

    fun setLastResultId(context: Context, query: String) =
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit {
                putString(PREF_LAST_RESULT_ID, query)
            }

    fun isPolling(context: Context): Boolean =
        PreferenceManager.getDefaultSharedPreferences(context).getBoolean(PREF_IS_POLLING, false)

    fun setPolling(context: Context, isOn: Boolean) =
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit {
                putBoolean(PREF_IS_POLLING, isOn)
            }
}
