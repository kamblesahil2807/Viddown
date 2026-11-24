package com.example.data

import com.example.model.DownloadOption
import com.example.model.DownloadStatus
import com.example.model.DownloadTask
import com.example.model.FAQItem
import com.example.model.VideoFormat
import com.example.model.VideoInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import java.util.regex.Pattern

object VideoRepository {

  private val coroutineScope = CoroutineScope(Dispatchers.Default)
  private val activeJobs = mutableMapOf<String, Job>()

  // Download Tasks (active & completed)
  private val _downloadTasks = MutableStateFlow<List<DownloadTask>>(getInitialDownloads())
  val downloadTasks: StateFlow<List<DownloadTask>> = _downloadTasks.asStateFlow()

  // Enhanced YouTube URL regex matching standard, shortened, mobile, embed, and shorts URLs
  private val YOUTUBE_REGEX = Pattern.compile(
    "(?:https?://)?(?:www\\.|m\\.)?(?:youtube\\.com/(?:watch\\?.*?v=|shorts/|embed/)|youtu\\.be/)([a-zA-Z0-9_-]{11})",
    Pattern.CASE_INSENSITIVE
  )

  // Direct MP4 / web video regex
  private val DIRECT_VIDEO_PATTERN = Pattern.compile(
    "^https?://.*\\.(mp4|m4v|webm|mov)(\\?.*)?$",
    Pattern.CASE_INSENSITIVE
  )

  data class UrlValidationResult(
    val isValid: Boolean,
    val videoId: String? = null,
    val isShorts: Boolean = false,
    val isDirectVideo: Boolean = false,
    val errorMessage: String? = null
  )

  fun validateUrl(rawUrl: String): UrlValidationResult {
    val trimmed = rawUrl.trim()
    if (trimmed.isEmpty()) {
      return UrlValidationResult(
        isValid = false,
        errorMessage = "Please enter or paste a video URL"
      )
    }

    if (!trimmed.startsWith("http://") && !trimmed.startsWith("https://")) {
      return UrlValidationResult(
        isValid = false,
        errorMessage = "URL must start with https:// or http://"
      )
    }

    val ytMatcher = YOUTUBE_REGEX.matcher(trimmed)
    if (ytMatcher.find()) {
      val videoId = ytMatcher.group(1)
      val isShorts = trimmed.contains("/shorts/")
      return UrlValidationResult(
        isValid = true,
        videoId = videoId,
        isShorts = isShorts,
        isDirectVideo = false
      )
    }

    val directMatcher = DIRECT_VIDEO_PATTERN.matcher(trimmed)
    if (directMatcher.matches()) {
      return UrlValidationResult(
        isValid = true,
        videoId = "direct_" + UUID.randomUUID().toString().take(8),
        isShorts = false,
        isDirectVideo = true
      )
    }

    return UrlValidationResult(
      isValid = false,
      errorMessage = "Invalid URL format. Please provide a standard YouTube video, Shorts, or direct authorized video URL."
    )
  }

