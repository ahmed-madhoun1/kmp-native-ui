package com.jetbrains.kmpapp.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.jetbrains.kmpapp.R

/**
 * A reusable Composable that displays an empty or loading state.
 * It centers a text message informing the user that no data is available yet.
 *
 * @param modifier The modifier to be applied to the layout.
 */
@Composable
fun EmptyScreenContent(
    modifier: Modifier = Modifier,
) {
    // A Box layout perfectly centers its content
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        // Displays a localized string resource indicating no data is available
        Text(stringResource(R.string.no_data_available))
    }
}
