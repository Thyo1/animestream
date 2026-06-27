package com.thyo.animestream.utils

import com.fasterxml.jackson.annotation.JsonIgnore
import com.thyo.animestream.IDownloadableMinimum
import com.thyo.animestream.SubtitleFile
import com.thyo.animestream.USER_AGENT
import com.thyo.animestream.app
import com.thyo.animestream.extractors.AStreamHub
import com.thyo.animestream.extractors.Acefile
import com.thyo.animestream.extractors.Ahvsh
import com.thyo.animestream.extractors.Aico
import com.thyo.animestream.extractors.AnimesagaStream
import com.thyo.animestream.extractors.Anplay
import com.thyo.animestream.extractors.AsianLoad
import com.thyo.animestream.extractors.Asnwish
import com.thyo.animestream.extractors.Awish
import com.thyo.animestream.extractors.Beastx
import com.thyo.animestream.extractors.Bestx
import com.thyo.animestream.extractors.BgwpCC
import com.thyo.animestream.extractors.BigwarpArt
import com.thyo.animestream.extractors.BigwarpIO
import com.thyo.animestream.extractors.Blogger
import com.thyo.animestream.extractors.Boltx
import com.thyo.animestream.extractors.Boosterx
import com.thyo.animestream.extractors.BullStream
import com.thyo.animestream.extractors.ByteShare
import com.thyo.animestream.extractors.Cda
import com.thyo.animestream.extractors.Cdnplayer
import com.thyo.animestream.extractors.CdnwishCom
import com.thyo.animestream.extractors.Chillx
import com.thyo.animestream.extractors.CineGrabber
import com.thyo.animestream.extractors.Cinestart
import com.thyo.animestream.extractors.CloudMailRu
import com.thyo.animestream.extractors.ContentX
import com.thyo.animestream.extractors.CsstOnline
import com.thyo.animestream.extractors.D0000d
import com.thyo.animestream.extractors.D000dCom
import com.thyo.animestream.extractors.DBfilm
import com.thyo.animestream.extractors.Dailymotion
import com.thyo.animestream.extractors.DatabaseGdrive
import com.thyo.animestream.extractors.DatabaseGdrive2
import com.thyo.animestream.extractors.DesuArcg
import com.thyo.animestream.extractors.DesuDrive
import com.thyo.animestream.extractors.DesuOdchan
import com.thyo.animestream.extractors.DesuOdvip
import com.thyo.animestream.extractors.Dhcplay
import com.thyo.animestream.extractors.Dhtpre
import com.thyo.animestream.extractors.Dokicloud
import com.thyo.animestream.extractors.DoodCxExtractor
import com.thyo.animestream.extractors.DoodLaExtractor
import com.thyo.animestream.extractors.DoodPmExtractor
import com.thyo.animestream.extractors.DoodShExtractor
import com.thyo.animestream.extractors.DoodSoExtractor
import com.thyo.animestream.extractors.DoodToExtractor
import com.thyo.animestream.extractors.DoodWatchExtractor
import com.thyo.animestream.extractors.DoodWfExtractor
import com.thyo.animestream.extractors.DoodWsExtractor
import com.thyo.animestream.extractors.DoodYtExtractor
import com.thyo.animestream.extractors.Doodporn
import com.thyo.animestream.extractors.DoodstreamCom
import com.thyo.animestream.extractors.Dooood
import com.thyo.animestream.extractors.Ds2play
import com.thyo.animestream.extractors.Ds2video
import com.thyo.animestream.extractors.DsstOnline
import com.thyo.animestream.extractors.Dwish
import com.thyo.animestream.extractors.EPlayExtractor
import com.thyo.animestream.extractors.Embedgram
import com.thyo.animestream.extractors.EmturbovidExtractor
import com.thyo.animestream.extractors.Evoload
import com.thyo.animestream.extractors.Evoload1
import com.thyo.animestream.extractors.Ewish
import com.thyo.animestream.extractors.FEmbed
import com.thyo.animestream.extractors.FEnet
import com.thyo.animestream.extractors.Fastream
import com.thyo.animestream.extractors.FeHD
import com.thyo.animestream.extractors.Fembed9hd
import com.thyo.animestream.extractors.FileMoonIn
import com.thyo.animestream.extractors.Filegram
import com.thyo.animestream.extractors.Filesim
import com.thyo.animestream.extractors.FlaswishCom
import com.thyo.animestream.extractors.FourCX
import com.thyo.animestream.extractors.FourPichive
import com.thyo.animestream.extractors.FourPlayRu
import com.thyo.animestream.extractors.Fplayer
import com.thyo.animestream.extractors.FsstOnline
import com.thyo.animestream.extractors.GDMirrorbot
import com.thyo.animestream.extractors.GMPlayer
import com.thyo.animestream.extractors.GamoVideo
import com.thyo.animestream.extractors.Gdriveplayer
import com.thyo.animestream.extractors.Gdriveplayerapi
import com.thyo.animestream.extractors.Gdriveplayerapp
import com.thyo.animestream.extractors.Gdriveplayerbiz
import com.thyo.animestream.extractors.Gdriveplayerco
import com.thyo.animestream.extractors.Gdriveplayerfun
import com.thyo.animestream.extractors.Gdriveplayerio
import com.thyo.animestream.extractors.Gdriveplayerme
import com.thyo.animestream.extractors.Gdriveplayerorg
import com.thyo.animestream.extractors.Gdriveplayerus
import com.thyo.animestream.extractors.Geodailymotion
import com.thyo.animestream.extractors.Gofile
import com.thyo.animestream.extractors.GoodstreamExtractor
import com.thyo.animestream.extractors.GuardareStream
import com.thyo.animestream.extractors.Guccihide
import com.thyo.animestream.extractors.HDMomPlayer
import com.thyo.animestream.extractors.HDPlayerSystem
import com.thyo.animestream.extractors.HDStreamAble
import com.thyo.animestream.extractors.Hotlinger
import com.thyo.animestream.extractors.Hxfile
import com.thyo.animestream.extractors.InternetArchive
import com.thyo.animestream.extractors.JWPlayer
import com.thyo.animestream.extractors.Jawcloud
import com.thyo.animestream.extractors.Jeniusplay
import com.thyo.animestream.extractors.Jodwish
import com.thyo.animestream.extractors.Keephealth
import com.thyo.animestream.extractors.Kinogeru
import com.thyo.animestream.extractors.KotakAnimeid
import com.thyo.animestream.extractors.Kotakajair
import com.thyo.animestream.extractors.Krakenfiles
import com.thyo.animestream.extractors.Kswplayer
import com.thyo.animestream.extractors.LayarKaca
import com.thyo.animestream.extractors.Linkbox
import com.thyo.animestream.extractors.LuluStream
import com.thyo.animestream.extractors.Lulustream1
import com.thyo.animestream.extractors.Lulustream2
import com.thyo.animestream.extractors.Luxubu
import com.thyo.animestream.extractors.Lvturbo
import com.thyo.animestream.extractors.MailRu
import com.thyo.animestream.extractors.Maxstream
import com.thyo.animestream.extractors.Mcloud
import com.thyo.animestream.extractors.Mediafire
import com.thyo.animestream.extractors.MegaF
import com.thyo.animestream.extractors.Megacloud
import com.thyo.animestream.extractors.Meownime
import com.thyo.animestream.extractors.MetaGnathTuggers
import com.thyo.animestream.extractors.Minoplres
import com.thyo.animestream.extractors.MixDrop
import com.thyo.animestream.extractors.MixDropAg
import com.thyo.animestream.extractors.MixDropBz
import com.thyo.animestream.extractors.MixDropCh
import com.thyo.animestream.extractors.MixDropTo
import com.thyo.animestream.extractors.Movhide
import com.thyo.animestream.extractors.Moviehab
import com.thyo.animestream.extractors.MoviehabNet
import com.thyo.animestream.extractors.Moviesapi
import com.thyo.animestream.extractors.Moviesm4u
import com.thyo.animestream.extractors.Mp4Upload
import com.thyo.animestream.extractors.Multimovies
import com.thyo.animestream.extractors.Mvidoo
import com.thyo.animestream.extractors.Mwish
import com.thyo.animestream.extractors.MwvnVizcloudInfo
import com.thyo.animestream.extractors.MyCloud
import com.thyo.animestream.extractors.NathanFromSubject
import com.thyo.animestream.extractors.Nekostream
import com.thyo.animestream.extractors.Nekowish
import com.thyo.animestream.extractors.Neonime7n
import com.thyo.animestream.extractors.Neonime8n
import com.thyo.animestream.extractors.Obeywish
import com.thyo.animestream.extractors.Odnoklassniki
import com.thyo.animestream.extractors.OkRuHTTP
import com.thyo.animestream.extractors.OkRuSSL
import com.thyo.animestream.extractors.Okrulink
import com.thyo.animestream.extractors.PeaceMakerst
import com.thyo.animestream.extractors.Peytonepre
import com.thyo.animestream.extractors.Pichive
import com.thyo.animestream.extractors.PixelDrain
import com.thyo.animestream.extractors.PlayLtXyz
import com.thyo.animestream.extractors.PlayRu
import com.thyo.animestream.extractors.PlayerVoxzer
import com.thyo.animestream.extractors.Playerwish
import com.thyo.animestream.extractors.Playerx
import com.thyo.animestream.extractors.Rabbitstream
import com.thyo.animestream.extractors.RapidVid
import com.thyo.animestream.extractors.Rasacintaku
import com.thyo.animestream.extractors.SBfull
import com.thyo.animestream.extractors.Sbasian
import com.thyo.animestream.extractors.Sbface
import com.thyo.animestream.extractors.Sbflix
import com.thyo.animestream.extractors.Sblona
import com.thyo.animestream.extractors.Sblongvu
import com.thyo.animestream.extractors.Sbnet
import com.thyo.animestream.extractors.Sbrapid
import com.thyo.animestream.extractors.Sbsonic
import com.thyo.animestream.extractors.Sbspeed
import com.thyo.animestream.extractors.Sbthe
import com.thyo.animestream.extractors.SecvideoOnline
import com.thyo.animestream.extractors.Sendvid
import com.thyo.animestream.extractors.Server1uns
import com.thyo.animestream.extractors.SfastwishCom
import com.thyo.animestream.extractors.ShaveTape
import com.thyo.animestream.extractors.SibNet
import com.thyo.animestream.extractors.Simpulumlamerop
import com.thyo.animestream.extractors.Smoothpre
import com.thyo.animestream.extractors.Sobreatsesuyp
import com.thyo.animestream.extractors.Solidfiles
import com.thyo.animestream.extractors.Ssbstream
import com.thyo.animestream.extractors.StreamM4u
import com.thyo.animestream.extractors.StreamSB
import com.thyo.animestream.extractors.StreamSB1
import com.thyo.animestream.extractors.StreamSB10
import com.thyo.animestream.extractors.StreamSB11
import com.thyo.animestream.extractors.StreamSB2
import com.thyo.animestream.extractors.StreamSB3
import com.thyo.animestream.extractors.StreamSB4
import com.thyo.animestream.extractors.StreamSB5
import com.thyo.animestream.extractors.StreamSB6
import com.thyo.animestream.extractors.StreamSB7
import com.thyo.animestream.extractors.StreamSB8
import com.thyo.animestream.extractors.StreamSB9
import com.thyo.animestream.extractors.StreamSilk
import com.thyo.animestream.extractors.StreamTape
import com.thyo.animestream.extractors.StreamTapeNet
import com.thyo.animestream.extractors.StreamTapeXyz
import com.thyo.animestream.extractors.StreamWishExtractor
import com.thyo.animestream.extractors.StreamhideCom
import com.thyo.animestream.extractors.StreamhideTo
import com.thyo.animestream.extractors.Streamhub2
import com.thyo.animestream.extractors.Streamlare
import com.thyo.animestream.extractors.StreamoUpload
import com.thyo.animestream.extractors.Streamplay
import com.thyo.animestream.extractors.Streamsss
import com.thyo.animestream.extractors.Streamwish2
import com.thyo.animestream.extractors.Strwish
import com.thyo.animestream.extractors.Strwish2
import com.thyo.animestream.extractors.Supervideo
import com.thyo.animestream.extractors.Swdyu
import com.thyo.animestream.extractors.Swhoi
import com.thyo.animestream.extractors.TRsTX
import com.thyo.animestream.extractors.Tantifilm
import com.thyo.animestream.extractors.TauVideo
import com.thyo.animestream.extractors.Tomatomatela
import com.thyo.animestream.extractors.TomatomatelalClub
import com.thyo.animestream.extractors.Tubeless
import com.thyo.animestream.extractors.Upstream
import com.thyo.animestream.extractors.UpstreamExtractor
import com.thyo.animestream.extractors.Uqload
import com.thyo.animestream.extractors.Uqload1
import com.thyo.animestream.extractors.Uqload2
import com.thyo.animestream.extractors.UqloadsXyz
import com.thyo.animestream.extractors.Urochsunloath
import com.thyo.animestream.extractors.Userload
import com.thyo.animestream.extractors.Userscloud
import com.thyo.animestream.extractors.Uservideo
import com.thyo.animestream.extractors.Vanfem
import com.thyo.animestream.extractors.Vectorx
import com.thyo.animestream.extractors.Vicloud
import com.thyo.animestream.extractors.VidHidePro
import com.thyo.animestream.extractors.VidHidePro1
import com.thyo.animestream.extractors.VidHidePro2
import com.thyo.animestream.extractors.VidHidePro3
import com.thyo.animestream.extractors.VidHidePro4
import com.thyo.animestream.extractors.VidHidePro5
import com.thyo.animestream.extractors.VidHidePro6
import com.thyo.animestream.extractors.VidMoxy
import com.thyo.animestream.extractors.VidSrcExtractor
import com.thyo.animestream.extractors.VidSrcExtractor2
import com.thyo.animestream.extractors.VidSrcTo
import com.thyo.animestream.extractors.VidStack
import com.thyo.animestream.extractors.VideoSeyred
import com.thyo.animestream.extractors.VideoVard
import com.thyo.animestream.extractors.VideovardSX
import com.thyo.animestream.extractors.Vidgomunime
import com.thyo.animestream.extractors.Vidgomunimesb
import com.thyo.animestream.extractors.Vidguardto
import com.thyo.animestream.extractors.Vidguardto1
import com.thyo.animestream.extractors.Vidguardto2
import com.thyo.animestream.extractors.Vidguardto3
import com.thyo.animestream.extractors.VidhideExtractor
import com.thyo.animestream.extractors.Vidmoly
import com.thyo.animestream.extractors.Vidmolyme
import com.thyo.animestream.extractors.Vido
import com.thyo.animestream.extractors.Vidplay
import com.thyo.animestream.extractors.VidplayOnline
import com.thyo.animestream.extractors.Vidstreamz
import com.thyo.animestream.extractors.Vidxstream
import com.thyo.animestream.extractors.VinovoSi
import com.thyo.animestream.extractors.VinovoTo
import com.thyo.animestream.extractors.Vizcloud
import com.thyo.animestream.extractors.Vizcloud2
import com.thyo.animestream.extractors.VizcloudCloud
import com.thyo.animestream.extractors.VizcloudDigital
import com.thyo.animestream.extractors.VizcloudInfo
import com.thyo.animestream.extractors.VizcloudLive
import com.thyo.animestream.extractors.VizcloudOnline
import com.thyo.animestream.extractors.VizcloudSite
import com.thyo.animestream.extractors.VizcloudXyz
import com.thyo.animestream.extractors.Voe
import com.thyo.animestream.extractors.Voe1
import com.thyo.animestream.extractors.Vtbe
import com.thyo.animestream.extractors.Watchx
import com.thyo.animestream.extractors.WcoStream
import com.thyo.animestream.extractors.Wibufile
import com.thyo.animestream.extractors.WishembedPro
import com.thyo.animestream.extractors.Wishfast
import com.thyo.animestream.extractors.Wishonly
import com.thyo.animestream.extractors.XStreamCdn
import com.thyo.animestream.extractors.Yipsu
import com.thyo.animestream.extractors.YourUpload
import com.thyo.animestream.extractors.YoutubeExtractor
import com.thyo.animestream.extractors.YoutubeMobileExtractor
import com.thyo.animestream.extractors.YoutubeNoCookieExtractor
import com.thyo.animestream.extractors.YoutubeShortLinkExtractor
import com.thyo.animestream.extractors.Yufiles
import com.thyo.animestream.extractors.Zorofile
import com.thyo.animestream.extractors.Zplayer
import com.thyo.animestream.extractors.ZplayerV2
import com.thyo.animestream.extractors.Ztreamhub
import com.thyo.animestream.extractors.FileMoon
import com.thyo.animestream.extractors.FileMoonSx
import com.thyo.animestream.extractors.FilemoonV2
import com.thyo.animestream.mvvm.logError
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import me.xdrop.fuzzywuzzy.FuzzySearch
import org.jsoup.Jsoup
import java.net.URI
import java.util.UUID
import kotlin.coroutines.cancellation.CancellationException

