package com.rgr.rutappa.android.screen.common

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.rgr.rutappa.android.R

@Composable
fun AlertDialogError(
    description: @Composable () -> Unit,
    onDismissRequest: () -> Unit,
) {
    AlertDialog(
        containerColor = Color(0xFFFFFFFF),
        title = {
            Text(text = stringResource(R.string.error), color = MaterialTheme.colorScheme.secondary)
        },
        text = {
            description()
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text(
                    text = stringResource(R.string.ok),
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
    )
}