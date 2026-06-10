package com.jetbrains.kmpapp.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.jetbrains.kmpapp.data.MuseumObject
import com.jetbrains.kmpapp.data.UserProfile
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

/**
 * The main screen of the Android application displaying a list of museum objects.
 * Features native Material Design components like ModalNavigationDrawer and NavigationBar.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListScreen(navigateToDetails: (objectId: Int) -> Unit) {
    // Shared ViewModel
    val viewModel: ListViewModel = koinViewModel()
    
    // Collecting shared state
    val objects by viewModel.objects.collectAsStateWithLifecycle()
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val likedObjectIds by viewModel.likedObjectIds.collectAsStateWithLifecycle()

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var selectedBottomTab by remember { mutableIntStateOf(0) }

    // Native Android Navigation Drawer
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                ProfileDrawerContent(userProfile)
            }
        }
    ) {
        // Native Android Scaffold with Bottom Navigation
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("KMP Native UI") },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    }
                )
            },
            bottomBar = {
                NavigationBar {
                    NavigationBarItem(
                        selected = selectedBottomTab == 0,
                        onClick = { selectedBottomTab = 0 },
                        icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                        label = { Text("Home") }
                    )
                    NavigationBarItem(
                        selected = selectedBottomTab == 1,
                        onClick = { selectedBottomTab = 1 },
                        icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                        label = { Text("Settings") }
                    )
                }
            }
        ) { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues)) {
                if (selectedBottomTab == 0) {
                    AnimatedContent(objects.isNotEmpty(), label = "ObjectsAnim") { objectsAvailable ->
                        if (objectsAvailable) {
                            ObjectGrid(
                                objects = objects,
                                likedObjectIds = likedObjectIds,
                                onObjectClick = navigateToDetails,
                                onToggleLike = { viewModel.toggleLike(it) }
                            )
                        } else {
                            EmptyScreenContent(Modifier.fillMaxSize())
                        }
                    }
                } else {
                    // Settings dummy tab
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Settings Tab", style = MaterialTheme.typography.headlineMedium)
                    }
                }
            }
        }
    }
}

/**
 * Android Native Drawer content populated by the shared UserProfile state.
 */
@Composable
private fun ProfileDrawerContent(profile: UserProfile) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        AsyncImage(
            model = profile.avatarUrl,
            contentDescription = "Profile Picture",
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(Color.LightGray),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(profile.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Text(profile.email, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
        
        Spacer(modifier = Modifier.height(32.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(16.dp))
        NavigationDrawerItem(
            label = { Text("My Profile") },
            selected = false,
            onClick = { /* dummy */ }
        )
        NavigationDrawerItem(
            label = { Text("Favorites") },
            selected = false,
            onClick = { /* dummy */ }
        )
    }
}

/**
 * A Composable that renders a lazy grid of museum objects.
 */
@Composable
private fun ObjectGrid(
    objects: List<MuseumObject>,
    likedObjectIds: List<Int>,
    onObjectClick: (Int) -> Unit,
    onToggleLike: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(180.dp),
        modifier = modifier.fillMaxSize(),
        contentPadding = WindowInsets.safeDrawing.asPaddingValues(),
    ) {
        items(objects, key = { it.objectID }) { obj ->
            ObjectFrame(
                obj = obj,
                isLiked = likedObjectIds.contains(obj.objectID),
                onClick = { onObjectClick(obj.objectID) },
                onToggleLike = { onToggleLike(obj.objectID) }
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
    isLiked: Boolean,
    onClick: () -> Unit,
    onToggleLike: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .padding(8.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            Box {
                AsyncImage(
                    model = obj.primaryImageSmall,
                    contentDescription = obj.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .background(Color.LightGray),
                )
                // Native UI element: IconToggleButton overlay
                IconToggleButton(
                    checked = isLiked,
                    onCheckedChange = { onToggleLike() },
                    modifier = Modifier.align(Alignment.TopEnd).padding(4.dp)
                ) {
                    Icon(
                        imageVector = if (isLiked) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = "Like",
                        tint = if (isLiked) Color.Red else Color.White
                    )
                }
            }

            Column(modifier = Modifier.padding(12.dp)) {
                Text(obj.title, style = MaterialTheme.typography.titleMedium, maxLines = 1)
                Spacer(Modifier.height(4.dp))
                Text(obj.artistDisplayName, style = MaterialTheme.typography.bodyMedium, maxLines = 1)
                Text(obj.objectDate, style = MaterialTheme.typography.bodySmall, maxLines = 1)
            }
        }
    }
}
