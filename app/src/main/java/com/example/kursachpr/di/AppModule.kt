package com.example.kursachpr.di

import com.example.kursachpr.data.database.AppDatabase
import com.example.kursachpr.data.repository.AppRepository
import com.example.kursachpr.viewmodel.MainViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

/**
 * DatabaseModule - модуль для предоставления базы данных и DAO
 * @Singleton - база данных создаётся один раз на всё приложение
 */
val databaseModule = module {
    // Singleton: AppDatabase
    single { AppDatabase.getDatabase(androidContext()) }
    
    // DAO провайдеры
    single { get<AppDatabase>().userDao() }
    single { get<AppDatabase>().clubDao() }
    single { get<AppDatabase>().childDao() }
    single { get<AppDatabase>().reviewDao() }
    single { get<AppDatabase>().applicationDao() }
    single { get<AppDatabase>().favoriteDao() }
}

/**
 * RepositoryModule - модуль для предоставления репозитория
 * Репозиторий получает все DAO через инъекцию зависимостей
 */
val repositoryModule = module {
    single {
        AppRepository(
            userDao = get(),
            clubDao = get(),
            childDao = get(),
            reviewDao = get(),
            applicationDao = get(),
            favoriteDao = get()
        )
    }
}

/**
 * ViewModelModule - модуль для предоставления ViewModel
 * ViewModel получает репозиторий через инъекцию
 */
val viewModelModule = module {
    viewModel { MainViewModel(get()) }
}

/**
 * Все модули приложения для регистрации в Koin
 */
val appModules = listOf(
    databaseModule,
    repositoryModule,
    viewModelModule
)


