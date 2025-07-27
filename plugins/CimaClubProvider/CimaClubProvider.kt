package com.zoomq8.cloudstream.plugins.cimaclub

import com.lagradost.cloudstream3.*
import com.lagradost.cloudstream3.utils.*
import org.jsoup.Jsoup

class CimaClubProvider : MainAPI() {
    override var name = "CimaClub"
    override var mainUrl = "https://colcima.online"
    override val hasMainPage = true
    override val supportedTypes = setOf(TvType.Movie)

    override suspend fun getMainPage(page: Int, request: MainPageRequest): HomePageResponse {
        val url = "$mainUrl/movies?page=$page"
        val doc = app.get(url).document
        val movies = doc.select("article").mapNotNull {
            val a = it.selectFirst("a") ?: return@mapNotNull null
            val href = a.attr("href")
            val title = a.attr("title") ?: "Unknown"
            val poster = a.selectFirst("img")?.attr("src")
            newMovieSearchResponse(title, href, TvType.Movie) {
                this.posterUrl = poster
            }
        }
        return HomePageResponse(listOf(HomePageList("أفلام كول سيما", movies)))
    }

    override suspend fun search(query: String): List<SearchResponse> {
        val url = "$mainUrl/search?query=$query"
        val doc = app.get(url).document
        return doc.select("article").mapNotNull {
            val a = it.selectFirst("a") ?: return@mapNotNull null
            val href = a.attr("href")
            val title = a.attr("title")
            val poster = a.selectFirst("img")?.attr("src")
            newMovieSearchResponse(title, href, TvType.Movie) {
                this.posterUrl = poster
            }
        }
    }

    override suspend fun load(url: String): LoadResponse {
        val doc = app.get(url).document
        val title = doc.selectFirst("h1")?.text() ?: "Cima Movie"
        val poster = doc.selectFirst("meta[property=og:image]")?.attr("content")
        val description = doc.selectFirst("meta[property=og:description]")?.attr("content")

        return MovieLoadResponse(
            title = title,
            url = url,
            apiName = this.name,
            type = TvType.Movie,
            dataUrl = url,
            posterUrl = poster,
            year = null,
            plot = description
        )
    }

    override suspend fun loadLinks(
        data: String,
        isCasting: Boolean,
        subtitleCallback: (SubtitleFile) -> Unit,
        callback: (ExtractorLink) -> Unit
