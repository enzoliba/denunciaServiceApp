package com.linaresbar.appdenuncias.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.linaresbar.appdenuncias.ui.viewmodel.FormState

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun DenunciaFormScreen(
    formState: FormState,
    onDniChanged: (String) -> Unit,
    onTipoChanged: (String) -> Unit,
    onDireccionChanged: (String) -> Unit,
    onDescripcionChanged: (String) -> Unit,
    onFechaChanged: (String) -> Unit,
    onSubmit: () -> Unit
) {
    val scrollState = rememberScrollState()

    val tiposPredefinidos = listOf("ROBO", "ASALTO", "VANDALISMO", "ACCIDENTE", "OTRO")
    var expandedTipoDropdown by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        Text(
            text = "Registrar Denuncia Ciudadana",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Complete la información requerida para registrar el hecho de inseguridad.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Success Alert
        if (formState.successMessage != null) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xE8E8F5E9)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color(0xFF2E7D32),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = formState.successMessage,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF1B5E20),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // Error Alert
        if (formState.errorMessage != null) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = formState.errorMessage,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
        }

        // Form Card
        OutlinedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {

                // DNI Input
                OutlinedTextField(
                    value = formState.dni,
                    onValueChange = onDniChanged,
                    label = { Text("DNI del Ciudadano *") },
                    placeholder = { Text("Ej: 71234567") },
                    leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    supportingText = { Text("${formState.dni.length}/8 dígitos") },
                    isError = formState.dni.isNotBlank() && formState.dni.length != 8,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Tipo Dropdown / Chips Selection
                Text(
                    text = "Tipo de Incidente *",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                FlowRow(modifier = Modifier.fillMaxWidth()) {
                    tiposPredefinidos.forEach { tipo ->
                        FilterChip(
                            selected = formState.tipo.uppercase() == tipo,
                            onClick = { onTipoChanged(tipo) },
                            label = { Text(tipo) },
                            leadingIcon = if (formState.tipo.uppercase() == tipo) {
                                { Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp)) }
                            } else null,
                            modifier = Modifier.padding(end = 6.dp, bottom = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Dirección Input
                OutlinedTextField(
                    value = formState.direccion,
                    onValueChange = onDireccionChanged,
                    label = { Text("Dirección *") },
                    placeholder = { Text("Ej: Av. Balta 1234, Chiclayo") },
                    leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) },
                    singleLine = true,
                    supportingText = { Text("${formState.direccion.length}/200 caracteres") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Descripción Input
                OutlinedTextField(
                    value = formState.descripcion,
                    onValueChange = onDescripcionChanged,
                    label = { Text("Descripción del Hecho *") },
                    placeholder = { Text("Detalle lo ocurrido...") },
                    leadingIcon = { Icon(Icons.Default.Description, contentDescription = null) },
                    minLines = 3,
                    maxLines = 5,
                    supportingText = { Text("${formState.descripcion.length}/255 caracteres") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Fecha Input
                OutlinedTextField(
                    value = formState.fecha,
                    onValueChange = onFechaChanged,
                    label = { Text("Fecha y Hora (yyyy-MM-dd HH:mm:ss)") },
                    leadingIcon = { Icon(Icons.Default.CalendarToday, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Estado info badge (Default 1 = Registrada/Pendiente)
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.ReportProblem,
                            contentDescription = null,
                            tint = Color(0xFFD32F2F)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Estado Inicial: PENDIENTE (1)",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFD32F2F)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Submit Button
                Button(
                    onClick = onSubmit,
                    enabled = !formState.isSubmitting && formState.dni.length == 8 && formState.direccion.isNotBlank() && formState.descripcion.isNotBlank(),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (formState.isSubmitting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Registrando...")
                    } else {
                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Registrar Denuncia", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
