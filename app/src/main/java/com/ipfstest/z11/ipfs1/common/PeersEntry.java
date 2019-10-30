package com.ipfstest.z11.ipfs1.common;

public class PeersEntry {
    private String address;
    private String id;

    public PeersEntry(String address, String id) {
        this.address = address;
        this.id = id;
    }

    public String getAddress() {
        return address;
    }


    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return "PeersEntry{" +
                "address='" + address + '\'' +
                ", id=" + id +
                '}';
    }
}
