package com.jetbrains.kmpapp.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.jetbrains.kmpapp.data.MuseumObject
import org.koin.compose.viewmodel.koinViewModel

/**
 * The main screen of the Android application displaying a list of museum objects.
 *
 * @param navigateToDetails A callback function triggered when an object is clicked.
 */
@Composable
fun ListScreen(navigateToDetails: (objectId: Int) -> Unit) {
    // Injects the shared KMP ViewModel natively using Koin
    val viewModel: ListViewModel = koinViewModel()
    
    // Safely collects the StateFlow as Compose state, respecting the Activity lifecycle
    val objects by viewModel.objects.collectAsStateWithLifecycle()

    // Animates the transition between the empty state and the data grid
    AnimatedContent(objects.isNotEmpty()) { objectsAvailable ->
        if (objectsAvailable) {
            ObjectGrid(
                objects = objects,
                onObjectClick = navigateToDetails,
            )
        } else {
            EmptyScreenContent(Modifier.fillMaxSize())
        }
    }
}

/**
 * A Composable that renders a lazy grid of museum objects.
 *
 * @param objects The list of objects to display.
 * @param onObjectClick The callback triggered when a grid item is clicked.
 */
@Composable
private fun ObjectGrid(
    objects: List<MuseumObject>,
    onObjectClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    // Creates a responsive vertical grid where each column is at least 180.dp wide
    LazyVerticalGrid(
        columns = GridCells.Adaptive(180.dp),
        modifier = modifier.fillMaxSize(),
        // Adds padding to avoid overlapping with system navigation bars
        contentPadding = WindowInsets.safeDrawing.asPaddingValues(),
    ) {
        // Provides the objectID as a stable key for better performance during recomposition
        items(objects, key = { it.objectID }) { obj ->
            ObjectFrame(
                obj = obj,
                onClick = { onObjectClick(obj.objectID) },
            )
        }
    }
}

/**
 * A Composable displaying a single museum object inside a grid cell.
 */
@Composable
private fun ObjectFrame(
    obj: MuseumObject,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier
            .padding(8.dp)
            .clickable { onClick() }
    ) {
        // Loads and displays the image asynchronously using Coil
        AsyncImage(
            model = obj.primaryImageSmall,
            contentDescription = obj.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f) // Ensures the image remains perfectly square
                .background(Color.LightGray), // Placeholder color while loading
        )

        Spacer(Modifier.height(2.dp))

        // Textual information
        Text(obj.title, style = MaterialTheme.typography.titleMedium)
        Text(obj.artistDisplayName, style = MaterialTheme.typography.bodyMedium)
        Text(obj.objectDate, style = MaterialTheme.typography.bodySmall)
    }
}
