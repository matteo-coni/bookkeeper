package org.apache.bookkeeper.bookie.storage.ldb;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

import io.netty.buffer.UnpooledByteBufAllocator;
import org.apache.bookkeeper.bookie.storage.ldb.entity.WriteCacheGetEntity;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;


@RunWith(value = Parameterized.class)
public class WriteCacheGetTest {

    private static WriteCache writeCache;
    private ByteBufAllocator byteBufAll = UnpooledByteBufAllocator.DEFAULT;

    /**
     * Entry dimension = 1024 bytes
     * Max entry in cache = 10
     */
    private final int ENTRY_NUMBER = 10;
    private final int ENTRY_SIZE = 1024;
    private static ByteBuf entry;
    private WriteCacheGetEntity writeEntity;
    private boolean expExcep;

    @Before
    public void setUp() throws Exception {
        //byteBufAll = UnpooledByteBufAllocator.DEFAULT;
        this.entry = byteBufAll.buffer(ENTRY_SIZE);
        this.entry.writeBytes("test prova".getBytes());

        writeCache = new WriteCache(byteBufAll, ENTRY_SIZE * ENTRY_NUMBER);

        writeCache.put(1, 1, this.entry);

    }

    @After
    public void tearDown() throws Exception {
        //writeCache.clear();
        //entry.release();
        //writeCache.close();
        //writeCache.clear();
        if (entry != null) entry.release();

    }

    @Parameterized.Parameters
    public static Collection<?> getParameters(){
        return Arrays.asList(new Object[][] {                   // mi aspetto l'eccezione? true, altrimenti false
                {new WriteCacheGetEntity(1L, 1L ), false},
                {new WriteCacheGetEntity(1L, 0L), false}, //Se non esiste l'entry, il metodo ritorna null
                {new WriteCacheGetEntity(1L, -1L), false},

                {new WriteCacheGetEntity(0L, 1L ), false},
                {new WriteCacheGetEntity(0L, 0L ), false},
                {new WriteCacheGetEntity(0L, -1L ), false},

                {new WriteCacheGetEntity(-1L, 1L ), true},
                {new WriteCacheGetEntity(-1L, 0L ), true},
                {new WriteCacheGetEntity(-1L, -1L ), true},

        });
    }

    public WriteCacheGetTest(WriteCacheGetEntity writeEntity, boolean expExcep){
        this.writeEntity = writeEntity;
        this.expExcep = expExcep;
    }
    @Test
    public void getFromCache(){



        long actualLedgerId = writeEntity.getLedgerId();
        long actualEntryId = writeEntity.getEntryId();

        try{

            ByteBuf result = writeCache.get(actualLedgerId, actualEntryId);

            if(result != null) {
                Assert.assertEquals(this.entry, result);
                System.out.println(this.entry);
            }
            else
                Assert.assertNull(result);
            //Assert.assertFalse(expExcep);

        }
        catch(Exception e){
            e.printStackTrace();
            //result = null;
            Assert.assertTrue(expExcep); //metto expRes = true in caso di eccezioni
        }

    }

}