package com.zoomq8.cloudstream.plugins.cimaclub

import com.lagradost.cloudstream3.*
import com.lagradost.cloudstream3.utils.*

class CimaClubProvider : MainAPI() {
    override var mainUrl = "https://colcima.online"
    override var name = "CimaClub"
    override val hasMainPage = true
    override val supportedTypes = setOf(TvType.Movie)

    override suspend fun getMainPage(page: Int, request: MainPageRequest): HomePageResponse {
        // يتم جلب الصفحة الرئيسية
        return HomePageResponse(emptyList())
    }
}
