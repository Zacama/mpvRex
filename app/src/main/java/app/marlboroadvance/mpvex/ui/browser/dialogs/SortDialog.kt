package app.marlboroadvance.mpvex.ui.browser.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ViewList
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AccountTree
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material.icons.filled.Title
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material.icons.filled.ViewModule
import androidx.compose.material3.AlertDialog
import androidx.compose.ui.res.stringResource
import app.marlboroadvance.mpvex.R
import app.marlboroadvance.mpvex.preferences.BrowserPreferences
import app.marlboroadvance.mpvex.preferences.AppearancePreferences
import app.marlboroadvance.mpvex.preferences.FolderSortType
import app.marlboroadvance.mpvex.preferences.VideoSortType
import app.marlboroadvance.mpvex.preferences.SortOrder
import app.marlboroadvance.mpvex.preferences.FolderViewMode
import app.marlboroadvance.mpvex.preferences.MediaLayoutMode
import app.marlboroadvance.mpvex.preferences.preference.collectAsState
import org.koin.compose.koinInject
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Switch
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ripple
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.SwitchDefaults
import androidx.compose.ui.draw.scale
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * Generic sort & view dialog. [sortType]/[types] are *localized labels* that
 * double as identity: callers map them back to their enums by comparing
 * strings. Translations of the sort type names (Title/Duration/Date/Size/
 * Count) must therefore stay distinct within each language — a collision
 * makes the affected option unselectable (no crash). See docs/i18n.md
 * ("Known limitations") for the typed-interface fix if this ever bites.
 */
@Composable
fun SortDialog(
  isOpen: Boolean,
  onDismiss: () -> Unit,
  title: String,
  sortType: String,
  onSortTypeChange: (String) -> Unit,
  sortOrderAsc: Boolean,
  onSortOrderChange: (Boolean) -> Unit,
  types: List<String>,
  icons: List<ImageVector>,
  getLabelForType: (String, Boolean) -> Pair<String, String>,
  modifier: Modifier = Modifier,
  visibilityToggles: List<VisibilityToggle> = emptyList(),
  viewModeSelector: MultiViewModeSelector? = null,
  layoutModeSelector:  ViewModeSelector? = null,
  folderGridColumnSelector: GridColumnSelector? = null,
  videoGridColumnSelector: GridColumnSelector? = null,
  showSortOptions: Boolean = true,
  enableViewModeOptions: Boolean = true,
  enableLayoutModeOptions: Boolean = true,
  contentToggles: List<ContentToggle> = emptyList(),
) {
  if (!isOpen) return

  val (ascLabel, descLabel) = getLabelForType(sortType, sortOrderAsc)

  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Text(
        text = title,
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.Medium,
        color = MaterialTheme.colorScheme.onSurface,
      )
    },
    text = {
      Column(
        modifier =
          Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp),
      ) {
        if (showSortOptions) {
          SortTypeSelector(
            sortType = sortType,
            onSortTypeChange = onSortTypeChange,
            types = types,
            icons = icons,
            sortOrderAsc = sortOrderAsc,
            onSortOrderChange = onSortOrderChange,
            ascLabel = ascLabel,
            descLabel = descLabel,
            modifier = Modifier.fillMaxWidth(),
          )
        }

        if (viewModeSelector != null || layoutModeSelector != null) {
          SectionCard {
            Row(
              modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min),
              horizontalArrangement = Arrangement.spacedBy(8.dp),
              verticalAlignment = Alignment.Top,
            ) {
              if (viewModeSelector != null) {
                MultiViewModeSelectorComponent(
                  selector = viewModeSelector,
                  enabled = enableViewModeOptions,
                  modifier = Modifier.weight(3f),
                )
              }

              if (viewModeSelector != null && layoutModeSelector != null) {
                VerticalDivider(
                  modifier = Modifier.padding(vertical = 8.dp),
                  color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)
                )
              }

              if (layoutModeSelector != null) {
                ViewModeSelectorComponent(
                  viewModeSelector = layoutModeSelector,
                  enabled = enableLayoutModeOptions,
                  modifier = Modifier.weight(2f),
                )
              }
            }
          }
        }

        GridColumnsSection(
          folderGridColumnSelector = folderGridColumnSelector,
          videoGridColumnSelector = videoGridColumnSelector,
        )

        if (contentToggles.isNotEmpty()) {
          ContentTogglesSection(
            toggles = contentToggles,
            modifier = Modifier.fillMaxWidth(),
          )
        }

        if (visibilityToggles.isNotEmpty()) {
          VisibilityTogglesSection(
            toggles = visibilityToggles,
            modifier = Modifier.fillMaxWidth(),
          )
        }
      }
    },
    confirmButton = {},
    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
    tonalElevation = 6.dp,
    shape = MaterialTheme.shapes.extraLarge,
    modifier = modifier,
  )
}

data class ContentToggle(
  val label: String,
  val checked: Boolean,
  val onCheckedChange: (Boolean) -> Unit,
)

data class VisibilityToggle(
  val label: String,
  val checked: Boolean,
  val onCheckedChange: (Boolean) -> Unit,
)

data class ViewModeSelector(
  val label: String,
  val firstOptionLabel: String,
  val secondOptionLabel: String,
  val firstOptionIcon: ImageVector,
  val secondOptionIcon: ImageVector,
  val isFirstOptionSelected: Boolean,
  val onViewModeChange: (Boolean) -> Unit,
)

data class ViewModeOption(
  val label: String,
  val icon: ImageVector,
  val isSelected: Boolean,
  val onClick: () -> Unit,
)

data class MultiViewModeSelector(
  val label: String,
  val options: List<ViewModeOption>,
)

data class GridColumnSelector(
  val label: String,
  val currentValue: Int,
  val onValueChange: (Int) -> Unit,
  val valueRange: ClosedFloatingPointRange<Float> = 1f..4f,
  val steps: Int = 2,
)

