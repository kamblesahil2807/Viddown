package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.SampleChip
import com.example.data.VideoRepository
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkCard
import com.example.ui.theme.PrimaryRed
import com.example.ui.theme.PrimaryRedHover
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun HeroSection(
  urlInput: String,
  onUrlChange: (String) -> Unit,
  onPasteClick: (String) -> Unit,
  onClearClick: () -> Unit,
  onGetVideoClick: () -> Unit,
  onSelectSample: (String) -> Unit,
  isLoading: Boolean,
  validationError: String?,
  modifier: Modifier = Modifier
) {
  val clipboardManager = LocalClipboardManager.current

  Column(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 20.dp),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    // Top SaaS Tagline Badge
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier
        .clip(RoundedCornerShape(20.dp))
        .background(Color(0xFF271318))
        .border(1.dp, Color(0xFF4C1D24), RoundedCornerShape(20.dp))
        .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
      Icon(
        imageVector = Icons.Default.AutoAwesome,
        contentDescription = "Feature",
        tint = PrimaryRed,
        modifier = Modifier.size(14.dp)
      )
      Spacer(modifier = Modifier.width(6.dp))
      Text(
        text = "Next-Gen SaaS Media Downloader",
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium,
        color = PrimaryRed
      )
    }

    Spacer(modifier = Modifier.height(14.dp))

    // Heading requested in prompt: "Download Videos Easily"
    Text(
      text = "Download Videos Easily",
      fontSize = 28.sp,
      fontWeight = FontWeight.Bold,
      color = TextPrimary,
      textAlign = TextAlign.Center,
      letterSpacing = (-0.5).sp
    )

    Spacer(modifier = Modifier.height(8.dp))

    // Subtitle requested in prompt: "A simple tool for managing videos you are authorized to download."
    Text(
      text = "A simple tool for managing videos you are authorized to download.",
      fontSize = 14.sp,
      fontWeight = FontWeight.Normal,
      color = TextSecondary,
      textAlign = TextAlign.Center,
      modifier = Modifier.padding(horizontal = 8.dp),
      lineHeight = 20.sp
    )

    Spacer(modifier = Modifier.height(24.dp))

    // URL Input Box with Glassmorphism / Dark Card Container
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(16.dp))
        .background(DarkCard)
        .border(1.dp, if (validationError != null) PrimaryRed else DarkBorder, RoundedCornerShape(16.dp))
        .padding(12.dp)
    ) {
      Column(modifier = Modifier.fillMaxWidth()) {
        // Text Field
        OutlinedTextField(
          value = urlInput,
          onValueChange = onUrlChange,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("video_url_input"),
          placeholder = {
            Text(
              text = "Paste YouTube or video link (e.g., https://youtu.be/...)",
              color = TextMuted,
              fontSize = 13.sp
            )
          },
          leadingIcon = {
            Icon(
              imageVector = Icons.Default.Link,
              contentDescription = "Link",
              tint = TextSecondary,
              modifier = Modifier.size(20.dp)
            )
          },
          trailingIcon = {
            Row(verticalAlignment = Alignment.CenterVertically) {
              if (urlInput.isNotEmpty()) {
                IconButton(
                  onClick = onClearClick,
                  modifier = Modifier.testTag("clear_url_button")
                ) {
                  Icon(
                    imageVector = Icons.Default.Clear,
                    contentDescription = "Clear",
                    tint = TextSecondary,
                    modifier = Modifier.size(18.dp)
                  )
                }
              }
              // Paste Button
              IconButton(
                onClick = {
                  val text = clipboardManager.getText()?.text ?: ""
                  onPasteClick(text)
                },
                modifier = Modifier.testTag("paste_url_button")
              ) {
                Icon(
                  imageVector = Icons.Default.ContentPaste,
                  contentDescription = "Paste",
                  tint = PrimaryRed,
                  modifier = Modifier.size(20.dp)
                )
              }
            }
          },
          singleLine = true,
          shape = RoundedCornerShape(12.dp),
          colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color(0xFF101014),
            unfocusedContainerColor = Color(0xFF101014),
            focusedBorderColor = PrimaryRed,
            unfocusedBorderColor = DarkBorder,
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary,
            cursorColor = PrimaryRed
          )
        )

        // Validation Error Message
        AnimatedVisibility(
          visible = validationError != null,
          enter = fadeIn(),
          exit = fadeOut()
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
              .fillMaxWidth()
              .padding(top = 8.dp, start = 4.dp)
          ) {
            Icon(
              imageVector = Icons.Default.ErrorOutline,
              contentDescription = "Error",
              tint = PrimaryRed,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = validationError ?: "",
              color = PrimaryRed,
              fontSize = 12.sp,
              fontWeight = FontWeight.Medium
            )
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Get Video Button
        Button(
          onClick = onGetVideoClick,
          enabled = !isLoading,
          modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .testTag("get_video_button"),
          shape = RoundedCornerShape(12.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = PrimaryRed,
            contentColor = Color.White,
            disabledContainerColor = Color(0xFF4C1D24),
            disabledContentColor = Color(0xFFA1A1AA)
          )
        ) {
          if (isLoading) {
            CircularProgressIndicator(
              modifier = Modifier.size(20.dp),
              color = Color.White,
              strokeWidth = 2.dp
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
              text = "Analyzing Stream...",
              fontSize = 14.sp,
              fontWeight = FontWeight.SemiBold
            )
          } else {
            Icon(
              imageVector = Icons.Default.Search,
              contentDescription = "Get Video",
              modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "Get Video Details",
              fontSize = 14.sp,
              fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.width(6.dp))
            Icon(
              imageVector = Icons.Default.ArrowForward,
              contentDescription = null,
              modifier = Modifier.size(16.dp)
            )
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Quick Sample URLs Row
    Column(
      modifier = Modifier.fillMaxWidth(),
      horizontalAlignment = Alignment.Start
    ) {
      Text(
        text = "Quick Demo / Authorized Samples:",
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium,
        color = TextSecondary,
        modifier = Modifier.padding(start = 2.dp, bottom = 6.dp)
      )

      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        VideoRepository.SAMPLE_CHIPS.forEach { chip ->
          SampleChipPill(
            chip = chip,
            onClick = { onSelectSample(chip.url) }
          )
        }
      }
    }
  }
}

@Composable
private fun SampleChipPill(
  chip: SampleChip,
  onClick: () -> Unit
) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier
      .clip(RoundedCornerShape(16.dp))
      .background(Color(0xFF202024))
      .border(1.dp, DarkBorder, RoundedCornerShape(16.dp))
      .clickable(onClick = onClick)
      .padding(horizontal = 10.dp, vertical = 6.dp)
  ) {
    Box(
      modifier = Modifier
        .size(6.dp)
        .clip(CircleShape)
        .background(PrimaryRed)
    )
    Spacer(modifier = Modifier.width(6.dp))
    Text(
      text = chip.label,
      fontSize = 11.sp,
      fontWeight = FontWeight.Medium,
      color = TextPrimary
    )
  }
}
