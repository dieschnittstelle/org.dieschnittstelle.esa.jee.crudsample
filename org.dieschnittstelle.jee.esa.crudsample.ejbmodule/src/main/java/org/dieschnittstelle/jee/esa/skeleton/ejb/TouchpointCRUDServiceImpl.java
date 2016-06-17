package org.dieschnittstelle.jee.esa.skeleton.ejb;

import org.dieschnittstelle.jee.esa.entities.crm.StationaryTouchpoint;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import org.apache.log4j.Logger;

/**
 * Created by master on 07.06.16.
 */
@Stateless
public class TouchpointCRUDServiceImpl implements TouchpointCRUDService {

    protected static Logger logger = Logger.getLogger(TouchpointCRUDServiceImpl.class);

    @EJB(beanName = "TouchpointCRUDStatelessMDB")
    private TouchpointCRUDLocal crudejbMDB;

    @EJB(beanName = "TouchpointCRUDStateless")
    private TouchpointCRUDLocal crudejb;

    @Context
    private HttpServletRequest request;

    @Override
    public CRUDResult<StationaryTouchpoint> createTouchpoint(StationaryTouchpoint tp) {
        String crudprovider = request.getParameter("crudprovider");
        logger.info("crudprovider: " + crudprovider);
        // determine the crudprovider
        if ("TouchpointCRUDStatelessMDB".equals(crudprovider)) {
            return crudejbMDB.createTouchpoint(tp);
        }
        else {
            return crudejb.createTouchpoint(tp);
        }
    }

    @Override
    public CRUDResult<StationaryTouchpoint> readAllTouchpoints() {
        String crudprovider = request.getParameter("crudprovider");
        if ("TouchpointCRUDStatelessMDB".equals(crudprovider)) {
            return crudejbMDB.readAllTouchpoints();
        }
        else {
            return crudejb.readAllTouchpoints();
        }
    }

    @Override
    public CRUDResult<StationaryTouchpoint> readTouchpoint(String id) {
        String crudprovider = request.getParameter("crudprovider");
        if ("TouchpointCRUDStatelessMDB".equals(crudprovider)) {
            return crudejbMDB.readTouchpoint(id);
        }
        else {
            return crudejb.readTouchpoint(id);
        }
    }
}
