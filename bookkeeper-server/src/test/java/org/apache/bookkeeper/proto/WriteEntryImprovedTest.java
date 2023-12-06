package org.apache.bookkeeper.proto;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import org.apache.bookkeeper.bookie.BookieImpl;
import org.apache.bookkeeper.stats.NullStatsLogger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collection;

import static org.apache.bookkeeper.proto.BookieProtocol.ADDENTRY;
import static org.mockito.Mockito.when;

@RunWith(Parameterized.class)
public class WriteEntryImprovedTest {

    boolean priorityBookie;
    boolean priorityRequest;
    public WriteEntryImprovedTest (boolean priorityBookie, boolean priorityRequest){
        this.priorityBookie = priorityBookie;
        this.priorityRequest = priorityRequest;

    }

    @Parameterized.Parameters
    public static Collection<?> getParameters() {

        return Arrays.asList(new Object[][]{

                {true, false},
                {false, true},
                {true, true},
                {false, false}

        });
    }

    @Test
    public void processHighPriorityTest(){

        BookieProtocol.ParsedAddRequest request = Mockito.mock(BookieProtocol.ParsedAddRequest.class);
        when(request.getOpCode()).thenReturn(ADDENTRY);
        when(request.getEntryId()).thenReturn(1L);
        when(request.getLedgerId()).thenReturn(1L);

        BookieRequestHandler handler = Mockito.mock(BookieRequestHandler.class);
        ChannelHandlerContext ctx = Mockito.mock(ChannelHandlerContext.class);

        Channel channel = Mockito.mock(Channel.class);
        when(channel.isOpen()).thenReturn(true);
        when(channel.isWritable()).thenReturn(true);
        when(channel.isActive()).thenReturn(true);

        when(ctx.channel()).thenReturn(channel);
        when(handler.ctx()).thenReturn(ctx);

        BookieImpl bookie = Mockito.mock(BookieImpl.class);
        when(bookie.isReadOnly()).thenReturn(true);

        BookieRequestProcessor processor = Mockito.mock(BookieRequestProcessor.class);
        when(processor.getBookie()).thenReturn(bookie);
        when(processor.getRequestStats()).thenReturn(new RequestStats(NullStatsLogger.INSTANCE));

        when(bookie.isAvailableForHighPriorityWrites()).thenReturn(priorityBookie);
        when(request.isHighPriority()).thenReturn(priorityRequest);

        WriteEntryProcessor wep = WriteEntryProcessor.create(request, handler, processor); //costruttore privato, quindi chiamo create
        System.out.println(wep);

        when(bookie.isReadOnly()).thenReturn(true);


        boolean resRec = false;
        try {
            wep.processPacket();
            resRec = true;
            Assert.assertTrue(resRec);

        } catch (Exception e) {
            e.printStackTrace();
            Assert.assertFalse(resRec);
        }


    }

}
