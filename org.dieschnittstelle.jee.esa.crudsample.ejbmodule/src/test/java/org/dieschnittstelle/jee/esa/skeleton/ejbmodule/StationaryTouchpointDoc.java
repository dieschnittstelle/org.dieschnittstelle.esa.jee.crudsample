package org.dieschnittstelle.jee.esa.skeleton.ejbmodule;

import org.dieschnittstelle.jee.esa.entities.crm.Address;
import org.dieschnittstelle.jee.esa.entities.crm.Location;
import org.dieschnittstelle.jee.esa.entities.crm.StationaryTouchpoint;

/**
 * Created by master on 07.06.16.
 */
public class StationaryTouchpointDoc extends StationaryTouchpoint {

    private String _id;

    public StationaryTouchpointDoc() {

    }

    public StationaryTouchpointDoc(int id,String name, Address location) {
        super(id,name,location);
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

}
