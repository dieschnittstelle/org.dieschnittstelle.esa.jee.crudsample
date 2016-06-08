package org.dieschnittstelle.jee.esa.skeleton.ejb;

import org.apache.log4j.Logger;
import org.dieschnittstelle.jee.esa.entities.crm.StationaryTouchpoint;
import org.dieschnittstelle.jee.esa.skeleton.ejb.mdb.MongoDBClientDocs;

import javax.ejb.Stateless;


@Stateless
public class TouchpointCRUDStatelessMDB implements TouchpointCRUDLocal {

	protected static Logger logger = Logger.getLogger(TouchpointCRUDStatelessMDB.class);

	// the mdb client
	private MongoDBClientDocs mdbclient = new MongoDBClientDocs("127.0.0.1", 27017, "testmdb");

	// the collection name
	private static String TP_COLLECTION = "touchpoints";

	@Override
	public CRUDResult<StationaryTouchpoint> createTouchpoint(StationaryTouchpoint stationaryTouchpoint) {

		String id = mdbclient.createDoc(TP_COLLECTION,stationaryTouchpoint);
		return new CRUDResult<StationaryTouchpoint>(stationaryTouchpoint);
	}

	@Override
	public CRUDResult<StationaryTouchpoint> readAllTouchpoints() {
		return new CRUDResult<StationaryTouchpoint>(mdbclient.readAllDocs(StationaryTouchpoint.class,TP_COLLECTION));
	}

	@Override
	public CRUDResult<StationaryTouchpoint> readTouchpoint(String id) {
		return new CRUDResult<StationaryTouchpoint>(mdbclient.readDocForID(StationaryTouchpoint.class,TP_COLLECTION,id));
	}

}
