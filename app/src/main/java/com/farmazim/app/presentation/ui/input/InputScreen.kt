package com.farmazim.app.presentation.ui.input

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.farmazim.app.R
import com.farmazim.app.domain.model.InputRecord
import com.farmazim.app.domain.model.InputType
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputScreen(
    contentPadding: PaddingValues,
    viewModel: InputViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.input_list_title)) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.button_add))
            }
        }
    ) { innerPadding ->
        if (uiState.inputs.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                Text(stringResource(R.string.input_empty_state), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.inputs, key = { it.id }) { input ->
                    InputCard(
                        input = input,
                        plotName = uiState.plots.find { it.id == input.plotId }?.name ?: "Unknown Plot",
                        onDelete = { viewModel.deleteInput(input) }
                    )
                }
            }
        }
    }

    if (showAddDialog) {
        AddInputDialog(
            plots = uiState.plots,
            onDismiss = { showAddDialog = false },
            onConfirm = { input ->
                viewModel.addInput(input)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun InputCard(input: InputRecord, plotName: String, onDelete: () -> Unit) {
    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) }
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Science, null, tint = MaterialTheme.colorScheme.secondary)
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(input.productName, style = MaterialTheme.typography.titleSmall)
                Text("${input.inputType.name.lowercase().replaceFirstChar { it.uppercase() }} • $plotName", style = MaterialTheme.typography.bodySmall)
                Text("${input.quantityKg}kg • \$${input.costUsd} • ${dateFormat.format(Date(input.appliedAt))}", style = MaterialTheme.typography.bodySmall)
            }
            IconButton(onClick = onDelete) { Icon(Icons.Default.Delete, "Delete", tint = MaterialTheme.colorScheme.error) }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddInputDialog(
    plots: List<com.farmazim.app.domain.model.Plot>,
    onDismiss: () -> Unit,
    onConfirm: (InputRecord) -> Unit
) {
    var productName by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }
    var cost by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(InputType.FERTILISER) }
    var selectedPlotId by remember { mutableStateOf(plots.firstOrNull()?.id ?: 0L) }
    var productNameError by remember { mutableStateOf(false) }
    var typeExpanded by remember { mutableStateOf(false) }
    var plotExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.input_add_title)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                // Plot selector
                if (plots.isNotEmpty()) {
                    ExposedDropdownMenuBox(expanded = plotExpanded, onExpandedChange = { plotExpanded = it }) {
                        OutlinedTextField(
                            value = plots.find { it.id == selectedPlotId }?.name ?: "",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text(stringResource(R.string.input_plot_label)) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(plotExpanded) },
                            modifier = Modifier.menuAnchor().fillMaxWidth()
                        )
                        ExposedDropdownMenu(expanded = plotExpanded, onDismissRequest = { plotExpanded = false }) {
                            plots.forEach { plot ->
                                DropdownMenuItem(text = { Text(plot.name) }, onClick = { selectedPlotId = plot.id; plotExpanded = false })
                            }
                        }
                    }
                }
                // Input type selector
                ExposedDropdownMenuBox(expanded = typeExpanded, onExpandedChange = { typeExpanded = it }) {
                    OutlinedTextField(
                        value = selectedType.name.lowercase().replaceFirstChar { it.uppercase() },
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(stringResource(R.string.input_type_label)) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(typeExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(expanded = typeExpanded, onDismissRequest = { typeExpanded = false }) {
                        InputType.entries.forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type.name.lowercase().replaceFirstChar { it.uppercase() }) },
                                onClick = { selectedType = type; typeExpanded = false }
                            )
                        }
                    }
                }
                OutlinedTextField(
                    value = productName, onValueChange = { productName = it; productNameError = false },
                    label = { Text(stringResource(R.string.input_product_name_label)) },
                    isError = productNameError,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = quantity, onValueChange = { quantity = it },
                    label = { Text(stringResource(R.string.input_quantity_label)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = cost, onValueChange = { cost = it },
                    label = { Text(stringResource(R.string.input_cost_label)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                productNameError = productName.isBlank()
                if (!productNameError) {
                    onConfirm(InputRecord(
                        plotId = selectedPlotId,
                        inputType = selectedType,
                        productName = productName.trim(),
                        quantityKg = quantity.toDoubleOrNull() ?: 0.0,
                        costUsd = cost.toDoubleOrNull() ?: 0.0,
                        appliedAt = System.currentTimeMillis()
                    ))
                }
            }) { Text(stringResource(R.string.button_save)) }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text(stringResource(R.string.button_cancel)) } }
    )
}
