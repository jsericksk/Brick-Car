package com.kproject.brickcar.data.repository.prefs

interface PreferenceRepository {

    fun <T> getPreference(key: String, defaultValue: T): T

    fun <T> savePreference(key: String, value: T)
}