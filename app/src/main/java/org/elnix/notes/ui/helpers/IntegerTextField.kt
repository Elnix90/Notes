package org.elnix.notes.ui.helpers

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.elnix.notes.ui.theme.AppObjectsColors
import org.elnix.notes.ui.theme.adjustBrightness

@Composable
fun SettingsOutlinedField(
    modifier: Modifier = Modifier,
    value: String,
    label: String,
    minValue: Int,
    maxValue: Int,
    keyboardType: KeyboardType = KeyboardType.Number,
    scope: CoroutineScope,
    enabled: Boolean = true,
    onValueChange: suspend (String) -> Unit,
) {
    var inputValue by remember { mutableStateOf(value) }
    var isError by remember { mutableStateOf(false) }

    LaunchedEffect(value) {
        inputValue = value
    }

    OutlinedTextField(
        modifier = modifier,
        value = inputValue,
        enabled = enabled,
        onValueChange = { newValue ->

            if (keyboardType == KeyboardType.Number && !newValue.matches(Regex("^[0-9]*$"))) return@OutlinedTextField

            inputValue = newValue
            val parsed = newValue.toIntOrNull()

            if (parsed != null) {
                if (parsed in minValue..maxValue) {
                    isError = false
                    scope.launch { onValueChange(newValue) }
                } else {
                    isError = true
                }
            } else {
                isError = false
                scope.launch { onValueChange(newValue) }
            }
        },
        label = { Text(label) },
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        isError = isError,
        singleLine = true,
        colors = AppObjectsColors.outlinedTextFieldColors(MaterialTheme.colorScheme.surface.adjustBrightness(if (enabled) 1f else 0.5f))
    )
}
