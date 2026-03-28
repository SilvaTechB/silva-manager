package app.silva.manager.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import app.silva.manager.data.room.apps.installed.AppliedPatch
import app.silva.manager.data.room.apps.installed.InstalledApp
import app.silva.manager.data.room.apps.installed.InstalledAppDao
import app.silva.manager.data.room.apps.original.OriginalApk
import app.silva.manager.data.room.apps.original.OriginalApkDao
import app.silva.manager.data.room.bundles.PatchBundleDao
import app.silva.manager.data.room.bundles.PatchBundleEntity
import app.silva.manager.data.room.options.Option
import app.silva.manager.data.room.options.OptionDao
import app.silva.manager.data.room.options.OptionGroup
import app.silva.manager.data.room.selection.PatchSelection
import app.silva.manager.data.room.selection.SelectedPatch
import app.silva.manager.data.room.selection.SelectionDao
import kotlin.random.Random

@Database(
    entities = [
        PatchBundleEntity::class,
        PatchSelection::class,
        SelectedPatch::class,
        InstalledApp::class,
        AppliedPatch::class,
        OptionGroup::class,
        Option::class,
        OriginalApk::class
    ],
    version = 11
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun patchBundleDao(): PatchBundleDao
    abstract fun selectionDao(): SelectionDao
    abstract fun installedAppDao(): InstalledAppDao
    abstract fun optionDao(): OptionDao
    abstract fun originalApkDao(): OriginalApkDao

    companion object {
        fun generateUid() = Random.nextInt()
    }
}
