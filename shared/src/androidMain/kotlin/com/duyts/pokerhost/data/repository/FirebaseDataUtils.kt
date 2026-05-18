package com.duyts.pokerhost.data.repository

import dev.gitlive.firebase.storage.Data

actual fun ByteArray.toData(): Data = Data(this)