/**
 * For use in the ConcatenatingMediaSource.
 * If features are missing (headers), please report and we can add it.
 * @param durationUs use Long.toUs() for easier input
 * */
data class PlayListItem(
    val url: String,
    val durationUs: Long,
)

/**
 * Converts Seconds to MicroSeconds, multiplication by 1_000_000
 * */
fun Long.toUs(): Long {
    return this * 1_000_000
}

/**
 * If your site has an unorthodox m3u8-like system where there are multiple smaller videos concatenated
 * use this.
 * */
data class ExtractorLinkPlayList(
    override val source: String,
    override val name: String,
    val playlist: List<PlayListItem>,
    override var referer: String,
    override var quality: Int,
    override var headers: Map<String, String> = mapOf(),
    /** Used for getExtractorVerifierJob() */
    override var extractorData: String? = null,
    override var type: ExtractorLinkType,
) : ExtractorLink(
    source = source,
    name = name,
    url = "",
    referer = referer,
    quality = quality,
    headers = headers,
    extractorData = extractorData,
    type = type
) {
    constructor(
        source: String,
        name: String,
        playlist: List<PlayListItem>,
        referer: String,
        quality: Int,
        isM3u8: Boolean = false,
        headers: Map<String, String> = mapOf(),
        extractorData: String? = null,
    ) : this(
        source = source,
        name = name,
        playlist = playlist,
        referer = referer,
        quality = quality,
        type = if (isM3u8) ExtractorLinkType.M3U8 else ExtractorLinkType.VIDEO,
        headers = headers,
        extractorData = extractorData,
    )
}

