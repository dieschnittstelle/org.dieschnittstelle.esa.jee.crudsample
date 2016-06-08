package org.dieschnittstelle.jee.esa.skeleton.ejb;

import java.util.List;

import javax.ejb.Local;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import org.dieschnittstelle.jee.esa.entities.crm.StationaryTouchpoint;

@Local
public interface TouchpointCRUDLocal {

	public CRUDResult<StationaryTouchpoint> createTouchpoint(StationaryTouchpoint todo);

	public CRUDResult<StationaryTouchpoint> readAllTouchpoints();

	public CRUDResult<StationaryTouchpoint> readTouchpoint(@PathParam("id") String id);
	
}
