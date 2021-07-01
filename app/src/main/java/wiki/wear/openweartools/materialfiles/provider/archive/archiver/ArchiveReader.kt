/*
 * Copyright (c) 2018 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package wiki.wear.openweartools.materialfiles.provider.archive.archiver

import android.os.Build
import androidx.preference.PreferenceManager
import eu.chainfire.librootjava.RootJava
import java8.nio.charset.StandardCharsets
import java8.nio.file.AccessMode
import java8.nio.file.NoSuchFileException
import java8.nio.file.NotLinkException
import java8.nio.file.Path
import wiki.wear.openweartools.BuildConfig
import wiki.wear.openweartools.R
import wiki.wear.openweartools.materialfiles.compat.use
import wiki.wear.openweartools.materialfiles.provider.common.IsDirectoryException
import wiki.wear.openweartools.materialfiles.provider.common.PosixFileType
import wiki.wear.openweartools.materialfiles.provider.common.checkAccess
import wiki.wear.openweartools.materialfiles.provider.common.posixFileType
import wiki.wear.openweartools.materialfiles.provider.root.isRunningAsRoot
import wiki.wear.openweartools.materialfiles.settings.Settings
import wiki.wear.openweartools.materialfiles.util.valueCompat
import org.apache.commons.compress.archivers.ArchiveEntry
import org.apache.commons.compress.archivers.ArchiveInputStream
import org.apache.commons.compress.archivers.ArchiveStreamFactory
import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry
import org.apache.commons.compress.archivers.sevenz.SevenZFile
import org.apache.commons.compress.archivers.tar.TarArchiveEntry
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry
import org.apache.commons.compress.compressors.CompressorException
import org.apache.commons.compress.compressors.CompressorStreamFactory
import java.io.BufferedInputStream
import java.io.Closeable
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream
import java.util.Date
import org.apache.commons.compress.archivers.ArchiveException as ApacheArchiveException

object ArchiveReader {
    private val compressorStreamFactory = CompressorStreamFactory()
    private val archiveStreamFactory = ArchiveStreamFactory()

    @Throws(IOException::class)
    fun readEntries(
        file: Path,
        rootPath: Path
    ): Pair<Map<Path, ArchiveEntry>, Map<Path, List<Path>>> {
        val entries = mutableMapOf<Path, ArchiveEntry>()
        val rawEntries = readEntries(file)
        for (entry in rawEntries) {
            var path = rootPath.resolve(entry.name)
            // Normalize an absolute path to prevent path traversal attack.
            if (!path.isAbsolute) {
                // TODO: Will this actually happen?
                throw AssertionError("Path must be absolute: $path")
            }
            if (path.nameCount > 0) {
                path = path.normalize()
                if (path.nameCount == 0) {
                    // Don't allow a path to become the root path only after normalization.
                    continue
                }
            }
            entries.getOrPut(path) { entry }
        }
        entries.getOrPut(rootPath) { DirectoryArchiveEntry("") }
        val tree = mutableMapOf<Path, MutableList<Path>>()
        tree[rootPath] = mutableListOf()
        val paths = entries.keys.toList()
        for (path in paths) {
            var path = path
            while (true) {
                val parentPath = path.parent ?: break
                val entry = entries[path]!!
                if (entry.isDirectory) {
                    tree.getOrPut(path) { mutableListOf() }
                }
                tree.getOrPut(parentPath) { mutableListOf() }.add(path)
                if (entries.containsKey(parentPath)) {
                    break
                }
                entries[parentPath] = DirectoryArchiveEntry(parentPath.toString())
                path = parentPath
            }
        }
        return entries to tree
    }

    @Throws(IOException::class)
    private fun readEntries(file: Path): List<ArchiveEntry> {
        val javaFile = file.toFile()
        val compressorType: String?
        val archiveType = try {
            javaFile.inputStream().buffered().use { inputStream ->
                compressorType = try {
                    // inputStream must be buffered for markSupported().
                    CompressorStreamFactory.detect(inputStream)
                } catch (e: CompressorException) {
                    // Ignored.
                    null
                }
                val compressorInputStream = if (compressorType != null) {
                    compressorStreamFactory.createCompressorInputStream(compressorType, inputStream)
                        .buffered()
                } else {
                    inputStream
                }
                try {
                    // compressorInputStream must be buffered for markSupported().
                    compressorInputStream.use { detectArchiveType(it) }
                } catch (e: ApacheArchiveException) {
                    throw ArchiveException(e)
                } catch (e: CompressorException) {
                    throw ArchiveException(e)
                }
            }
        } catch (e: FileNotFoundException) {
            file.checkAccess(AccessMode.READ)
            throw NoSuchFileException(file.toString()).apply { initCause(e) }
        }
        val encoding = archiveFileNameEncoding
        if (compressorType == null) {
            when (archiveType) {
                ArchiveStreamFactory.ZIP ->
                    return ZipFileCompat(javaFile, encoding).use { it.entries.toList() }
                ArchiveStreamFactory.SEVEN_Z -> {
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                        throw IOException(UnsupportedOperationException("SevenZFile"))
                    }
                    return SevenZFile(javaFile).use { it.entries.toList() }
                }
                RarFile.RAR -> return RarFile(javaFile, encoding).use { it.entries.toList() }
                // Unnecessary, but teaches lint that compressorType != null below might be false.
                else -> {}
            }
        }
        return try {
            javaFile.inputStream().buffered().use { inputStream ->
                val compressorInputStream = if (compressorType != null) {
                    compressorStreamFactory.createCompressorInputStream(compressorType, inputStream)
                } else {
                    inputStream
                }
                compressorInputStream.use {
                    archiveStreamFactory.createArchiveInputStream(
                        archiveType, compressorInputStream, encoding
                    ).use { archiveInputStream ->
                        val entries = mutableListOf<ArchiveEntry>()
                        while (true) {
                            val entry = archiveInputStream.nextEntry ?: break
                            entries += entry
                        }
                        entries
                    }
                }
            }
        } catch (e: FileNotFoundException) {
            throw NoSuchFileException(file.toString()).apply { initCause(e) }
        } catch (e: ApacheArchiveException) {
            throw ArchiveException(e)
        } catch (e: CompressorException) {
            throw ArchiveException(e)
        }
    }

    private val archiveFileNameEncoding: String
        get() =
            if (isRunningAsRoot) {
                try {
                    val context = RootJava.getPackageContext(BuildConfig.APPLICATION_ID)
                    val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
                    val key = context.getString(R.string.pref_key_archive_file_name_encoding)
                    val defaultValue = context.getString(
                        R.string.pref_default_value_archive_file_name_encoding
                    )
                    sharedPreferences.getString(key, defaultValue)!!
                } catch (e: Exception) {
                    e.printStackTrace()
                    StandardCharsets.UTF_8.name()
                }
            } else {
                Settings.ARCHIVE_FILE_NAME_ENCODING.valueCompat
            }

    @Throws(IOException::class)
    fun newInputStream(file: Path, entry: ArchiveEntry): InputStream {
        if (entry.isDirectory) {
            throw IsDirectoryException(file.toString())
        }
        val javaFile = file.toFile()
        val compressorType: String?
        val archiveType = try {
            javaFile.inputStream().buffered().use { inputStream ->
                compressorType = try {
                    // inputStream must be buffered for markSupported().
                    CompressorStreamFactory.detect(inputStream)
                } catch (e: CompressorException) {
                    // Ignored.
                    null
                }
                val compressorInputStream = if (compressorType != null) {
                    compressorStreamFactory.createCompressorInputStream(compressorType, inputStream)
                        .buffered()
                } else {
                    inputStream
                }
                try {
                    // compressorInputStream must be buffered for markSupported().
                    compressorInputStream.use { detectArchiveType(it) }
                } catch (e: ApacheArchiveException) {
                    throw ArchiveException(e)
                } catch (e: CompressorException) {
                    throw ArchiveException(e)
                }
            }
        } catch (e: FileNotFoundException) {
            file.checkAccess(AccessMode.READ)
            throw NoSuchFileException(file.toString()).apply { initCause(e) }
        }
        val encoding = archiveFileNameEncoding
        if (compressorType == null) {
            when (entry) {
                is ZipArchiveEntry -> {
                    var successful = false
                    var zipFile: ZipFileCompat? = null
                    var zipEntryInputStream: InputStream? = null
                    return try {
                        zipFile = ZipFileCompat(javaFile, encoding)
                        zipEntryInputStream = zipFile.getInputStream(entry)
                            ?: throw NoSuchFileException(file.toString())
                        val inputStream = CloseableInputStream(zipEntryInputStream, zipFile)
                        successful = true
                        inputStream
                    } finally {
                        if (!successful) {
                            zipEntryInputStream?.close()
                            zipFile?.close()
                        }
                    }
                }
                is SevenZArchiveEntry -> {
                    var successful = false
                    var sevenZFile: SevenZFile? = null
                    return try {
                        sevenZFile = SevenZFile(javaFile)
                        var inputStream: InputStream? = null
                        while (true) {
                            val currentEntry = sevenZFile.nextEntry ?: break
                            if (currentEntry.name != entry.name) {
                                continue
                            }
                            inputStream = SevenZArchiveEntryInputStream(sevenZFile, currentEntry)
                            successful = true
                            break
                        }
                        inputStream ?: throw NoSuchFileException(file.toString())
                    } finally {
                        if (!successful) {
                            sevenZFile?.close()
                        }
                    }
                }
                is RarArchiveEntry -> {
                    var successful = false
                    var rarFile: RarFile? = null
                    return try {
                        rarFile = RarFile(javaFile, encoding)
                        var inputStream: InputStream? = null
                        while (true) {
                            val currentEntry = rarFile.nextEntry ?: break
                            if (currentEntry.name != entry.name) {
                                continue
                            }
                            inputStream = rarFile.getInputStream(currentEntry)
                            successful = true
                            break
                        }
                        inputStream ?: throw NoSuchFileException(file.toString())
                    } finally {
                        if (!successful) {
                            rarFile?.close()
                        }
                    }
                }
                // Unnecessary, but teaches lint that compressorType != null below might be false.
                else -> {}
            }
        }
        var successful = false
        var inputStream: BufferedInputStream? = null
        var compressorInputStream: InputStream? = null
        var archiveInputStream: ArchiveInputStream? = null
        return try {
            inputStream = javaFile.inputStream().buffered()
            compressorInputStream = if (compressorType != null) {
                compressorStreamFactory.createCompressorInputStream(compressorType, inputStream)
            } else {
                inputStream
            }
            archiveInputStream = archiveStreamFactory.createArchiveInputStream(
                archiveType, compressorInputStream, encoding
            )
            while (true) {
                val currentEntry = archiveInputStream.nextEntry ?: break
                if (currentEntry.name != entry.name) {
                    continue
                }
                successful = true
                break
            }
            if (successful) {
                archiveInputStream
            } else {
                throw NoSuchFileException(file.toString())
            }
        } catch (e: FileNotFoundException) {
            throw NoSuchFileException(file.toString()).apply { initCause(e) }
        } catch (e: ApacheArchiveException) {
            throw ArchiveException(e)
        } catch (e: CompressorException) {
            throw ArchiveException(e)
        } finally {
            if (!successful) {
                archiveInputStream?.close()
                compressorInputStream?.close()
                inputStream?.close()
            }
        }
    }

    @Throws(ApacheArchiveException::class)
    private fun detectArchiveType(inputStream: InputStream): String =
        try {
            RarFile.detect(inputStream)
        } catch (e: IOException) {
            throw ApacheArchiveException("RarFile.detect()", e)
        } ?: ArchiveStreamFactory.detect(inputStream)

    @Throws(IOException::class)
    fun readSymbolicLink(file: Path, entry: ArchiveEntry): String {
        if (!isSymbolicLink(entry)) {
            throw NotLinkException(file.toString())
        }
        return if (entry is TarArchiveEntry) {
            entry.linkName
        } else {
            newInputStream(file, entry).use { it.reader(StandardCharsets.UTF_8).readText() }
        }
    }

    private fun isSymbolicLink(entry: ArchiveEntry): Boolean =
        entry.posixFileType == PosixFileType.SYMBOLIC_LINK

    private class DirectoryArchiveEntry(name: String) : ArchiveEntry {
        init {
            require(!name.endsWith("/")) { "name $name should not end with a slash" }
        }

        private val name = "$name/"

        override fun getName(): String = name

        override fun getSize(): Long = 0

        override fun isDirectory(): Boolean = true

        override fun getLastModifiedDate(): Date = Date(-1)

        override fun equals(other: Any?): Boolean {
            if (this === other) {
                return true
            }
            if (javaClass != other?.javaClass) {
                return false
            }
            other as DirectoryArchiveEntry
            return name == other.name
        }

        override fun hashCode(): Int = name.hashCode()
    }

    private class CloseableInputStream(
        private val inputStream: InputStream,
        private val closeable: Closeable
    ) : InputStream() {
        @Throws(IOException::class)
        override fun available(): Int = inputStream.available()

        @Throws(IOException::class)
        override fun read(): Int = inputStream.read()

        @Throws(IOException::class)
        override fun read(b: ByteArray): Int = inputStream.read(b)

        @Throws(IOException::class)
        override fun read(b: ByteArray, off: Int, len: Int): Int = inputStream.read(b, off, len)

        @Throws(IOException::class)
        override fun close() {
            inputStream.close()
            closeable.close()
        }
    }

    private class SevenZArchiveEntryInputStream(
        private val file: SevenZFile,
        private val entry: SevenZArchiveEntry
    ) : InputStream() {
        override fun available(): Int {
            val size = entry.size
            val read = file.statisticsForCurrentEntry
                .uncompressedCount
            val available = size - read
            return available.coerceAtMost(Int.MAX_VALUE.toLong()).toInt()
        }

        @Throws(IOException::class)
        override fun read(): Int = file.read()

        @Throws(IOException::class)
        override fun read(b: ByteArray): Int = file.read(b)

        @Throws(IOException::class)
        override fun read(b: ByteArray, off: Int, len: Int): Int = file.read(b, off, len)

        @Throws(IOException::class)
        override fun close() {
            file.close()
        }
    }
}
