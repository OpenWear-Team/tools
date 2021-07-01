package wiki.wear.openweartools.materialfiles.provider.remote;

import wiki.wear.openweartools.materialfiles.provider.remote.ParcelableException;

interface IRemoteInputStream {
    int read(out ParcelableException exception);

    int read2(out byte[] buffer, out ParcelableException exception);

    long skip(long size, out ParcelableException exception);

    int available(out ParcelableException exception);

    void close(out ParcelableException exception);
}
