package com.duyts.pokerhost.data.repository

import com.duyts.pokerhost.domain.repository.StorageRepository
import kotlinx.coroutines.suspendCancellableCoroutine
import me.tatarka.inject.annotations.Inject
import kotlin.coroutines.resume

@OptIn(ExperimentalWasmJsInterop::class)
@JsFun("(size) => new Uint8Array(size)")
external fun createUint8Array(size: Int): JsAny

@OptIn(ExperimentalWasmJsInterop::class)
@JsFun("(array, index, value) => array[index] = value")
external fun setUint8ArrayValue(array: JsAny, index: Int, value: Int)

@OptIn(ExperimentalWasmJsInterop::class)
@JsFun("(path, bytes, onSuccess, onError) => { const ref = firebase.storage().ref(path); ref.put(bytes).then(() => ref.getDownloadURL()).then((url) => onSuccess(url)).catch((e) => onError(e.message)); }")
external fun firebaseUploadBytes(
	path: String,
	bytes: JsAny,
	onSuccess: (String) -> Unit,
	onError: (String) -> Unit,
)

@Inject
class WasmStorageRepository : StorageRepository {
	@OptIn(ExperimentalWasmJsInterop::class)
	override suspend fun uploadProfilePicture(userId: String, bytes: ByteArray): Result<String> =
		suspendCancellableCoroutine { continuation ->
			val uint8Array = createUint8Array(bytes.size)
			for (i in bytes.indices) {
				setUint8ArrayValue(uint8Array, i, bytes[i].toInt())
			}

			firebaseUploadBytes("profile_pictures/$userId.jpg", uint8Array, { url ->
				continuation.resume(Result.success(url))
			}, { error ->
				continuation.resume(Result.failure(Exception(error)))
			})
		}
}