// -----------------------------------------------------------------------------
// Consolidated internal composable for sort UI (Material You styling)
// -----------------------------------------------------------------------------

@Composable
private fun SectionCard(
  modifier: Modifier = Modifier,
  content: @Composable ColumnScope.() -> Unit
) {
  Column(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(16.dp))
      .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
      .padding(12.dp),
    verticalArrangement = Arrangement.spacedBy(12.dp),
    content = content
  )
}

@Composable
private fun SortTypeSelector(
  sortType: String,
  onSortTypeChange: (String) -> Unit,
  types: List<String>,
  icons: List<ImageVector>,
  sortOrderAsc: Boolean,
  onSortOrderChange: (Boolean) -> Unit,
  ascLabel: String,
  descLabel: String,
  modifier: Modifier = Modifier,
) {
  SectionCard(modifier = modifier) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Text(
        text = stringResource(R.string.sort_dialog_sort_by),
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Medium,
        color = MaterialTheme.colorScheme.onSurface,
      )

      // Compact order toggle
      val activeLabel = if (sortOrderAsc) ascLabel else descLabel
      val icon = if (sortOrderAsc) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown
      
      Row(
        modifier = Modifier
          .clip(RoundedCornerShape(8.dp))
          .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.7f))
          .clickable { onSortOrderChange(!sortOrderAsc) }
          .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
      ) {
        Text(
          text = activeLabel,
          style = MaterialTheme.typography.labelMedium,
          color = MaterialTheme.colorScheme.onSecondaryContainer,
          fontWeight = FontWeight.Medium
        )
        Icon(
          imageVector = icon,
          contentDescription = stringResource(R.string.sort_dialog_toggle_sort_order),
          tint = MaterialTheme.colorScheme.onSecondaryContainer,
          modifier = Modifier.size(16.dp)
        )
      }
    }

    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceEvenly,
      verticalAlignment = Alignment.CenterVertically,
    ) {
      types.forEachIndexed { index, type ->
        val selected = sortType == type
        val shape = RoundedCornerShape(16.dp)

        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
          Box(
            modifier =
              Modifier
                .size(48.dp)
                .clip(shape)
                .background(
                  color =
                    if (selected) {
                      MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                    } else {
                      Color.Transparent
                    },
                )
                .then(
                  if (!selected) {
                    Modifier.background(
                      MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.3f),
                      shape
                    )
                  } else Modifier
                )
                .clickable(
                  onClick = { onSortTypeChange(type) },
                  interactionSource = remember { MutableInteractionSource() },
                  indication = ripple(bounded = true),
                ),
            contentAlignment = Alignment.Center,
          ) {
            Icon(
              imageVector = icons[index],
              contentDescription = type,
              tint =
                if (selected) {
                  MaterialTheme.colorScheme.primary
                } else {
                  MaterialTheme.colorScheme.onSurfaceVariant
                },
              modifier = Modifier.size(24.dp),
            )
          }

          Text(
            text = type,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            color =
              if (selected) {
                MaterialTheme.colorScheme.primary
              } else {
                MaterialTheme.colorScheme.onSurface
              },
          )
        }
      }
    }
  }
}

@Composable
private fun MultiViewModeSelectorComponent(
  selector: MultiViewModeSelector,
  enabled: Boolean = true,
  modifier: Modifier = Modifier,
) {
  Column(
    modifier = modifier,
    verticalArrangement = Arrangement.spacedBy(8.dp),
  ) {
    Text(
      text = selector.label,
      style = MaterialTheme.typography.titleMedium,
      fontWeight = FontWeight.Medium,
      color = if (enabled) {
        MaterialTheme.colorScheme.onSurface
      } else {
        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
      },
    )

    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceEvenly,
      verticalAlignment = Alignment.CenterVertically,
    ) {
      selector.options.forEach { option ->
        val selected = option.isSelected
        val shape = RoundedCornerShape(12.dp)

        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
          Box(
            modifier = Modifier
              .size(48.dp)
              .clip(shape)
              .background(
                color = if (selected && enabled) {
                  MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                } else if (enabled) {
                  MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.3f)
                } else {
                  MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.2f)
                },
              )
              .clickable(
                enabled = enabled,
                onClick = { option.onClick() },
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = true),
              ),
            contentAlignment = Alignment.Center,
          ) {
            Icon(
              imageVector = option.icon,
              contentDescription = null,
              modifier = Modifier.size(24.dp),
              tint = if (selected && enabled) {
                MaterialTheme.colorScheme.primary
              } else if (enabled) {
                MaterialTheme.colorScheme.onSurfaceVariant
              } else {
                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.38f)
              },
            )
          }

          Text(
            text = option.label,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = if (selected && enabled) FontWeight.Bold else FontWeight.Normal,
            color = if (selected && enabled) {
              MaterialTheme.colorScheme.primary
            } else if (enabled) {
              MaterialTheme.colorScheme.onSurface
            } else {
              MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
            },
          )
        }
      }
    }
  }
}

