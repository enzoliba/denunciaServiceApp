package com.linaresbar.appdenuncias.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.linaresbar.appdenuncias.data.model.Denuncia
import com.linaresbar.appdenuncias.ui.components.DenunciaCard
import com.linaresbar.appdenuncias.ui.viewmodel.UiState

@Composable
fun DenunciasListScreen(
    uiState: UiState,
    onRefresh: () -> Unit,
    onLoadMore: () -> Unit,
    onSelectDenuncia: (Denuncia) -> Unit,
    onNavigateToCreate: () -> Unit
) {
    var selectedFilter by remember { mutableStateOf("TODAS") }

    val filteredList = when (selectedFilter) {
        "PENDIENTE" -> uiState.denuncias.filter { it.estado == 1 }
        "ATENDIDA" -> uiState.denuncias.filter { it.estado == 0 }
        else -> uiState.denuncias
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {

            // Header info & Filter Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Registros (${uiState.denuncias.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Row {
                    FilterChip(
                        selected = selectedFilter == "TODAS",
                        onClick = { selectedFilter = "TODAS" },
                        label = { Text("Todas") },
                        modifier = Modifier.padding(end = 4.dp)
                    )
                    FilterChip(
                        selected = selectedFilter == "PENDIENTE",
                        onClick = { selectedFilter = "PENDIENTE" },
                        label = { Text("Pendientes") },
                        modifier = Modifier.padding(end = 4.dp)
                    )
                    FilterChip(
                        selected = selectedFilter == "ATENDIDA",
                        onClick = { selectedFilter = "ATENDIDA" },
                        label = { Text("Atendidas") }
                    )
                }
            }

            // Error banner if any
            if (uiState.listError != null) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = uiState.listError,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                        OutlinedButton(onClick = onRefresh) {
                            Text("Reintentar")
                        }
                    }
                }
            }

            // Main List
            if (uiState.isLoadingList && uiState.denuncias.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Cargando denuncias desde la API...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else if (filteredList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.outline
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = if (uiState.denuncias.isEmpty()) "No hay denuncias registradas." else "No se encontraron denuncias con este filtro.",
                            style = MaterialTheme.typography.titleMedium,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = onRefresh) {
                            Icon(Icons.Default.Refresh, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Cargar Denuncias")
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    items(filteredList) { denuncia ->
                        DenunciaCard(
                            denuncia = denuncia,
                            onClick = { onSelectDenuncia(denuncia) }
                        )
                    }

                    // Pagination Load More footer button
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            if (uiState.isLoadingList) {
                                CircularProgressIndicator(modifier = Modifier.size(28.dp))
                            } else if (uiState.hasMoreData) {
                                ElevatedButton(
                                    onClick = onLoadMore,
                                    modifier = Modifier.fillMaxWidth(0.8f)
                                ) {
                                    Text("Cargar 5 siguientes denuncias")
                                }
                            } else {
                                Text(
                                    text = "Se cargaron todas las denuncias disponibles.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }

        // Floating Action Button to create a new complaint
        FloatingActionButton(
            onClick = onNavigateToCreate,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp),
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Add, contentDescription = "Nueva Denuncia")
                Spacer(modifier = Modifier.width(4.dp))
                Text("Denunciar", fontWeight = FontWeight.Bold)
            }
        }
    }
}
