package org.dieschnittstelle.jee.esa.skeleton.ejb.mdb;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.apache.log4j.Logger;
import org.bson.*;
import org.bson.types.ObjectId;
import org.dieschnittstelle.esa.vertx.crud.api.EntityMarshaller;

import javax.swing.text.html.parser.Entity;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class MongoDBClientDocs implements MongoDBClient {

    protected static Logger logger = Logger.getLogger(MongoDBClientDocs.class);

    protected MongoClient mongoClient;
    protected MongoDatabase db;
    protected ObjectMapper mapper = new ObjectMapper();

    private boolean bindocs = false;

    // we use our own marshaller here that directly maps Java POJOs to bson documents
    private EntityMarshaller marshaller = new EntityMarshaller(new EntityMarshaller.Delegate() {
        public Object createObject() {
            return new Document();
        }

        public Object createArray() {
            return new ArrayList();
        }

        public boolean isArray(Object o) {
            return o instanceof List;
        }

        public void add(Object o, Object o1) {
            ((List)o).add(getValue(o1));
        }

        public void put(Object o, String attr, Object val) {
            ((Document)o).put(attr,getValue(val));
        }

        private Object getValue(Object obj) {
            if (obj != null && ((obj instanceof List) || (obj instanceof Document))) {
                return obj;
            }
            else if (obj != null) {
                return String.valueOf(obj);
            }
            else {
                return obj;
            }
        }
    });


    public MongoDBClientDocs(String URL, int port, String database) {
        mongoClient = new MongoClient(URL, port);
        db = mongoClient.getDatabase(database);
    }

    @Override
    public String createDoc(String table, Object data) {
        try {
            Document doc = toDoc(data);
            MongoCollection<Document> collection = db.getCollection(table);
            if (collection == null) {
                db.createCollection(table);
            }
            collection.insertOne(doc);
            String id = ((ObjectId) doc.get("_id")).toHexString();
            return id;
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        } finally {
            //mongoClient.close();
        }
    }

    @Override
    public <T> T readDocForID(Class<T> entityklass, String table, String id) {
        try {
            T result = null;
            MongoCollection<Document> collection = db.getCollection(table);
            if (collection != null) {
                FindIterable<Document> response = collection.find(Filters.eq("_id", new ObjectId(id)));
                if (response != null && response.first() != null) {
                    logger.warn("readDocForID(): found object " + response.first());
                    result = fromDoc(entityklass, response.first());
                } else
                    logger.warn("readDocForID(): no object could be found for id " + id);
            }
            return result;
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        } finally {
            //mongoClient.close();
        }
    }

    @Override
    public <T> List<T> readAllDocs(Class<T> entityklass, String table) {
        return readAllDocs(entityklass, table, null, null);
    }

    @Override
    public <T> List<T> readAllDocs(Class<T> entityklass, String table, String attr, Object attrvalue) {
        try {
            MongoCollection<Document> collection = db.getCollection(table);
            List<T> entities = new ArrayList<T>();
            if (collection != null) {
                FindIterable<Document> response = attr == null ? collection.find() : collection.find(Filters.eq(attr, attrvalue));
                if (response != null) {
                    for (MongoCursor<Document> iterator = response.iterator(); iterator.hasNext(); ) {
                        entities.add(fromDoc(entityklass, iterator.next()));
                    }
                }
            }
            return entities;
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        } finally {
            //mongoClient.close();
        }
    }

    @Override
    public <T> T readDocForAttr(Class<T> entityklass, String table, String attrname, Object attrvalue) {
        try {
            T result = null;
            MongoCollection<Document> collection = db.getCollection(table, Document.class);
            if (collection != null) {
                FindIterable<Document> response = collection.find(Filters.eq(attrname, attrvalue));
                if (response != null && response.first() != null) {
                    result = fromDoc(entityklass, response.first());
                }
            }
            return result;
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        } finally {
            //mongoClient.close();
        }
    }

    @Override
    public long updateDoc(String table, String id, Object data) {
        try {
            long count = 0;
            Document doc = toDoc(data);
            doc.remove("_id");
            MongoCollection<Document> collection = db.getCollection(table, Document.class);
            if (collection != null) {
                UpdateResult response = collection.replaceOne(Filters.eq("_id", new ObjectId(id)), doc);
                count = response.getModifiedCount();
            }
            return count;

        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        } finally {
            //mongoClient.close();
        }
    }

    @Override
    public long deleteDoc(String table, String id) {
        boolean deleted = false;
        long count = 0;
        MongoCollection<Document> collection = db.getCollection(table, Document.class);
        if (collection != null) {
            DeleteResult response = collection.deleteOne(Filters.eq("_id", new ObjectId(id)));
            count = response.getDeletedCount();
        }
        //mongoClient.close();
        return count;
    }

    @Override
    public boolean dropTable(String table) {
        db.drop();
        //mongoClient.close();
        return true;
    }

    public Document toDoc(Object obj) throws IOException {

        try {
            Document doc = (Document)((Document)marshaller.marshal(null,obj,null)).get("data");
            logger.debug("marshalled: " + doc);
//            String json = mapper.writeValueAsString(obj);
//            Document doc = Document.parse(json);
            doc.remove("_id");
            return doc;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public <T> T fromDoc(Class<T> entityklass, Document doc) throws IOException {
        String id = ((ObjectId)doc.get("_id")).toHexString();
        doc.remove("_id");
        T obj = mapper.readValue(doc.toJson(), entityklass);

        try {
            obj.getClass().getMethod("set_id",new Class[]{String.class}).invoke(obj,id);
            return obj;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

    }


}
