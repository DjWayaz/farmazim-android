package com.farmazim.app.presentation.ui.crop

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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.farmazim.app.R
import com.farmazim.app.data.billing.PremiumManager
import com.farmazim.app.domain.model.CropRecord
import com.farmazim.app.domain.model.Plot
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CropScreen(
    contentPadding: PaddingValues,
    onUpgradeRequired: () -> Unit,
    viewModel: CropViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showAddPlotDialog by remember { mutableStateOf(false) }
    var showAddCropDialog by remember { mutableStateOf<Plot?>(null) }

    uiState.error?.let { LaunchedEffect(it) { viewModel.clearError() } }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.crop_list_title)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                top = innerPadding.calculateTopPadding() + 16.dp,
                bottom = contentPadding.calculateBottomPadding() + 16.dp,
                start = 16.dp,
                end = 16.dp
            ),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (uiState.plots.isEmpty()) {
                // Empty state with big inline Add Plot button
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Default.Grass,
                                contentDescription = null,
                                modifier = Modifier.size(56.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(Modifier.height(12.dp))
                            Text(
                                "No plots yet",
                                style = MaterialTheme.typography.titleMedium,
                                textAlign = TextAlign.Center
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                "Add your first plot to start tracking crops",
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                            Spacer(Modifier.height(20.dp))
                            Button(
                                onClick = {
                                    if (viewModel.canAddPlot()) showAddPlotDialog = true
                                    else onUpgradeRequired()
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null)
                                Spacer(Modifier.width(8.dp))
                                Text("Add My First Plot")
                            }
                        }
                    }
                }
            } else {
                // Plot cards
                items(uiState.plots, key = { it.id }) { plot ->
                    PlotCard(
                        plot = plot,
                        crops = uiState.crops.filter { it.plotId == plot.id },
                        onAddCrop = { showAddCropDialog = plot },
                        onDeletePlot = { viewModel.deletePlot(plot) },
                        onDeleteCrop = { viewModel.deleteCrop(it) }
                    )
                }

                // Add another plot button below the list
                item {
                    OutlinedButton(
                        onClick = {
                            if (viewModel.canAddPlot()) showAddPlotDialog = true
                            else onUpgradeRequired()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text(
                            if (uiState.isPremium) "Add Another Plot"
                            else "Add Another Plot (${uiState.plotCount}/${PremiumManager.FREE_PLOT_LIMIT} free)"
                        )
                    }
                }
            }
        }
    }

    if (showAddPlotDialog) {
        AddPlotDialog(
            onDismiss = { showAddPlotDialog = false },
            onConfirm = { name, size ->
                viewModel.addPlot(name, size)
                showAddPlotDialog = false
            }
        )
    }

    showAddCropDialog?.let { plot ->
        AddCropDialog(
            plot = plot,
            onDismiss = { showAddCropDialog = null },
            onConfirm = { crop ->
                viewModel.addCrop(crop)
                showAddCropDialog = null
            }
        )
    }
}

@Composable
fun PlotCard(
    plot: Plot,
    crops: List<CropRecord>,
    onAddCrop: () -> Unit,
    onDeletePlot: () -> Unit,
    onDeleteCrop: (CropRecord) -> Unit
) {
    var expanded by remember { mutableStateOf(true) }
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header row
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Landscape, null, tint = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(plot.name, style = MaterialTheme.typography.titleMedium)
                    Text("${plot.sizeHectares} ha", style = MaterialTheme.typography.bodySmall)
                }
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = "Expand"
                    )
                }
                IconButton(onClick = onDeletePlot) {
                    Icon(Icons.Default.Delete, null, tint = MaterialTheme.colorScheme.error)
                }
            }

            if (expanded) {
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                if (crops.isEmpty()) {
                    Text(
                        "No crops recorded yet",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                    Spacer(Modifier.height(8.dp))
                } else {
                    crops.forEach { crop ->
                        CropRow(crop = crop, onDelete = { onDeleteCrop(crop) })
                    }
                    Spacer(Modifier.height(8.dp))
                }

                // Inline "Add Crop Record" button — impossible to miss
                Button(
                    onClick = onAddCrop,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Add Crop Record")
                }
            }
        }
    }
}

