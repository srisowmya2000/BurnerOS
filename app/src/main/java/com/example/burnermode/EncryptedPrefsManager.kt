package com.example.burnermode

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class EncryptedPrefsManager(context: Context) {
    private val prefs: SharedPreferences

    init {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        prefs = EncryptedSharedPreferences.create(
            context,
            "burner_prefs",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    fun setBurnerActive(active: Boolean) {
        prefs.edit().putBoolean(KEY_BURNER_ACTIVE, active).apply()
    }

    fun isBurnerActive(): Boolean = prefs.getBoolean(KEY_BURNER_ACTIVE, false)

    fun setStartTime(ts: Long) {
        prefs.edit().putLong(KEY_START_TIME, ts).apply()
    }

    fun getStartTime(): Long = prefs.getLong(KEY_START_TIME, 0L)

    // Secure Notes
    fun saveSecureNote(note: String) {
        prefs.edit().putString(KEY_SECURE_NOTE, note).apply()
    }

    fun getSecureNote(): String = prefs.getString(KEY_SECURE_NOTE, "") ?: ""

    // Secure Contacts (Stored as a single string for simplicity)
    fun saveContacts(contacts: String) {
        prefs.edit().putString(KEY_CONTACTS, contacts).apply()
    }

    fun getContacts(): String = prefs.getString(KEY_CONTACTS, "") ?: ""

    // PIN helpers - Now with auto-recovery
    fun putPin(pin: String) {
        prefs.edit().putString(KEY_PIN, pin).apply()
    }

    fun getPin(): String {
        val p = prefs.getString(KEY_PIN, null)
        if (p.isNullOrEmpty()) {
            putPin("1234") 
            return "1234"
        }
        return p
    }

    fun putDuressPin(pin: String) {
        prefs.edit().putString(KEY_DURESS_PIN, pin).apply()
    }

    fun getDuressPin(): String {
        val p = prefs.getString(KEY_DURESS_PIN, null)
        if (p.isNullOrEmpty()) {
            putDuressPin("9999")
            return "9999"
        }
        return p
    }

    fun clearAll() {
        prefs.edit().clear().apply()
        putPin("1234")
        putDuressPin("9999")
    }

    companion object {
        private const val KEY_BURNER_ACTIVE = "burner_active"
        private const val KEY_START_TIME = "start_time"
        private const val KEY_PIN = "pin"
        private const val KEY_DURESS_PIN = "duress_pin"
        private const val KEY_SECURE_NOTE = "secure_note"
        private const val KEY_CONTACTS = "secure_contacts"
    }
}
