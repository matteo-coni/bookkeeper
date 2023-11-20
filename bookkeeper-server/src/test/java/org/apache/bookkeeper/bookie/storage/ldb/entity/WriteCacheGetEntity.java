package org.apache.bookkeeper.bookie.storage.ldb.entity;

public class WriteCacheGetEntity {

    private long ledgerId;
    private long entryId;


    public WriteCacheGetEntity(long ledgerId, long entryId) {
        this.ledgerId = ledgerId;
        this.entryId = entryId;
    }

    public long getEntryId() {
        return entryId;
    }

    public long getLedgerId() {
        return ledgerId;
    }

    public void setLedgerId(long ledgerId) {
        this.ledgerId = ledgerId;
    }

    public void setEntryId(long entryId) {
        this.entryId = entryId;
    }

}