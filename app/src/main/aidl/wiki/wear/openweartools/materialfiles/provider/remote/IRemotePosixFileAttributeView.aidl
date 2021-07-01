package wiki.wear.openweartools.materialfiles.provider.remote;

import wiki.wear.openweartools.materialfiles.provider.common.ParcelableFileTime;
import wiki.wear.openweartools.materialfiles.provider.common.ParcelablePosixFileMode;
import wiki.wear.openweartools.materialfiles.provider.common.PosixGroup;
import wiki.wear.openweartools.materialfiles.provider.common.PosixUser;
import wiki.wear.openweartools.materialfiles.provider.remote.ParcelableException;
import wiki.wear.openweartools.materialfiles.provider.remote.ParcelableObject;

interface IRemotePosixFileAttributeView {
    ParcelableObject readAttributes(out ParcelableException exception);

    void setTimes(
        in ParcelableFileTime lastModifiedTime,
        in ParcelableFileTime lastAccessTime,
        in ParcelableFileTime createTime,
        out ParcelableException exception
    );

    void setOwner(in PosixUser owner, out ParcelableException exception);

    void setGroup(in PosixGroup group, out ParcelableException exception);

    void setMode(in ParcelablePosixFileMode mode, out ParcelableException exception);

    void setSeLinuxContext(in ParcelableObject context, out ParcelableException exception);

    void restoreSeLinuxContext(out ParcelableException exception);
}