/** Metadata about the file type used for downloads and exoplayer hint,
 * if you respond with the wrong one the file will fail to download or be played */
enum class ExtractorLinkType {
    /** Single stream of bytes no matter the actual file type */
    VIDEO,

    /** Split into several .ts files, has support for encrypted m3u8s */
    M3U8,

    /** Like m3u8 but uses xml, currently no download support */
    DASH,

    /** No support at the moment */
    TORRENT,

    /** No support at the moment */
    MAGNET;

    // See https://www.iana.org/assignments/media-types/media-types.xhtml
    fun getMimeType(): String {
        return when (this) {
            VIDEO -> "video/mp4"
            M3U8 -> "application/x-mpegURL"
            DASH -> "application/dash+xml"
            TORRENT -> "application/x-bittorrent"
            MAGNET -> "application/x-bittorrent"
        }
    }
}

private fun inferTypeFromUrl(url: String): ExtractorLinkType {
    val path = try {
        URI(url).path
    } catch (_: Throwable) {
        // don't log magnet links as errors
        null
    }
    return when {
        path?.endsWith(".m3u8") == true -> ExtractorLinkType.M3U8
        path?.endsWith(".mpd") == true -> ExtractorLinkType.DASH
        path?.endsWith(".torrent") == true -> ExtractorLinkType.TORRENT
        url.startsWith("magnet:") -> ExtractorLinkType.MAGNET
        else -> ExtractorLinkType.VIDEO
    }
}

