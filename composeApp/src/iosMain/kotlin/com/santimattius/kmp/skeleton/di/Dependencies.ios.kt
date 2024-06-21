package com.santimattius.kmp.skeleton.di

import dev.icerock.moko.permissions.PermissionsController
import org.koin.core.module.Module
import org.koin.dsl.module
import dev.icerock.moko.permissions.ios.PermissionsController as IOSPermissionsController

actual val platformModules: Module
    get() = module {
        single<PermissionsController> { IOSPermissionsController() }
    }