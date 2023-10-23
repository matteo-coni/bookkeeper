package org.apache.bookkeeper.client;

import org.apache.bookkeeper.conf.BookKeeperClusterTestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class BookKeeperAdminFormatTest extends BookKeeperClusterTestCase {

    private static final int bookies = 2;
    private boolean validConf;
    private boolean isInteractive;
    private boolean force;
    private boolean expRes;

    public BookKeeperAdminFormatTest(boolean validConf, boolean isInteractive, boolean force, boolean expRes){

        super(bookies); //chiama il costruttore di BookkeeperClusterTestCase con number of bookie pari a 2

        this.validConf = validConf;
        this.isInteractive = isInteractive;
        this.force = force;
        this.expRes = expRes;
    }

    @Before
    public void setUp(){

    }

    @After
    public void tearDown() throws Exception {
        super.tearDown();
    }

    @Test
    public void formatTest(){

    }


}