val INFER_TYPE: ExtractorLinkType? = null

/**
 * UUID for the ClearKey DRM scheme.
 *
 *
 * ClearKey is supported on Android devices running Android 5.0 (API Level 21) and up.
 */
val CLEARKEY_UUID = UUID(-0x1d8e62a7567a4c37L, 0x781AB030AF78D30EL)

/**
 * UUID for the Widevine DRM scheme.
 *
 *
 * Widevine is supported on Android devices running Android 4.3 (API Level 18) and up.
 */
val WIDEVINE_UUID = UUID(-0x121074568629b532L, -0x5c37d8232ae2de13L)

/**
 * UUID for the PlayReady DRM scheme.
 *
 *
 * PlayReady is supported on all AndroidTV devices. Note that most other Android devices do not
 * provide PlayReady support.
 */
val PLAYREADY_UUID = UUID(-0x65fb0f8667bfbd7aL, -0x546d19a41f77a06bL)

suspend fun newExtractorLink(
    source: String,
    name: String,
    url: String,
    type: ExtractorLinkType? = null,
    initializer: suspend ExtractorLink.() -> Unit = { }
): ExtractorLink {

    @Suppress("DEPRECATION_ERROR")
    val builder =
        ExtractorLink(
            source = source,
            name = name,
            url = url,
            type = type ?: INFER_TYPE
        )

    builder.initializer()
    return builder
}

suspend fun newDrmExtractorLink(
    source: String,
    name: String,
    url: String,
    type: ExtractorLinkType? = null,
    uuid: UUID,
    initializer: suspend DrmExtractorLink.() -> Unit = { }
): DrmExtractorLink {

    @Suppress("DEPRECATION_ERROR")
    val builder =
        DrmExtractorLink(
            source = source,
            name = name,
            url = url,
            uuid = uuid,
            type = type ?: INFER_TYPE
        )

    builder.initializer()
    return builder
}

/** Class holds extracted DRM media info to be passed to the player.
 * @property source Name of the media source, appears on player layout.
 * @property name Title of the media, appears on player layout.
 * @property url Url string of media file
 * @property referer Referer that will be used by network request.
 * @property quality Quality of the media file
 * @property headers Headers <String, String> map that will be used by network request.
 * @property extractorData Used for getExtractorVerifierJob()
 * @property type the type of the media, use [INFER_TYPE] if you want to auto infer the type from the url
 * @property kid  Base64 value of The KID element (Key Id) contains the identifier of the key associated with a license.
 * @property key Base64 value of Key to be used to decrypt the media file.
 * @property uuid Drm UUID [WIDEVINE_UUID], [PLAYREADY_UUID], [CLEARKEY_UUID] (by default) .. etc
 * @property kty Key type "oct" (octet sequence) by default
 * @property keyRequestParameters Parameters that will used to request the key.
 * */
