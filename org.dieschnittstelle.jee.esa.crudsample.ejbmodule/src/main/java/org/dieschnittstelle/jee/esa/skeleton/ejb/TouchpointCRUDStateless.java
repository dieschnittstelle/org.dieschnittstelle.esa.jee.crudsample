package org.dieschnittstelle.jee.esa.skeleton.ejb;

import org.apache.log4j.Logger;
import org.dieschnittstelle.jee.esa.entities.crm.AbstractTouchpoint;
import org.dieschnittstelle.jee.esa.entities.crm.StationaryTouchpoint;

import java.util.Arrays;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;


@Stateless
public class TouchpointCRUDStateless implements TouchpointCRUDLocal {

	protected static Logger logger = Logger.getLogger(TouchpointCRUDStateless.class);

	@PersistenceContext(unitName="skeleton_PU")
	private EntityManager em;

	@Override
	public CRUDResult<StationaryTouchpoint> createTouchpoint(StationaryTouchpoint StationaryTouchpoint) {

		try {
			logger.info("createTouchpoint(): annotations on customers attribute are: " + Arrays.asList(AbstractTouchpoint.class.getDeclaredField("customers").getDeclaredAnnotations()));
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		}

		em.persist(StationaryTouchpoint);
		return new CRUDResult<StationaryTouchpoint>(cleanupEntity(StationaryTouchpoint));
	}

	@Override
	public CRUDResult<StationaryTouchpoint> readAllTouchpoints() {
		return new CRUDResult<StationaryTouchpoint>(cleanupEntities(em.createQuery("FROM StationaryTouchpoint").getResultList()));
	}

	@Override
	public CRUDResult<StationaryTouchpoint> readTouchpoint(String id) {
		return new CRUDResult<StationaryTouchpoint>(cleanupEntity(em.find(StationaryTouchpoint.class, Long.parseLong(id))));
	}

	/* this is not a solution but a dirty workaround: for some reason, jackson within jboss 8 ignores the @JsonIgnore annotations on the customers attribute even though it is imported from the jackson 2 package and is available at runtime (see above), so we delete the attribute before returning an entity... */

	private List<StationaryTouchpoint> cleanupEntities(List<StationaryTouchpoint> tps) {
		for (StationaryTouchpoint tp : tps) {
			cleanupEntity(tp);
		}
		return tps;
	}

	private StationaryTouchpoint cleanupEntity(StationaryTouchpoint tp) {
		tp.setCustomers(null);
		tp.setTransactions(null);
		return tp;
	}

}
