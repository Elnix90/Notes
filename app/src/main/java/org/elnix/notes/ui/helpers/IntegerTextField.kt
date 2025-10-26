package org.elnix.notes.ui.helpers

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.elnix.notes.ui.theme.AppObjectsColors
import org.elnix.notes.ui.theme.adjustBrightness

@Composable
fun SettingsOutlinedField(
    modifier: Modifier = Modifier,
    value: String,
    label: String? = null,
    minValue: Int? = null,
    maxValue: Int? = null,
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

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surface.adjustBrightness(if (enabled) 1f else 0.5f),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = inputValue,
            enabled = enabled,
            onValueChange = { newValue ->

                if (keyboardType == KeyboardType.Number && !newValue.matches(Regex("^[0-9]*$"))) return@OutlinedTextField

                inputValue = newValue
                val parsed = newValue.toIntOrNull()

                if (minValue != null && maxValue != null && parsed != null) {
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
            label = { if (label != null) Text(label) },
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            isError = isError,
            singleLine = true,
            colors = AppObjectsColors.outlinedTextFieldColors(MaterialTheme.colorScheme.surface.adjustBrightness(if (enabled) 1f else 0.5f))
        )
    }
}
