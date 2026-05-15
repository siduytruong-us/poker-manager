package com.duyts.android.myapplication.presentation.ui.profile

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.browser.document
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.events.Event
import org.w3c.files.FileReader
import org.w3c.files.get
import kotlin.js.ExperimentalWasmJsInterop

@OptIn(ExperimentalWasmJsInterop::class)
@JsFun("(file, callback) => { const reader = new FileReader(); reader.onload = () => { const arrayBuffer = reader.result; const uint8Array = new Uint8Array(arrayBuffer); callback(uint8Array); }; reader.readAsArrayBuffer(file); }")
external fun readFileAsUint8Array(file: org.w3c.files.File, callback: (JsAny) -> Unit)

@OptIn(ExperimentalWasmJsInterop::class)
@JsFun("(uint8Array, index) => uint8Array[index]")
external fun getUint8ArrayValue(uint8Array: JsAny, index: Int): Int

@OptIn(ExperimentalWasmJsInterop::class)
@JsFun("(uint8Array) => uint8Array.length")
external fun getUint8ArrayLength(uint8Array: JsAny): Int

@Composable
actual fun rememberImagePickerLauncher(
    onImagePicked: (ByteArray) -> Unit
): ImagePickerLauncher {
    return remember {
        object : ImagePickerLauncher {
            @OptIn(ExperimentalWasmJsInterop::class)
            override fun launch() {
                val input = document.createElement("input") as HTMLInputElement
                input.type = "file"
                input.accept = "image/*"
                
                input.onchange = { _: Event ->
                    val file = input.files?.get(0)
                    if (file != null) {
                        readFileAsUint8Array(file) { uint8Array ->
                            val length = getUint8ArrayLength(uint8Array)
                            val bytes = ByteArray(length) { i -> 
                                getUint8ArrayValue(uint8Array, i).toByte()
                            }
                            onImagePicked(bytes)
                        }
                    }
                }
                
                input.click()
            }
        }
    }
}