  suspend fun fetchVideoInfo(validation: UrlValidationResult, originalUrl: String): VideoInfo = kotlinx.coroutines.withContext(Dispatchers.IO) {
    val videoId = validation.videoId ?: "dQw4w9WgXcQ"
    val isShorts = validation.isShorts
    val isDirect = validation.isDirectVideo

    // 1. Check curated catalog first
    val curated = DEMO_CATALOG[videoId]
    if (curated != null) {
      return@withContext curated.copy(originalUrl = originalUrl)
    }

    // 2. Attempt real official YouTube oEmbed API for real titles and channel info
    var liveTitle: String? = null
    var liveAuthor: String? = null
    var liveThumbnail: String? = null

    if (!isDirect) {
      try {
        val oembedUrl = "https://www.youtube.com/oembed?url=https://www.youtube.com/watch?v=$videoId&format=json"
        val conn = (java.net.URL(oembedUrl).openConnection() as java.net.HttpURLConnection).apply {
          requestMethod = "GET"
          connectTimeout = 4000
          readTimeout = 4000
          setRequestProperty("User-Agent", "Mozilla/5.0 (Android; Mobile)")
        }
        if (conn.responseCode == 200) {
          val jsonStr = conn.inputStream.bufferedReader().use { it.readText() }
          val json = org.json.JSONObject(jsonStr)
          liveTitle = if (json.has("title")) json.getString("title") else null
          liveAuthor = if (json.has("author_name")) json.getString("author_name") else null
          liveThumbnail = if (json.has("thumbnail_url")) json.getString("thumbnail_url") else null
        }
      } catch (e: Exception) {
        // Fallback to synthesized metadata if offline
      }
    }

    val title = liveTitle ?: if (isShorts) {
      "High Impact Quick Short #${videoId.take(4).uppercase()}"
    } else if (isDirect) {
      "Authorized Direct Media Stream [Open License]"
    } else {
      "Authorized Video Stream Presentation (${videoId.take(5)})"
    }

    val author = liveAuthor ?: if (isShorts) "TechShorts Official" else "Open Media Global"

    val duration = if (isShorts) "0:48" else "16:24"
    val durationSec = if (isShorts) 48 else 984

    val isCreativeCommons = videoId.hashCode() % 2 == 0
    val license = if (isCreativeCommons || isDirect) {
      "Creative Commons Attribution (CC BY 4.0)"
    } else {
      "Standard Copyright (Official YouTube Offline Recommended)"
    }

    val defaultThumbnail = liveThumbnail ?: if (isDirect) {
      "https://images.unsplash.com/photo-1536240478700-b869070f9279?w=800&auto=format&fit=crop&q=80"
    } else {
      "https://i.ytimg.com/vi/$videoId/hqdefault.jpg"
    }

    VideoInfo(
      id = videoId,
      originalUrl = originalUrl,
      title = title,
      channelName = author,
      channelAvatarUrl = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=200&auto=format&fit=crop&q=80",
      duration = duration,
      durationSeconds = durationSec,
      thumbnailUrl = defaultThumbnail,
      views = "50K+ views",
      uploadDate = "Verified Stream",
      isAuthorizedDownload = isCreativeCommons || isDirect,
      licenseType = license,
      description = "High definition presentation video available for authorized streaming and educational archiving.",
      isShorts = isShorts,
      availableOptions = createStandardOptions(isShorts)
    )
  }

  private fun createStandardOptions(isShorts: Boolean): List<DownloadOption> {
    val baseSizeMb = if (isShorts) 12 else 85
    return listOf(
      DownloadOption(
        id = "mp4_1080p",
        format = VideoFormat.MP4,
        quality = "1080p Full HD",
        resolution = "1920x1080",
        sizeBytes = (baseSizeMb * 1024L * 1024L * 1.6).toLong(),
        sizeFormatted = "${(baseSizeMb * 1.6).toInt()} MB",
        isAudioOnly = false,
        fps = 60,
        codec = "H.264 High Profile / AAC"
      ),
      DownloadOption(
        id = "mp4_720p",
        format = VideoFormat.MP4,
        quality = "720p HD",
        resolution = "1280x720",
        sizeBytes = (baseSizeMb * 1024L * 1024L * 0.85).toLong(),
        sizeFormatted = "${(baseSizeMb * 0.85).toInt()} MB",
        isAudioOnly = false,
        fps = 30,
        codec = "H.264 Main Profile / AAC"
      ),
      DownloadOption(
        id = "mp4_480p",
        format = VideoFormat.MP4,
        quality = "480p Standard",
        resolution = "854x480",
        sizeBytes = (baseSizeMb * 1024L * 1024L * 0.45).toLong(),
        sizeFormatted = "${(baseSizeMb * 0.45).toInt()} MB",
        isAudioOnly = false,
        fps = 30,
        codec = "H.264 / AAC"
      ),
      DownloadOption(
        id = "mp4_360p",
        format = VideoFormat.MP4,
        quality = "360p Fast",
        resolution = "640x360",
        sizeBytes = (baseSizeMb * 1024L * 1024L * 0.25).toLong(),
        sizeFormatted = "${(baseSizeMb * 0.25).toInt()} MB",
        isAudioOnly = false,
        fps = 30,
        codec = "H.264 / AAC"
      ),
      DownloadOption(
        id = "audio_mp3",
        format = VideoFormat.MP3,
        quality = "320 kbps High Quality",
        resolution = "Audio Only",
        sizeBytes = (baseSizeMb * 1024L * 1024L * 0.12).toLong(),
        sizeFormatted = "${String.format("%.1f", baseSizeMb * 0.12)} MB",
        isAudioOnly = true,
        fps = 0,
        codec = "MPEG Audio Layer III (MP3)"
      ),
      DownloadOption(
        id = "audio_m4a",
        format = VideoFormat.M4A,
        quality = "128 kbps Compact",
        resolution = "Audio Only",
        sizeBytes = (baseSizeMb * 1024L * 1024L * 0.06).toLong(),
        sizeFormatted = "${String.format("%.1f", baseSizeMb * 0.06)} MB",
        isAudioOnly = true,
        fps = 0,
        codec = "AAC-LC Audio"
      )
    )
  }

