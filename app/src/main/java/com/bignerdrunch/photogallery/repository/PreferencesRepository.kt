package com.bignerdrunch.photogallery.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStoreFile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlin.math.acos

class PreferencesRepository private constructor (private val dataStore: DataStore<Preferences>) {

    /**
     * Return stored string
     */
    //dataStore.data - access to file's data
    val storedQuery: Flow<String> = dataStore.data.map{
        it[SEARCH_QUERY_KEY] ?: ""

        //Returns flow where all subsequent repetitions of the same value are filtered out
    }.distinctUntilChanged()

    /**
     * Set a new string in a file
     */
    suspend fun setStoredQuery(query: String) {
        dataStore.edit {
            it[SEARCH_QUERY_KEY] = query
        }
    }

    val lastResultId: Flow<String> = dataStore.data.map {
        it[PREF_LAST_RESULT_ID] ?: ""
    }.distinctUntilChanged()

    /**
     * write the last photo's id in the file
     */
    suspend fun setLastResultId(lastResultId: String) {
        dataStore.edit {
            it[PREF_LAST_RESULT_ID] = lastResultId
        }
    }

    val isPolling: Flow<Boolean> = dataStore.data.map {
        it[PREF_IS_POLLING] ?: false
    }.distinctUntilChanged()

    suspend fun setPolling(isPolling: Boolean) {
        dataStore.edit {
            it[PREF_IS_POLLING] = isPolling
        }
    }

    companion object {

        //Get a key for a String preference
        private val SEARCH_QUERY_KEY = stringPreferencesKey("search_query")
        private val PREF_LAST_RESULT_ID = stringPreferencesKey("lastResultId")
        private val PREF_IS_POLLING = booleanPreferencesKey("osPolling")
        private var INSTANCE: PreferencesRepository? = null

        fun initialize(context: Context) {
            if (INSTANCE == null) {
                val dataStore = PreferenceDataStoreFactory.create{

                    //create a file
                    context.preferencesDataStoreFile("settings")
            }

                INSTANCE = PreferencesRepository(dataStore)
            }
        }

        fun get(): PreferencesRepository {
            return INSTANCE ?: throw IllegalStateException(
                "PreferencesRepository must be initialized"
            )
        }
    }
}