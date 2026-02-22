package com.farmazim.app.presentation.ui.livestock

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
import com.farmazim.app.domain.model.LivestockGroup

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LivestockScreen(
    contentPadding: PaddingValues,
    onUpgradeRequired: () -> Unit,
    viewModel: LivestockViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    // Gate: redirect to paywall if not premium
    LaunchedEffect(uiState.isPremium) {
        if (!uiState.isPremium) onUpgradeRequired()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.livestock_list_title)) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = null)
            }
        }
    ) { innerPadding ->
        if (uiState.livestock.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                Text(stringResource(R.string.livestock_empty_state), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.livestock, key = { it.id }) { group ->
                    LivestockCard(group = group, onDelete = { viewModel.deleteLivestock(group) })
                }
            }
        }
    }

    if (showAddDialog) {
        AddLivestockDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { species, count, notes ->
                viewModel.addLivestock(species, count, notes)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun LivestockCard(group: LivestockGroup, onDelete: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Pets, null, tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(group.species, style = MaterialTheme.typography.titleSmall)
                Text("${group.count} animals", style = MaterialTheme.typography.bodySmall)
                group.notes?.let { Text(it, style = MaterialTheme.typography.bodySmall) }
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, "Delete", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
fun AddLivestockDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, Int, String?) -> Unit
) {
    var species by remember { mutableStateOf("") }
    var count by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var speciesError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.livestock_add_title)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = species, onValueChange = { species = it; speciesError = false },
                    label = { Text(stringResource(R.string.livestock_species_label)) },
                    isError = speciesError, modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = count, onValueChange = { count = it },
                    label = { Text(stringResource(R.string.livestock_count_label)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = notes, onValueChange = { notes = it },
                    label = { Text(stringResource(R.string.livestock_notes_label)) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                speciesError = species.isBlank()
                if (!speciesError) onConfirm(species.trim(), count.toIntOrNull() ?: 0, notes.ifBlank { null })
            }) { Text(stringResource(R.string.button_save)) }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text(stringResource(R.string.button_cancel)) } }
    )
}
