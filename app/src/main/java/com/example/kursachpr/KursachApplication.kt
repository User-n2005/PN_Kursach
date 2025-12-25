package com.example.kursachpr

import android.app.Application
import com.example.kursachpr.data.database.AppDatabase
import com.example.kursachpr.di.appModules
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

/**
 * Класс приложения - точка входа для инициализации Koin DI
 * Регистрируется в AndroidManifest.xml
 */
class KursachApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        // Инициализация Koin DI
        startKoin {
            // Логирование для отладки (Level.ERROR в production)
            androidLogger(Level.DEBUG)
            
            // Контекст приложения для инъекций
            androidContext(this@KursachApplication)
            
            // Регистрация всех модулей DI
            modules(appModules)
        }
        
        // Инициализация базы данных с начальными данными
        CoroutineScope(Dispatchers.IO).launch {
            val database = AppDatabase.getDatabase(this@KursachApplication)
            AppDatabase.ensurePopulated(database)
        }
    }
}


