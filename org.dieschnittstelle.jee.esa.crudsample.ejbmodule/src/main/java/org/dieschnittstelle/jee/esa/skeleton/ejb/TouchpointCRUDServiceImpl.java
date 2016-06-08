package org.dieschnittstelle.jee.esa.skeleton.ejb;

import org.dieschnittstelle.jee.esa.entities.crm.StationaryTouchpoint;

import javax.ejb.EJB;
import javax.ejb.Stateless;

/**
 * Created by master on 07.06.16.
 */
@Stateless
public class TouchpointCRUDServiceImpl implements TouchpointCRUDService {

    @EJB(beanName = "TouchpointCRUDStatelessMDB")
    private TouchpointCRUDLocal crudejb;

    @Override
    public CRUDResult<StationaryTouchpoint> createTouchpoint(StationaryTouchpoint tp) {
        return crudejb.createTouchpoint(tp);
    }

    @Override
    public CRUDResult<StationaryTouchpoint> readAllTouchpoints() {
        return crudejb.readAllTouchpoints();
    }

    @Override
    public CRUDResult<StationaryTouchpoint> readTouchpoint(String id) {
        return crudejb.readTouchpoint(id);
    }
}
