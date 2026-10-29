package com.example.wordcounter

import androidx.activity.ComponentActivity
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography as MaterialTypography
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.unit.sp
import com.example.wordcounter.ui.theme.Pink40
import com.example.wordcounter.ui.theme.Pink80
import com.example.wordcounter.ui.theme.Purple40
import com.example.wordcounter.ui.theme.Purple80
import com.example.wordcounter.ui.theme.PurpleGrey40
import com.example.wordcounter.ui.theme.PurpleGrey80
import com.example.wordcounter.ui.theme.Typography
import com.example.wordcounter.ui.theme.WordCounterTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import org.robolectric.annotation.LooperMode

/**
 * Local JVM unit tests for the application's theme, typography, and color tokens:
 * [WordCounterTheme], [Typography], and Color constants.
 *
 * Exercises both static (Light/Dark) and dynamic (Light/Dark on Android S+) color
 * scheme branches, verifying that theme providers configure MaterialTheme properly
 * and that all color and typography property getters return expected constant values.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@LooperMode(LooperMode.Mode.PAUSED)
class WordCounterThemeLocalTest {

    @get:Rule
    val compose = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun theme_lightStatic_appliesLightSchemeAndTypography() {
        var capturedColorScheme: ColorScheme? = null
        var capturedTypography: MaterialTypography? = null

        compose.setContent {
            WordCounterTheme(darkTheme = false, dynamicColor = false) {
                capturedColorScheme = MaterialTheme.colorScheme
                capturedTypography = MaterialTheme.typography
            }
        }
        compose.waitForIdle()

        assertNotNull(capturedColorScheme)
        assertEquals(Purple40, capturedColorScheme?.primary)
        assertEquals(PurpleGrey40, capturedColorScheme?.secondary)
        assertEquals(Pink40, capturedColorScheme?.tertiary)
        assertEquals(Typography, capturedTypography)
    }

    @Test
    fun theme_darkStatic_appliesDarkSchemeAndTypography() {
        var capturedColorScheme: ColorScheme? = null
        var capturedTypography: MaterialTypography? = null

        compose.setContent {
            WordCounterTheme(darkTheme = true, dynamicColor = false) {
                capturedColorScheme = MaterialTheme.colorScheme
                capturedTypography = MaterialTheme.typography
            }
        }
        compose.waitForIdle()

        assertNotNull(capturedColorScheme)
        assertEquals(Purple80, capturedColorScheme?.primary)
        assertEquals(PurpleGrey80, capturedColorScheme?.secondary)
        assertEquals(Pink80, capturedColorScheme?.tertiary)
        assertEquals(Typography, capturedTypography)
    }

    @Test
    fun theme_dynamicLight_appliesDynamicLightColorSchemeOnAndroidS() {
        var capturedColorScheme: ColorScheme? = null

        compose.setContent {
            WordCounterTheme(darkTheme = false, dynamicColor = true) {
                capturedColorScheme = MaterialTheme.colorScheme
            }
        }
        compose.waitForIdle()

        val expectedScheme = dynamicLightColorScheme(compose.activity)
        assertNotNull(capturedColorScheme)
        assertEquals(expectedScheme.primary, capturedColorScheme?.primary)
        assertEquals(expectedScheme.surface, capturedColorScheme?.surface)
        assertEquals(expectedScheme.background, capturedColorScheme?.background)
    }

    @Test
    fun theme_dynamicDark_appliesDynamicDarkColorSchemeOnAndroidS() {
        var capturedColorScheme: ColorScheme? = null

        compose.setContent {
            WordCounterTheme(darkTheme = true, dynamicColor = true) {
                capturedColorScheme = MaterialTheme.colorScheme
            }
        }
        compose.waitForIdle()

        val expectedScheme = dynamicDarkColorScheme(compose.activity)
        assertNotNull(capturedColorScheme)
        assertEquals(expectedScheme.primary, capturedColorScheme?.primary)
        assertEquals(expectedScheme.surface, capturedColorScheme?.surface)
        assertEquals(expectedScheme.background, capturedColorScheme?.background)
    }

    @Test
    fun colorTokensAndTypography_haveExpectedConstantValues() {
        // Assert color constants to invoke their property getters and verify tokens
        assertEquals(Color(0xFFD0BCFF), Purple80)
        assertEquals(Color(0xFFCCC2DC), PurpleGrey80)
        assertEquals(Color(0xFFEFB8C8), Pink80)
        assertEquals(Color(0xFF6650a4), Purple40)
        assertEquals(Color(0xFF625b71), PurpleGrey40)
        assertEquals(Color(0xFF7D5260), Pink40)

        // Assert typography properties to invoke getTypography() and verify definition
        assertEquals(16.sp, Typography.bodyLarge.fontSize)
        assertEquals(24.sp, Typography.bodyLarge.lineHeight)
        assertEquals(0.5.sp, Typography.bodyLarge.letterSpacing)
    }
}
