package org.apache.bookkeeper.bookie.storage.ldb;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.UnpooledByteBufAllocator;
import org.apache.bookkeeper.bookie.storage.ldb.entity.WriteCacheGetEntity;
import org.apache.bookkeeper.bookie.storage.ldb.entity.WriteCachePutEntity;
import org.apache.bookkeeper.conf.ServerConfiguration;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.AfterAll;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mockito;

import static org.mockito.Mockito.mock;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(value = Parameterized.class)
public class WriteCachePutTest {

    private static WriteCache writeCache;
    private static ByteBufAllocator byteBufAll;

    /**
     * Entry dimension = 1024 bytes
     * Max entry in cache = 10
     */

    private static final int ENTRY_NUMBER = 10;
    private static final int ENTRY_SIZE = 1024;

    private static ByteBuf entry;
    private WriteCachePutEntity writePutEntity;
    private boolean expRes;

    enum entryInst {
        VALID,
        INVALID
    }

    @Before
    public void setUp() throws Exception {

        //byteBufAll = UnpooledByteBufAllocator.DEFAULT;
        //writeCache = new WriteCache(byteBufAll, ENTRY_SIZE * ENTRY_NUMBER);
        //entry = byteBufAll.buffer(ENTRY_SIZE);

    }

    @Parameterized.Parameters
    public static Collection<?> getParameters(){

        return Arrays.asList(new Object[][] {


                {new WriteCachePutEntity(1L, 1L), true, getByteBuff(entryInst.VALID)}, //ok
                {new WriteCachePutEntity(1L, 0L), true, getByteBuff(entryInst.VALID)},
                {new WriteCachePutEntity(1L, -1L), false, getByteBuff(entryInst.VALID)},

                {new WriteCachePutEntity(1L, 1L), false, getByteBuff(entryInst.INVALID)}, //ok
                {new WriteCachePutEntity(1L, 0L), false, getByteBuff(entryInst.INVALID)},
                {new WriteCachePutEntity(1L, -1L), false, getByteBuff(entryInst.INVALID)}, //ok

                {new WriteCachePutEntity(1L, 1L), false, null}, //ok
                {new WriteCachePutEntity(1L, 0L), false, null},
                {new WriteCachePutEntity(1L, -1L), false, null},

                {new WriteCachePutEntity(0L, 1L), true, getByteBuff(entryInst.VALID)}, //ok
                {new WriteCachePutEntity(0L, 0L), true, getByteBuff(entryInst.VALID)},
                {new WriteCachePutEntity(0L, -1L), false, getByteBuff(entryInst.VALID)},

                {new WriteCachePutEntity(0L, 1L), false, getByteBuff(entryInst.INVALID)}, //ok
                {new WriteCachePutEntity(0L, 0L), false, getByteBuff(entryInst.INVALID)},
                {new WriteCachePutEntity(0L, -1L), false, getByteBuff(entryInst.INVALID)},

                {new WriteCachePutEntity(0L, 1L), false, null}, //ok
                {new WriteCachePutEntity(0L, 0L), false, null},
                {new WriteCachePutEntity(0L, -1L), false, null},

                {new WriteCachePutEntity(-1L, 1L), false, getByteBuff(entryInst.VALID)}, //ok
                {new WriteCachePutEntity(-1L, 0L), false, getByteBuff(entryInst.VALID)},
                {new WriteCachePutEntity(-1L, -1L), false, getByteBuff(entryInst.VALID)},

                {new WriteCachePutEntity(-1L, 1L), false, getByteBuff(entryInst.INVALID)}, //ok
                {new WriteCachePutEntity(-1L, 0L), false, getByteBuff(entryInst.INVALID)},
                {new WriteCachePutEntity(-1L, -1L), false, getByteBuff(entryInst.INVALID)},

                {new WriteCachePutEntity(-1L, 1L), false, null}, //ok
                {new WriteCachePutEntity(-1L, 0L), false, null},
                {new WriteCachePutEntity(-1L, -1L), false, null}

        });
    }

    private static ByteBuf getByteBuff(entryInst type) {

        byteBufAll = UnpooledByteBufAllocator.DEFAULT;
        writeCache = new WriteCache(byteBufAll, ENTRY_SIZE * ENTRY_NUMBER);
        switch (type){

            case VALID:

                entry = byteBufAll.buffer(ENTRY_SIZE);
                entry.writeBytes("ciao".getBytes());
                return entry;

            case INVALID:

                entry = Mockito.mock(ByteBuf.class);
                when(entry.readableBytes()).thenThrow(new IllegalArgumentException("invalid ByteBuf"));

                System.out.println("prova mock");

                return entry;

            default:
                return null;
        }
    }

    @After
    public void tearDown() throws Exception {
        writeCache.clear();
        if (entry != null) entry.release();
        //writeCache.close();
    }


    public WriteCachePutTest(WriteCachePutEntity writePutEntity, boolean expRes, ByteBuf entry){
        this.writePutEntity = writePutEntity;
        this.expRes = expRes;
        this.entry = entry;
        //System.out.println(entry);


    }

    @Test
    public void putCache(){
        boolean res = false;
        long size_before = 0;
        long size_after = 0;

        try{
            size_before = writeCache.size();
            res = writeCache.put(writePutEntity.getLedgerId(), writePutEntity.getEntryId(), entry);

            //System.out.println(writePutEntity.getEntryId());
            System.out.println("size dopo put: " + writeCache.size());
            size_after = writeCache.size();

            Assert.assertNotEquals(size_before,size_after);
            Assert.assertTrue(res);


        } catch (IllegalArgumentException | NullPointerException  e){
            e.printStackTrace();
            Assert.assertEquals(size_before, size_after);
            Assert.assertFalse(res);
        }

    }
}