@Composable
private fun ViewModeSelectorComponent(
  viewModeSelector: ViewModeSelector,
  enabled: Boolean = true,
  modifier: Modifier = Modifier,
) {
  val options = listOf(viewModeSelector.firstOptionLabel, viewModeSelector.secondOptionLabel)
  val icons = listOf(viewModeSelector.firstOptionIcon, viewModeSelector.secondOptionIcon)
  val selectedIndex = if (viewModeSelector.isFirstOptionSelected) 0 else 1

  Column(
    modifier = modifier,
    verticalArrangement = Arrangement.spacedBy(8.dp),
  ) {
    Text(
      text = viewModeSelector.label,
      style = MaterialTheme.typography.titleMedium,
      fontWeight = FontWeight.Medium,
      color = if (enabled) {
        MaterialTheme.colorScheme.onSurface
      } else {
        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
      },
    )

    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceEvenly,
      verticalAlignment = Alignment.CenterVertically,
    ) {
      options.forEachIndexed { index, label ->
        val selected = index == selectedIndex
        val shape = RoundedCornerShape(12.dp)

        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
          Box(
            modifier = Modifier
              .size(48.dp)
              .clip(shape)
              .background(
                color = if (selected && enabled) {
                  MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                } else if (enabled) {
                  MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.3f)
                } else {
                  MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.2f)
                },
              )
              .clickable(
                enabled = enabled,
                onClick = { viewModeSelector.onViewModeChange(index == 0) },
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = true),
              ),
            contentAlignment = Alignment.Center,
          ) {
            Icon(
              imageVector = icons[index],
              contentDescription = label,
              tint = if (selected && enabled) {
                MaterialTheme.colorScheme.primary
              } else if (enabled) {
                MaterialTheme.colorScheme.onSurfaceVariant
              } else {
                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.38f)
              },
              modifier = Modifier.size(24.dp),
            )
          }

          Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = if (selected && enabled) FontWeight.Bold else FontWeight.Normal,
            color = if (selected && enabled) {
              MaterialTheme.colorScheme.primary
            } else if (enabled) {
              MaterialTheme.colorScheme.onSurface
            } else {
              MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
            },
          )
        }
      }
    }
  }
}

@Composable
private fun ContentTogglesSection(
  toggles: List<ContentToggle>,
  modifier: Modifier = Modifier,
) {
  SectionCard(modifier = modifier) {
    Text(
      text = stringResource(R.string.sort_dialog_filters),
      style = MaterialTheme.typography.titleMedium,
      fontWeight = FontWeight.Medium,
      color = MaterialTheme.colorScheme.onSurface,
    )

    Column(
      verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
      toggles.forEach { toggle ->
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable(
              interactionSource = remember { MutableInteractionSource() },
              indication = ripple(bounded = true),
              onClick = { toggle.onCheckedChange(!toggle.checked) },
            )
            .padding(vertical = 4.dp, horizontal = 4.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Text(
            text = toggle.label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
          )
          Switch(
            checked = toggle.checked,
            onCheckedChange = toggle.onCheckedChange,
            modifier = Modifier.scale(0.8f),
            thumbContent = {
              Crossfade(
                targetState = toggle.checked,
                animationSpec = tween(durationMillis = 200),
                label = "SwitchIconAnimation"
              ) { isChecked ->
                if (isChecked) {
                  Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = null,
                    modifier = Modifier.size(SwitchDefaults.IconSize),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                  )
                } else {
                  Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = null,
                    modifier = Modifier.size(SwitchDefaults.IconSize),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                }
              }
            }
          )
        }
      }
    }
  }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun VisibilityTogglesSection(
  toggles: List<VisibilityToggle>,
  modifier: Modifier = Modifier,
) {
  var expanded by remember { mutableStateOf(false) }

  Column(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(16.dp))
      .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
  ) {
    // Header row with Fields text and dropdown button
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .clickable(
          interactionSource = remember { MutableInteractionSource() },
          indication = ripple(bounded = true),
          onClick = { expanded = !expanded }
        )
        .padding(12.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Text(
        text = stringResource(R.string.sort_dialog_fields),
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Medium,
        color = MaterialTheme.colorScheme.onSurface,
      )

      Icon(
        imageVector = if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.ArrowDropDown,
        contentDescription = if (expanded) {
          stringResource(R.string.sort_dialog_collapse)
        } else {
          stringResource(R.string.sort_dialog_expand)
        },
        tint = MaterialTheme.colorScheme.onSurfaceVariant,
      )
    }

    // Expandable filter chips section
    if (expanded) {
      FlowRow(
        modifier = Modifier
          .fillMaxWidth()
          .padding(start = 12.dp, end = 12.dp, bottom = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
      ) {
        toggles.forEach { toggle ->
          FilterChip(
            selected = toggle.checked,
            onClick = { toggle.onCheckedChange(!toggle.checked) },
            label = {
              Text(
                text = toggle.label,
                style = MaterialTheme.typography.labelMedium,
              )
            },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
              selectedLabelColor = MaterialTheme.colorScheme.primary,
              containerColor = MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.5f),
              labelColor = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            border = null,
            leadingIcon = null,
          )
        }
      }
    }
  }
}

@Composable
private fun GridColumnSelectorComponent(
  gridColumnSelector: GridColumnSelector,
  modifier: Modifier = Modifier,
) {
  Column(
    modifier = modifier,
    verticalArrangement = Arrangement.spacedBy(12.dp),
  ) {
    Text(
      text = gridColumnSelector.label,
      style = MaterialTheme.typography.titleMedium,
      fontWeight = FontWeight.Medium,
      color = MaterialTheme.colorScheme.onSurface,
    )

    Slider(
      value = gridColumnSelector.currentValue.toFloat(),
      onValueChange = { gridColumnSelector.onValueChange(it.toInt()) },
      valueRange = gridColumnSelector.valueRange,
      steps = gridColumnSelector.steps,
      modifier = Modifier.fillMaxWidth(),
    )

    Text(
      text = stringResource(R.string.sort_dialog_columns_count, gridColumnSelector.currentValue),
      style = MaterialTheme.typography.bodyMedium,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
      modifier = Modifier.align(Alignment.CenterHorizontally),
    )
  }
}

