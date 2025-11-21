package com.bboost.brainboost.ui.theme

import androidx.compose.ui.graphics.Color

// ========= Colores antiguos (compatibilidad con screens previos) =========
val Orange = Color(0xFFF15A29)       // Naranjo original del hangman
val LightGray = Color(0xFFC6C6C6)
val DarkGray = Color(0xFF2F2F2F)
val Blue = Color(0xFF004A9F)
val White = Color(0xFFFFFFFF)


// ========= NUEVA PALETA OFICIAL (BrainBoost UI Moderno) =========

// Principales
val BB_Primary = Color(0xFF2A2E65)      // Azul oscuro (corporativo)
val BB_Secondary = Color(0xFFF78A38)    // Naranjo moderno
val BB_Accent = Color(0xFFFFD447)       // Amarillo
val BB_Background = Color(0xFFF9F9F9)
val BB_Surface = Color.White
val BB_Error = Color(0xFFFBB0A9)

// Grises
val BB_DarkText = Color(0xFF2F2F2F)
val BB_LightGray = Color(0xFFC6C6C6)
val BB_White = Color.White


// ========= Material 3 Light =========
val md_theme_light_primary = BB_Primary
val md_theme_light_onPrimary = BB_White

val md_theme_light_secondary = BB_Secondary
val md_theme_light_onSecondary = BB_White

val md_theme_light_tertiary = BB_Accent
val md_theme_light_onTertiary = BB_DarkText

val md_theme_light_background = BB_Background
val md_theme_light_onBackground = BB_DarkText

val md_theme_light_surface = BB_Surface
val md_theme_light_onSurface = BB_DarkText

val md_theme_light_surfaceVariant = BB_LightGray
val md_theme_light_onSurfaceVariant = BB_DarkText

val md_theme_light_error = BB_Error
val md_theme_light_onError = BB_DarkText


// ========= Material 3 Dark =========
val md_theme_dark_primary = BB_Accent
val md_theme_dark_onPrimary = Color(0xFF1D1D1D)

val md_theme_dark_secondary = BB_Secondary
val md_theme_dark_onSecondary = BB_White

val md_theme_dark_tertiary = BB_Accent
val md_theme_dark_onTertiary = BB_DarkText

val md_theme_dark_background = Color(0xFF121212)
val md_theme_dark_onBackground = BB_White

val md_theme_dark_surface = Color(0xFF1E1E1E)
val md_theme_dark_onSurface = BB_White

val md_theme_dark_surfaceVariant = Color(0xFF2A2A2A)
val md_theme_dark_onSurfaceVariant = BB_LightGray

val md_theme_dark_error = BB_Error
val md_theme_dark_onError = BB_DarkText
