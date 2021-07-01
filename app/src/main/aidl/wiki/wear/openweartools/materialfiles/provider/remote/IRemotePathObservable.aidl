package wiki.wear.openweartools.materialfiles.provider.remote;

import wiki.wear.openweartools.materialfiles.provider.remote.ParcelableException;
import wiki.wear.openweartools.materialfiles.util.RemoteCallback;

interface IRemotePathObservable {
    void addObserver(in RemoteCallback observer);

    void close(out ParcelableException exception);
}
