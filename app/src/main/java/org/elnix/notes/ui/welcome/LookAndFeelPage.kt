package org.elnix.notes.ui.welcome

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.elnix.notes.R
import org.elnix.notes.data.settings.DefaultThemes
import org.elnix.notes.data.settings.applyDefaultThemeColors
import org.elnix.notes.data.settings.defaultThemeName
import org.elnix.notes.data.settings.stores.ColorModesSettingsStore
import org.elnix.notes.ui.theme.AppObjectsColors


@Composable
fun LookAndFeelPage() {

    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()

    val selectedDefaultTheme by ColorModesSettingsStore.getDefaultTheme(ctx).collectAsState(initial = DefaultThemes.DARK)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(25.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {

        Text(
            text = stringResource(R.string.look_and_feel),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(Modifier.height(25.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            DefaultThemes.entries.forEach {
                Column(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable {
                            scope.launch {
                                ColorModesSettingsStore.setDefaultTheme(ctx, it)
                                applyDefaultThemeColors(ctx, it)
                            }
                        }
                        .padding(5.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    when (it) {
                        DefaultThemes.AMOLED -> Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color.Black)
                                .border(
                                    1.dp,
                                    MaterialTheme.colorScheme.outline.copy(0.5f),
                                    CircleShape
                                )
                        )
                        DefaultThemes.DARK -> Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color.DarkGray)
                                .border(
                                    1.dp,
                                    MaterialTheme.colorScheme.outline.copy(0.5f),
                                    CircleShape
                                )
                        )
                        DefaultThemes.LIGHT -> Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                                .border(
                                    1.dp,
                                    MaterialTheme.colorScheme.outline.copy(0.5f),
                                    CircleShape
                                )
                        )
                    }

                    Spacer(Modifier.height(5.dp))

                    Text(
                        text = defaultThemeName(it),
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.labelSmall
                    )

                    RadioButton(
                        selected = selectedDefaultTheme == it,
                        onClick = {
                            scope.launch {
                                ColorModesSettingsStore.setDefaultTheme(ctx, it)
                                applyDefaultThemeColors(ctx, it)
                            }
                        },
                        colors = AppObjectsColors.radioButtonColors()
                    )
                }
            }
        }

        Spacer(Modifier.height(80.dp))

        Text(
            text = stringResource(R.string.main_settings_text),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )
    }
}