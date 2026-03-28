package app.silva.manager.di

import app.silva.manager.domain.installer.RootInstaller
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val rootModule = module {
    singleOf(::RootInstaller)
}