package app.marlboroadvance.mpvex.preferences

import androidx.compose.ui.res.stringResource
import app.marlboroadvance.mpvex.R
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.Segment
import androidx.compose.material.icons.outlined.AspectRatio
import androidx.compose.material.icons.outlined.Audiotrack
import androidx.compose.material.icons.outlined.Bookmarks
import androidx.compose.material.icons.outlined.Camera
import androidx.compose.material.icons.outlined.LockOpen
import androidx.compose.material.icons.outlined.Memory
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.PictureInPictureAlt
import androidx.compose.material.icons.outlined.ScreenRotation
import androidx.compose.material.icons.outlined.Speed
import androidx.compose.material.icons.outlined.Subtitles
import androidx.compose.material.icons.outlined.Title
import androidx.compose.material.icons.outlined.Flip
import androidx.compose.material.icons.outlined.Repeat
import androidx.compose.material.icons.outlined.Segment
import androidx.compose.material.icons.outlined.ZoomIn
import androidx.compose.material.icons.outlined.FastForward
import androidx.compose.material.icons.outlined.Shuffle
import androidx.compose.material.icons.outlined.SwapVert
import androidx.compose.material.icons.outlined.PlayCircle
import androidx.compose.material.icons.outlined.Headset
import androidx.compose.material.icons.outlined.BlurOn
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Represents a customizable button in the player controls.
 * Now includes an icon for the preference UI.
 */
enum class PlayerButton(
  val icon: ImageVector,
) {
  BACK_ARROW(Icons.AutoMirrored.Outlined.ArrowBack),
  VIDEO_TITLE(Icons.Outlined.Title),
  BOOKMARKS_CHAPTERS(Icons.Outlined.Bookmarks),
  PLAYBACK_SPEED(Icons.Outlined.Speed),
  DECODER(Icons.Outlined.Memory),
  SCREEN_ROTATION(Icons.Outlined.ScreenRotation),
  FRAME_NAVIGATION(Icons.Outlined.Camera),
  VIDEO_ZOOM(Icons.Outlined.ZoomIn),
  PICTURE_IN_PICTURE(Icons.Outlined.PictureInPictureAlt),
  ASPECT_RATIO(Icons.Outlined.AspectRatio),
  LOCK_CONTROLS(Icons.Outlined.LockOpen),
  AUDIO_TRACK(Icons.Outlined.Audiotrack),
  SUBTITLES(Icons.Outlined.Subtitles),
  MORE_OPTIONS(Icons.Outlined.MoreVert),
  CURRENT_CHAPTER(Icons.Outlined.Bookmarks), // <-- CHANGED ICON
  REPEAT_MODE(Icons.Outlined.Repeat),
  SHUFFLE(Icons.Outlined.Shuffle),
  MIRROR(Icons.Outlined.Flip),
  VERTICAL_FLIP(Icons.Outlined.Flip),
  AB_LOOP(Icons.AutoMirrored.Outlined.Segment),
  CUSTOM_SKIP(Icons.Outlined.FastForward),
  BACKGROUND_PLAYBACK(Icons.Outlined.Headset),
  AMBIENT_MODE(Icons.Outlined.BlurOn),
  NONE(Icons.Outlined.Bookmarks),
}

/**
 * A list of all buttons that the user can choose from in the customization menu.
 * Excludes NONE (placeholder) and constant buttons (BACK_ARROW, VIDEO_TITLE).
 */
val allPlayerButtons =
  PlayerButton.values().filter {
    it != PlayerButton.NONE &&
      it != PlayerButton.BACK_ARROW &&
      it != PlayerButton.VIDEO_TITLE
  }

/**
 * Gets the human-readable label for a player button.
 */
@Composable
fun getPlayerButtonLabel(button: PlayerButton): String =
  stringResource(
    when (button) {
      PlayerButton.BACK_ARROW -> R.string.btn_label_back
      PlayerButton.VIDEO_TITLE -> R.string.btn_label_title
      PlayerButton.BOOKMARKS_CHAPTERS -> R.string.btn_label_bookmarks
      PlayerButton.PLAYBACK_SPEED -> R.string.btn_label_speed
      PlayerButton.DECODER -> R.string.btn_label_decoder
      PlayerButton.SCREEN_ROTATION -> R.string.btn_label_rotation
      PlayerButton.FRAME_NAVIGATION -> R.string.btn_label_frame_nav
      PlayerButton.VIDEO_ZOOM -> R.string.btn_label_zoom
      PlayerButton.PICTURE_IN_PICTURE -> R.string.btn_label_pip
      PlayerButton.ASPECT_RATIO -> R.string.btn_label_aspect
      PlayerButton.LOCK_CONTROLS -> R.string.btn_label_lock
      PlayerButton.AUDIO_TRACK -> R.string.btn_label_audio
      PlayerButton.SUBTITLES -> R.string.btn_label_subtitles
      PlayerButton.MORE_OPTIONS -> R.string.btn_label_more
      PlayerButton.CURRENT_CHAPTER -> R.string.btn_label_chapter
      PlayerButton.REPEAT_MODE -> R.string.btn_label_repeat_mode
      PlayerButton.SHUFFLE -> R.string.btn_label_shuffle
      PlayerButton.MIRROR -> R.string.btn_label_mirror
      PlayerButton.VERTICAL_FLIP -> R.string.btn_label_vertical_flip
      PlayerButton.AB_LOOP -> R.string.btn_label_ab_loop
      PlayerButton.CUSTOM_SKIP -> R.string.btn_label_custom_skip
      PlayerButton.BACKGROUND_PLAYBACK -> R.string.btn_label_background
      PlayerButton.AMBIENT_MODE -> R.string.btn_label_ambient
      PlayerButton.NONE -> R.string.btn_label_none
    },
  )
