/*
 * Copyright 2026 Silva.
 * https://github.com/SilvaApp/silva-manager
 */

package app.silva.manager.ui.screen.shared

import android.annotation.SuppressLint
import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material.icons.outlined.FolderOff
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import app.silva.manager.R

// Constants
private object SilvaDefaults {
    val CardElevation = 2.dp
    val CardCornerRadius = 16.dp
    val SettingsCornerRadius = 14.dp
    val SectionCornerRadius = 18.dp
    val IconSize = 24.dp
    const val ANIMATION_DURATION = 300
    val ContentPadding = 16.dp
    val ItemSpacing = 12.dp
}

/**
 * Elevated card with proper Material 3 theming
 * Base card for all other card types
 */
@Composable
fun SilvaCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    elevation: Dp = SilvaDefaults.CardElevation,
    cornerRadius: Dp = SilvaDefaults.CardCornerRadius,
    borderWidth: Dp = 0.dp,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(cornerRadius))
            .then(
                if (onClick != null) {
                    Modifier.clickable(enabled = enabled, onClick = onClick)
                } else Modifier
            ),
        shape = RoundedCornerShape(cornerRadius),
        color = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp),
        contentColor = MaterialTheme.colorScheme.onSurface,
        tonalElevation = elevation,
        border = if (borderWidth > 0.dp) {
            BorderStroke(borderWidth, MaterialTheme.colorScheme.outlineVariant)
        } else null
    ) {
        content()
    }
}

/**
 * Horizontal divider for settings sections
 */
@Composable
fun SilvaSettingsDivider(
    modifier: Modifier = Modifier,
    fullWidth: Boolean = false
) {
    HorizontalDivider(
        modifier = if (fullWidth) modifier else modifier.padding(horizontal = SilvaDefaults.ContentPadding),
        color = lerp(
            MaterialTheme.colorScheme.outlineVariant,
            MaterialTheme.colorScheme.surfaceTint,
            0.18f
        ).copy(alpha = 0.55f)
    )
}

/**
 * Reusable icon component with standard styling
 */
@Composable
fun SilvaIcon(
    icon: ImageVector,
    modifier: Modifier = Modifier,
    size: Dp = SilvaDefaults.IconSize,
    tint: Color = MaterialTheme.colorScheme.primary,
    contentDescription: String? = null
) {
    Icon(
        imageVector = icon,
        contentDescription = contentDescription,
        tint = tint,
        modifier = modifier.size(size)
    )
}

/**
 * Circular icon with gradient background for section titles
 */
@Composable
fun GradientCircleIcon(
    icon: ImageVector,
    modifier: Modifier = Modifier,
    size: Dp = 40.dp,
    iconSize: Dp = SilvaDefaults.IconSize,
    contentDescription: String? = null,
    gradientColors: List<Color> = listOf(Color(0xFF1E5AA8), Color(0xFF00AFAE))
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(brush = Brush.linearGradient(colors = gradientColors)),
        contentAlignment = Alignment.Center
    ) {
        SilvaIcon(
            icon = icon,
            contentDescription = contentDescription,
            tint = Color.White,
            size = iconSize
        )
    }
}

/**
 * Row with optional icon and text content
 */
@Composable
fun IconTextRow(
    modifier: Modifier = Modifier,
    leadingContent: @Composable (() -> Unit)? = null,
    title: String,
    description: String? = null,
    titleStyle: TextStyle = MaterialTheme.typography.bodyMedium,
    titleWeight: FontWeight = FontWeight.Medium,
    descriptionStyle: TextStyle = MaterialTheme.typography.bodySmall,
    trailingContent: @Composable (() -> Unit)? = null,
    spacing: Dp = SilvaDefaults.ItemSpacing
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(spacing),
        verticalAlignment = Alignment.CenterVertically
    ) {
        leadingContent?.invoke()

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = titleStyle,
                fontWeight = titleWeight,
                color = MaterialTheme.colorScheme.onSurface
            )
            description?.let {
                Text(
                    text = it,
                    style = descriptionStyle,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        trailingContent?.invoke()
    }
}

/**
 * Settings item card wrapper
 * Private component used by settings item variants
 */
@Composable
fun SettingsItemCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    borderWidth: Dp = 0.dp,
    content: @Composable () -> Unit
) {
    SilvaCard(
        onClick = onClick,
        enabled = enabled,
        elevation = 1.dp,
        cornerRadius = SilvaDefaults.SettingsCornerRadius,
        borderWidth = borderWidth,
        modifier = modifier
    ) {
        content()
    }
}

/**
 * Base settings item component
 * Shared implementation for SettingsItem and RichSettingsItem
 */
@Composable
fun BaseSettingsItem(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    showBorder: Boolean = false,
    leadingContent: @Composable () -> Unit,
    title: String,
    description: String? = null,
    trailingContent: @Composable (() -> Unit)? = {
        SilvaIcon(icon = Icons.Outlined.ChevronRight)
    }
) {
    SettingsItemCard(
        onClick = onClick,
        borderWidth = if (showBorder) 1.dp else 0.dp,
        modifier = modifier
    ) {
        IconTextRow(
            modifier = Modifier.padding(SilvaDefaults.ContentPadding),
            leadingContent = leadingContent,
            title = title,
            description = description,
            trailingContent = trailingContent
        )
    }
}

