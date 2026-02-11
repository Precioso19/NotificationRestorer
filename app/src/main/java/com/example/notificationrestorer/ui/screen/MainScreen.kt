package com.example.notificationrestorer.ui.screen

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.notificationrestorer.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: MainViewModel,
    onRequestPermission: () -> Unit
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    var showSortMenu by remember { mutableStateOf(false) }
    var showClearDialog by remember { mutableStateOf(false) }
    var searchExpanded by remember { mutableStateOf(false) }

    val isListenerEnabled = remember {
        isNotificationListenerEnabled(context)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    if (searchExpanded) {
                        TextField(
                            value = uiState.filterQuery,
                            onValueChange = { viewModel.setFilterQuery(it) },
                            placeholder = { Text("Buscar...") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.surface,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                                focusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
                                unfocusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent
                            ),
                            singleLine = true
                        )
                    } else {
                        Text("Notification Restorer")
                    }
                },
                actions = {
                    if (searchExpanded) {
                        IconButton(onClick = {
                            searchExpanded = false
                            viewModel.setFilterQuery("")
                        }) {
                            Icon(Icons.Default.Close, "Cerrar búsqueda")
                        }
                    } else {
                        IconButton(onClick = { searchExpanded = true }) {
                            Icon(Icons.Default.Search, "Buscar")
                        }
                        
                        Box {
                            IconButton(onClick = { showSortMenu = true }) {
                                Icon(Icons.Default.Sort, "Ordenar")
                            }
                            
                            DropdownMenu(
                                expanded = showSortMenu,
                                onDismissRequest = { showSortMenu = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Más reciente") },
                                    onClick = {
                                        viewModel.setSortBy(MainViewModel.SortBy.TIME_DESC)
                                        showSortMenu = false
                                    },
                                    leadingIcon = {
                                        Icon(Icons.Default.ArrowDownward, null)
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Más antiguo") },
                                    onClick = {
                                        viewModel.setSortBy(MainViewModel.SortBy.TIME_ASC)
                                        showSortMenu = false
                                    },
                                    leadingIcon = {
                                        Icon(Icons.Default.ArrowUpward, null)
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Por app") },
                                    onClick = {
                                        viewModel.setSortBy(MainViewModel.SortBy.APP_NAME)
                                        showSortMenu = false
                                    },
                                    leadingIcon = {
                                        Icon(Icons.Default.Apps, null)
                                    }
                                )
                            }
                        }
                        
                        IconButton(onClick = { showClearDialog = true }) {
                            Icon(Icons.Default.Delete, "Limpiar")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (!isListenerEnabled) {
                PermissionCard(onRequestPermission = onRequestPermission)
            }

            TabRow(
                selectedTabIndex = if (uiState.selectedTab == MainViewModel.Tab.ACTIVE) 0 else 1,
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                Tab(
                    selected = uiState.selectedTab == MainViewModel.Tab.ACTIVE,
                    onClick = { viewModel.setSelectedTab(MainViewModel.Tab.ACTIVE) },
                    text = {
                        Text("Activas (${uiState.activeNotifications.size})")
                    },
                    icon = {
                        Icon(Icons.Default.Notifications, null)
                    }
                )
                Tab(
                    selected = uiState.selectedTab == MainViewModel.Tab.DISMISSED,
                    onClick = { viewModel.setSelectedTab(MainViewModel.Tab.DISMISSED) },
                    text = {
                        Text("Cerradas (${uiState.dismissedNotifications.size})")
                    },
                    icon = {
                        Icon(Icons.Default.NotificationsOff, null)
                    }
                )
            }

            val notifications = when (uiState.selectedTab) {
                MainViewModel.Tab.ACTIVE -> uiState.activeNotifications
                MainViewModel.Tab.DISMISSED -> uiState.dismissedNotifications
            }

            if (notifications.isEmpty()) {
                EmptyState(uiState.selectedTab)
            } else {
                NotificationList(
                    notifications = notifications,
                    onNotificationClick = { viewModel.restoreNotification(it) },
                    onDeleteClick = { viewModel.deleteNotification(it.id) }
                )
            }
        }
    }

    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = { Text("Limpiar notificaciones") },
            text = {
                Text(
                    when (uiState.selectedTab) {
                        MainViewModel.Tab.ACTIVE -> "¿Deseas eliminar todas las notificaciones activas?"
                        MainViewModel.Tab.DISMISSED -> "¿Deseas eliminar todas las notificaciones cerradas?"
                    }
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        when (uiState.selectedTab) {
                            MainViewModel.Tab.ACTIVE -> viewModel.clearAll()
                            MainViewModel.Tab.DISMISSED -> viewModel.clearDismissed()
                        }
                        showClearDialog = false
                    }
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun PermissionCard(onRequestPermission: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Icon(
                Icons.Default.Warning,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onErrorContainer,
                modifier = Modifier.size(48.dp)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                "Permiso requerido",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                "Esta app necesita acceso a las notificaciones para poder guardarlas y restaurarlas.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = onRequestPermission,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Conceder permiso")
            }
        }
    }
}

@Composable
fun EmptyState(tab: MainViewModel.Tab) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(
                if (tab == MainViewModel.Tab.ACTIVE) Icons.Default.NotificationsNone
                else Icons.Default.NotificationsOff,
                contentDescription = null,
                modifier = Modifier.size(72.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = if (tab == MainViewModel.Tab.ACTIVE)
                    "No hay notificaciones activas"
                else
                    "No hay notificaciones cerradas",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = if (tab == MainViewModel.Tab.ACTIVE)
                    "Las notificaciones nuevas aparecerán aquí"
                else
                    "Las notificaciones cerradas aparecerán aquí",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun isNotificationListenerEnabled(context: Context): Boolean {
    val enabledNotificationListeners = Settings.Secure.getString(
        context.contentResolver,
        "enabled_notification_listeners"
    )
    return enabledNotificationListeners?.contains(context.packageName) == true
}
