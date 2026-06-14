package app.marlboroadvance.mpvex.ui.preferences

import android.app.Activity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Block
import androidx.compose.material.icons.outlined.HorizontalRule
import androidx.compose.material.icons.outlined.Repeat
import androidx.compose.material.icons.outlined.Shuffle
import androidx.compose.material.icons.outlined.VideoLibrary
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import app.marlboroadvance.mpvex.R
import app.marlboroadvance.mpvex.preferences.BrowserPreferences
import app.marlboroadvance.mpvex.preferences.preference.collectAsState
import app.marlboroadvance.mpvex.presentation.Screen
import app.marlboroadvance.mpvex.ui.utils.LocalBackStack
import kotlinx.serialization.Serializable
import me.zhanghai.compose.preference.Preference
import me.zhanghai.compose.preference.ProvidePreferenceLocals
import me.zhanghai.compose.preference.SliderPreference
import app.marlboroadvance.mpvex.ui.preferences.components.SwitchPreference
import org.koin.compose.koinInject
import kotlin.math.roundToInt

@Serializable
object ShortsPreferencesScreen : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val backstack = LocalBackStack.current
        val browserPreferences = koinInject<BrowserPreferences>()
        val enableShorts by browserPreferences.enableShorts.collectAsState()
        val autoSwipeShorts by browserPreferences.autoSwipeShorts.collectAsState()
        val includeHorizontal by browserPreferences.includeShortHorizontalVideos.collectAsState()
        val maxDuration by browserPreferences.maxHorizontalVideoDurationMinutes.collectAsState()

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = stringResource(R.string.pref_shorts_title),
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary,
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = backstack::removeLastOrNull) {
                            Icon(
                                Icons.AutoMirrored.Outlined.ArrowBack,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.secondary,
                            )
                        }
                    },
                )
            },
        ) { padding ->
            ProvidePreferenceLocals {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                ) {
                    item {
                        PreferenceSectionHeader(title = stringResource(R.string.pref_section_general))
                    }

                    item {
                        PreferenceCard {
                            SwitchPreference(
                                value = enableShorts,
                                onValueChange = { browserPreferences.enableShorts.set(it) },
                                title = { Text(stringResource(R.string.pref_shorts_enable_title)) },
                                summary = { Text(stringResource(R.string.pref_shorts_enable_summary)) },
                                icon = {
                                    Icon(
                                        Icons.Outlined.VideoLibrary,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            )

                            PreferenceDivider()

                            SwitchPreference(
                                value = autoSwipeShorts,
                                onValueChange = { browserPreferences.autoSwipeShorts.set(it) },
                                title = { Text(stringResource(R.string.pref_shorts_auto_swipe_title)) },
                                summary = { Text(stringResource(R.string.pref_shorts_auto_swipe_summary)) },
                                icon = {
                                    Icon(
                                        Icons.Outlined.Repeat,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            )
                        }
                    }

                    item {
                        PreferenceSectionHeader(title = stringResource(R.string.pref_shorts_section_discovery))
                    }

                    item {
                        PreferenceCard {
                            SwitchPreference(
                                value = includeHorizontal,
                                onValueChange = { browserPreferences.includeShortHorizontalVideos.set(it) },
                                title = { Text(stringResource(R.string.pref_shorts_include_horizontal_title)) },
                                summary = { Text(stringResource(R.string.pref_shorts_include_horizontal_summary)) },
                                icon = {
                                    Icon(
                                        Icons.Outlined.HorizontalRule,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            )

                            PreferenceDivider()

                            SliderPreference(
                                value = maxDuration.toFloat(),
                                onValueChange = { browserPreferences.maxHorizontalVideoDurationMinutes.set(it.roundToInt()) },
                                sliderValue = maxDuration.toFloat(),
                                onSliderValueChange = { browserPreferences.maxHorizontalVideoDurationMinutes.set(it.roundToInt()) },
                                title = { Text(stringResource(R.string.pref_shorts_max_horizontal_title)) },
                                summary = { 
                                    Text(
                                        text = pluralStringResource(R.plurals.pref_shorts_max_horizontal_summary, maxDuration, maxDuration),
                                        color = MaterialTheme.colorScheme.outline
                                    ) 
                                },
                                valueRange = 1f..10f,
                                valueSteps = 9,
                                enabled = includeHorizontal,
                                icon = {
                                    // Empty icon for alignment if needed or specific icon
                                }
                            )
                        }
                    }

                    item {
                        PreferenceSectionHeader(title = stringResource(R.string.pref_shorts_section_content))
                    }

                    item {
                        PreferenceCard {
                            Preference(
                                title = { Text(stringResource(R.string.pref_shorts_blocked_title)) },
                                summary = { 
                                    Text(
                                        text = stringResource(R.string.pref_shorts_blocked_summary),
                                        color = MaterialTheme.colorScheme.outline
                                    ) 
                                },
                                icon = {
                                    Icon(
                                        Icons.Outlined.Block,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                },
                                onClick = { backstack.add(BlockedShortsScreen) }
                            )
                        }
                    }
                }
            }
        }
    }
}
