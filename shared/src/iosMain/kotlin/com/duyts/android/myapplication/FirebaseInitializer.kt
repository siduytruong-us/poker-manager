package com.duyts.android.myapplication

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.initialize

class FirebaseInitializer {
    companion object {
        fun doInit() {
            Firebase.initialize()
        }
    }
}
