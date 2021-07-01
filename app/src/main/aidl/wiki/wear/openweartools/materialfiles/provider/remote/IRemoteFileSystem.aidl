package wiki.wear.openweartools.materialfiles.provider.remote;

import wiki.wear.openweartools.materialfiles.provider.remote.ParcelableException;

interface IRemoteFileSystem {
    void close(out ParcelableException exception);
}
