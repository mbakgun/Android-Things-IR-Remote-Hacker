package com.mbakgun.things.di.modules

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.mbakgun.things.App
import com.mbakgun.things.data.IrDao
import com.mbakgun.things.data.IrDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Created by burakakgun on 8.06.2019.
 */
@Module
class AppModule {

    @Provides
    @Singleton
    fun provideApplication(app: App): Application = app

    @Provides
    @Singleton
    fun provideApplicationContext(app: App): Context = app.applicationContext

    @Singleton
    @Provides
    fun provideDb(app: Application): IrDatabase {
        return Room
            .databaseBuilder(app, IrDatabase::class.java, "irDbThings.db")
            .fallbackToDestructiveMigration()
            .build()
    }

    @Singleton
    @Provides
    fun provideIrDao(db: IrDatabase): IrDao {
        return db.getIrDao()
    }
}
