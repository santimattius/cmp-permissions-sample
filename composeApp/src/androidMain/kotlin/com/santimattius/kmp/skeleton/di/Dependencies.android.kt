package com.santimattius.kmp.skeleton.di

import android.content.Context
import com.santimattius.kmp.skeleton.initializer.applicationContext
import dev.icerock.moko.permissions.PermissionsController
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModules: Module
    get() = module {
        single<Context> {
            applicationContext ?: run {
                throw IllegalStateException("Context is null")
            }
        }
        single<PermissionsController> { PermissionsController(get<Context>()) }
    }