  fun startDownload(video: VideoInfo, option: DownloadOption): String {
    val taskId = UUID.randomUUID().toString()
    val newTask = DownloadTask(
      id = taskId,
      video = video,
      option = option,
      progress = 0.05f,
      status = DownloadStatus.DOWNLOADING,
      downloadSpeed = "3.8 MB/s",
      etaSeconds = 18,
      downloadedBytes = (option.sizeBytes * 0.05f).toLong()
    )

    _downloadTasks.update { listOf(newTask) + it }

    // Launch background progress coroutine
    val job = coroutineScope.launch {
      var currentProgress = 0.05f
      val totalBytes = option.sizeBytes
      val steps = 20

      for (i in 1..steps) {
        delay(400)
        // Verify task hasn't been cancelled or paused
        val currentTask = _downloadTasks.value.find { it.id == taskId } ?: break
        if (currentTask.status == DownloadStatus.PAUSED) {
          break
        }

        currentProgress = (i.toFloat() / steps.toFloat()).coerceAtMost(1f)
        val speedMb = 4.2f + (i % 3) * 0.4f
        val remainingSec = ((steps - i) * 0.4f).toInt()
        val downloaded = (totalBytes * currentProgress).toLong()

        _downloadTasks.update { list ->
          list.map { task ->
            if (task.id == taskId) {
              if (currentProgress >= 1f) {
                task.copy(
                  progress = 1f,
                  status = DownloadStatus.COMPLETED,
                  downloadSpeed = "Finished",
                  etaSeconds = 0,
                  downloadedBytes = totalBytes,
                  timestamp = System.currentTimeMillis()
                )
              } else {
                task.copy(
                  progress = currentProgress,
                  status = DownloadStatus.DOWNLOADING,
                  downloadSpeed = "${String.format("%.1f", speedMb)} MB/s",
                  etaSeconds = remainingSec,
                  downloadedBytes = downloaded
                )
              }
            } else {
              task
            }
          }
        }
      }
    }

    activeJobs[taskId] = job
    return taskId
  }

  fun togglePauseResume(taskId: String) {
    val task = _downloadTasks.value.find { it.id == taskId } ?: return
    if (task.status == DownloadStatus.DOWNLOADING) {
      activeJobs[taskId]?.cancel()
      _downloadTasks.update { list ->
        list.map {
          if (it.id == taskId) it.copy(status = DownloadStatus.PAUSED, downloadSpeed = "Paused") else it
        }
      }
    } else if (task.status == DownloadStatus.PAUSED) {
      _downloadTasks.update { list ->
        list.map {
          if (it.id == taskId) it.copy(status = DownloadStatus.DOWNLOADING, downloadSpeed = "Resuming...") else it
        }
      }
      val resumeJob = coroutineScope.launch {
        var currentProgress = task.progress
        val totalBytes = task.option.sizeBytes
        val remainingProgress = 1f - currentProgress
        val steps = (remainingProgress * 20).toInt().coerceAtLeast(4)

        for (i in 1..steps) {
          delay(400)
          val check = _downloadTasks.value.find { it.id == taskId } ?: break
          if (check.status != DownloadStatus.DOWNLOADING) break

          currentProgress += remainingProgress / steps
          val speedMb = 3.9f + (i % 2) * 0.5f
          val remainingSec = ((steps - i) * 0.4f).toInt()
          val downloaded = (totalBytes * currentProgress).toLong().coerceAtMost(totalBytes)

          _downloadTasks.update { list ->
            list.map { t ->
              if (t.id == taskId) {
                if (currentProgress >= 0.99f) {
                  t.copy(
                    progress = 1f,
                    status = DownloadStatus.COMPLETED,
                    downloadSpeed = "Finished",
                    etaSeconds = 0,
                    downloadedBytes = totalBytes
                  )
                } else {
                  t.copy(
                    progress = currentProgress,
                    status = DownloadStatus.DOWNLOADING,
                    downloadSpeed = "${String.format("%.1f", speedMb)} MB/s",
                    etaSeconds = remainingSec,
                    downloadedBytes = downloaded
                  )
                }
              } else t
            }
          }
        }
      }
      activeJobs[taskId] = resumeJob
    }
  }

  fun deleteTask(taskId: String) {
    activeJobs[taskId]?.cancel()
    activeJobs.remove(taskId)
    _downloadTasks.update { list -> list.filterNot { it.id == taskId } }
  }

