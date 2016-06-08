package org.dieschnittstelle.jee.esa.skeleton.ejb.mdb;

import org.bson.Document;

import java.io.IOException;
import java.util.List;

/**
 * Created by master on 07.06.16.
 */
public interface MongoDBClient {
    String createDoc(String table, Object data);

    <T> T readDocForID(Class<T> entityklass, String table, String id);

    <T> List<T> readAllDocs(Class<T> entityklass, String table);

    <T> List<T> readAllDocs(Class<T> entityklass, String table, String attr, Object attrvalue);

    <T> T readDocForAttr(Class<T> entityklass, String table, String attrname, Object attrvalue);

    long updateDoc(String table, String id, Object data);

    long deleteDoc(String table, String id);

    boolean dropTable(String table);

}
