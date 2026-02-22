package com.farmazim.app.presentation.ui.home

import androidx.lifecycle.ViewModel
import com.farmazim.app.data.billing.PremiumManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    val premiumManager: PremiumManager
) : ViewModel()
