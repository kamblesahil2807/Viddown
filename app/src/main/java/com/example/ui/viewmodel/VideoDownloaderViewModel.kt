package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.VideoRepository
import com.example.model.DownloadOption
import com.example.model.DownloadStatus
import com.example.model.DownloadTask
import com.example.model.VideoInfo
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class AppTab(val label: String) {
  HOME("Downloader"),
  LIBRARY("Library"),
  HOW_IT_WORKS("How It Works"),
  FAQ("FAQ & Legal"),
  ABOUT("About")
}

data class UiNotification(
  val id: Long = System.currentTimeMillis(),
  val message: String,
  val isError: Boolean = false
)

data class HomeUiState(
  val currentTab: AppTab = AppTab.HOME,
  val inputUrl: String = "https://youtu.be/TjUXBudyUxg?si=1_vehbFO0I1w16h5",
  val isLoadingInfo: Boolean = false,
  val validationError: String? = null,
  val videoInfo: VideoInfo? = null,
  val selectedOption: DownloadOption? = null,
  val showOfficialGuideDialog: Boolean = false,
  val librarySearchQuery: String = "",
  val notification: UiNotification? = null
)

class VideoDownloaderViewModel : ViewModel() {

  private val _uiState = MutableStateFlow(HomeUiState())
  val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

  init {
    fetchVideo()
  }

  val allTasks: StateFlow<List<DownloadTask>> = VideoRepository.downloadTasks

  // Filtered downloads for Library tab based on search query
  val filteredTasks: StateFlow<List<DownloadTask>> = combine(
    allTasks,
    _uiState
  ) { tasks, state ->
    val query = state.librarySearchQuery.trim().lowercase()
    if (query.isEmpty()) {
      tasks
    } else {
      tasks.filter { task ->
        task.video.title.lowercase().contains(query) ||
            task.video.channelName.lowercase().contains(query) ||
            task.option.quality.lowercase().contains(query) ||
            task.option.format.name.lowercase().contains(query)
      }
    }
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  fun setTab(tab: AppTab) {
    _uiState.update { it.copy(currentTab = tab) }
  }

  fun updateInputUrl(url: String) {
    _uiState.update { it.copy(inputUrl = url, validationError = null) }
  }

  fun clearInputUrl() {
    _uiState.update { it.copy(inputUrl = "", validationError = null, videoInfo = null, selectedOption = null) }
  }

  fun onPasteFromClipboard(pasted: String) {
    if (pasted.isNotBlank()) {
      _uiState.update { it.copy(inputUrl = pasted.trim(), validationError = null) }
      showNotification("URL pasted from clipboard")
      // Auto fetch video
      fetchVideo()
    }
  }

  fun selectSample(url: String) {
    _uiState.update { it.copy(inputUrl = url, validationError = null) }
    fetchVideo()
  }

  fun fetchVideo() {
    val currentUrl = _uiState.value.inputUrl.trim()
    val validation = VideoRepository.validateUrl(currentUrl)

    if (!validation.isValid) {
      _uiState.update {
        it.copy(
          validationError = validation.errorMessage ?: "Please enter a valid video URL",
          videoInfo = null,
          selectedOption = null
        )
      }
      return
    }

    viewModelScope.launch {
      _uiState.update { it.copy(isLoadingInfo = true, validationError = null) }
      // Brief network simulated analysis
      delay(450)
      val info = VideoRepository.fetchVideoInfo(validation, currentUrl)
      val defaultOption = info.availableOptions.firstOrNull { it.quality.contains("720p") }
        ?: info.availableOptions.firstOrNull()

      _uiState.update {
        it.copy(
          isLoadingInfo = false,
          videoInfo = info,
          selectedOption = defaultOption
        )
      }
      showNotification("Video details retrieved successfully")
    }
  }

  fun selectOption(option: DownloadOption) {
    _uiState.update { it.copy(selectedOption = option) }
  }

  fun initiateDownload() {
    val state = _uiState.value
    val video = state.videoInfo ?: return
    val option = state.selectedOption ?: return

    if (!video.isAuthorizedDownload) {
      // The video is standard copyright; guide user to the official YouTube offline mode!
      _uiState.update { it.copy(showOfficialGuideDialog = true) }
      return
    }

    // Video is authorized for download
    val taskId = VideoRepository.startDownload(video, option)
    showNotification("Downloading ${option.quality} to Library...")
  }

  fun dismissOfficialGuide() {
    _uiState.update { it.copy(showOfficialGuideDialog = false) }
  }

  fun togglePauseResume(taskId: String) {
    VideoRepository.togglePauseResume(taskId)
  }

  fun deleteTask(taskId: String) {
    VideoRepository.deleteTask(taskId)
    showNotification("Download removed from Library")
  }

  fun updateSearchQuery(query: String) {
    _uiState.update { it.copy(librarySearchQuery = query) }
  }

  fun dismissNotification() {
    _uiState.update { it.copy(notification = null) }
  }

  private fun showNotification(message: String, isError: Boolean = false) {
    _uiState.update { it.copy(notification = UiNotification(message = message, isError = isError)) }
  }
}
