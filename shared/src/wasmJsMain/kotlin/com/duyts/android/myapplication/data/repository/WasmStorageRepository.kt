package com.duyts.android.myapplication.data.repository

import com.duyts.android.myapplication.domain.repository.StorageRepository
import kotlinx.coroutines.suspendCancellableCoroutine
import me.tatarka.inject.annotations.Inject
import kotlin.coroutines.resume
import kotlin.js.ExperimentalWasmJsInterop
import kotlin.js.Uint8Array

@OptIn(ExperimentalWasmJsInterop::class)
@JsFun("(path, bytes, onSuccess, onError) => { const ref = firebase.storage().ref(path); ref.put(bytes).then(() => ref.getDownloadURL()).then((url) => onSuccess(url)).catch((e) => onError(e.message)); }")
external fun firebaseUploadBytes(path: String, bytes: Uint8Array, onSuccess: (String) -> Unit, onError: (String) -> Unit)

@Inject
class WasmStorageRepository : StorageRepository {
    override suspend fun uploadProfilePicture(userId: String, bytes: ByteArray): Result<String> = suspendCancellableCoroutine { continuation ->
        val uint8Array = Uint8Array(bytes.size)
        for (i in bytes.indices) {
            uint8Array[i] = bytes[i].toInt().toJsNumber()
        }
        
        firebaseUploadBytes("profile_pictures/$userId.jpg", uint8Array, { url ->
            continuation.resume(Result.success(url))
        }, { error ->
            continuation.resume(Result.failure(Exception(error)))
        })
    }
}
