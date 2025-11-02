package org.elnix.notes.ui.helpers

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.elnix.notes.R
import org.elnix.notes.ui.theme.AppObjectsColors

@Composable
fun ValidateCancelButtons(
    onValidate: () -> Unit,
    onCancel: () -> Unit
) {
    Row(horizontalArrangement = Arrangement.SpaceEvenly) {
        Button(
            onClick = onValidate,
            colors = AppObjectsColors.buttonColors(),
            modifier = Modifier.weight(1.5f)
        ) {
            Text(
                text = stringResource(R.string.save),
                color = MaterialTheme.colorScheme.onPrimary
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        OutlinedButton(
            onClick = onCancel,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
            colors = AppObjectsColors.cancelButtonColors(),
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = stringResource(R.string.cancel),
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
