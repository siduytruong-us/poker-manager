package com.duyts.pokerhost.data.repository

import dev.gitlive.firebase.storage.Data

expect fun ByteArray.toData(): Data
