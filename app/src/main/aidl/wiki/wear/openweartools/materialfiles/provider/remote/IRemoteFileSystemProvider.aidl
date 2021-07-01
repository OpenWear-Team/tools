package wiki.wear.openweartools.materialfiles.provider.remote;

import wiki.wear.openweartools.materialfiles.provider.remote.ParcelableCopyOptions;
import wiki.wear.openweartools.materialfiles.provider.remote.ParcelableDirectoryStream;
import wiki.wear.openweartools.materialfiles.provider.remote.ParcelableException;
import wiki.wear.openweartools.materialfiles.provider.remote.ParcelableFileAttributes;
import wiki.wear.openweartools.materialfiles.provider.remote.ParcelableObject;
import wiki.wear.openweartools.materialfiles.provider.remote.ParcelablePathListConsumer;
import wiki.wear.openweartools.materialfiles.provider.remote.ParcelableSerializable;
import wiki.wear.openweartools.materialfiles.provider.remote.RemotePathObservable;
import wiki.wear.openweartools.materialfiles.provider.remote.RemoteInputStream;
import wiki.wear.openweartools.materialfiles.provider.remote.RemoteSeekableByteChannel;
import wiki.wear.openweartools.materialfiles.util.RemoteCallback;

interface IRemoteFileSystemProvider {
    RemoteInputStream newInputStream(
        in ParcelableObject file,
        in ParcelableSerializable options,
        out ParcelableException exception
    );

    RemoteSeekableByteChannel newByteChannel(
        in ParcelableObject file,
        in ParcelableSerializable options,
        in ParcelableFileAttributes attributes,
        out ParcelableException exception
    );

    ParcelableDirectoryStream newDirectoryStream(
        in ParcelableObject directory,
        in ParcelableObject filter,
        out ParcelableException exception
    );

    void createDirectory(
        in ParcelableObject directory,
        in ParcelableFileAttributes attributes,
        out ParcelableException exception
    );

    void createSymbolicLink(
        in ParcelableObject link,
        in ParcelableObject target,
        in ParcelableFileAttributes attributes,
        out ParcelableException exception
    );

    void createLink(
        in ParcelableObject link,
        in ParcelableObject existing,
        out ParcelableException exception
    );

    void delete(in ParcelableObject path, out ParcelableException exception);

    ParcelableObject readSymbolicLink(in ParcelableObject link, out ParcelableException exception);

    RemoteCallback copy(
        in ParcelableObject source,
        in ParcelableObject target,
        in ParcelableCopyOptions options,
        in RemoteCallback callback
    );

    RemoteCallback move(
        in ParcelableObject source,
        in ParcelableObject target,
        in ParcelableCopyOptions options,
        in RemoteCallback callback
    );

    boolean isSameFile(
        in ParcelableObject path,
        in ParcelableObject path2,
        out ParcelableException exception
    );

    boolean isHidden(in ParcelableObject path, out ParcelableException exception);

    ParcelableObject getFileStore(in ParcelableObject path, out ParcelableException exception);

    void checkAccess(
        in ParcelableObject path,
        in ParcelableSerializable modes,
        out ParcelableException exception
    );

    ParcelableObject readAttributes(
        in ParcelableObject path,
        in ParcelableSerializable type,
        in ParcelableSerializable options,
        out ParcelableException exception
    );

    RemotePathObservable observe(
        in ParcelableObject path,
        long intervalMillis,
        out ParcelableException exception
    );

    RemoteCallback search(
        in ParcelableObject directory,
        in String query,
        long intervalMillis,
        in ParcelablePathListConsumer listener,
        in RemoteCallback callback
    );
}
