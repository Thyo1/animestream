package com.thyo.animestream.extractors.helper

import com.fasterxml.jackson.annotation.JsonProperty
import com.thyo.animestream.app

class WcoHelper {
    companion object {
        private const val BACKUP_KEY_DATA = "github_keys_backup"

        data class ExternalKeys(
            @JsonProperty("wco_key")
            val wcoKey: String? = null,
            @JsonProperty("wco_cipher_key")
            val wcocipher: String? = null
        )

        data class NewExternalKeys(
            @JsonProperty("cipherKey")
            val cipherkey: String? = null,
            @JsonProperty("encryptKey")
            val encryptKey: String? = null,
            @JsonProperty("mainKey")
            val mainKey: String? = null,
        )

        private var keys: ExternalKeys? = null
        private var newKeys: NewExternalKeys? = null
        
        // Mengambil kunci rahasia langsung dari link GitHub Pages milik Thyo
        private suspend fun getKeys() {
            keys = keys
                ?: app.get("https://thyo1.github.io/animestream-repo/keys.json")
                    .parsedSafe<ExternalKeys>()
        }

        suspend fun getWcoKey(): ExternalKeys? {
            getKeys()
            return keys
        }

        // Menggunakan link yang sama untuk kunci baru
        private suspend fun getNewKeys() {
            newKeys = newKeys
                ?: app.get("https://thyo1.github.io/animestream-repo/keys.json")
                    .parsedSafe<NewExternalKeys>()
        }

        suspend fun getNewWcoKey(): NewExternalKeys? {
            getNewKeys()
            return newKeys
        }
    }
}
