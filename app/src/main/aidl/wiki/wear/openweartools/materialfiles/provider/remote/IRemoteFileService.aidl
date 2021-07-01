package wiki.wear.openweartools.materialfiles.provider.remote;

import wiki.wear.openweartools.materialfiles.provider.remote.IRemoteFileSystem;
import wiki.wear.openweartools.materialfiles.provider.remote.IRemoteFileSystemProvider;
import wiki.wear.openweartools.materialfiles.provider.remote.IRemotePosixFileAttributeView;
import wiki.wear.openweartools.materialfiles.provider.remote.IRemotePosixFileStore;
import wiki.wear.openweartools.materialfiles.provider.remote.ParcelableObject;

interface IRemoteFileService {
    IRemoteFileSystemProvider getRemoteFileSystemProviderInterface(String scheme);

    IRemoteFileSystem getRemoteFileSystemInterface(in ParcelableObject fileSystem);

    IRemotePosixFileStore getRemotePosixFileStoreInterface(in ParcelableObject fileStore);

    IRemotePosixFileAttributeView getRemotePosixFileAttributeViewInterface(
        in ParcelableObject attributeView
    );

    void refreshArchiveFileSystem(in ParcelableObject fileSystem);
}
