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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.farmazim.app.R
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

    uiState.error?.let {
        LaunchedEffect(it) { viewModel.clearError() }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.crop_list_title)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (viewModel.canAddPlot()) showAddPlotDialog = true
                    else onUpgradeRequired()
                },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = stringResource(R.string.button_add),
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    ) { innerPadding ->
        // Combine scaffold padding with bottom nav padding passed from NavGraph
        val topPadding = innerPadding.calculateTopPadding()
        val bottomPadding = maxOf(
            innerPadding.calculateBottomPadding(),
            contentPadding.calculateBottomPadding()
        )

        if (uiState.plots.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        top = topPadding,
                        bottom = bottomPadding,
                        start = 16.dp,
                        end = 16.dp
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.Grass,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                    )
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = stringResource(R.string.crop_empty_state),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Tap the + button below to add your first plot",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    top = topPadding + 8.dp,
                    bottom = bottomPadding + 80.dp, // extra space so FAB doesn't cover last item
                    start = 16.dp,
                    end = 16.dp
                ),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(uiState.plots, key = { it.id }) { plot ->
                    PlotCard(
                        plot = plot,
                        crops = uiState.crops.filter { it.plotId == plot.id },
                        onAddCrop = { showAddCropDialog = plot },
                        onDeletePlot = { viewModel.deletePlot(plot) },
                        onDeleteCrop = { viewModel.deleteCrop(it) }
                    )
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
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Landscape,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(plot.name, style = MaterialTheme.typography.titleMedium)
                    Text("${plot.sizeHectares} ha", style = MaterialTheme.typography.bodySmall)
                }
                IconButton(onClick = onAddCrop) {
                    Icon(Icons.Default.Add, contentDescription = "Add crop")
                }
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = "Toggle"
                    )
                }
                IconButton(onClick = onDeletePlot) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
            if (expanded && crops.isNotEmpty()) {
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                crops.forEach { crop ->
                    CropRow(crop = crop, onDelete = { onDeleteCrop(crop) })
                }
            }
            if (expanded && crops.isEmpty()) {
                Spacer(Modifier.height(4.dp))
                Text(
                    "No crops yet — tap + to add",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }
        }
    }
}

@Composable
fun CropRow(crop: CropRecord, onDelete: () -> Unit) {
    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            Icons.Default.Grass,
            null,
            tint = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.size(16.dp)
        )
        Spacer(Modifier.width(8.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(crop.cropType, style = MaterialTheme.typography.bodyMedium)
            Text(
                "Planted: ${dateFormat.format(Date(crop.plantedAt))}",
                style = MaterialTheme.typography.bodySmall
            )
            crop.yieldKg?.let {
                Text("Yield: ${it}kg", style = MaterialTheme.typography.bodySmall)
            }
            crop.saleAmountUsd?.let {
                Text("Sale: \$$it", style = MaterialTheme.typography.bodySmall)
            }
        }
        IconButton(onClick = onDelete) {
            Icon(
                Icons.Default.Delete,
                contentDescription = "Delete crop",
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.error
            )
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
                    value = name,
                    onValueChange = { name = it; nameError = false },
                    label = { Text(stringResource(R.string.crop_plot_name_label)) },
                    placeholder = { Text("e.g. North Field") },
                    isError = nameError,
                    supportingText = if (nameError) ({
                        Text(stringResource(R.string.error_required_field))
                    }) else null,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = size,
                    onValueChange = { size = it; sizeError = false },
                    label = { Text(stringResource(R.string.crop_plot_size_label)) },
                    placeholder = { Text("e.g. 0.5") },
                    isError = sizeError,
                    supportingText = if (sizeError) ({
                        Text(stringResource(R.string.error_invalid_number))
                    }) else null,
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
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.button_cancel)) }
        }
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
                    value = cropType,
                    onValueChange = { cropType = it; cropTypeError = false },
                    label = { Text(stringResource(R.string.crop_type_label)) },
                    placeholder = { Text("e.g. Maize, Soya, Groundnuts") },
                    isError = cropTypeError,
                    supportingText = if (cropTypeError) ({
                        Text(stringResource(R.string.error_required_field))
                    }) else null,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = yieldKg,
                    onValueChange = { yieldKg = it },
                    label = { Text(stringResource(R.string.crop_yield_label)) },
                    placeholder = { Text("e.g. 500") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = saleAmount,
                    onValueChange = { saleAmount = it },
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
                    onConfirm(
                        CropRecord(
                            plotId = plot.id,
                            cropType = cropType.trim(),
                            plantedAt = System.currentTimeMillis(),
                            yieldKg = yieldKg.toDoubleOrNull(),
                            saleAmountUsd = saleAmount.toDoubleOrNull()
                        )
                    )
                }
            }) { Text(stringResource(R.string.button_save)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.button_cancel)) }
        }
    )
}
