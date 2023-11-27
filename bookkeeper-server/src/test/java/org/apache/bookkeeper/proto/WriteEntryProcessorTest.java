package org.apache.bookkeeper.proto;

import io.netty.channel.ChannelHandlerContext;
import org.apache.bookkeeper.bookie.BookieImpl;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collection;

import static org.apache.bookkeeper.proto.BookieProtocol.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@RunWith(Parameterized.class)
public class WriteEntryProcessorTest {

    enum instance {
        VALID,
        INVALID,
        BOOKIE
    }

    private ParsedAddRequest request;
    private BookieRequestHandler handler;
    private BookieRequestProcessor processor;
    private boolean excExp;

    public WriteEntryProcessorTest(ParsedAddRequest request, BookieRequestHandler handler, BookieRequestProcessor processor, boolean excExp){
        this.request = request;
        this.handler = handler;
        this.processor = processor;
        this.excExp = excExp;
    }

    @Parameterized.Parameters
    public static Collection<?> getParameters(){

        return Arrays.asList(new Object[][] {
                {getMockRequest(instance.VALID), getMockHandler(instance.VALID), getMockProcessor(instance.VALID), false}, //non mi aspetto l'eccezione
                {getMockRequest(instance.VALID), getMockHandler(instance.INVALID), getMockProcessor(instance.VALID), true},
                {getMockRequest(instance.VALID),       null                      , getMockProcessor(instance.VALID), true},

                {getMockRequest(instance.VALID), getMockHandler(instance.VALID), getMockProcessor(instance.INVALID), true},
                {getMockRequest(instance.VALID), getMockHandler(instance.INVALID), getMockProcessor(instance.INVALID), true},
                {getMockRequest(instance.VALID),       null                      , getMockProcessor(instance.INVALID), true},

                {getMockRequest(instance.VALID), getMockHandler(instance.VALID),         null                      , true},
                {getMockRequest(instance.VALID), getMockHandler(instance.INVALID),       null                      , true},
                {getMockRequest(instance.VALID),       null                      ,       null                      , true},

                {getMockRequest(instance.INVALID), getMockHandler(instance.VALID), getMockProcessor(instance.VALID), false}, //attenzione
                {getMockRequest(instance.INVALID), getMockHandler(instance.INVALID), getMockProcessor(instance.VALID), true},
                {getMockRequest(instance.INVALID),       null                      , getMockProcessor(instance.VALID), true},

                {getMockRequest(instance.INVALID), getMockHandler(instance.VALID), getMockProcessor(instance.INVALID), false}, //attenzione
                {getMockRequest(instance.INVALID), getMockHandler(instance.INVALID), getMockProcessor(instance.INVALID), true},
                {getMockRequest(instance.INVALID),       null                      , getMockProcessor(instance.INVALID), true},

                {getMockRequest(instance.INVALID), getMockHandler(instance.VALID),         null                      , true},
                {getMockRequest(instance.INVALID), getMockHandler(instance.INVALID),       null                      , true},
                {getMockRequest(instance.INVALID),       null                      ,       null                      , true},

                {        null                  , getMockHandler(instance.VALID)  , getMockProcessor(instance.VALID), false}, //attenzione
                {        null                  , getMockHandler(instance.INVALID), getMockProcessor(instance.VALID), true},
                {        null                  ,         null                    , getMockProcessor(instance.VALID), true},

                {        null                  , getMockHandler(instance.VALID), getMockProcessor(instance.INVALID), true},
                {        null                  , getMockHandler(instance.INVALID), getMockProcessor(instance.INVALID), true},
                {        null                  ,         null                  , getMockProcessor(instance.INVALID), true},

                {        null                  , getMockHandler(instance.VALID),         null                  , true},
                {        null                  , getMockHandler(instance.INVALID),         null                  , true},
                {        null                  ,         null                  ,         null                  , true},

        });
    }

    private static ParsedAddRequest getMockRequest(instance type) {

        ParsedAddRequest mockRequest = Mockito.mock(ParsedAddRequest.class);

        switch (type){
            case VALID:
                //caso valido
                return mockRequest;
            case INVALID:
                //caso invalido
                return mockRequest;
            default:
                return null;
        }
    }

    private static BookieRequestHandler getMockHandler(instance type) {

        BookieRequestHandler mockHandler = Mockito.mock(BookieRequestHandler.class);
        ChannelHandlerContext ctx = Mockito.mock(ChannelHandlerContext.class); //!!!

        switch (type){
            case VALID:
                //caso valido
                when(mockHandler.ctx()).thenReturn(ctx);
                return mockHandler;
            case INVALID:
                //caso invalido
                when(mockHandler.ctx()).thenThrow(new IllegalArgumentException("Invalid BookieRequestHandler"));
                return mockHandler;
            default:
                return null;
        }
    }

    private static BookieRequestProcessor getMockProcessor(instance type) {

        BookieRequestProcessor mockProcessor = Mockito.mock(BookieRequestProcessor.class);
        BookieImpl bookie = Mockito.mock(BookieImpl.class); //!!

        switch (type){
            case VALID:
                //caso valido
                return mockProcessor;
            case INVALID:
                //caso invalido
                return mockProcessor;
            case BOOKIE:
                when(mockProcessor.getBookie()).thenReturn(bookie);
            default:
                return null;
        }
    }

    @Test
    public void createTest(){

        try {

            WriteEntryProcessor wep = WriteEntryProcessor.create(this.request, this.handler, this.processor);
            Assert.assertNotNull(wep);

        } catch (Exception e){

            e.printStackTrace();
            assertTrue(excExp);

        }
    }

}