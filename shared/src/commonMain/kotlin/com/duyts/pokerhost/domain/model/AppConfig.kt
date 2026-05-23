package com.duyts.pokerhost.domain.model

data class AppConfig(
    val androidBannerAdId: String,
    val iosBannerAdId: String,
    val minVersionCode: Int = 0,
    val isMaintenanceMode: Boolean = false,
)
