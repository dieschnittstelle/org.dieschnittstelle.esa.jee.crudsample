package org.dieschnittstelle.jee.esa.skeleton.ejb;

import java.util.List;

/**
 * Created by master on 27.05.16.
 */
public class CRUDResult<T> {

    private T entity;
    private List<T> entityList;
    private long entityId;

    public CRUDResult() {

    }

    public CRUDResult(long entityId) {
        this.entityId = entityId;
    }

    public CRUDResult(T entity) {
        this.entity = entity;
    }

    public CRUDResult(int rowsChanged) {
        this.rowsChanged = rowsChanged;
    }

    public CRUDResult(List<T> entityList) {
        this.entityList = entityList;
    }

    public T getEntity() {
        return entity;
    }

    public void setEntity(T entity) {
        this.entity = entity;
    }

    public List<T> getEntityList() {
        return entityList;
    }

    public void setEntityList(List<T> entityList) {
        this.entityList = entityList;
    }

    public long getEntityId() {
        return entityId;
    }

    public void setEntityId(long entityId) {
        this.entityId = entityId;
    }

    public int getRowsChanged() {
        return rowsChanged;
    }

    public void setRowsChanged(int rowsChanged) {
        this.rowsChanged = rowsChanged;
    }

    private int rowsChanged;


}
