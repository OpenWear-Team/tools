/*
 * Copyright (c) 2019 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package wiki.wear.openweartools.materialfiles.provider.document.resolver

import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.Point
import android.net.Uri
import android.os.Build
import android.os.ParcelFileDescriptor
import android.provider.DocumentsContract
import androidx.annotation.RequiresApi
import wiki.wear.openweartools.materialfiles.app.contentResolver
import wiki.wear.openweartools.materialfiles.compat.DocumentsContractCompat
import wiki.wear.openweartools.materialfiles.file.MimeType
import wiki.wear.openweartools.materialfiles.provider.common.copyTo
import wiki.wear.openweartools.materialfiles.provider.content.resolver.Resolver
import wiki.wear.openweartools.materialfiles.provider.content.resolver.ResolverException
import wiki.wear.openweartools.materialfiles.provider.content.resolver.getLong
import wiki.wear.openweartools.materialfiles.provider.content.resolver.getString
import wiki.wear.openweartools.materialfiles.provider.content.resolver.moveToFirstOrThrow
import wiki.wear.openweartools.materialfiles.provider.content.resolver.requireString
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.Collections
import java.util.WeakHashMap

object DocumentResolver {
    // @see com.android.shell.BugreportStorageProvider#AUTHORITY
    private const val BUGREPORT_STORAGE_PROVIDER_AUTHORITY = "com.android.shell.documents"
    // @see com.android.mtp.MtpDocumentsProvider#AUTHORITY
    private const val MTP_DOCUMENTS_PROVIDER_AUTHORITY = "com.android.mtp.documents"

    private val LOCAL_AUTHORITIES = setOf(
        BUGREPORT_STORAGE_PROVIDER_AUTHORITY,
        DocumentsContractCompat.EXTERNAL_STORAGE_PROVIDER_AUTHORITY,
        MTP_DOCUMENTS_PROVIDER_AUTHORITY
    )
    private val COPY_UNSUPPORTED_AUTHORITIES = setOf(
        BUGREPORT_STORAGE_PROVIDER_AUTHORITY,
        DocumentsContractCompat.EXTERNAL_STORAGE_PROVIDER_AUTHORITY,
        MTP_DOCUMENTS_PROVIDER_AUTHORITY
    )
    private val MOVE_UNSUPPORTED_AUTHORITIES = setOf(
        MTP_DOCUMENTS_PROVIDER_AUTHORITY
    )
    private val REMOVE_UNSUPPORTED_AUTHORITIES = setOf(
        BUGREPORT_STORAGE_PROVIDER_AUTHORITY,
        DocumentsContractCompat.EXTERNAL_STORAGE_PROVIDER_AUTHORITY,
        MTP_DOCUMENTS_PROVIDER_AUTHORITY
    )

    private val pathDocumentIdCache = Collections.synchronizedMap(WeakHashMap<Path, String>())

    @Throws(ResolverException::class)
    fun checkExistence(path: Path) {
        // Prevent cache from interfering with our check. Cache will be added again if
        // queryDocumentId() succeeds.
        pathDocumentIdCache.remove(path)
        queryDocumentId(path)
    }

    @Throws(ResolverException::class)
    fun copy(
        sourcePath: Path,
        targetPath: Path,
        intervalMillis: Long,
        listener: ((Long) -> Unit)?
    ): Uri {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
            && sourcePath.hasSameAuthority(targetPath) && !sourcePath.isCopyUnsupported) {
            copyApi24(sourcePath, targetPath, intervalMillis, listener)
        } else {
            copyManually(sourcePath, targetPath, intervalMillis, listener)
        }
    }

    private val Path.isCopyUnsupported: Boolean
        get() = treeUri.authority in COPY_UNSUPPORTED_AUTHORITIES

    @RequiresApi(Build.VERSION_CODES.N)
    @Throws(ResolverException::class)
    private fun copyApi24(
        sourcePath: Path,
        targetPath: Path,
        intervalMillis: Long,
        listener: ((Long) -> Unit)?
    ): Uri {
        val sourceUri = getDocumentUri(sourcePath)
        val targetParentUri = getDocumentUri(targetPath.requireParent())
        val copiedTargetUri = try {
            // This doesn't support progress interval millis and interruption.
            DocumentsContract.copyDocument(contentResolver, sourceUri, targetParentUri)
        } catch (e: UnsupportedOperationException) {
            // Ignored.
            return copyManually(sourcePath, targetPath, intervalMillis, listener)
        } catch (e: Exception) {
            throw ResolverException(e)
        } ?: throw ResolverException(
            "DocumentsContract.copyDocument() with $sourceUri and $targetParentUri returned null"
        )
        val sourceDisplayName = sourcePath.displayName
        val targetDisplayName = targetPath.displayName
        if (sourceDisplayName == targetDisplayName) {
            listener?.invokeWithSize(copiedTargetUri)
            return copiedTargetUri
        }
        val renamedTargetUri = try {
            rename(copiedTargetUri, targetDisplayName!!)
        } catch (e: ResolverException) {
            try {
                remove(copiedTargetUri, targetParentUri)
            } catch (e2: ResolverException) {
                e.addSuppressed(e2)
            }
            throw e
        }
        listener?.invokeWithSize(renamedTargetUri)
        return renamedTargetUri
    }

    @Throws(ResolverException::class)
    private fun copyManually(
        sourcePath: Path,
        targetPath: Path,
        intervalMillis: Long,
        listener: ((Long) -> Unit)?
    ): Uri {
        val sourceUri = getDocumentUri(sourcePath)
        val mimeType = try {
            getMimeType(sourceUri)
        } catch (e: ResolverException) {
            e.printStackTrace()
            null
        } ?: MimeType.GENERIC.value
        if (mimeType == MimeType.DIRECTORY.value) {
            return create(targetPath, MimeType.DIRECTORY.value)
        }
        val targetUri = create(targetPath, mimeType)
        try {
            Resolver.openInputStream(sourceUri, "r").use { inputStream ->
                Resolver.openOutputStream(targetUri, "wt").use { outputStream ->
                    inputStream.copyTo(outputStream, intervalMillis, listener)
                }
            }
        } catch (e: IOException) {
            val targetParentPath = targetPath.parent
            if (targetParentPath != null) {
                try {
                    val targetParentUri = getDocumentUri(targetParentPath)
                    remove(targetUri, targetParentUri)
                } catch (e2: ResolverException) {
                    e.addSuppressed(e2)
                }
            }
            throw ResolverException(e)
        }
        return targetUri
    }

    @Throws(ResolverException::class)
    fun create(path: Path, mimeType: String): Uri {
        val parentUri = getDocumentUri(path.requireParent())
        // The display name might have been changed so we cannot add the new URI to cache.
        return try {
            DocumentsContract.createDocument(
                contentResolver, parentUri, mimeType, path.displayName!!
            )
        } catch (e: Exception) {
            throw ResolverException(e)
        } ?: throw ResolverException(
            "DocumentsContract.createDocument() with $parentUri returned null"
        )
    }

    @Deprecated("", ReplaceWith("remove(path)"))
    @Throws(ResolverException::class)
    fun delete(path: Path) {
        val uri = getDocumentUri(path)
        // Always remove the path from cache, in case a deletion actually succeeded despite
        // exception being thrown.
        pathDocumentIdCache.remove(path)
        @Suppress("DEPRECATION")
        delete(uri)
    }

    @Deprecated("", ReplaceWith("remove(uri)"))
    @Throws(ResolverException::class)
    fun delete(uri: Uri) {
        val deleted = try {
            DocumentsContract.deleteDocument(contentResolver, uri)
        } catch (e: Exception) {
            throw ResolverException(e)
        }
        if (!deleted) {
            throw ResolverException("DocumentsContract.deleteDocument() with $uri returned false")
        }
    }

    fun exists(path: Path): Boolean =
        try {
            checkExistence(path)
            true
        } catch (e: ResolverException) {
            false
        }

    @Throws(ResolverException::class)
    fun getMimeType(path: Path): String? {
        val uri = getDocumentUri(path)
        return getMimeType(uri)
    }

    @Throws(ResolverException::class)
    fun getMimeType(uri: Uri): String? =
        query(uri, arrayOf(DocumentsContract.Document.COLUMN_MIME_TYPE), null).use { cursor ->
            cursor.moveToFirstOrThrow()
            cursor.getString(DocumentsContract.Document.COLUMN_MIME_TYPE)
        }?.takeIf { it.isNotEmpty() && it != MimeType.GENERIC.value }

    @Throws(ResolverException::class)
    fun getSize(path: Path): Long {
        val uri = getDocumentUri(path)
        return getSize(uri)
    }

    @Throws(ResolverException::class)
    fun getSize(uri: Uri): Long =
        query(uri, arrayOf(DocumentsContract.Document.COLUMN_SIZE), null).use { cursor ->
            cursor.moveToFirstOrThrow()
            cursor.getLong(DocumentsContract.Document.COLUMN_SIZE)
        }

    @Throws(ResolverException::class)
    fun getThumbnail(path: Path, width: Int, height: Int): Bitmap? {
        val uri = getDocumentUri(path)
        return try {
            DocumentsContract.getDocumentThumbnail(contentResolver, uri, Point(width, height), null)
        } catch (e: Exception) {
            throw ResolverException(e)
        }
    }

    fun isLocal(path: Path): Boolean {
        val authority = path.treeUri.authority
        return authority in LOCAL_AUTHORITIES
    }

    @Throws(ResolverException::class)
    fun move(
        sourcePath: Path,
        targetPath: Path,
        moveOnly: Boolean,
        intervalMillis: Long,
        listener: ((Long) -> Unit)?
    ): Uri {
        val sourceParentPath = sourcePath.requireParent()
        val targetParentPath = targetPath.requireParent()
        if (sourceParentPath == targetParentPath) {
            return rename(sourcePath, targetPath.displayName!!)
        }
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
            && sourcePath.hasSameAuthority(targetPath) && !sourcePath.isMoveUnsupported) {
            moveApi24(sourcePath, targetPath, moveOnly, intervalMillis, listener)
        } else {
            if (moveOnly) {
                // @see DocumentsProvider.moveDocument(String, String, String)
                throw ResolverException(UnsupportedOperationException("Move not supported"))
            }
            moveByCopy(sourcePath, targetPath, intervalMillis, listener)
        }
    }

    private fun Path.hasSameAuthority(other: Path): Boolean =
        treeUri.authority == other.treeUri.authority

    private val Path.isMoveUnsupported: Boolean
        get() = treeUri.authority in MOVE_UNSUPPORTED_AUTHORITIES

    @RequiresApi(Build.VERSION_CODES.N)
    @Throws(ResolverException::class)
    private fun moveApi24(
        sourcePath: Path,
        targetPath: Path,
        moveOnly: Boolean,
        intervalMillis: Long,
        listener: ((Long) -> Unit)?
    ): Uri {
        val sourceParentUri = getDocumentUri(sourcePath.requireParent())
        val sourceUri = getDocumentUri(sourcePath)
        val targetParentUri = getDocumentUri(targetPath.requireParent())
        val movedTargetUri = try {
            // This doesn't support progress interval millis and interruption.
            DocumentsContract.moveDocument(
                contentResolver, sourceUri, sourceParentUri, targetParentUri
            )
        } catch (e: UnsupportedOperationException) {
            if (moveOnly) {
                throw ResolverException(e)
            }
            return moveByCopy(sourcePath, targetPath, intervalMillis, listener)
        } catch (e: Exception) {
            throw ResolverException(e)
        } ?: throw ResolverException(
            "DocumentsContract.moveDocument() with $sourceUri and $targetParentUri returned null"
        )
        val sourceDisplayName = sourcePath.displayName
        val targetDisplayName = targetPath.displayName
        if (sourceDisplayName == targetDisplayName) {
            listener?.invokeWithSize(movedTargetUri)
            return movedTargetUri
        }
        val renamedTargetUri = rename(movedTargetUri, targetDisplayName!!)
        listener?.invokeWithSize(renamedTargetUri)
        return renamedTargetUri
    }

    private fun ((Long) -> Unit).invokeWithSize(uri: Uri) {
        val size = try {
            getSize(uri)
        } catch (e: ResolverException) {
            e.printStackTrace()
            return
        }
        this(size)
    }

    @Throws(ResolverException::class)
    private fun moveByCopy(
        sourcePath: Path,
        targetPath: Path,
        intervalMillis: Long,
        listener: ((Long) -> Unit)?
    ): Uri {
        val targetUri = copy(sourcePath, targetPath, intervalMillis, listener)
        try {
            val sourceUri = getDocumentUri(sourcePath)
            val sourceParentUri = getDocumentUri(sourcePath.requireParent())
            remove(sourceUri, sourceParentUri)
        } catch (e: ResolverException) {
            try {
                val targetParentUri = getDocumentUri(targetPath.requireParent())
                remove(targetUri, targetParentUri)
            } catch (e2: ResolverException) {
                e.addSuppressed(e2)
            }
        }
        return targetUri
    }

    @Throws(ResolverException::class)
    fun openInputStream(path: Path, mode: String): InputStream {
        val uri = getDocumentUri(path)
        return Resolver.openInputStream(uri, mode)
    }

    @Throws(ResolverException::class)
    fun openOutputStream(path: Path, mode: String): OutputStream {
        val uri = getDocumentUri(path)
        return Resolver.openOutputStream(uri, mode)
    }

    @Throws(ResolverException::class)
    fun openParcelFileDescriptor(
        path: Path,
        mode: String
    ): ParcelFileDescriptor {
        val uri = getDocumentUri(path)
        return Resolver.openParcelFileDescriptor(uri, mode)
    }

    @Throws(ResolverException::class)
    fun queryChildren(parentPath: Path): List<Path> {
        val parentDocumentId = queryDocumentId(parentPath)
        val childrenUri = DocumentsContract.buildChildDocumentsUriUsingTree(
            parentPath.treeUri, parentDocumentId
        )
        val childrenPaths = mutableListOf<Path>()
        query(
            childrenUri, arrayOf(
                DocumentsContract.Document.COLUMN_DOCUMENT_ID,
                DocumentsContract.Document.COLUMN_DISPLAY_NAME
            ), null
        ).use { cursor ->
            while (cursor.moveToNext()) {
                val childDocumentId = cursor.requireString(
                    DocumentsContract.Document.COLUMN_DOCUMENT_ID
                )
                val childDisplayName = cursor.requireString(
                    DocumentsContract.Document.COLUMN_DISPLAY_NAME
                )
                val childPath = parentPath.resolve(childDisplayName)
                pathDocumentIdCache[childPath] = childDocumentId
                childrenPaths += childPath
            }
        }
        return childrenPaths
    }

    @Throws(ResolverException::class)
    fun remove(path: Path) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && !isRemoveUnsupported(path)) {
            removeApi24(path)
        } else {
            @Suppress("DEPRECATION")
            delete(path)
        }
    }

    @Throws(ResolverException::class)
    fun remove(uri: Uri, parentUri: Uri) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && !isRemoveUnsupported(uri)) {
            removeApi24(uri, parentUri)
        } else {
            delete(uri)
        }
    }

    private fun isRemoveUnsupported(path: Path): Boolean = isRemoveUnsupported(path.treeUri)

    private fun isRemoveUnsupported(uri: Uri): Boolean =
        uri.authority in REMOVE_UNSUPPORTED_AUTHORITIES

    @RequiresApi(Build.VERSION_CODES.N)
    @Throws(ResolverException::class)
    private fun removeApi24(path: Path) {
        val uri = getDocumentUri(path)
        val parentUri = getDocumentUri(path.requireParent())
        // Always remove the path from cache, in case a removal actually succeeded despite exception
        // being thrown.
        pathDocumentIdCache.remove(path)
        removeApi24(uri, parentUri)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    @Throws(ResolverException::class)
    private fun removeApi24(uri: Uri, parentUri: Uri) {
        val removed = try {
            DocumentsContract.removeDocument(contentResolver, uri, parentUri)
        } catch (e: UnsupportedOperationException) {
            // Ignored.
            @Suppress("DEPRECATION")
            delete(uri)
            return
        } catch (e: Exception) {
            throw ResolverException(e)
        }
        if (!removed) {
            throw ResolverException("DocumentsContract.removeDocument() $uri returned false")
        }
    }

    @Throws(ResolverException::class)
    fun rename(path: Path, displayName: String): Uri {
        val uri = getDocumentUri(path)
        // Always remove the path from cache, in case a rename actually succeeded despite exception
        // being thrown.
        pathDocumentIdCache.remove(path)
        return rename(uri, displayName)
    }

    @Throws(ResolverException::class)
    fun rename(uri: Uri, displayName: String): Uri =
        try {
            DocumentsContract.renameDocument(contentResolver, uri, displayName)
        } catch (e: Exception) {
            throw ResolverException(e)
        } ?: throw ResolverException(
            "DocumentsContract.renameDocument() with $uri and $displayName returned null"
        )

    @Throws(ResolverException::class)
    fun getDocumentUri(path: Path): Uri {
        val documentId = queryDocumentId(path)
        return DocumentsContract.buildDocumentUriUsingTree(path.treeUri, documentId)
    }

    @Throws(ResolverException::class)
    fun getDocumentChildrenUri(path: Path): Uri {
        val documentId = queryDocumentId(path)
        return DocumentsContract.buildChildDocumentsUriUsingTree(path.treeUri, documentId)
    }

    @Throws(ResolverException::class)
    private fun queryDocumentId(path: Path): String {
        var documentId = pathDocumentIdCache[path]
        if (documentId != null) {
            return documentId
        }
        val parentPath = path.parent
        val treeUri = path.treeUri
        documentId = if (parentPath != null) {
            queryChildDocumentId(parentPath, path.displayName!!, treeUri)
        } else {
            // TODO: kotlinc: Type mismatch: inferred type is String? but String was expected
            //DocumentsContract.getTreeDocumentId(treeUri)
            DocumentsContract.getTreeDocumentId(treeUri)!!
        }
        pathDocumentIdCache[path] = documentId
        return documentId
    }

    @Throws(ResolverException::class)
    private fun queryChildDocumentId(parentPath: Path, displayName: String, treeUri: Uri): String {
        val parentDocumentId = queryDocumentId(parentPath)
        val childrenUri = DocumentsContract.buildChildDocumentsUriUsingTree(
            treeUri, parentDocumentId
        )
        query(
            childrenUri, arrayOf(
                DocumentsContract.Document.COLUMN_DOCUMENT_ID,
                DocumentsContract.Document.COLUMN_DISPLAY_NAME
            ), null
        ).use { cursor ->
            while (cursor.moveToNext()) {
                val childDocumentId = cursor.requireString(
                    DocumentsContract.Document.COLUMN_DOCUMENT_ID
                )
                val childDisplayName = cursor.requireString(
                    DocumentsContract.Document.COLUMN_DISPLAY_NAME
                )
                val childPath = parentPath.resolve(childDisplayName)
                pathDocumentIdCache[childPath] = childDocumentId
                if (childDisplayName == displayName) {
                    return childDocumentId
                }
            }
        }
        throw ResolverException(
            FileNotFoundException("Cannot find document ID for ${parentPath.resolve(displayName)}")
        )
    }

    @Throws(ResolverException::class)
    fun query(uri: Uri, projection: Array<out String?>?, sortOrder: String?): Cursor {
        // DocumentsProvider doesn't support selection and selectionArgs.
        var cursor = Resolver.query(uri, projection, null, null, sortOrder)
        cursor = ExternalStorageProviderHack.transformQueryResult(uri, cursor)
        return cursor
    }

    @Throws(ResolverException::class)
    private fun Path.requireParent(): Path =
        parent ?: throw ResolverException("Path.getParent() with $this returned null")

    interface Path {
        val treeUri: Uri
        val displayName: String?
        val parent: Path?
        fun resolve(other: String): Path
    }
}
