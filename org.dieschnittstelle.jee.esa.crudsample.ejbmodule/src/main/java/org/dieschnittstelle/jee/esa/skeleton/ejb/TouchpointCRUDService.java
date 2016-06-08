package org.dieschnittstelle.jee.esa.skeleton.ejb;

import org.dieschnittstelle.jee.esa.entities.crm.StationaryTouchpoint;

import javax.ejb.Local;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Created by master on 07.06.16.
 */
@Path("/touchpoints")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
public interface TouchpointCRUDService {

    @POST
    public CRUDResult<StationaryTouchpoint> createTouchpoint(StationaryTouchpoint todo);

    @GET
    public CRUDResult<StationaryTouchpoint> readAllTouchpoints();

    @GET
    @Path("/{id}")
    public CRUDResult<StationaryTouchpoint> readTouchpoint(@PathParam("id") String id);

}
