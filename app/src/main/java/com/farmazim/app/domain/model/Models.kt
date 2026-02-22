package com.farmazim.app.domain.model

data class Plot(
    val id: Long = 0,
    val name: String,
    val sizeHectares: Double,
    val gpsLat: Double? = null,
    val gpsLng: Double? = null,
    val createdAt: Long = System.currentTimeMillis()
)

data class CropRecord(
    val id: Long = 0,
    val plotId: Long,
    val cropType: String,
    val plantedAt: Long,
    val harvestedAt: Long? = null,
    val yieldKg: Double? = null,
    val saleAmountUsd: Double? = null,
    val notes: String? = null
)

data class InputRecord(
    val id: Long = 0,
    val plotId: Long,
    val inputType: InputType,
    val productName: String,
    val quantityKg: Double,
    val costUsd: Double,
    val appliedAt: Long,
    val notes: String? = null
)

enum class InputType { FERTILISER, PESTICIDE, SEED, LABOUR, OTHER }

data class LivestockGroup(
    val id: Long = 0,
    val species: String,
    val count: Int,
    val acquiredAt: Long,
    val notes: String? = null
)

data class LivestockEvent(
    val id: Long = 0,
    val livestockGroupId: Long,
    val eventType: LivestockEventType,
    val date: Long,
    val notes: String? = null,
    val costUsd: Double? = null
)

enum class LivestockEventType { VACCINATION, DEATH, TREATMENT, BIRTH, SALE }

data class FinanceSummary(
    val totalIncomeUsd: Double,
    val totalExpensesUsd: Double,
    val netProfitUsd: Double = totalIncomeUsd - totalExpensesUsd
)