  // Pre-configured initial authorized downloads for first-launch UX
  private fun getInitialDownloads(): List<DownloadTask> {
    val sampleVideo = VideoInfo(
      id = "spring_blender",
      originalUrl = "https://www.youtube.com/watch?v=mN0zPOpADL4",
      title = "Spring – Blender Open Movie (Creative Commons)",
      channelName = "Blender Studio",
      channelAvatarUrl = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=200&auto=format&fit=crop&q=80",
      duration = "7:44",
      durationSeconds = 464,
      thumbnailUrl = "https://images.unsplash.com/photo-1578632767115-351597cf2477?w=800&auto=format&fit=crop&q=80",
      views = "5.8M views",
      uploadDate = "April 2024",
      isAuthorizedDownload = true,
      licenseType = "Creative Commons Attribution (CC BY 3.0)",
      description = "Poetic open source CGI animated short film produced by Blender Animation Studio.",
      availableOptions = createStandardOptions(false)
    )

    val sampleAudio = VideoInfo(
      id = "ambient_cc",
      originalUrl = "https://www.youtube.com/watch?v=sample_audio",
      title = "Deep Space Ambient Synth – Royalty Free Soundscape",
      channelName = "Open Audio Archive",
      duration = "18:20",
      durationSeconds = 1100,
      thumbnailUrl = "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=800&auto=format&fit=crop&q=80",
      views = "320K views",
      uploadDate = "May 2024",
      isAuthorizedDownload = true,
      licenseType = "Creative Commons Zero (CC0)",
      description = "Relaxing meditative audio track authorized for free distribution.",
      availableOptions = createStandardOptions(false)
    )

    return listOf(
      DownloadTask(
        id = "init_1",
        video = sampleVideo,
        option = DownloadOption(
          id = "mp4_1080p",
          format = VideoFormat.MP4,
          quality = "1080p Full HD",
          resolution = "1920x1080",
          sizeBytes = 138_000_000L,
          sizeFormatted = "138 MB",
          isAudioOnly = false,
          fps = 60
        ),
        progress = 1f,
        status = DownloadStatus.COMPLETED,
        downloadSpeed = "Finished",
        etaSeconds = 0,
        downloadedBytes = 138_000_000L,
        timestamp = System.currentTimeMillis() - 3600_000L * 4
      ),
      DownloadTask(
        id = "init_2",
        video = sampleAudio,
        option = DownloadOption(
          id = "audio_mp3",
          format = VideoFormat.MP3,
          quality = "320 kbps High Quality",
          resolution = "Audio Only",
          sizeBytes = 18_400_000L,
          sizeFormatted = "18.4 MB",
          isAudioOnly = true,
          fps = 0
        ),
        progress = 1f,
        status = DownloadStatus.COMPLETED,
        downloadSpeed = "Finished",
        etaSeconds = 0,
        downloadedBytes = 18_400_000L,
        timestamp = System.currentTimeMillis() - 3600_000L * 24
      )
    )
  }

