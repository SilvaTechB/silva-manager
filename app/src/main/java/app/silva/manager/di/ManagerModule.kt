package app.silva.manager.di

import app.silva.manager.domain.installer.InstallerManager
import app.silva.manager.domain.installer.RootInstaller
import app.silva.manager.domain.installer.ShizukuInstaller
import app.silva.manager.domain.manager.AppIconManager
import app.silva.manager.domain.manager.HomeAppButtonPreferences
import app.silva.manager.domain.manager.KeystoreManager
import app.silva.manager.domain.manager.PatchOptionsPreferencesManager
import app.silva.manager.util.PM
import app.silva.manager.util.UpdateNotificationManager
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val managerModule = module {
    singleOf(::KeystoreManager)
    singleOf(::PM)
    singleOf(::RootInstaller)
    singleOf(::ShizukuInstaller)
    singleOf(::InstallerManager)
    singleOf(::PatchOptionsPreferencesManager)
    singleOf(::AppIconManager)
    singleOf(::UpdateNotificationManager)
    singleOf(::HomeAppButtonPreferences)
}
