package com.ipfstest.z11.ipfs1;

enum SERVICE_STATUS {
    starting, started, stopping, stopped
}

public class DaemonStatus {
    private SERVICE_STATUS status;

    public DaemonStatus(SERVICE_STATUS status) {
        this.status = status;
    }

    public SERVICE_STATUS getDaemonStatus() {
        return this.status;
    }

}
