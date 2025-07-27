package com.zoomq8.cloudstream.plugins.akwam

import com.lagradost.cloudstream3.*
import com.lagradost.cloudstream3.utils.*
import org.jsoup.Jsoup

class AkwamProvider : MainAPI() {
    override var name = "Akwam"
    override var mainUrl = "https://akw.onl"
    override val hasMainPage = true
    override val supportedTypes = setOf(TvType.Movie)

    override suspend fun getMainPage(page: Int, request: MainPageRequest): HomePageResponse {
        val url = "$mainUrl/movies?page=$page"
        val doc = app.get(url).document
        val movies = doc.select("div.GridItem").mapNotNull {
            val title = it.selectFirst("h3")?.text() ?: return@mapNotNull null
            val href = it.selectFirst("a")?.attr("href") ?: return@mapNotNull null
            val poster = it.selectFirst("img")?.attr("src") ?: ""
            newMovieSearchResponse(title, "$mainUrl$href", TvType.Movie) {
                this.posterUrl = poster
            }
        }
        return HomePageResponse(listOf(HomePageList("أحدث الأفلام", movies)))
    }

    override suspend fun search(query: String): List<SearchResponse> {
        val url = "$mainUrl/search?q=$query"
        val doc = app.get(url).document
        return doc.select("div.GridItem").mapNotNull {
            val title = it.selectFirst("h3")?.text() ?: return@mapNotNull null
            val href = it.selectFirst("a")?.attr("href") ?: return@mapNotNull null
            val poster = it.selectFirst("img")?.attr("src") ?: ""
            newMovieSearchResponse(title, "$mainUrl$href", TvType.Movie) {
                this.posterUrl = poster
            }
        }
    }

    override suspend fun load(url: String): LoadResponse {
        val doc = app.get(url).document
        val title = doc.selectFirst("h1")?.text() ?: "Akwam Movie"
        val poster = doc.selectFirst("img")?.attr("src")
        val description = doc.selectFirst("meta[name=description]")?.attr("content")

        val data = MovieLoadResponse(
            title,
            url,
            this.name,
            TvType.Movie,
            url,
            poster,
            year = null,
            plot = description,
        )
        return data
    }

    override suspend fun loadLinks(
        data: String,
        isCasting: Boolean,
        subtitleCallback: (SubtitleFile) -> Unit,
        callback: (ExtractorLink) -> Unit
    ): Boolean {
        val doc = app.get(data).document
        val iframeUrl = doc.selectFirst("iframe")?.attr("src") ?: return false
        val frameContent = app.get(iframeUrl).text
        val videoUrl = Regex("file:\\s*\"(.*?)\"").find(frameContent)?.groupValues?.get(1) ?: return false
        callback(
            ExtractorLink(
                name = this.name,
                source = "Akwam",
                url = videoUrl,
                referer = data,
                quality = Qualities.Unknown.value,
                isM3u8 = videoUrl.endsWith(".m3u8")
            )
        )
        return true
