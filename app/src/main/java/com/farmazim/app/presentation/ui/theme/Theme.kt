package com.farmazim.app.presentation.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val Green700 = Color(0xFF388E3C)
private val Green500 = Color(0xFF4CAF50)
private val Green100 = Color(0xFFC8E6C9)
private val Amber700 = Color(0xFFFFA000)
private val Brown800 = Color(0xFF4E342E)

private val FarmaZimColorScheme = lightColorScheme(
    primary = Green700,
    onPrimary = Color.White,
    primaryContainer = Green100,
    onPrimaryContainer = Brown800,
    secondary = Amber700,
    onSecondary = Color.White,
    background = Color(0xFFF9FBF9),
    surface = Color.White,
    onBackground = Color(0xFF1A1C1A),
    onSurface = Color(0xFF1A1C1A)
)

@Composable
fun FarmaZimTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = FarmaZimColorScheme,
        content = content
    )
}
