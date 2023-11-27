package org.apache.bookkeeper.bookie.storage.ldb.entity;

import io.netty.buffer.ByteBuf;

public class WriteCachePutEntity{

    private long entryId;
    private long ledgerId;

    public WriteCachePutEntity(long ledgerId, long entryId) {

        this.entryId = entryId;
        this.ledgerId = ledgerId;
    }

    public long getEntryId() {
        return entryId;
    }

    public long getLedgerId() {
        return ledgerId;
    }

    public void setEntryId(long entryId) {
        this.entryId = entryId;
    }

    public void setLedgerId(long ledgerId) {
        this.ledgerId = ledgerId;
    }

}
