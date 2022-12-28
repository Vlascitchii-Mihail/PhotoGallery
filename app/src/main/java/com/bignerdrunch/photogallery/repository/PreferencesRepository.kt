package com.bignerdrunch.photogallery.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStoreFile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

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

    companion object {

        //Get a key for a String preference
        private val SEARCH_QUERY_KEY = stringPreferencesKey("search_query")
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