@Composable
private fun GridColumnsSection(
  folderGridColumnSelector: GridColumnSelector?,
  videoGridColumnSelector: GridColumnSelector?,
  modifier: Modifier = Modifier,
) {
  if (folderGridColumnSelector == null && videoGridColumnSelector == null) return

  SectionCard(modifier = modifier) {
    Text(
      text = stringResource(R.string.sort_dialog_grid_columns),
      style = MaterialTheme.typography.titleMedium,
      fontWeight = FontWeight.Medium,
      color = MaterialTheme.colorScheme.onSurface,
    )

    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(16.dp),
      verticalAlignment = Alignment.Top,
    ) {
      if (folderGridColumnSelector != null) {
        Column(
          modifier = Modifier.weight(1f),
          verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
          Text(
            text = stringResource(R.string.sort_dialog_folder_grid),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
          )
          Slider(
            value = folderGridColumnSelector.currentValue.toFloat(),
            onValueChange = { folderGridColumnSelector.onValueChange(it.toInt()) },
            valueRange = folderGridColumnSelector.valueRange,
            steps = folderGridColumnSelector.steps,
            modifier = Modifier.fillMaxWidth(),
          )
          Text(
            text = stringResource(R.string.sort_dialog_columns_count, folderGridColumnSelector.currentValue),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.align(Alignment.CenterHorizontally),
          )
        }
      }

      if (videoGridColumnSelector != null) {
        Column(
          modifier = Modifier.weight(1f),
          verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
          Text(
            text = stringResource(R.string.sort_dialog_video_grid),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
          )
          Slider(
            value = videoGridColumnSelector.currentValue.toFloat(),
            onValueChange = { videoGridColumnSelector.onValueChange(it.toInt()) },
            valueRange = videoGridColumnSelector.valueRange,
            steps = videoGridColumnSelector.steps,
            modifier = Modifier.fillMaxWidth(),
          )
          Text(
            text = stringResource(R.string.sort_dialog_columns_count, videoGridColumnSelector.currentValue),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.align(Alignment.CenterHorizontally),
          )
        }
      }
    }
  }
}

/**
 * Ascending/descending labels keyed by the localized sort type name.
 * Shared by FolderSortType and VideoSortType since both use the same type names.
 */
@Composable
private fun sortOrderLabels(): Map<String, Pair<String, String>> =
  mapOf(
    stringResource(R.string.sort_type_title) to
      (stringResource(R.string.sort_order_a_z) to stringResource(R.string.sort_order_z_a)),
    stringResource(R.string.sort_type_duration) to
      (stringResource(R.string.sort_order_shortest) to stringResource(R.string.sort_order_longest)),
    stringResource(R.string.sort_type_date) to
      (stringResource(R.string.sort_order_oldest) to stringResource(R.string.sort_order_newest)),
    stringResource(R.string.sort_type_size) to
      (stringResource(R.string.sort_order_smallest) to stringResource(R.string.sort_order_largest)),
  )

@Composable
private fun defaultSortOrderLabels(): Pair<String, String> =
  stringResource(R.string.sort_order_asc) to stringResource(R.string.sort_order_desc)