@Composable
fun CropRow(crop: CropRecord, onDelete: () -> Unit) {
    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) }
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Default.Grass, null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(16.dp))
        Spacer(Modifier.width(8.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(crop.cropType, style = MaterialTheme.typography.bodyMedium)
            Text("Planted: ${dateFormat.format(Date(crop.plantedAt))}", style = MaterialTheme.typography.bodySmall)
            crop.yieldKg?.let { Text("Yield: ${it}kg", style = MaterialTheme.typography.bodySmall) }
            crop.saleAmountUsd?.let { Text("Sale: \$$it", style = MaterialTheme.typography.bodySmall) }
        }
        IconButton(onClick = onDelete) {
            Icon(Icons.Default.Delete, null, modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.error)
        }
    }
}

@Composable
fun AddPlotDialog(onDismiss: () -> Unit, onConfirm: (String, Double) -> Unit) {
    var name by remember { mutableStateOf("") }
    var size by remember { mutableStateOf("") }
    var nameError by remember { mutableStateOf(false) }
    var sizeError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Plot") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name, onValueChange = { name = it; nameError = false },
                    label = { Text(stringResource(R.string.crop_plot_name_label)) },
                    placeholder = { Text("e.g. North Field") },
                    isError = nameError,
                    supportingText = if (nameError) ({ Text(stringResource(R.string.error_required_field)) }) else null,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = size, onValueChange = { size = it; sizeError = false },
                    label = { Text(stringResource(R.string.crop_plot_size_label)) },
                    placeholder = { Text("e.g. 0.5") },
                    isError = sizeError,
                    supportingText = if (sizeError) ({ Text(stringResource(R.string.error_invalid_number)) }) else null,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                nameError = name.isBlank()
                val sizeDouble = size.toDoubleOrNull()
                sizeError = sizeDouble == null || sizeDouble <= 0
                if (!nameError && !sizeError) onConfirm(name.trim(), sizeDouble!!)
            }) { Text(stringResource(R.string.button_save)) }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text(stringResource(R.string.button_cancel)) } }
    )
}

@Composable
fun AddCropDialog(plot: Plot, onDismiss: () -> Unit, onConfirm: (CropRecord) -> Unit) {
    var cropType by remember { mutableStateOf("") }
    var yieldKg by remember { mutableStateOf("") }
    var saleAmount by remember { mutableStateOf("") }
    var cropTypeError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Crop to ${plot.name}") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = cropType, onValueChange = { cropType = it; cropTypeError = false },
                    label = { Text(stringResource(R.string.crop_type_label)) },
                    placeholder = { Text("e.g. Maize, Soya, Groundnuts") },
                    isError = cropTypeError,
                    supportingText = if (cropTypeError) ({ Text(stringResource(R.string.error_required_field)) }) else null,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = yieldKg, onValueChange = { yieldKg = it },
                    label = { Text(stringResource(R.string.crop_yield_label)) },
                    placeholder = { Text("e.g. 500") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = saleAmount, onValueChange = { saleAmount = it },
                    label = { Text(stringResource(R.string.crop_sale_amount_label)) },
                    placeholder = { Text("e.g. 120.00") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                cropTypeError = cropType.isBlank()
                if (!cropTypeError) {
                    onConfirm(CropRecord(
                        plotId = plot.id,
                        cropType = cropType.trim(),
                        plantedAt = System.currentTimeMillis(),
                        yieldKg = yieldKg.toDoubleOrNull(),
                        saleAmountUsd = saleAmount.toDoubleOrNull()
                    ))
                }
            }) { Text(stringResource(R.string.button_save)) }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text(stringResource(R.string.button_cancel)) } }
    )
}