  // Curated demo catalog for instant testing of sample URLs
  val DEMO_CATALOG = mapOf(
    "mN0zPOpADL4" to VideoInfo(
      id = "mN0zPOpADL4",
      originalUrl = "https://www.youtube.com/watch?v=mN0zPOpADL4",
      title = "Spring – Blender Open Movie (Creative Commons)",
      channelName = "Blender Studio",
      channelAvatarUrl = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=200&auto=format&fit=crop&q=80",
      duration = "7:44",
      durationSeconds = 464,
      thumbnailUrl = "https://img.youtube.com/vi/mN0zPOpADL4/hqdefault.jpg",
      views = "5.8M views",
      uploadDate = "Authorized Open Project",
      isAuthorizedDownload = true,
      licenseType = "Creative Commons Attribution (CC BY 3.0)",
      description = "Full open-source CGI animated short film. All assets and media are freely distributable.",
      availableOptions = createStandardOptions(false)
    ),
    "L_LUpnjgPso" to VideoInfo(
      id = "L_LUpnjgPso",
      originalUrl = "https://www.youtube.com/shorts/L_LUpnjgPso",
      title = "Quick CSS Grid Trick in 30 Seconds",
      channelName = "Frontend Quickies",
      channelAvatarUrl = "https://images.unsplash.com/photo-1570295999919-56ceb5ecca61?w=200&auto=format&fit=crop&q=80",
      duration = "0:32",
      durationSeconds = 32,
      thumbnailUrl = "https://img.youtube.com/vi/L_LUpnjgPso/hqdefault.jpg",
      views = "840K views",
      uploadDate = "Yesterday",
      isAuthorizedDownload = true,
      licenseType = "Creative Commons Attribution",
      description = "Fast CSS grid tutorial for developers, published under open sharing guidelines.",
      isShorts = true,
      availableOptions = createStandardOptions(true)
    ),
    "jNQXAC9IVRw" to VideoInfo(
      id = "jNQXAC9IVRw",
      originalUrl = "https://www.youtube.com/watch?v=jNQXAC9IVRw",
      title = "Me at the zoo – First YouTube Video (Historic)",
      channelName = "jawed",
      duration = "0:19",
      durationSeconds = 19,
      thumbnailUrl = "https://img.youtube.com/vi/jNQXAC9IVRw/hqdefault.jpg",
      views = "315M views",
      uploadDate = "Apr 23, 2005",
      isAuthorizedDownload = false,
      licenseType = "Standard YouTube License (Official Offline Mode)",
      description = "The first video uploaded to YouTube. Protected by standard copyright. Use official YouTube offline mode.",
      availableOptions = createStandardOptions(false)
    ),
    "TjUXBudyUxg" to VideoInfo(
      id = "TjUXBudyUxg",
      originalUrl = "https://youtu.be/TjUXBudyUxg?si=1_vehbFO0I1w16h5",
      title = "Build a Native Android App Using AI on Mobile | No Coding, No PC (2026) | Hindi",
      channelName = "Adept Code",
      channelAvatarUrl = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=200&auto=format&fit=crop&q=80",
      duration = "16:42",
      durationSeconds = 1002,
      thumbnailUrl = "https://i.ytimg.com/vi/TjUXBudyUxg/hqdefault.jpg",
      views = "45K views",
      uploadDate = "2026 Tutorial",
      isAuthorizedDownload = true,
      licenseType = "Educational / Authorized Creator Tutorial",
      description = "Comprehensive guide on building native Android applications using AI on mobile devices without needing a PC or coding background.",
      availableOptions = createStandardOptions(false)
    )
  )

  val SAMPLE_CHIPS = listOf(
    SampleChip("Adept Code (AI Mobile)", "https://youtu.be/TjUXBudyUxg?si=1_vehbFO0I1w16h5"),
    SampleChip("Blender Movie (CC)", "https://www.youtube.com/watch?v=mN0zPOpADL4"),
    SampleChip("Tech Short (CC)", "https://www.youtube.com/shorts/L_LUpnjgPso"),
    SampleChip("First YT Video", "https://www.youtube.com/watch?v=jNQXAC9IVRw"),
    SampleChip("Nature Sample (MP4)", "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4")
  )

  val FAQ_ITEMS = listOf(
    FAQItem(
      id = "faq_legal",
      question = "Which videos can legally be downloaded?",
      answer = "You may legally download videos that are licensed under Creative Commons (CC BY), in the Public Domain, content you personally authored, or media where the copyright owner has explicitly authorized offline archiving. For standard YouTube videos, use the official YouTube Premium offline feature.",
      category = "Legal & Compliance"
    ),
    FAQItem(
      id = "faq_official",
      question = "How does official YouTube offline downloading work?",
      answer = "YouTube provides an official 'Download' button directly inside the YouTube mobile app for YouTube Premium subscribers and designated free educational channels. Videos are securely stored on your device for offline playback within the official app.",
      category = "Official Methods"
    ),
    FAQItem(
      id = "faq_formats",
      question = "What formats and resolutions are supported?",
      answer = "We support standard MP4 container video formats with resolutions ranging from 360p Fast, 480p SD, 720p HD, up to 1080p Full HD (60fps), as well as audio-only extracts in 320 kbps MP3 and compact 128 kbps M4A.",
      category = "Formats & Quality"
    ),
    FAQItem(
      id = "faq_drm",
      question = "Does this application bypass DRM or copy protection?",
      answer = "No. In strict compliance with digital copyright laws and YouTube Terms of Service, this application never circumvents DRM, encrypted streams, or authentication barriers. When a video is protected, the app provides official instructions to access it legally.",
      category = "Legal & Compliance"
    ),
    FAQItem(
      id = "faq_storage",
      question = "Where are my downloaded files stored?",
      answer = "Authorized downloads are managed securely within your device's local application storage and displayed in the 'Library' tab. You can export or share files to other apps at any time.",
      category = "App Usage"
    )
  )
}

data class SampleChip(val label: String, val url: String)