@Suppress("DEPRECATION_ERROR")
open class DrmExtractorLink private constructor(
    override val source: String,
    override val name: String,
    override val url: String,
    override var referer: String,
    override var quality: Int,
    override var headers: Map<String, String> = mapOf(),
    /** Used for getExtractorVerifierJob() */
    override var extractorData: String? = null,
    override var type: ExtractorLinkType,
    open var kid: String? = null,
    open var key: String? = null,
    open var uuid: UUID,
    open var kty: String? = null,
    open var keyRequestParameters: HashMap<String, String>,
    open var licenseUrl: String? = null,
) : ExtractorLink(
    source, name, url, referer, quality, type, headers, extractorData
) {
    @Deprecated("Use newDrmExtractorLink", level = DeprecationLevel.ERROR)
    constructor(
        source: String,
        name: String,
        url: String,
        referer: String? = null,
        quality: Int? = null,
        /** the type of the media, use INFER_TYPE if you want to auto infer the type from the url */
        type: ExtractorLinkType? = INFER_TYPE,
        headers: Map<String, String> = mapOf(),
        /** Used for getExtractorVerifierJob() */
        extractorData: String? = null,
        kid: String? = null,
        key: String? = null,
        uuid: UUID = CLEARKEY_UUID,
        kty: String? = "oct",
        keyRequestParameters: HashMap<String, String> = hashMapOf(),
        licenseUrl: String? = null,
    ) : this(
        source = source,
        name = name,
        url = url,
        referer = referer ?: "",
        quality = quality ?: Qualities.Unknown.value,
        headers = headers,
        extractorData = extractorData,
        type = type ?: inferTypeFromUrl(url),
        kid = kid,
        key = key,
        uuid = uuid,
        keyRequestParameters = keyRequestParameters,
        kty = kty,
        licenseUrl = licenseUrl,
    )

    @Deprecated("Use newDrmExtractorLink", level = DeprecationLevel.ERROR)
    constructor(
        source: String,
        name: String,
        url: String,
        referer: String,
        quality: Int,
        /** the type of the media, use INFER_TYPE if you want to auto infer the type from the url */
        type: ExtractorLinkType?,
        headers: Map<String, String> = mapOf(),
        /** Used for getExtractorVerifierJob() */
        extractorData: String? = null,
        kid: String? = null,
        key: String? = null,
        uuid: UUID = CLEARKEY_UUID,
        kty: String? = "oct",
        keyRequestParameters: HashMap<String, String> = hashMapOf(),
        licenseUrl: String? = null,
    ) : this(
        source = source,
        name = name,
        url = url,
        referer = referer,
        quality = quality,
        headers = headers,
        extractorData = extractorData,
        type = type ?: inferTypeFromUrl(url),
        kid = kid,
        key = key,
        uuid = uuid,
        keyRequestParameters = keyRequestParameters,
        kty = kty,
        licenseUrl = licenseUrl,
    )
}

/** Class holds extracted media info to be passed to the player.
 * @property source Name of the media source, appears on player layout.
 * @property name Title of the media, appears on player layout.
 * @property url Url string of media file
 * @property referer Referer that will be used by network request.
 * @property quality Quality of the media file
 * @property headers Headers <String, String> map that will be used by network request.
 * @property extractorData Used for getExtractorVerifierJob()
 * @property type Extracted link type (Video, M3u8, Dash, Torrent or Magnet)
 * */
open class ExtractorLink constructor(
    open val source: String,
    open val name: String,
    override val url: String,
    override var referer: String,
    open var quality: Int,
    override var headers: Map<String, String> = mapOf(),
    /** Used for getExtractorVerifierJob() */
    open var extractorData: String? = null,
    open var type: ExtractorLinkType,
) : IDownloadableMinimum {
    val isM3u8: Boolean get() = type == ExtractorLinkType.M3U8
    val isDash: Boolean get() = type == ExtractorLinkType.DASH

    // Cached video size
    private var videoSize: Long? = null

    /**
     * Get video size in bytes with one head request. Only available for ExtractorLinkType.Video
     * @param timeoutSeconds timeout of the head request.
     */
    suspend fun getVideoSize(timeoutSeconds: Long = 3L): Long? {
        // Content-Length is not applicable to other types of formats
        if (this.type != ExtractorLinkType.VIDEO) return null

        videoSize = videoSize ?: runCatching {
            val response =
                app.head(this.url, headers = headers, referer = referer, timeout = timeoutSeconds)
            response.headers["Content-Length"]?.toLong()
        }.getOrNull()

        return videoSize
    }

    @JsonIgnore
    fun getAllHeaders(): Map<String, String> {
        if (referer.isBlank()) {
            return headers
        } else if (headers.keys.none { it.equals("referer", ignoreCase = true) }) {
            return headers + mapOf("referer" to referer)
        }
        return headers
    }

    @Deprecated("Use newExtractorLink", level = DeprecationLevel.ERROR)
    constructor(
        source: String,
        name: String,
        url: String,
        referer: String? = null,
        quality: Int? = null,
        /** the type of the media, use INFER_TYPE if you want to auto infer the type from the url */
        type: ExtractorLinkType? = INFER_TYPE,
        headers: Map<String, String> = mapOf(),
        /** Used for getExtractorVerifierJob() */
        extractorData: String? = null,
    ) : this(
        source = source,
        name = name,
        url = url,
        referer = referer ?: "",
        quality = quality ?: Qualities.Unknown.value,
        headers = headers,
        extractorData = extractorData,
        type = type ?: inferTypeFromUrl(url)
    )

    @Deprecated("Use newExtractorLink", level = DeprecationLevel.ERROR)
    constructor(
        source: String,
        name: String,
        url: String,
        referer: String,
        quality: Int,
        /** the type of the media, use INFER_TYPE if you want to auto infer the type from the url */
        type: ExtractorLinkType?,
        headers: Map<String, String> = mapOf(),
        /** Used for getExtractorVerifierJob() */
        extractorData: String? = null,
    ) : this(
        source = source,
        name = name,
        url = url,
        referer = referer,
        quality = quality,
        headers = headers,
        extractorData = extractorData,
        type = type ?: inferTypeFromUrl(url)
    )

    @Suppress("DEPRECATION_ERROR")
    @Deprecated("Use newExtractorLink", level = DeprecationLevel.ERROR)
    constructor(
        source: String,
        name: String,
        url: String,
        referer: String,
        quality: Int,
        isM3u8: Boolean = false,
        headers: Map<String, String> = mapOf(),
        /** Used for getExtractorVerifierJob() */
        extractorData: String? = null
    ) : this(source, name, url, referer, quality, isM3u8, headers, extractorData, false)

    @Deprecated("Use newExtractorLink", level = DeprecationLevel.ERROR)
    constructor(
        source: String,
        name: String,
        url: String,
        referer: String,
        quality: Int,
        isM3u8: Boolean = false,
        headers: Map<String, String> = mapOf(),
        /** Used for getExtractorVerifierJob() */
        extractorData: String? = null,
        isDash: Boolean,
    ) : this(
        source = source,
        name = name,
        url = url,
        referer = referer,
        quality = quality,
        headers = headers,
        extractorData = extractorData,
        type = if (isDash) ExtractorLinkType.DASH else if (isM3u8) ExtractorLinkType.M3U8 else ExtractorLinkType.VIDEO
    )

    override fun toString(): String {
        return "ExtractorLink(name=$name, url=$url, referer=$referer, type=$type)"
    }
}

