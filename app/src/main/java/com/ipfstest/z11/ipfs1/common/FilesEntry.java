package com.ipfstest.z11.ipfs1.common;

public class FilesEntry {
    private String name;
    private String hash;
    private String size;
    private String path;

    public FilesEntry() {
    }

    public FilesEntry(String name, String hash, String size) {
        this.name = name;
        this.hash = hash;
        this.size = size;
    }

    public String getName() {
        return name;
    }


    public String getHash() {
        return hash;
    }

    public String getSize() {
        return size;
    }


    @Override
    public String toString() {
        return "StudentEntity{" +
                "name='" + name + '\'' +
                "hash='" + hash + '\'' +
                ", size=" + size +
                '}';
    }
}
