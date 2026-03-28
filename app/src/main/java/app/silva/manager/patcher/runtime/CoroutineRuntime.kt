package app.silva.manager.patcher.runtime

import android.content.Context
import app.silva.manager.patcher.Session
import app.silva.manager.patcher.logger.Logger
import app.silva.manager.patcher.patch.PatchBundle
import app.silva.manager.patcher.split.SplitApkPreparer
import app.silva.manager.patcher.worker.ProgressEventHandler
import app.silva.manager.ui.model.State
import app.silva.manager.util.Options
import app.silva.manager.util.PatchSelection
import java.io.File

/**
 * Simple [Runtime] implementation that runs the patcher using coroutines.
 */
class CoroutineRuntime(private val context: Context) : Runtime(context) {
    override suspend fun execute(
        inputFile: String,
        outputFile: String,
        packageName: String,
        selectedPatches: PatchSelection,
        options: Options,
        logger: Logger,
        onPatchCompleted: suspend () -> Unit,
        onProgress: ProgressEventHandler,
        stripNativeLibs: Boolean,
    ) {
        MemoryMonitor.startMemoryPolling(logger)

        val selectedBundles = selectedPatches.keys
        val bundles = bundles()
        val uids = bundles.entries.associate { (key, value) -> value to key }

        val allPatches =
            PatchBundle.Loader.patches(bundles.values, packageName)
                .mapKeys { (b, _) -> uids[b]!! }
                .filterKeys { it in selectedBundles }

        val patchList = selectedPatches.flatMap { (bundle, selected) ->
            allPatches[bundle]?.filter { it.name in selected }
                ?: throw IllegalArgumentException("Patch bundle $bundle does not exist")
        }

        // Set all patch options.
        options.forEach { (bundle, bundlePatchOptions) ->
            val patches = allPatches[bundle] ?: return@forEach
            val patchesByName = patches.associateBy { it.name }

            bundlePatchOptions.forEach { (patchName, configuredPatchOptions) ->
                // Morphe: Skip if patch doesn't exist in this bundle
                val patch = patchesByName[patchName] ?: return@forEach

                configuredPatchOptions.forEach { (key, value) ->
                    patch.options[key] = value
                }
            }
        }

        onProgress(null, State.COMPLETED, null) // Loading patches

        val preparation = SplitApkPreparer.prepareIfNeeded(
            File(inputFile),
            File(cacheDir),
            logger,
            stripNativeLibs
        )
        try {
            if (preparation.merged) {
                onProgress(null, State.COMPLETED, null)
            }

            Session(
                cacheDir,
                frameworkPath,
                aaptPath,
                context,
                logger,
                preparation.file,
                onPatchCompleted = onPatchCompleted,
                onProgress
            ).use { session ->
                session.run(
                    File(outputFile),
                    patchList
                )
            }
        } finally {
            preparation.cleanup()

            MemoryMonitor.stopMemoryPolling(logger)
        }
    }
}
