package app.silva.manager.patcher.patch

import android.os.Parcelable
import app.morphe.patcher.patch.Patch
import app.morphe.patcher.patch.loadPatchesFromDex
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import java.io.File
import java.io.IOException
import java.util.jar.JarFile

@Parcelize
data class PatchBundle(val patchesJar: String) : Parcelable {
    /**
     * The [java.util.jar.Manifest] of [patchesJar].
     */
    @IgnoredOnParcel
    private val manifest by lazy {
        try {
            JarFile(patchesJar).use { it.manifest }
        } catch (_: IOException) {
            null
        }
    }

    @IgnoredOnParcel
    val manifestAttributes by lazy {
        if (manifest != null)
            ManifestAttributes(
                name = readManifestAttribute("Name"),
                version = readManifestAttribute("Version"),
                description = readManifestAttribute("Description"),
                source = readManifestAttribute("Source"),
                author = readManifestAttribute("Author"),
                contact = readManifestAttribute("Contact"),
                website = readManifestAttribute("Website"),
                license = readManifestAttribute("License"),
                patcherVersion = readManifestAttribute("Patcher-Version"),
            ) else
            null
    }

    private fun readManifestAttribute(name: String) = manifest?.mainAttributes?.getValue(name)
        ?.takeIf { it.isNotBlank() } // If empty, set it to null instead.

    data class ManifestAttributes(
        val name: String?,
        val version: String?,
        val description: String?,
        val source: String?,
        val author: String?,
        val contact: String?,
        val website: String?,
        val license: String?,
        val patcherVersion: String?
    )

    object Loader {
        private fun loadBundle(bundle: PatchBundle): Collection<Patch<*>> {
            validateDexEntries(bundle.patchesJar)
            val patchFiles = runCatching {
                loadPatchesFromDex(setOf(File(bundle.patchesJar))).byPatchesFile
            }.getOrElse { error ->
                throw IllegalStateException("Patch bundle is corrupted or incomplete", error)
            }
            val entry = patchFiles.entries.singleOrNull()
                ?: throw IllegalStateException("Unexpected patch bundle load result for ${bundle.patchesJar}")

            return entry.value
        }

        private fun metadataFor(bundle: PatchBundle) = loadBundle(bundle).map(::PatchInfo)

        fun metadata(bundles: Iterable<PatchBundle>) =
            bundles.associateWith(::metadataFor)

        fun metadata(bundle: PatchBundle) = metadataFor(bundle)

        fun patches(bundles: Iterable<PatchBundle>, packageName: String) =
            bundles.associateWith { bundle ->
                loadBundle(bundle).filter { patch ->
                    val compatibility = patch.compatibility
                        ?: return@filter true // Universal patch

                    compatibility.any { compat ->
                        compat.packageName == packageName ||
                                compat.packageName == null // Universal compatibility entry
                    }
                }.toSet()
            }

        private fun validateDexEntries(jarPath: String) {
            JarFile(jarPath).use { jar ->
                val entries = jar.entries().toList()
                val dexEntries = entries.filter { it.name.lowercase().endsWith(".dex") }
                val classEntries = entries.filter { it.name.lowercase().endsWith(".class") }

                when {
                    dexEntries.isNotEmpty() -> {
                        val hasEmptyDex = dexEntries.any { it.size <= 0L }
                        if (hasEmptyDex) {
                            throw IllegalStateException("Patch bundle contains empty dex entries")
                        }
                    }
                    classEntries.isNotEmpty() -> {
                        // Bundle uses JVM class files — valid format for this patcher version
                    }
                    else -> throw IllegalStateException("Patch bundle is missing dex or class entries")
                }
            }
        }
    }
}
