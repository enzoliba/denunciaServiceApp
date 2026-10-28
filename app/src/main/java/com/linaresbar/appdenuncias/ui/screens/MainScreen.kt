package com.linaresbar.appdenuncias.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.AddAlert
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.viewmodel.compose.viewModel
import com.linaresbar.appdenuncias.ui.components.DenunciaDetailDialog
import com.linaresbar.appdenuncias.ui.components.ServerConfigDialog
import com.linaresbar.appdenuncias.ui.viewmodel.DenunciaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: DenunciaViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val formState by viewModel.formState.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) }
    var showServerDialog by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    // Display snackbar when actionMessage arrives
    LaunchedEffect(uiState.actionMessage) {
        uiState.actionMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.resetActionMessage()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Denuncias Inseguridad",
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    // Server Config Button
                    IconButton(onClick = { showServerDialog = true }) {
                        Icon(Icons.Default.Dns, contentDescription = "Configurar Servidor")
                    }
                    // Refresh Button
                    IconButton(onClick = { viewModel.cargarDenuncias(reset = true) }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Actualizar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = "Lista") },
                    label = { Text("Lista") }
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.Default.AddAlert, contentDescription = "Registrar") },
                    label = { Text("Registrar") }
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = { Icon(Icons.Default.Search, contentDescription = "Buscar DNI") },
                    label = { Text("Buscar DNI") }
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (selectedTab) {
                0 -> DenunciasListScreen(
                    uiState = uiState,
                    onRefresh = { viewModel.cargarDenuncias(reset = true) },
                    onLoadMore = { viewModel.cargarDenuncias(reset = false) },
                    onSelectDenuncia = { viewModel.seleccionarDenuncia(it) },
                    onNavigateToCreate = { selectedTab = 1 }
                )

                1 -> DenunciaFormScreen(
                    formState = formState,
                    onDniChanged = { viewModel.onFormDniChanged(it) },
                    onTipoChanged = { viewModel.onFormTipoChanged(it) },
                    onDireccionChanged = { viewModel.onFormDireccionChanged(it) },
                    onDescripcionChanged = { viewModel.onFormDescripcionChanged(it) },
                    onFechaChanged = { viewModel.onFormFechaChanged(it) },
                    onSubmit = { viewModel.registrarDenuncia() }
                )

                2 -> DenunciaSearchScreen(
                    uiState = uiState,
                    onSearchDniChanged = { viewModel.onSearchDniChanged(it) },
                    onPerformSearch = { viewModel.buscarDenunciasPorDni() },
                    onSelectDenuncia = { viewModel.seleccionarDenuncia(it) }
                )
            }
        }
    }

    // Detail Dialog
    uiState.selectedDenuncia?.let { denuncia ->
        DenunciaDetailDialog(
            denuncia = denuncia,
            isAttending = uiState.isAttending,
            onDismiss = { viewModel.seleccionarDenuncia(null) },
            onAtender = { viewModel.atenderDenuncia(denuncia) }
        )
    }

    // Server Config Dialog
    if (showServerDialog) {
        ServerConfigDialog(
            currentUrl = uiState.currentBaseUrl,
            onDismiss = { showServerDialog = false },
            onConfirm = { newUrl -> viewModel.updateBaseUrl(newUrl) }
        )
    }
}