/**
 * Simple settings item with icon, title, and action
 */
@Composable
fun SettingsItem(
    icon: ImageVector,
    title: String,
    description: String? = null,
    onClick: () -> Unit,
    @SuppressLint("ModifierParameter")
    modifier: Modifier = Modifier,
    showBorder: Boolean = false
) {
    BaseSettingsItem(
        onClick = onClick,
        modifier = modifier,
        showBorder = showBorder,
        leadingContent = { SilvaIcon(icon = icon) },
        title = title,
        description = description
    )
}

/**
 * Rich settings item with custom leading content
 */
@Composable
fun RichSettingsItem(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    showBorder: Boolean = false,
    leadingContent: @Composable (() -> Unit) = {},
    title: String,
    subtitle: String? = null,
    trailingContent: @Composable (() -> Unit)? = {
        SilvaIcon(icon = Icons.Outlined.ChevronRight)
    }
) {
    BaseSettingsItem(
        onClick = onClick,
        modifier = modifier,
        showBorder = showBorder,
        leadingContent = leadingContent,
        title = title,
        description = subtitle,
        trailingContent = trailingContent
    )
}

/**
 * Section container card
 */
@Composable
fun SectionCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    SilvaCard(
        onClick = onClick,
        elevation = SilvaDefaults.CardElevation,
        cornerRadius = SilvaDefaults.SectionCornerRadius,
        borderWidth = 1.dp,
        modifier = modifier
    ) {
        content()
    }
}

/**
 * Section title with gradient icon
 */
@Composable
fun SectionTitle(
    text: String,
    icon: ImageVector? = null
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(SilvaDefaults.ItemSpacing),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icon != null) {
            GradientCircleIcon(
                icon = icon,
                size = 36.dp,
                iconSize = 20.dp
            )
        }
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

/**
 * Card header with icon and text
 */
@Composable
fun CardHeader(
    icon: ImageVector,
    title: String,
    description: String? = null,
    @SuppressLint("ModifierParameter")
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            shape = RoundedCornerShape(topStart = SilvaDefaults.SectionCornerRadius, topEnd = SilvaDefaults.SectionCornerRadius)
        ) {
            IconTextRow(
                modifier = Modifier.padding(SilvaDefaults.ContentPadding),
                leadingContent = { SilvaIcon(icon = icon) },
                title = title,
                description = description
            )
        }

        SilvaSettingsDivider(fullWidth = true)
    }
}

/**
 * Expandable section with animated header and content
 */
@Composable
fun ExpandableSection(
    icon: ImageVector? = null,
    title: String,
    description: String,
    expanded: Boolean,
    onExpandChange: (Boolean) -> Unit,
    @SuppressLint("ModifierParameter")
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val rotationAngle by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        animationSpec = tween(SilvaDefaults.ANIMATION_DURATION),
        label = "expand_rotation"
    )

    SilvaCard(modifier = modifier) {
        Column {
            // Header
            IconTextRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onExpandChange(!expanded) }
                    .padding(SilvaDefaults.ContentPadding),
                leadingContent = {
                    if (icon != null) {
                        SilvaIcon(icon = icon)
                    }
                },
                title = title,
                description = description,
                trailingContent = {
                    SilvaIcon(
                        icon = Icons.Outlined.ExpandMore,
                        contentDescription = if (expanded)
                            stringResource(R.string.collapse)
                        else
                            stringResource(R.string.expand),
                        modifier = Modifier.rotate(rotationAngle)
                    )
                }
            )

            // Content
            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically(tween(SilvaDefaults.ANIMATION_DURATION)) +
                        fadeIn(tween(SilvaDefaults.ANIMATION_DURATION)),
                exit = shrinkVertically(tween(SilvaDefaults.ANIMATION_DURATION)) +
                        fadeOut(tween(SilvaDefaults.ANIMATION_DURATION))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = SilvaDefaults.ContentPadding, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(SilvaDefaults.ContentPadding)
                ) {
                    content()
                }
            }
        }
    }
}

/**
 * A single item in a deletion list with an icon and text
 * Used in confirmation dialogs to show what will be deleted
 */
@Composable
fun DeleteListItem(
    icon: ImageVector,
    text: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = LocalDialogSecondaryTextColor.current
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = LocalDialogSecondaryTextColor.current
        )
    }
}

/**
 * A container showing what will be deleted in a destructive action
 * Displays a warning message followed by a list of items
 */
@Composable
fun DeletionWarningBox(
    warningText: String,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = warningText,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.error
            )

            content()
        }
    }
}

/**
 * Info box component to display grouped information in a visually distinct container
 */
@Composable
fun InfoBox(
    title: String,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
    titleColor: Color = MaterialTheme.colorScheme.onSurface,
    icon: ImageVector? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = containerColor
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Main content column
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = titleColor
                )

                content()
            }

            // Trailing icon
            icon?.let {
                Icon(
                    imageVector = it,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun EmptyState(
    message: String,
    icon: ImageVector? = Icons.Outlined.FolderOff
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = LocalDialogSecondaryTextColor.current.copy(alpha = 0.5f)
            )
        }
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = LocalDialogSecondaryTextColor.current,
            textAlign = TextAlign.Center
        )
    }
}