/**
 * Removes https:// and www.
 * To match urls regardless of schema, perhaps Uri() can be used?
 */
val schemaStripRegex = Regex("""^(https:|)//(www\.|)""")

enum class Qualities(var value: Int, val defaultPriority: Int) {
    Unknown(400, 4),
    P144(144, 0), // 144p
    P240(240, 2), // 240p
    P360(360, 3), // 360p
    P480(480, 4), // 480p
    P720(720, 5), // 720p
    P1080(1080, 6), // 1080p
    P1440(1440, 7), // 1440p
    P2160(2160, 8); // 4k or 2160p

    companion object {
        fun getStringByInt(qual: Int?): String {
            return when (qual) {
                0 -> "Auto"
                Unknown.value -> ""
                P2160.value -> "4K"
                null -> ""
                else -> "${qual}p"
            }
        }

        fun getStringByIntFull(quality: Int): String {
            return when (quality) {
                0 -> "Auto"
                Unknown.value -> "Unknown"
                P2160.value -> "4K"
                else -> "${quality}p"
            }
        }
    }
}

fun getQualityFromName(qualityName: String?): Int {
    if (qualityName == null)
        return Qualities.Unknown.value

    val match = qualityName.lowercase().replace("p", "").trim()
    return when (match) {
        "4k" -> Qualities.P2160
        else -> null
    }?.value ?: match.toIntOrNull() ?: Qualities.Unknown.value
}

private val packedRegex = Regex("""eval\(function\(p,a,c,k,e,.*\)\)""")
fun getPacked(string: String): String? {
    return packedRegex.find(string)?.value
}

fun getAndUnpack(string: String): String {
    val packedText = getPacked(string)
    return JsUnpacker(packedText).unpack() ?: string
}

suspend fun unshortenLinkSafe(url: String): String {
    return try {
        if (ShortLink.isShortLink(url))
            ShortLink.unshorten(url)
        else url
    } catch (e: Exception) {
        logError(e)
        url
    }
}

suspend fun loadExtractor(
    url: String,
    subtitleCallback: (SubtitleFile) -> Unit,
    callback: (ExtractorLink) -> Unit
): Boolean {
    return loadExtractor(
        url = url,
        referer = null,
        subtitleCallback = subtitleCallback,
        callback = callback
    )
}

/**
 * Tries to load the appropriate extractor based on link, returns true if any extractor is loaded.
 * */
@Throws(CancellationException::class)
suspend fun loadExtractor(
    url: String,
    referer: String? = null,
    subtitleCallback: (SubtitleFile) -> Unit,
    callback: (ExtractorLink) -> Unit
): Boolean {
    // Ensure this coroutine has not timed out
    coroutineScope { ensureActive() }

    val currentUrl = unshortenLinkSafe(url)
    val compareUrl = currentUrl.lowercase().replace(schemaStripRegex, "")

    // Iterate in reverse order so the new registered ExtractorApi takes priority
    for (index in extractorApis.lastIndex downTo 0) {
        val extractor = extractorApis[index]
        if (compareUrl.startsWith(extractor.mainUrl.replace(schemaStripRegex, ""))) {
            try {
                extractor.getUrl(currentUrl, referer, subtitleCallback, callback)
            } catch (e: Exception) {
                logError(e)
                // Rethrow if we have timed out
                if (e is CancellationException) {
                    throw e
                }
            }
            return true
        }
    }

    // this is to match mirror domains - like example.com, example.net
    for (index in extractorApis.lastIndex downTo 0) {
        val extractor = extractorApis[index]
        if (FuzzySearch.partialRatio(
                extractor.mainUrl,
                currentUrl
            ) > 80
        ) {
            try {
                extractor.getUrl(currentUrl, referer, subtitleCallback, callback)
            } catch (e: Exception) {
                logError(e)
                // Rethrow if we have timed out
                if (e is CancellationException) {
                    throw e
                }
            }
            return true
        }
    }

    return false
}

