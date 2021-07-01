/*
 * Copyright (c) 2018 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package wiki.wear.openweartools.materialfiles.provider.linux

import java8.nio.file.attribute.FileTime
import wiki.wear.openweartools.materialfiles.provider.common.ByteString
import wiki.wear.openweartools.materialfiles.provider.common.PosixFileAttributeView
import wiki.wear.openweartools.materialfiles.provider.common.PosixFileModeBit
import wiki.wear.openweartools.materialfiles.provider.common.PosixGroup
import wiki.wear.openweartools.materialfiles.provider.common.PosixUser
import wiki.wear.openweartools.materialfiles.provider.common.toInt
import wiki.wear.openweartools.materialfiles.provider.linux.syscall.Constants
import wiki.wear.openweartools.materialfiles.provider.linux.syscall.StructTimespec
import wiki.wear.openweartools.materialfiles.provider.linux.syscall.SyscallException
import wiki.wear.openweartools.materialfiles.provider.linux.syscall.Syscalls
import java.io.IOException

internal class LocalLinuxFileAttributeView(
    private val path: ByteString,
    private val noFollowLinks: Boolean
) : PosixFileAttributeView {
    override fun name(): String = NAME

    @Throws(IOException::class)
    override fun readAttributes(): LinuxFileAttributes {
        val stat = try {
            if (noFollowLinks) {
                Syscalls.lstat(path)
            } else {
                Syscalls.stat(path)
            }
        } catch (e: SyscallException) {
            throw e.toFileSystemException(path.toString())
        }
        val owner = try {
            LinuxUserPrincipalLookupService.getUserById(stat.st_uid)
        } catch (e: SyscallException) {
            // It's okay to have a non-existent UID.
            e.toFileSystemException(path.toString()).printStackTrace()
            PosixUser(stat.st_uid, null)
        }
        val group = try {
            LinuxUserPrincipalLookupService.getGroupById(stat.st_gid)
        } catch (e: SyscallException) {
            // It's okay to have a non-existent GID.
            e.toFileSystemException(path.toString()).printStackTrace()
            PosixGroup(stat.st_gid, null)
        }
        val seLinuxContext = try {
            if (noFollowLinks) {
                Syscalls.lgetfilecon(path)
            } else {
                Syscalls.getfilecon(path)
            }
        } catch (e: SyscallException) {
            // Filesystem may not support xattrs and SELinux calls may fail with EOPNOTSUPP.
            e.toFileSystemException(path.toString()).printStackTrace()
            null
        }
        return LinuxFileAttributes.from(stat, owner, group, seLinuxContext)
    }

    @Throws(IOException::class)
    override fun setTimes(
        lastModifiedTime: FileTime?,
        lastAccessTime: FileTime?,
        createTime: FileTime?
    ) {
        if (lastAccessTime == null && lastModifiedTime == null) {
            // Only throw if caller is trying to set only create time, so that foreign copy move can
            // still set other times.
            if (createTime != null) {
                throw UnsupportedOperationException("createTime")
            }
            return
        }
        val times = arrayOf(lastAccessTime.toTimespec(), lastModifiedTime.toTimespec())
        try {
            if (noFollowLinks) {
                Syscalls.lutimens(path, times)
            } else {
                Syscalls.utimens(path, times)
            }
        } catch (e: SyscallException) {
            throw e.toFileSystemException(path.toString())
        }
    }

    private fun FileTime?.toTimespec(): StructTimespec {
        if (this == null) {
            return StructTimespec(0, Constants.UTIME_OMIT)
        }
        val instant = toInstant()
        return StructTimespec(instant.epochSecond, instant.nano.toLong())
    }

    @Throws(IOException::class)
    override fun setOwner(owner: PosixUser) {
        val uid = owner.id
        try {
            if (noFollowLinks) {
                Syscalls.lchown(path, uid, -1)
            } else {
                Syscalls.chown(path, uid, -1)
            }
        } catch (e: SyscallException) {
            throw e.toFileSystemException(path.toString())
        }
    }

    @Throws(IOException::class)
    override fun setGroup(group: PosixGroup) {
        val gid = group.id
        try {
            if (noFollowLinks) {
                Syscalls.lchown(path, -1, gid)
            } else {
                Syscalls.chown(path, -1, gid)
            }
        } catch (e: SyscallException) {
            throw e.toFileSystemException(path.toString())
        }
    }

    @Throws(IOException::class)
    override fun setMode(mode: Set<PosixFileModeBit>) {
        if (noFollowLinks) {
            throw UnsupportedOperationException("Cannot set mode for symbolic links")
        }
        val modeInt = mode.toInt()
        try {
            Syscalls.chmod(path, modeInt)
        } catch (e: SyscallException) {
            throw e.toFileSystemException(path.toString())
        }
    }

    @Throws(IOException::class)
    override fun setSeLinuxContext(context: ByteString) {
        try {
            if (noFollowLinks) {
                Syscalls.lsetfilecon(path, context)
            } else {
                Syscalls.setfilecon(path, context)
            }
        } catch (e: SyscallException) {
            throw e.toFileSystemException(path.toString())
        }
    }

    @Throws(IOException::class)
    override fun restoreSeLinuxContext() {
        val path = if (noFollowLinks) {
            path
        } else {
            try {
                Syscalls.realpath(path)
            } catch (e: SyscallException) {
                throw e.toFileSystemException(path.toString())
            }
        }
        try {
            Syscalls.selinux_android_restorecon(path, 0)
        } catch (e: SyscallException) {
            throw e.toFileSystemException(path.toString())
        }
    }

    companion object {
        private val NAME = LinuxFileSystemProvider.scheme

        val SUPPORTED_NAMES = setOf("basic", "posix", NAME)
    }
}