@Composable
fun FolderSortDialog(
  isOpen: Boolean,
  onDismiss: () -> Unit,
  sortType: FolderSortType,
  sortOrder: SortOrder,
  onSortTypeChange: (FolderSortType) -> Unit,
  onSortOrderChange: (SortOrder) -> Unit,
) {
  val browserPreferences = koinInject<BrowserPreferences>()
  val appearancePreferences = koinInject<AppearancePreferences>()
  val showTotalVideosChip by browserPreferences.showTotalVideosChip.collectAsState()
  val showTotalDurationChip by browserPreferences.showTotalDurationChip.collectAsState()
  val showTotalSizeChip by browserPreferences.showTotalSizeChip.collectAsState()
  val showDateChip by browserPreferences.showDateChip.collectAsState()
  val showFolderPath by browserPreferences.showFolderPath.collectAsState()
  val showProgressBar by browserPreferences.showProgressBar.collectAsState()
  val showSubtitleIndicator by browserPreferences.showSubtitleIndicator.collectAsState()
  val showAudioFiles by browserPreferences.showAudioFiles.collectAsState()
  val unlimitedNameLines by appearancePreferences.unlimitedNameLines.collectAsState()
  val folderViewMode by browserPreferences.folderViewMode.collectAsState()
  val mediaLayoutMode by browserPreferences.mediaLayoutMode.collectAsState()
  val folderGridColumnsPortrait by browserPreferences.folderGridColumnsPortrait.collectAsState()
  val folderGridColumnsLandscape by browserPreferences.folderGridColumnsLandscape.collectAsState()
  val videoGridColumnsPortrait by browserPreferences.videoGridColumnsPortrait.collectAsState()
  val videoGridColumnsLandscape by browserPreferences.videoGridColumnsLandscape.collectAsState()

  val configuration = androidx.compose.ui.platform.LocalConfiguration.current
  val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE
  val orientationLabel = stringResource(
    if (isLandscape) R.string.generic_landscape else R.string.generic_portrait,
  )

  val folderGridColumns = if (isLandscape) folderGridColumnsLandscape else folderGridColumnsPortrait
  val videoGridColumns = if (isLandscape) videoGridColumnsLandscape else videoGridColumnsPortrait

  val folderGridColumnSelector = if (mediaLayoutMode == MediaLayoutMode.GRID) {
    GridColumnSelector(
      label = stringResource(R.string.sort_dialog_grid_columns_fmt, orientationLabel),
      currentValue = folderGridColumns,
      onValueChange = {
        if (isLandscape) browserPreferences.folderGridColumnsLandscape.set(it)
        else browserPreferences.folderGridColumnsPortrait.set(it)
      },
      valueRange = if (isLandscape) 3f..5f else 2f..4f,
      steps = if (isLandscape) 1 else 1,
    )
  } else null

  val videoGridColumnSelector = if (mediaLayoutMode == MediaLayoutMode.GRID) {
    GridColumnSelector(
      label = stringResource(R.string.sort_dialog_video_grid_columns_fmt, orientationLabel),
      currentValue = videoGridColumns,
      onValueChange = {
        if (isLandscape) browserPreferences.videoGridColumnsLandscape.set(it)
        else browserPreferences.videoGridColumnsPortrait.set(it)
      },
      valueRange = if (isLandscape) 3f..5f else 1f..3f,
      steps = if (isLandscape) 1 else 1,
    )
  } else null

  val isAlbumView = folderViewMode == FolderViewMode.AlbumView

  val typeNames = FolderSortType.entries.associateWith { stringResource(it.titleRes) }
  val orderLabels = sortOrderLabels()
  val defaultOrderLabels = defaultSortOrderLabels()

  SortDialog(
    isOpen = isOpen,
    onDismiss = onDismiss,
    title = if (isAlbumView) {
      stringResource(R.string.sort_dialog_title_sort_view)
    } else {
      stringResource(R.string.sort_dialog_title_view)
    },
    sortType = typeNames.getValue(sortType),
    onSortTypeChange = { typeName ->
      typeNames.entries
        .find { it.value == typeName }
        ?.key
        ?.let(onSortTypeChange)
    },
    sortOrderAsc = sortOrder.isAscending,
    onSortOrderChange = { isAsc ->
      onSortOrderChange(if (isAsc) SortOrder.Ascending else SortOrder.Descending)
    },
    types = listOf(
      typeNames.getValue(FolderSortType.Title),
      typeNames.getValue(FolderSortType.Duration),
      typeNames.getValue(FolderSortType.Date),
      typeNames.getValue(FolderSortType.Size),
    ),
    icons = listOf(
      Icons.Filled.Title,
      Icons.Filled.AccessTime,
      Icons.Filled.CalendarToday,
      Icons.Filled.SwapVert,
    ),
    getLabelForType = { type, _ -> orderLabels[type] ?: defaultOrderLabels },
    showSortOptions = isAlbumView,
    viewModeSelector = MultiViewModeSelector(
      label = stringResource(R.string.sort_dialog_view_mode),
      options = listOf(
        ViewModeOption(
          label = stringResource(R.string.sort_dialog_view_mode_folder),
          icon = Icons.Filled.ViewModule,
          isSelected = folderViewMode == FolderViewMode.AlbumView,
          onClick = { browserPreferences.folderViewMode.set(FolderViewMode.AlbumView) }
        ),
        ViewModeOption(
          label = stringResource(R.string.sort_dialog_view_mode_tree),
          icon = Icons.Filled.AccountTree,
          isSelected = folderViewMode == FolderViewMode.FileManager,
          onClick = { browserPreferences.folderViewMode.set(FolderViewMode.FileManager) }
        ),
        ViewModeOption(
          label = stringResource(R.string.sort_dialog_view_mode_library),
          icon = Icons.Filled.VideoLibrary,
          isSelected = folderViewMode == FolderViewMode.MediaLibrary,
          onClick = { browserPreferences.folderViewMode.set(FolderViewMode.MediaLibrary) }
        )
      )
    ),
    layoutModeSelector = ViewModeSelector(
      label = stringResource(R.string.sort_dialog_layout),
      firstOptionLabel = stringResource(R.string.sort_dialog_layout_list),
      secondOptionLabel = stringResource(R.string.sort_dialog_layout_grid),
      firstOptionIcon = Icons.AutoMirrored.Filled.ViewList,
      secondOptionIcon = Icons.Filled.GridView,
      isFirstOptionSelected = mediaLayoutMode == MediaLayoutMode.LIST,
      onViewModeChange = { isFirstOption ->
        browserPreferences.mediaLayoutMode.set(
          if (isFirstOption) MediaLayoutMode.LIST else MediaLayoutMode.GRID
        )
      },
    ),
    contentToggles = listOf(
      ContentToggle(
        label = stringResource(R.string.sort_dialog_toggle_audio_files),
        checked = showAudioFiles,
        onCheckedChange = { browserPreferences.showAudioFiles.set(it) },
      ),
    ),
    visibilityToggles = listOf(
      VisibilityToggle(
        label = stringResource(R.string.sort_dialog_toggle_full_name),
        checked = unlimitedNameLines,
        onCheckedChange = { appearancePreferences.unlimitedNameLines.set(it) },
      ),
      VisibilityToggle(
        label = stringResource(R.string.sort_dialog_toggle_path),
        checked = showFolderPath,
        onCheckedChange = { browserPreferences.showFolderPath.set(it) },
      ),
      VisibilityToggle(
        label = stringResource(R.string.sort_dialog_toggle_total_videos),
        checked = showTotalVideosChip,
        onCheckedChange = { browserPreferences.showTotalVideosChip.set(it) },
      ),
      VisibilityToggle(
        label = stringResource(R.string.sort_dialog_toggle_total_duration),
        checked = showTotalDurationChip,
        onCheckedChange = { browserPreferences.showTotalDurationChip.set(it) },
      ),
      VisibilityToggle(
        label = stringResource(R.string.sort_dialog_toggle_folder_size),
        checked = showTotalSizeChip,
        onCheckedChange = { browserPreferences.showTotalSizeChip.set(it) },
      ),
      VisibilityToggle(
        label = stringResource(R.string.sort_dialog_toggle_date),
        checked = showDateChip,
        onCheckedChange = { browserPreferences.showDateChip.set(it) },
      ),
      VisibilityToggle(
        label = stringResource(R.string.sort_dialog_toggle_progress_bar),
        checked = showProgressBar,
        onCheckedChange = { browserPreferences.showProgressBar.set(it) },
      ),
      VisibilityToggle(
        label = stringResource(R.string.sort_dialog_toggle_subtitle_indicator),
        checked = showSubtitleIndicator,
        onCheckedChange = { browserPreferences.showSubtitleIndicator.set(it) },
      ),
    ),
    folderGridColumnSelector = folderGridColumnSelector,
    videoGridColumnSelector = videoGridColumnSelector,
  )
}

