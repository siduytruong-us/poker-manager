package com.duyts.android.myapplication.data.repository

import dev.gitlive.firebase.storage.Data

expect fun ByteArray.toData(): Data
