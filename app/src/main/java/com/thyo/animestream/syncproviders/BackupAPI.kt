package com.thyo.animestream.syncproviders

/** Work in progress */
abstract class BackupAPI : AuthAPI() {
    // MENGGANTI NAMA FILE BACKUP MENJADI ANIME STREAM
    open val filename : String = "animestream-backup.json"

    /** Get the backup file as a JSON string from the remote storage. Return null if not found/empty */
    @Throws
    open suspend fun downloadFile(auth: AuthData?) : String? = throw NotImplementedError()

    /** Get the backup file as a JSON string from the remote storage. */
    @Throws
    open suspend fun uploadFile(auth: AuthData?, data : String) : String? = throw NotImplementedError()
}
