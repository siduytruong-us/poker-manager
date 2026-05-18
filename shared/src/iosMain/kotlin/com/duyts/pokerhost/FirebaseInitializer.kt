package com.duyts.pokerhost

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.initialize

class FirebaseInitializer {
	companion object {
		fun doInit() {
			Firebase.initialize()
		}
	}
}
