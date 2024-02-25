package com.rgr.rutappa.android

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        darkColorScheme(
            primary = Color(0xFFefdbb2),
            secondary = Color(0xFF473829),
            tertiary = Color(0xFFffce04),
            onBackground = Color(0xFF473829),//TEXT
            onPrimary = Color(0xFFefdbb2),//TEXT BUTTONS
            secondaryContainer = Color.White //SLIDER
        )
    } else {
        lightColorScheme(
            primary = Color(0xFFefdbb2),
            secondary = Color(0xFF473829),
            tertiary = Color(0xFFffce04),
            onBackground = Color(0xFF473829),//TEXT
            onPrimary = Color(0xFFefdbb2),//TEXT BUTTONS
            secondaryContainer = Color.White //SLIDER


           /* primaryContainer = Color.Red,
            onPrimaryContainer = Color.Red,
            inversePrimary = Color.Red,
            onSecondary = Color.Red,*/
            /*secondaryContainer = Color.Red,
            onSecondaryContainer = Color.Red,
            onTertiary = Color.Red,
            tertiaryContainer = Color.Red,
            onTertiaryContainer = Color.Red,
            background = Color.Red,
            surface = Color.Red,
            onSurface = Color.Red,
            surfaceVariant = Color.Red,
            onSurfaceVariant = Color.Red,
            surfaceTint = Color.Red,*/
            /*inverseSurface = Color.Red,
            inverseOnSurface = Color.Red,
            error = Color.Red,
            onError = Color.Red,
            errorContainer = Color.Red,
            onErrorContainer = Color.Red,
            outline = Color.Red,
            outlineVariant = Color.Red,
            scrim = Color.Red*/
        )
    }
    val typography = Typography(
        titleMedium = TextStyle(
            fontFamily = fontMontserrat,
            fontWeight = FontWeight.W600,
            fontSize = 16.sp
        ),
        titleSmall = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
        ),
        bodyLarge = TextStyle(
            fontFamily = fontBerlin,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
        ),
        bodyMedium = TextStyle(
            fontFamily = fontMontserrat,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp
        ),
        bodySmall = TextStyle(
            fontFamily = fontMontserrat,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        ),
        displayLarge = TextStyle(
            fontFamily = fontBerlin,
            fontWeight = FontWeight.SemiBold,
            fontSize = 20.sp,
        ),
        displayMedium = TextStyle(
            fontFamily = fontBerlin,
            fontWeight = FontWeight.SemiBold,
            fontSize = 18.sp,
        ),
        displaySmall = TextStyle(
            fontFamily = fontMontserrat,
            fontWeight = FontWeight.Normal,
            fontSize = 12.sp,
        ),
    )
    val shapes = Shapes(
        small = RoundedCornerShape(4.dp),
        medium = RoundedCornerShape(4.dp),
        large = RoundedCornerShape(0.dp)
    )

    MaterialTheme(
        colorScheme = colors,
        typography = typography,
        shapes = shapes,
        content = content
    )
}

val ShadowColor = arrayOf(
    0.0f to Color(0xFF473829).copy(alpha = 0.7f),
    0.3f to Color(0xFF473829).copy(alpha = 0.85f),
    1f to Color(0xFF473829).copy(alpha = 1f)
)

fun backgroundColor(darkTheme: Boolean) = if(darkTheme) {
    arrayOf(
        0.0f to Color(0xFFFFFFFF),
        0.5f to Color(0xFFefdbb2).copy(alpha = 0.6f),
        1f to Color(0xFFefdbb2)
    )
} else {
    arrayOf(
        0.0f to Color(0xFFFFFFFF),
        0.5f to Color(0xFFefdbb2).copy(alpha = 0.6f),
        1f to Color(0xFFefdbb2)
    )
}

val fontBerlin = FontFamily(
    Font(R.font.berlin_sans_demi_bold)
)

val fontMontserrat = FontFamily(
    Font(R.font.montserrat_regular),
    Font(R.font.montserrat_semi_bold)

)
