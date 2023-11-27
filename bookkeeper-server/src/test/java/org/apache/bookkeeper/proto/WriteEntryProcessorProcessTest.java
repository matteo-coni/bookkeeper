package org.apache.bookkeeper.proto;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import org.apache.bookkeeper.bookie.BookieImpl;
import org.apache.bookkeeper.conf.ServerConfiguration;
import org.apache.bookkeeper.stats.NullStatsLogger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collection;

import static org.apache.bookkeeper.proto.BookieProtocol.ADDENTRY;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(Parameterized.class)
public class WriteEntryProcessorProcessTest {
    boolean isHighPriority;
    boolean isAvHighPriority;

    public WriteEntryProcessorProcessTest(boolean isHighPriority, boolean isAvHighPriority){
        this.isHighPriority = isHighPriority;
        this.isAvHighPriority = isAvHighPriority;
    }

    @Parameterized.Parameters
    public static Collection<?> getParameters(){

        return Arrays.asList(new Object[][]{
                {true,true}
        });
    }


    @Test
    public void testProcessPacketTest(){

        BookieProtocol.ParsedAddRequest request = Mockito.mock(BookieProtocol.ParsedAddRequest.class);
        BookieRequestHandler handler = Mockito.mock(BookieRequestHandler.class);
        ChannelHandlerContext ctx = Mockito.mock(ChannelHandlerContext.class);

        when(handler.ctx()).thenReturn(ctx);

        BookieImpl bookie = Mockito.mock(BookieImpl.class);

        BookieRequestProcessor processor = Mockito.mock(BookieRequestProcessor.class);
        when(processor.getBookie()).thenReturn(bookie);

        WriteEntryProcessor wep =  WriteEntryProcessor.create(request, handler, processor); //costruttore privato, quindi chiamo create
        System.out.println(wep);

        //test del not read-only
        when(bookie.isReadOnly()).thenReturn(false);

        boolean res = false;
        try{
            wep.processPacket();
            res = true;
            Assert.assertTrue(res);

        } catch (Exception e) {
            e.printStackTrace();
            Assert.assertFalse(res);
        }

        //qui entro nell'if
        when(request.isRecoveryAdd()).thenReturn(true);
        boolean resRec = false;
        try{
            wep.processPacket();
            resRec = true;
            Assert.assertTrue(resRec);


        } catch (Exception e) {
            e.printStackTrace();
            Assert.assertFalse(resRec);
        }
    }

    @Test
    public void processPacketReadOnlyTest(){
        BookieProtocol.ParsedAddRequest request = Mockito.mock(BookieProtocol.ParsedAddRequest.class);
        when(request.getOpCode()).thenReturn(ADDENTRY);

        BookieRequestHandler handler = Mockito.mock(BookieRequestHandler.class);
        ChannelHandlerContext ctx = Mockito.mock(ChannelHandlerContext.class);

        Channel channel = Mockito.mock(Channel.class);
        when(channel.isOpen()).thenReturn(true);
        when(channel.isWritable()).thenReturn(true);
        when(channel.isActive()).thenReturn(true);

        when(ctx.channel()).thenReturn(channel);

        when(handler.ctx()).thenReturn(ctx);

        BookieImpl bookie = Mockito.mock(BookieImpl.class);

        BookieRequestProcessor processor = Mockito.mock(BookieRequestProcessor.class);

        when(processor.getBookie()).thenReturn(bookie);

        //when(processor.getRequestStats()).thenReturn(mockRequestStats);
        when(processor.getRequestStats()).thenReturn(new RequestStats(NullStatsLogger.INSTANCE));

        WriteEntryProcessor wep =  WriteEntryProcessor.create(request, handler, processor); //costruttore privato, quindi chiamo create
        System.out.println(wep);

        //test del not read-only
        when(bookie.isReadOnly()).thenReturn(true);
        when(bookie.isAvailableForHighPriorityWrites()).thenReturn(isAvHighPriority);

        boolean resRec = false;
        try{
            wep.processPacket();
            resRec = true;
            Assert.assertTrue(resRec);

        } catch (Exception e) {
            e.printStackTrace();
            Assert.assertFalse(resRec);
        }



    }

}