@Composable
fun VideoSortDialog(
  isOpen: Boolean,
  onDismiss: () -> Unit,
  sortType: VideoSortType,
  sortOrder: SortOrder,
  onSortTypeChange: (VideoSortType) -> Unit,
  onSortOrderChange: (SortOrder) -> Unit,
) {
  val browserPreferences = koinInject<BrowserPreferences>()
  val videoGridColumnsPortrait by browserPreferences.videoGridColumnsPortrait.collectAsState()
  val videoGridColumnsLandscape by browserPreferences.videoGridColumnsLandscape.collectAsState()
  val folderGridColumnsPortrait by browserPreferences.folderGridColumnsPortrait.collectAsState()
  val folderGridColumnsLandscape by browserPreferences.folderGridColumnsLandscape.collectAsState()

  val configuration = androidx.compose.ui.platform.LocalConfiguration.current
  val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE
  val orientationLabel = stringResource(
    if (isLandscape) R.string.generic_landscape else R.string.generic_portrait,
  )

  val videoGridColumns = if (isLandscape) videoGridColumnsLandscape else videoGridColumnsPortrait
  val folderGridColumns = if (isLandscape) folderGridColumnsLandscape else folderGridColumnsPortrait
  val appearancePreferences = koinInject<AppearancePreferences>()
  val showAudioFiles by browserPreferences.showAudioFiles.collectAsState()
  val showThumbnails by browserPreferences.showVideoThumbnails.collectAsState()
  val showSizeChip by browserPreferences.showSizeChip.collectAsState()
  val showResolutionChip by browserPreferences.showResolutionChip.collectAsState()
  val showFramerateInResolution by browserPreferences.showFramerateInResolution.collectAsState()
  val showProgressBar by browserPreferences.showProgressBar.collectAsState()
  val showDateChip by browserPreferences.showDateChip.collectAsState()
  val showSubtitleIndicator by browserPreferences.showSubtitleIndicator.collectAsState()
  val unlimitedNameLines by appearancePreferences.unlimitedNameLines.collectAsState()
  val mediaLayoutMode by browserPreferences.mediaLayoutMode.collectAsState()
  val folderViewMode by browserPreferences.folderViewMode.collectAsState()

  val folderGridColumnSelector = if (mediaLayoutMode == MediaLayoutMode.GRID) {
    GridColumnSelector(
      label = stringResource(R.string.sort_dialog_folder_grid_columns_fmt, orientationLabel),
      currentValue = folderGridColumns,
      onValueChange = {
        if (isLandscape) browserPreferences.folderGridColumnsLandscape.set(it)
        else browserPreferences.folderGridColumnsPortrait.set(it)
      },
      valueRange = if (isLandscape) 3f..5f else 2f..4f,
      steps = if (isLandscape) 1 else 1,
    )
  } else null

  val videoGridColumnSelector = if (mediaLayoutMode == MediaLayoutMode.GRID) {
    GridColumnSelector(
      label = stringResource(R.string.sort_dialog_grid_columns_fmt, orientationLabel),
      currentValue = videoGridColumns,
      onValueChange = {
        if (isLandscape) browserPreferences.videoGridColumnsLandscape.set(it)
        else browserPreferences.videoGridColumnsPortrait.set(it)
      },
      valueRange = if (isLandscape) 3f..5f else 1f..3f,
      steps = if (isLandscape) 1 else 1,
    )
  } else null

  val typeNames = VideoSortType.entries.associateWith { stringResource(it.titleRes) }
  val orderLabels = sortOrderLabels()
  val defaultOrderLabels = defaultSortOrderLabels()

  SortDialog(
    isOpen = isOpen,
    onDismiss = onDismiss,
    title = stringResource(R.string.sort_dialog_title_sort_view),
    sortType = typeNames.getValue(sortType),
    onSortTypeChange = { typeName ->
      typeNames.entries.find { it.value == typeName }?.key?.let(onSortTypeChange)
    },
    sortOrderAsc = sortOrder.isAscending,
    onSortOrderChange = { isAsc ->
      onSortOrderChange(if (isAsc) SortOrder.Ascending else SortOrder.Descending)
    },
    types =
      listOf(
        typeNames.getValue(VideoSortType.Title),
        typeNames.getValue(VideoSortType.Duration),
        typeNames.getValue(VideoSortType.Date),
        typeNames.getValue(VideoSortType.Size),
      ),
    icons =
      listOf(
        Icons.Filled.Title,
        Icons.Filled.AccessTime,
        Icons.Filled.CalendarToday,
        Icons.Filled.SwapVert,
      ),
    getLabelForType = { type, _ -> orderLabels[type] ?: defaultOrderLabels },
    viewModeSelector = MultiViewModeSelector(
      label = stringResource(R.string.sort_dialog_view_mode),
      options = listOf(
        ViewModeOption(
          label = stringResource(R.string.sort_dialog_view_mode_folder),
          icon = Icons.Filled.ViewModule,
          isSelected = folderViewMode == FolderViewMode.AlbumView,
          onClick = { browserPreferences.folderViewMode.set(FolderViewMode.AlbumView) }
        ),
        ViewModeOption(
          label = stringResource(R.string.sort_dialog_view_mode_tree),
          icon = Icons.Filled.AccountTree,
          isSelected = folderViewMode == FolderViewMode.FileManager,
          onClick = { browserPreferences.folderViewMode.set(FolderViewMode.FileManager) }
        ),
        ViewModeOption(
          label = stringResource(R.string.sort_dialog_view_mode_library),
          icon = Icons.Filled.VideoLibrary,
          isSelected = folderViewMode == FolderViewMode.MediaLibrary,
          onClick = { browserPreferences.folderViewMode.set(FolderViewMode.MediaLibrary) }
        )
      )
    ),
    layoutModeSelector = ViewModeSelector(
      label = stringResource(R.string.sort_dialog_layout),
      firstOptionLabel = stringResource(R.string.sort_dialog_layout_list),
      secondOptionLabel = stringResource(R.string.sort_dialog_layout_grid),
      firstOptionIcon = Icons.AutoMirrored.Filled.ViewList,
      secondOptionIcon = Icons.Filled.GridView,
      isFirstOptionSelected = mediaLayoutMode == MediaLayoutMode.LIST,
      onViewModeChange = { isFirstOption ->
        browserPreferences.mediaLayoutMode.set(
          if (isFirstOption) MediaLayoutMode.LIST else MediaLayoutMode.GRID
        )
      },
    ),
    contentToggles = listOf(
      ContentToggle(
        label = stringResource(R.string.sort_dialog_toggle_audio_files),
        checked = showAudioFiles,
        onCheckedChange = { browserPreferences.showAudioFiles.set(it) },
      ),
    ),
    visibilityToggles =
      listOf(
        VisibilityToggle(
          label = stringResource(R.string.sort_dialog_toggle_thumbnails),
          checked = showThumbnails,
          onCheckedChange = { browserPreferences.showVideoThumbnails.set(it) },
        ),
        VisibilityToggle(
          label = stringResource(R.string.sort_dialog_toggle_subtitle_indicator),
          checked = showSubtitleIndicator,
          onCheckedChange = { browserPreferences.showSubtitleIndicator.set(it) },
        ),
        VisibilityToggle(
          label = stringResource(R.string.sort_dialog_toggle_full_name),
          checked = unlimitedNameLines,
          onCheckedChange = { appearancePreferences.unlimitedNameLines.set(it) },
        ),
        VisibilityToggle(
          label = stringResource(R.string.sort_dialog_toggle_size),
          checked = showSizeChip,
          onCheckedChange = { browserPreferences.showSizeChip.set(it) },
        ),
        VisibilityToggle(
          label = stringResource(R.string.sort_dialog_toggle_resolution),
          checked = showResolutionChip,
          onCheckedChange = { browserPreferences.showResolutionChip.set(it) },
        ),
        VisibilityToggle(
          label = stringResource(R.string.sort_dialog_toggle_framerate),
          checked = showFramerateInResolution,
          onCheckedChange = { browserPreferences.showFramerateInResolution.set(it) },
        ),
        VisibilityToggle(
          label = stringResource(R.string.sort_dialog_toggle_date),
          checked = showDateChip,
          onCheckedChange = { browserPreferences.showDateChip.set(it) },
        ),
        VisibilityToggle(
          label = stringResource(R.string.sort_dialog_toggle_progress_bar),
          checked = showProgressBar,
          onCheckedChange = { browserPreferences.showProgressBar.set(it) },
        ),
      ),
    folderGridColumnSelector = folderGridColumnSelector,
    videoGridColumnSelector = videoGridColumnSelector,
  )
}

