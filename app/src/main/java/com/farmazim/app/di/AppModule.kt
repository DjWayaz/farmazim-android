package com.farmazim.app.di

import android.content.Context
import androidx.room.Room
import com.farmazim.app.data.local.AppDatabase
import com.farmazim.app.data.local.dao.*
import com.farmazim.app.data.repository.*
import com.farmazim.app.domain.repository.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "farmazim.db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides fun providePlotDao(db: AppDatabase): PlotDao = db.plotDao()
    @Provides fun provideCropDao(db: AppDatabase): CropRecordDao = db.cropRecordDao()
    @Provides fun provideInputDao(db: AppDatabase): InputRecordDao = db.inputRecordDao()
    @Provides fun provideLivestockGroupDao(db: AppDatabase): LivestockGroupDao = db.livestockGroupDao()
    @Provides fun provideLivestockEventDao(db: AppDatabase): LivestockEventDao = db.livestockEventDao()

    @Provides
    @Singleton
    fun providePlotRepository(dao: PlotDao): PlotRepository = PlotRepositoryImpl(dao)

    @Provides
    @Singleton
    fun provideCropRepository(dao: CropRecordDao): CropRepository = CropRepositoryImpl(dao)

    @Provides
    @Singleton
    fun provideInputRepository(dao: InputRecordDao): InputRepository = InputRepositoryImpl(dao)

    @Provides
    @Singleton
    fun provideLivestockRepository(
        groupDao: LivestockGroupDao,
        eventDao: LivestockEventDao
    ): LivestockRepository = LivestockRepositoryImpl(groupDao, eventDao)

    @Provides
    @Singleton
    fun provideFinanceRepository(
        cropDao: CropRecordDao,
        inputDao: InputRecordDao
    ): FinanceRepository = FinanceRepositoryImpl(cropDao, inputDao)
}
