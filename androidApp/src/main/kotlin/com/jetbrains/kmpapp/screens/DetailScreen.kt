package com.jetbrains.kmpapp.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.jetbrains.kmpapp.R
import com.jetbrains.kmpapp.data.MuseumObject
import org.koin.compose.viewmodel.koinViewModel

/**
 * A Composable screen that displays the detailed information of a selected museum object.
 *
 * @param objectId The ID of the museum object to fetch and display.
 * @param navigateBack A callback to navigate the user back to the list screen.
 */
@Composable
fun DetailScreen(objectId: Int, navigateBack: () -> Unit) {
    // Injects the shared KMP ViewModel
    val viewModel: DetailViewModel = koinViewModel()
    
    // Collects the state from the ViewModel
    val obj by viewModel.museumObject.collectAsStateWithLifecycle()

    // Triggers the ViewModel to fetch data whenever the objectId changes
    LaunchedEffect(objectId) {
        viewModel.setId(objectId)
    }

    // Animates the transition once the object data is successfully loaded
    AnimatedContent(obj != null) { objectAvailable ->
        if (objectAvailable) {
            ObjectDetails(obj!!, onBackClick = navigateBack)
        } else {
            EmptyScreenContent(Modifier.fillMaxSize())
        }
    }
}

/**
 * A Composable that renders the actual object details and an AppBar.
 */
@Composable
private fun ObjectDetails(
    obj: MuseumObject,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // Scaffold provides the standard Material structural layout
    Scaffold(
        topBar = {
            @OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(
                title = {},
                // Back button to trigger the navigation callback
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.back))
                    }
                }
            )
        },
        modifier = modifier.windowInsetsPadding(WindowInsets.systemBars),
    ) { paddingValues ->
        // Makes the column vertically scrollable to accommodate long content
        Column(
            Modifier
                .verticalScroll(rememberScrollState())
                .padding(paddingValues)
        ) {
            // Displays the high-resolution primary image using Coil
            AsyncImage(
                model = obj.primaryImageSmall,
                contentDescription = obj.title,
                contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.LightGray)
            )

            // Allows the user to select and copy text
            SelectionContainer {
                Column(Modifier.padding(12.dp)) {
                    Text(obj.title, style = MaterialTheme.typography.headlineMedium)
                    Spacer(Modifier.height(6.dp))
                    
                    // Displays various attributes using a helper Composable
                    LabeledInfo(stringResource(R.string.label_artist), obj.artistDisplayName)
                    LabeledInfo(stringResource(R.string.label_date), obj.objectDate)
                    LabeledInfo(stringResource(R.string.label_dimensions), obj.dimensions)
                    LabeledInfo(stringResource(R.string.label_medium), obj.medium)
                    LabeledInfo(stringResource(R.string.label_department), obj.department)
                    LabeledInfo(stringResource(R.string.label_repository), obj.repository)
                    LabeledInfo(stringResource(R.string.label_credits), obj.creditLine)
                }
            }
        }
    }
}

/**
 * A helper Composable that formats and displays a label in bold followed by its value.
 */
@Composable
private fun LabeledInfo(
    label: String,
    data: String,
    modifier: Modifier = Modifier,
) {
    Column(modifier.padding(vertical = 4.dp)) {
        Spacer(Modifier.height(6.dp))
        // Uses buildAnnotatedString to style parts of the text differently
        Text(
            buildAnnotatedString {
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                    append("$label: ")
                }
                append(data)
            }
        )
    }
}