@Composable
fun FileSystemSortDialog(
  isOpen: Boolean,
  onDismiss: () -> Unit,
  isAtRoot: Boolean = true,
) {
  val browserPreferences = koinInject<BrowserPreferences>()
  val appearancePreferences = koinInject<AppearancePreferences>()
  val folderViewMode by browserPreferences.folderViewMode.collectAsState()
  val folderSortType by browserPreferences.folderSortType.collectAsState()
  val folderSortOrder by browserPreferences.folderSortOrder.collectAsState()
  val showVideoThumbnails by browserPreferences.showVideoThumbnails.collectAsState()
  val showTotalVideosChip by browserPreferences.showTotalVideosChip.collectAsState()
  val showTotalSizeChip by browserPreferences.showTotalSizeChip.collectAsState()
  val showFolderPath by browserPreferences.showFolderPath.collectAsState()
  val showSizeChip by browserPreferences.showSizeChip.collectAsState()
  val showResolutionChip by browserPreferences.showResolutionChip.collectAsState()
  val showFramerateInResolution by browserPreferences.showFramerateInResolution.collectAsState()
  val showProgressBar by browserPreferences.showProgressBar.collectAsState()
  val showSubtitleIndicator by browserPreferences.showSubtitleIndicator.collectAsState()
  val unlimitedNameLines by appearancePreferences.unlimitedNameLines.collectAsState()
  val showAudioFiles by browserPreferences.showAudioFiles.collectAsState()
  val mediaLayoutMode by browserPreferences.mediaLayoutMode.collectAsState()
  val folderGridColumnsPortrait by browserPreferences.folderGridColumnsPortrait.collectAsState()
  val folderGridColumnsLandscape by browserPreferences.folderGridColumnsLandscape.collectAsState()
  val videoGridColumnsPortrait by browserPreferences.videoGridColumnsPortrait.collectAsState()
  val videoGridColumnsLandscape by browserPreferences.videoGridColumnsLandscape.collectAsState()

  val configuration = androidx.compose.ui.platform.LocalConfiguration.current
  val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE
  val orientationLabel = stringResource(
    if (isLandscape) R.string.generic_landscape else R.string.generic_portrait,
  )
  val folderGridColumns = if (isLandscape) folderGridColumnsLandscape else folderGridColumnsPortrait
  val videoGridColumns = if (isLandscape) videoGridColumnsLandscape else videoGridColumnsPortrait

  val folderGridColumnSelector = if (mediaLayoutMode == MediaLayoutMode.GRID) {
    GridColumnSelector(
      label = stringResource(R.string.sort_dialog_grid_columns_fmt, orientationLabel),
      currentValue = folderGridColumns,
      onValueChange = {
        if (isLandscape) browserPreferences.folderGridColumnsLandscape.set(it)
        else browserPreferences.folderGridColumnsPortrait.set(it)
      },
      valueRange = if (isLandscape) 3f..5f else 2f..4f,
      steps = if (isLandscape) 1 else 1,
    )
  } else null

  val videoGridColumnSelector = if (mediaLayoutMode == MediaLayoutMode.GRID) {
    GridColumnSelector(
      label = stringResource(R.string.sort_dialog_video_grid_columns_fmt, orientationLabel),
      currentValue = videoGridColumns,
      onValueChange = {
        if (isLandscape) browserPreferences.videoGridColumnsLandscape.set(it)
        else browserPreferences.videoGridColumnsPortrait.set(it)
      },
      valueRange = if (isLandscape) 3f..5f else 1f..3f,
      steps = if (isLandscape) 1 else 1,
    )
  } else null

  val typeNames = FolderSortType.entries.associateWith { stringResource(it.titleRes) }
  val orderLabels = sortOrderLabels()
  val defaultOrderLabels = defaultSortOrderLabels()

  SortDialog(
    isOpen = isOpen,
    onDismiss = onDismiss,
    title = stringResource(R.string.sort_dialog_title_sort_view),
    sortType = typeNames.getValue(folderSortType),
    onSortTypeChange = { typeName ->
      typeNames.entries.find { it.value == typeName }?.key?.let {
        browserPreferences.folderSortType.set(it)
      }
    },
    sortOrderAsc = folderSortOrder.isAscending,
    onSortOrderChange = { isAsc ->
      browserPreferences.folderSortOrder.set(
        if (isAsc) SortOrder.Ascending
        else SortOrder.Descending,
      )
    },
    types = listOf(
      typeNames.getValue(FolderSortType.Title),
      typeNames.getValue(FolderSortType.Duration),
      typeNames.getValue(FolderSortType.Date),
      typeNames.getValue(FolderSortType.Size),
    ),
    icons = listOf(
      Icons.Filled.Title,
      Icons.Filled.AccessTime,
      Icons.Filled.CalendarToday,
      Icons.Filled.SwapVert,
    ),
    getLabelForType = { type, _ -> orderLabels[type] ?: defaultOrderLabels },
    showSortOptions = true,
    viewModeSelector = MultiViewModeSelector(
      label = stringResource(R.string.sort_dialog_view_mode),
      options = listOf(
        ViewModeOption(
          label = stringResource(R.string.sort_dialog_view_mode_folder),
          icon = Icons.Filled.ViewModule,
          isSelected = folderViewMode == FolderViewMode.AlbumView,
          onClick = { browserPreferences.folderViewMode.set(FolderViewMode.AlbumView) }
        ),
        ViewModeOption(
          label = stringResource(R.string.sort_dialog_view_mode_tree),
          icon = Icons.Filled.AccountTree,
          isSelected = folderViewMode == FolderViewMode.FileManager,
          onClick = { browserPreferences.folderViewMode.set(FolderViewMode.FileManager) }
        ),
        ViewModeOption(
          label = stringResource(R.string.sort_dialog_view_mode_library),
          icon = Icons.Filled.VideoLibrary,
          isSelected = folderViewMode == FolderViewMode.MediaLibrary,
          onClick = { browserPreferences.folderViewMode.set(FolderViewMode.MediaLibrary) }
        )
      )
    ),
    layoutModeSelector = ViewModeSelector(
      label = stringResource(R.string.sort_dialog_layout),
      firstOptionLabel = stringResource(R.string.sort_dialog_layout_list),
      secondOptionLabel = stringResource(R.string.sort_dialog_layout_grid),
      firstOptionIcon = Icons.AutoMirrored.Filled.ViewList,
      secondOptionIcon = Icons.Filled.GridView,
      isFirstOptionSelected = mediaLayoutMode == MediaLayoutMode.LIST,
      onViewModeChange = { isFirstOption ->
        browserPreferences.mediaLayoutMode.set(
          if (isFirstOption) MediaLayoutMode.LIST
          else MediaLayoutMode.GRID
        )
      },
    ),
    folderGridColumnSelector = folderGridColumnSelector,
    videoGridColumnSelector = videoGridColumnSelector,
    enableViewModeOptions = isAtRoot,
    enableLayoutModeOptions = true, // Enabled for FileSystem/Tree view too!
    contentToggles = listOf(
      ContentToggle(
        label = stringResource(R.string.sort_dialog_toggle_audio_files),
        checked = showAudioFiles,
        onCheckedChange = { browserPreferences.showAudioFiles.set(it) },
      ),
    ),
    visibilityToggles = listOf(
      VisibilityToggle(
        label = stringResource(R.string.sort_dialog_toggle_video_thumbnails),
        checked = showVideoThumbnails,
        onCheckedChange = { browserPreferences.showVideoThumbnails.set(it) },
      ),
      VisibilityToggle(
        label = stringResource(R.string.sort_dialog_toggle_full_name),
        checked = unlimitedNameLines,
        onCheckedChange = { appearancePreferences.unlimitedNameLines.set(it) },
      ),
      VisibilityToggle(
        label = stringResource(R.string.sort_dialog_toggle_path),
        checked = showFolderPath,
        onCheckedChange = { browserPreferences.showFolderPath.set(it) },
      ),
      VisibilityToggle(
        label = stringResource(R.string.sort_dialog_toggle_total_videos),
        checked = showTotalVideosChip,
        onCheckedChange = { browserPreferences.showTotalVideosChip.set(it) },
      ),
      VisibilityToggle(
        label = stringResource(R.string.sort_dialog_toggle_folder_size),
        checked = showTotalSizeChip,
        onCheckedChange = { browserPreferences.showTotalSizeChip.set(it) },
      ),
      VisibilityToggle(
        label = stringResource(R.string.sort_dialog_toggle_size),
        checked = showSizeChip,
        onCheckedChange = { browserPreferences.showSizeChip.set(it) },
      ),
      VisibilityToggle(
        label = stringResource(R.string.sort_dialog_toggle_resolution),
        checked = showResolutionChip,
        onCheckedChange = { browserPreferences.showResolutionChip.set(it) },
      ),
      VisibilityToggle(
        label = stringResource(R.string.sort_dialog_toggle_framerate),
        checked = showFramerateInResolution,
        onCheckedChange = { browserPreferences.showFramerateInResolution.set(it) },
      ),
      VisibilityToggle(
        label = stringResource(R.string.sort_dialog_toggle_subtitle),
        checked = showSubtitleIndicator,
        onCheckedChange = { browserPreferences.showSubtitleIndicator.set(it) },
      ),
      VisibilityToggle(
        label = stringResource(R.string.sort_dialog_toggle_progress_bar),
        checked = showProgressBar,
        onCheckedChange = { browserPreferences.showProgressBar.set(it) },
      ),
    )
  )
}