val extractorApis: MutableList<ExtractorApi> = arrayListOf(
    //AllProvider(),
    WcoStream(),
    Vidstreamz(),
    Vizcloud(),
    Vizcloud2(),
    VizcloudOnline(),
    VizcloudXyz(),
    VizcloudLive(),
    VizcloudInfo(),
    MwvnVizcloudInfo(),
    VizcloudDigital(),
    VizcloudCloud(),
    VizcloudSite(),
    VideoVard(),
    VideovardSX(),
    Mp4Upload(),
    StreamTape(),
    StreamTapeNet(),
    ShaveTape(),
    StreamTapeXyz(),

    //mixdrop extractors
    MixDropBz(),
    MixDropCh(),
    MixDropTo(),
    MixDropAg(),

    MixDrop(),

    Mcloud(),
    XStreamCdn(),

    StreamSB(),
    Sblona(),
    Vidgomunimesb(),
    StreamSilk(),
    StreamSB1(),
    StreamSB2(),
    StreamSB3(),
    StreamSB4(),
    StreamSB5(),
    StreamSB6(),
    StreamSB7(),
    StreamSB8(),
    StreamSB9(),
    StreamSB10(),
    StreamSB11(),
    SBfull(),
    // Streamhub(), cause Streamhub2() works
    Streamhub2(),
    Ssbstream(),
    Sbthe(),
    Vidgomunime(),
    Sbflix(),
    Streamsss(),
    Sbspeed(),
    Sbsonic(),
    Sbface(),
    Sbrapid(),
    Lvturbo(),

    Fastream(),

    FEmbed(),
    FeHD(),
    Fplayer(),
    DBfilm(),
    Luxubu(),
    LayarKaca(),
    Rasacintaku(),
    FEnet(),
    Kotakajair(),
    Cdnplayer(),
    //  WatchSB(), 'cause StreamSB.kt works
    Uqload(),
    Uqload1(),
    Uqload2(),
    Evoload(),
    Evoload1(),
    UpstreamExtractor(),

    Odnoklassniki(),
    TauVideo(),
    SibNet(),
    ContentX(),
    Hotlinger(),
    FourCX(),
    PlayRu(),
    FourPlayRu(),
    Pichive(),
    FourPichive(),
    HDMomPlayer(),
    HDPlayerSystem(),
    VideoSeyred(),
    PeaceMakerst(),
    HDStreamAble(),
    RapidVid(),
    TRsTX(),
    VidMoxy(),
    Sobreatsesuyp(),
    PixelDrain(),
    MailRu(),

    Tomatomatela(),
    TomatomatelalClub(),
    Cinestart(),
    OkRuSSL(),
    OkRuHTTP(),
    Okrulink(),
    Sendvid(),

    // dood extractors
    DoodCxExtractor(),
    DoodPmExtractor(),
    DoodToExtractor(),
    DoodSoExtractor(),
    DoodLaExtractor(),
    Dooood(),
    D0000d(),
    D000dCom(),
    DoodstreamCom(),
    DoodWsExtractor(),
    DoodShExtractor(),
    DoodWatchExtractor(),
    DoodWfExtractor(),
    DoodYtExtractor(),

    AsianLoad(),

    // GenericM3U8(),
    Jawcloud(),
    Zplayer(),
    ZplayerV2(),
    Upstream(),

    Maxstream(),
    Tantifilm(),
    Userload(),
    Supervideo(),
    GuardareStream(),
    CineGrabber(),
    Vanfem(),

    // StreamSB.kt works
    //  SBPlay(),
    //  SBPlay1(),
    //  SBPlay2(),

    PlayerVoxzer(),

    BullStream(),
    GMPlayer(),

    Blogger(),
    Solidfiles(),
    YourUpload(),

    Hxfile(),
    KotakAnimeid(),
    Neonime8n(),
    Neonime7n(),
    Yufiles(),
    Aico(),

    JWPlayer(),
    Meownime(),
    DesuArcg(),
    DesuOdchan(),
    DesuOdvip(),
    DesuDrive(),

    Chillx(),
    Moviesapi(),
    Watchx(),
    Bestx(),
    Keephealth(),
    Sbnet(),
    Sbasian(),
    Sblongvu(),
    Fembed9hd(),
    StreamM4u(),
    Krakenfiles(),
    Gofile(),
    Vicloud(),
    Uservideo(),
    Userscloud(),

    Movhide(),
    StreamhideCom(),
    StreamhideTo(),
    Wibufile(),
    FileMoonIn(),
    Moviesm4u(),
    Filesim(),
    Ahvsh(),
    Guccihide(),
    FileMoon(),
    FileMoonSx(),
    FilemoonV2(),

    Vido(),
    Linkbox(),
    Acefile(),
    Minoplres(), // formerly SpeedoStream
    Zorofile(),
    Embedgram(),
    Mvidoo(),
    Streamplay(),
    Vidmoly(),
    Vidmolyme(),
    Voe(),
    Voe1(),
    Tubeless(),
    Moviehab(),
    MoviehabNet(),
    Jeniusplay(),
    StreamoUpload(),

    GamoVideo(),
    Gdriveplayerapi(),
    Gdriveplayerapp(),
    Gdriveplayerfun(),
    Gdriveplayerio(),
    Gdriveplayerme(),
    Gdriveplayerbiz(),
    Gdriveplayerorg(),
    Gdriveplayerus(),
    Gdriveplayerco(),
    GoodstreamExtractor(),
    Gdriveplayer(),
    DatabaseGdrive(),
    DatabaseGdrive2(),
    Mediafire(),

    YoutubeExtractor(),
    YoutubeShortLinkExtractor(),
    YoutubeMobileExtractor(),
    YoutubeNoCookieExtractor(),
    Streamlare(),
    VidSrcExtractor(),
    VidSrcExtractor2(),
    VidSrcTo(),
    PlayLtXyz(),
    AStreamHub(),
    Vidplay(),
    VidplayOnline(),
    MyCloud(),
    MegaF(),

    Cda(),
    Dailymotion(),
    ByteShare(),
    Ztreamhub(),
    Rabbitstream(),
    Dokicloud(),
    Megacloud(),
    VidhideExtractor(),
    VidHidePro(),
    VidHidePro1(),
    VidHidePro2(),
    VidHidePro3(),
    VidHidePro4(),
    VidHidePro5(),
    VidHidePro6(),
    Dhtpre(),
    Dhcplay(),
    Smoothpre(),
    Peytonepre(),
    LuluStream(),
    Lulustream1(),
    Lulustream2(),
    StreamWishExtractor(),
    BigwarpIO(),
    BigwarpArt(),
    BgwpCC(),
    WishembedPro(),
    CdnwishCom(),
    FlaswishCom(),
    SfastwishCom(),
    Playerwish(),
    EmturbovidExtractor(),
    Vtbe(),
    EPlayExtractor(),
    Vidguardto(),
    Vidguardto1(),
    Vidguardto2(),
    Vidguardto3(),
    SecvideoOnline(),
    FsstOnline(),
    CsstOnline(),
    DsstOnline(),
    Simpulumlamerop(),
    Urochsunloath(),
    NathanFromSubject(),
    Yipsu(),
    MetaGnathTuggers(),
    Geodailymotion(),
    Mwish(),
    Dwish(),
    Ewish(),
    Kswplayer(),
    Wishfast(),
    Streamwish2(),
    Strwish(),
    Strwish2(),
    Awish(),
    Obeywish(),
    Jodwish(),
    Swhoi(),
    Multimovies(),
    UqloadsXyz(),
    Doodporn(),
    Asnwish(),
    Nekowish(),
    Nekostream(),
    Swdyu(),
    Wishonly(),
    Beastx(),
    Playerx(),
    AnimesagaStream(),
    Anplay(),
    Kinogeru(),
    Vidxstream(),
    Boltx(),
    Vectorx(),
    Boosterx(),
    Ds2play(),
    Ds2video(),
    Filegram(),
    InternetArchive(),
    VidStack(),
    GDMirrorbot(),
    Server1uns(),
    VinovoSi(),
    VinovoTo(),
    CloudMailRu(),
)


