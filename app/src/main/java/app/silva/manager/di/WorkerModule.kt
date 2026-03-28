package app.silva.manager.di

import app.silva.manager.patcher.worker.PatcherWorker
import app.silva.manager.worker.UpdateCheckWorker
import org.koin.androidx.workmanager.dsl.workerOf
import org.koin.dsl.module

val workerModule = module {
    workerOf(::PatcherWorker)
    workerOf(::UpdateCheckWorker)
}
