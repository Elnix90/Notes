package org.elnix.notes.ui.helpers

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.elnix.notes.R

@Composable
fun ValidateCancelButtons(
    validateText: String = stringResource(R.string.save),
    validateColor: Color = MaterialTheme.colorScheme.onPrimary,
    validateContainerColor: Color = MaterialTheme.colorScheme.primary,
    cancelText: String = stringResource(R.string.cancel),
    cancelColor: Color = MaterialTheme.colorScheme.error,
    cancelContainerColor: Color = MaterialTheme.colorScheme.surface,
    onValidate: () -> Unit,
    onCancel: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        OutlinedButton(
            onClick = onCancel,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.error),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = cancelContainerColor,
                contentColor = cancelColor
            ),
            shape = CircleShape,
            modifier = Modifier.weight(1f)
        ) {
            Text(text = cancelText)
        }

        Spacer(Modifier.width(5.dp))

        TextButton(
            onClick = onValidate,
            colors = ButtonDefaults.buttonColors(
                contentColor = validateColor,
                containerColor = validateContainerColor
            ),
            modifier = Modifier.weight(1.5f),
            shape = CircleShape
        ) {
            Text(validateText)
        }

        Spacer(modifier = Modifier.width(10.dp))
    }
}
