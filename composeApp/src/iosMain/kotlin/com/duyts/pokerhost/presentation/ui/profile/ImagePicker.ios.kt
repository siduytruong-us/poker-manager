package com.duyts.pokerhost.presentation.ui.profile

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.Foundation.NSData
import platform.UIKit.UIApplication
import platform.UIKit.UIImageJPEGRepresentation
import platform.UIKit.UIImagePickerController
import platform.UIKit.UIImagePickerControllerDelegateProtocol
import platform.UIKit.UIImagePickerControllerEditedImage
import platform.UIKit.UIImagePickerControllerOriginalImage
import platform.UIKit.UIImagePickerControllerSourceType
import platform.UIKit.UINavigationControllerDelegateProtocol
import platform.darwin.NSObject

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun rememberImagePickerLauncher(
    onImagePicked: (ByteArray) -> Unit,
): ImagePickerLauncher {
    return remember {
        IosImagePickerLauncher(onImagePicked)
    }
}

@OptIn(ExperimentalForeignApi::class)
private class IosImagePickerLauncher(
    private val onImagePicked: (ByteArray) -> Unit,
) : ImagePickerLauncher {

    private var delegate: PickerDelegate? = null

    override fun launch() {
        val rootViewController = UIApplication.sharedApplication.keyWindow?.rootViewController
            ?: return

        val picker = UIImagePickerController()
        picker.sourceType =
            UIImagePickerControllerSourceType.UIImagePickerControllerSourceTypePhotoLibrary
        picker.allowsEditing = false

        val pickerDelegate = PickerDelegate(onImagePicked) {
            rootViewController.dismissViewControllerAnimated(true, null)
            delegate = null
        }
        delegate = pickerDelegate
        picker.delegate = pickerDelegate

        rootViewController.presentViewController(picker, animated = true, completion = null)
    }
}

@OptIn(ExperimentalForeignApi::class)
private class PickerDelegate(
    private val onImagePicked: (ByteArray) -> Unit,
    private val onDismiss: () -> Unit,
) : NSObject(), UIImagePickerControllerDelegateProtocol, UINavigationControllerDelegateProtocol {

    override fun imagePickerController(
        picker: UIImagePickerController,
        didFinishPickingMediaWithInfo: Map<Any?, *>,
    ) {
        val image = (didFinishPickingMediaWithInfo[UIImagePickerControllerEditedImage]
            ?: didFinishPickingMediaWithInfo[UIImagePickerControllerOriginalImage])
                as? platform.UIKit.UIImage

        image?.let {
            val data: NSData? = UIImageJPEGRepresentation(it, 0.8)
            if (data != null) {
                val bytes = ByteArray(data.length.toInt())
                bytes.usePinned { pinned ->
                    platform.posix.memcpy(pinned.addressOf(0), data.bytes, data.length)
                }
                onImagePicked(bytes)
            }
        }
        onDismiss()
    }

    override fun imagePickerControllerDidCancel(picker: UIImagePickerController) {
        onDismiss()
    }
}