fun getExtractorApiFromName(name: String): ExtractorApi {
    for (api in extractorApis) {
        if (api.name == name) return api
    }
    return extractorApis[0]
}

fun requireReferer(name: String): Boolean {
    return getExtractorApiFromName(name).requiresReferer
}

fun httpsify(url: String): String {
    return if (url.startsWith("//")) "https:$url" else url
}

suspend fun getPostForm(requestUrl: String, html: String): String? {
    val document = Jsoup.parse(html)
    val inputs = document.select("Form > input")
    if (inputs.size < 4) return null
    var op: String? = null
    var id: String? = null
    var mode: String? = null
    var hash: String? = null

    for (input in inputs) {
        val value = input.attr("value") ?: continue
        when (input.attr("name")) {
            "op" -> op = value
            "id" -> id = value
            "mode" -> mode = value
            "hash" -> hash = value
            else -> Unit
        }
    }
    if (op == null || id == null || mode == null || hash == null) {
        return null
    }
    delay(5000) // ye this is needed, wont work with 0 delay

    return app.post(
        requestUrl,
        headers = mapOf(
            "content-type" to "application/x-www-form-urlencoded",
            "referer" to requestUrl,
            "user-agent" to USER_AGENT,
            "accept" to "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9"
        ),
        data = mapOf("op" to op, "id" to id, "mode" to mode, "hash" to hash)
    ).text
}

fun ExtractorApi.fixUrl(url: String): String {
    if (url.startsWith("http") ||
        // Do not fix JSON objects when passed as urls.
        url.startsWith("{\"")
    ) {
        return url
    }
    if (url.isEmpty()) {
        return ""
    }

    val startsWithNoHttp = url.startsWith("//")
    if (startsWithNoHttp) {
        return "https:$url"
    } else {
        if (url.startsWith('/')) {
            return mainUrl + url
        }
        return "$mainUrl/$url"
    }
}

abstract class ExtractorApi {
    abstract val name: String
    abstract val mainUrl: String
    abstract val requiresReferer: Boolean

    /** Determines which plugin a given provider is from. This is the full path to the plugin. */
    var sourcePlugin: String? = null

    //suspend fun getSafeUrl(url: String, referer: String? = null): List<ExtractorLink>? {
    //    return safeAsync { getUrl(url, referer) }
    //}

    // this is the new extractorapi, override to add subtitles and stuff
    @Throws
    open suspend fun getUrl(
        url: String,
        referer: String? = null,
        subtitleCallback: (SubtitleFile) -> Unit,
        callback: (ExtractorLink) -> Unit
    ) {
        getUrl(url, referer)?.forEach(callback)
    }

    suspend fun getSafeUrl(
        url: String,
        referer: String? = null,
        subtitleCallback: (SubtitleFile) -> Unit,
        callback: (ExtractorLink) -> Unit
    ) {
        try {
            getUrl(url, referer, subtitleCallback, callback)
        } catch (e: Exception) {
            logError(e)
        }
    }

    /**
     * Will throw errors, use getSafeUrl if you don't want to handle the exception yourself
     */
    @Throws
    open suspend fun getUrl(url: String, referer: String? = null): List<ExtractorLink>? {
        return emptyList()
    }

    open fun getExtractorUrl(id: String): String {
        return id
    }
}
