package com.example

import com.example.data.VideoRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

  @Test
  fun validate_standard_youtube_url() {
    val result = VideoRepository.validateUrl("https://www.youtube.com/watch?v=mN0zPOpADL4")
    assertTrue(result.isValid)
    assertEquals("mN0zPOpADL4", result.videoId)
    assertFalse(result.isShorts)
  }

  @Test
  fun validate_shortened_youtube_url() {
    val result = VideoRepository.validateUrl("https://youtu.be/mN0zPOpADL4")
    assertTrue(result.isValid)
    assertEquals("mN0zPOpADL4", result.videoId)
  }

  @Test
  fun validate_user_provided_youtube_url_with_query_params() {
    val result = VideoRepository.validateUrl("https://youtu.be/TjUXBudyUxg?si=1_vehbFO0I1w16h5")
    assertTrue(result.isValid)
    assertEquals("TjUXBudyUxg", result.videoId)
    assertFalse(result.isShorts)
  }

  @Test
  fun validate_youtube_shorts_url() {
    val result = VideoRepository.validateUrl("https://www.youtube.com/shorts/L_LUpnjgPso")
    assertTrue(result.isValid)
    assertEquals("L_LUpnjgPso", result.videoId)
    assertTrue(result.isShorts)
  }

  @Test
  fun validate_direct_video_stream_url() {
    val result = VideoRepository.validateUrl("https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4")
    assertTrue(result.isValid)
    assertTrue(result.isDirectVideo)
  }

  @Test
  fun validate_invalid_url_returns_error() {
    val result = VideoRepository.validateUrl("not_a_valid_url")
    assertFalse(result.isValid)
    assertTrue(result.errorMessage != null)
  }
}
