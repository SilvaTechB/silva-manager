package app.silva.manager.di

import app.silva.manager.data.platform.Filesystem
import app.silva.manager.data.platform.NetworkInfo
import app.silva.manager.domain.repository.*
import app.silva.manager.domain.worker.WorkerRepository
import app.silva.manager.network.api.SilvaAPI
import app.silva.manager.util.AppDataResolver
import org.koin.core.module.dsl.createdAtStart
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val repositoryModule = module {
    singleOf(::SilvaAPI)
    singleOf(::Filesystem) {
        createdAtStart()
    }
    singleOf(::NetworkInfo)
    singleOf(::PatchSelectionRepository)
    singleOf(::PatchOptionsRepository)
    singleOf(::PatchBundleRepository) {
        // It is best to load patch bundles ASAP
        createdAtStart()
    }
    singleOf(::WorkerRepository)
    singleOf(::InstalledAppRepository)
    singleOf(::OriginalApkRepository)
    singleOf(::AppDataResolver)
}
