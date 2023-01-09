package com.bignerdrunch.photogallery.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStoreFile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlin.math.acos

/**
 * saves the data in the file system
 */
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

    /**
     * The last API's request result using for WorkManager
     */
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

    /**
     * polling state indicator
     */
    val isPolling: Flow<Boolean> = dataStore.data.map {
        it[PREF_IS_POLLING] ?: false
    }.distinctUntilChanged()

    /**
     * set the polling state indicator
     */
    suspend fun setPolling(isPolling: Boolean) {
        dataStore.edit {
            it[PREF_IS_POLLING] = isPolling
        }
    }

    companion object {

        //Get a key for a String preference
        private val SEARCH_QUERY_KEY = stringPreferencesKey("search_query")
        private val PREF_LAST_RESULT_ID = stringPreferencesKey("lastResultId")

        /**
         * polling key in the file
         */
        private val PREF_IS_POLLING = booleanPreferencesKey("osPolling")

        /**
         * PreferenceReposytory's variable
         */
        private var INSTANCE: PreferencesRepository? = null

        /**
         * creates a new PreferencesRepository's object
         */
        fun initialize(context: Context) {
            if (INSTANCE == null) {

                //creates a new DataStore<Preferences> object
                val dataStore = PreferenceDataStoreFactory.create{

                    //create a file for PreferencesRepository's object
                    context.preferencesDataStoreFile("settings")
            }

                /**
                 * a new PreferencesRepository's object
                 */
                INSTANCE = PreferencesRepository(dataStore)
            }
        }

        /**
         * get the PreferencesRepository's object if it exists
         */
        fun get(): PreferencesRepository {
            return INSTANCE ?: throw IllegalStateException(
                "PreferencesRepository must be initialized"
            )
        }
    }
}