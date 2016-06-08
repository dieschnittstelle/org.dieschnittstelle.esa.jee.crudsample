package org.dieschnittstelle.jee.esa.skeleton.ejbmodule;

import org.apache.log4j.Logger;
import org.dieschnittstelle.jee.esa.entities.crm.Address;
import org.dieschnittstelle.jee.esa.entities.crm.StationaryTouchpoint;
import org.dieschnittstelle.jee.esa.skeleton.ejb.mdb.MongoDBClient;
import org.dieschnittstelle.jee.esa.skeleton.ejb.mdb.MongoDBClientDocs;
import org.dieschnittstelle.jee.esa.skeleton.ejb.mdb.MongoDBClientBindocs;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by master on 07.06.16.
 */
public class TestMDBClient {

    protected static Logger logger = Logger.getLogger(TestMDBClient.class);

    private MongoDBClient mdbclient;


    @Test
    public void testNormaldocs() {
        mdbclient = new MongoDBClientDocs("127.0.0.1", 27017, "testmdb");
        mdbclient.dropTable("touchpoints");

        runTests();
    }

//    @Test
    public void testBindocs() {
        mdbclient = new MongoDBClientBindocs("127.0.0.1", 27017, "testmdb");
        mdbclient.dropTable("touchpoints");

        runTests();
    }


    private void runTests() {
        // create request
        StationaryTouchpointDoc tp = new StationaryTouchpointDoc(-1,"dorem",new Address("lipsum","olor","-42","adispiscing"));
        //tp.set_id(new ObjectId().toHexString());

        // create
        String id = mdbclient.createDoc("touchpoints",tp);
        logger.info("created: " + id);
        assertNotNull(id);

        // readall
        List<StationaryTouchpointDoc> readall = mdbclient.readAllDocs(StationaryTouchpointDoc.class,"touchpoints");
        logger.info("readall: " + readall);
        assertEquals(readall.size(),1);

        // read
        StationaryTouchpointDoc read = mdbclient.readDocForID(StationaryTouchpointDoc.class,"touchpoints",id);
        logger.info("read: " + read);
        assertNotNull(read);
        assertEquals(tp.getName(),read.getName());
        assertEquals(tp.getLocation().getStreet(),read.getLocation().getStreet());

        // update
        read.setName(read.getName() + " " + read.getName());
        long changes = mdbclient.updateDoc("touchpoints",read.get_id(),read);
        logger.info("updated: " + changes);
        assertTrue(changes > 0);
        // read again
        StationaryTouchpoint read2 = mdbclient.readDocForID(StationaryTouchpointDoc.class,"touchpoints",id);
        assertEquals(read.getName(),read2.getName());

        // delete
        changes = mdbclient.deleteDoc("touchpoints",id);
        logger.info("deleted: " + changes);
        assertTrue(changes > 0);
    }



}
