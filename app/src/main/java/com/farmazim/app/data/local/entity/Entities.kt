package com.farmazim.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "plots")
data class PlotEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val sizeHectares: Double,
    val gpsLat: Double? = null,
    val gpsLng: Double? = null,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "crop_records",
    foreignKeys = [ForeignKey(
        entity = PlotEntity::class,
        parentColumns = ["id"],
        childColumns = ["plotId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("plotId")]
)
data class CropRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val plotId: Long,
    val cropType: String,
    val plantedAt: Long,
    val harvestedAt: Long? = null,
    val yieldKg: Double? = null,
    val saleAmountUsd: Double? = null,
    val notes: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "input_records",
    foreignKeys = [ForeignKey(
        entity = PlotEntity::class,
        parentColumns = ["id"],
        childColumns = ["plotId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("plotId")]
)
data class InputRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val plotId: Long,
    val inputType: String,
    val productName: String,
    val quantityKg: Double,
    val costUsd: Double,
    val appliedAt: Long,
    val notes: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "livestock_groups")
data class LivestockGroupEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val species: String,
    val count: Int,
    val acquiredAt: Long,
    val notes: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "livestock_events",
    foreignKeys = [ForeignKey(
        entity = LivestockGroupEntity::class,
        parentColumns = ["id"],
        childColumns = ["livestockGroupId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("livestockGroupId")]
)
data class LivestockEventEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val livestockGroupId: Long,
    val eventType: String,
    val date: Long,
    val notes: String? = null,
    val costUsd: Double? = null,
    val createdAt: Long = System.currentTimeMillis()
)
