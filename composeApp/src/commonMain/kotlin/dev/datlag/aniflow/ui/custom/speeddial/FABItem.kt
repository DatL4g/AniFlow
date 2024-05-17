package dev.datlag.aniflow.ui.custom.speeddial

import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

open class FABItem(
    val icon: ImageVector? = null,
    val painter: Painter? = null,
    val tint: Boolean = false,
    val modifier: Modifier = if (painter == null) Modifier else Modifier.size(24.dp),
    val label: String,
    val onClick: () -> Unit,
)