package com.example.model

enum class VideoFormat(val extension: String, val label: String) {
  MP4("mp4", "MP4 Video"),
  MP3("mp3", "MP3 Audio"),
  M4A("m4a", "M4A Audio")
}

enum class DownloadStatus {
  QUEUED,
  DOWNLOADING,
  PAUSED,
  COMPLETED,
  FAILED
}

data class DownloadOption(
  val id: String,
  val format: VideoFormat,
  val quality: String,
  val resolution: String,
  val sizeBytes: Long,
  val sizeFormatted: String,
  val isAudioOnly: Boolean = false,
  val fps: Int = 30,
  val codec: String = "H.264 / AAC"
)

data class VideoInfo(
  val id: String,
  val originalUrl: String,
  val title: String,
  val channelName: String,
  val channelAvatarUrl: String? = null,
  val duration: String,
  val durationSeconds: Int,
  val thumbnailUrl: String,
  val views: String,
  val uploadDate: String,
  val isAuthorizedDownload: Boolean,
  val licenseType: String,
  val description: String,
  val isShorts: Boolean = false,
  val availableOptions: List<DownloadOption> = emptyList()
)

data class DownloadTask(
  val id: String,
  val video: VideoInfo,
  val option: DownloadOption,
  val progress: Float = 0f,
  val status: DownloadStatus = DownloadStatus.QUEUED,
  val downloadSpeed: String = "0 KB/s",
  val etaSeconds: Int = 0,
  val downloadedBytes: Long = 0L,
  val timestamp: Long = System.currentTimeMillis()
)

data class FAQItem(
  val id: String,
  val question: String,
  val answer: String,
  val category: String = "General"
)
