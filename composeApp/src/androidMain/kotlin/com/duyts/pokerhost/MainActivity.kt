package com.duyts.pokerhost

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.duyts.pokerhost.di.TodoContext
import com.google.android.gms.ads.MobileAds

class MainActivity : ComponentActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		enableEdgeToEdge()
		TodoContext.context = applicationContext
		MobileAds.initialize(this) {}
		setContent {
			App()
		}
	}
}