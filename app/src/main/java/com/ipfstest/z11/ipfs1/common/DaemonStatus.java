package com.ipfstest.z11.ipfs1.common;

public class DaemonStatus {
    private Status status;

    public DaemonStatus(Status status) {
        this.status = status;
    }

    public Status getDaemonStatus() {
        return this.status;
    }

}
