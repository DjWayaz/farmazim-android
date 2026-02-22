package com.farmazim.app.presentation.ui.finance

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.farmazim.app.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinanceScreen(
    contentPadding: PaddingValues,
    onUpgradeRequired: () -> Unit,
    viewModel: FinanceViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.isPremium) {
        if (!uiState.isPremium) onUpgradeRequired()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.finance_title)) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            )
        }
    ) { innerPadding ->
        if (uiState.isLoading) {
            Box(Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            val summary = uiState.summary
            if (summary == null) {
                Box(Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                    Text(stringResource(R.string.finance_empty_state), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                }
            } else {
                Column(
                    modifier = Modifier.fillMaxSize().padding(innerPadding).padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    FinanceSummaryCard(
                        label = stringResource(R.string.finance_total_income),
                        amount = summary.totalIncomeUsd,
                        icon = Icons.Default.TrendingUp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    FinanceSummaryCard(
                        label = stringResource(R.string.finance_total_expenses),
                        amount = summary.totalExpensesUsd,
                        icon = Icons.Default.TrendingDown,
                        color = MaterialTheme.colorScheme.error
                    )
                    HorizontalDivider()
                    FinanceSummaryCard(
                        label = stringResource(R.string.finance_net_profit),
                        amount = summary.netProfitUsd,
                        icon = Icons.Default.AccountBalance,
                        color = if (summary.netProfitUsd >= 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                        emphasized = true
                    )
                }
            }
        }
    }
}

@Composable
fun FinanceSummaryCard(
    label: String,
    amount: Double,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: androidx.compose.ui.graphics.Color,
    emphasized: Boolean = false
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (emphasized) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
        )
    ) {
        Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = color, modifier = Modifier.size(if (emphasized) 32.dp else 24.dp))
            Spacer(Modifier.width(16.dp))
            Column {
                Text(label, style = MaterialTheme.typography.bodyMedium)
                Text(
                    text = "$${String.format("%.2f", amount)}",
                    style = if (emphasized) MaterialTheme.typography.headlineMedium else MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = color
                )
            }
        }
    }
}
