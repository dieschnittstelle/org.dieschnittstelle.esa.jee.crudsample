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
import de.undercouch.bson4jackson.BsonFactory;
import org.apache.log4j.Logger;
import org.bson.BsonDocument;
import org.bson.BsonObjectId;
import org.bson.Document;
import org.bson.RawBsonDocument;
import org.bson.types.ObjectId;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class MongoDBClientBindocs implements MongoDBClient {

    protected static Logger logger = Logger.getLogger(MongoDBClientBindocs.class);

    protected MongoClient mongoClient;
    protected MongoDatabase db;
    protected ObjectMapper binmapper = new ObjectMapper(new BsonFactory());
    protected ObjectMapper mapper = new ObjectMapper();

    private boolean bindocs = false;

    public MongoDBClientBindocs(String URL, int port, String database) {
        mongoClient = new MongoClient(URL, port);
        db = mongoClient.getDatabase(database);
    }

    public String createDoc(String table, Object obj) {
        try {
            // we need to set the id manually on the object as otherwise binary document creation fails
            obj.getClass().getMethod("set_id",new Class[]{String.class}).invoke(obj,new ObjectId().toHexString());

            BsonDocument doc = toBindoc(obj);
            MongoCollection<BsonDocument> collection = db.getCollection(table, BsonDocument.class);
            if (collection == null) {
                db.createCollection(table);
            }
            BsonObjectId oid = null;
            collection.insertOne(doc);
            logger.debug("createDoc(): id on insert is: " + doc.get("_id"));
            //oid = (BsonObjectId) doc.get("_id");
            return doc.get("_id").asString().getValue();//oid.asString().getValue();
        } catch (Exception ioe) {
            throw new RuntimeException(ioe);
        } finally {
            //mongoClient.close();
        }
    }

    public <T> T readDocForID(Class<T> entityklass, String table, String id) {
        try {
            T result = null;
            MongoCollection<BsonDocument> collection = db.getCollection(table, BsonDocument.class);
            if (collection != null) {
                FindIterable<BsonDocument> response = collection.find(Filters.eq("_id", new ObjectId(id)));
                if (response != null && response.first() != null) {
                    logger.warn("readDocForID(): found object " + response.first());
                    result = fromBindoc(entityklass, response.first());
                } else {
                    logger.warn("readDocForID(): no object could be found for id " + id);
                }
            }
            return result;
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        } finally {
            //mongoClient.close();
        }
    }

    public <T> List<T> readAllDocs(Class<T> entityklass, String table) {
        return readAllDocs(entityklass, table, null, null);
    }

    public <T> List<T> readAllDocs(Class<T> entityklass, String table, String attr, Object attrvalue) {
        try {
            MongoCollection<BsonDocument> collection = db.getCollection(table, BsonDocument.class);
            List<T> entities = new ArrayList<T>();
            if (collection != null) {
                FindIterable<BsonDocument> response = attr == null ? collection.find() : collection.find(Filters.eq(attr, attrvalue));
                if (response != null) {
                    for (MongoCursor<BsonDocument> iterator = response.iterator(); iterator.hasNext(); ) {
                        entities.add(fromBindoc(entityklass, iterator.next()));
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

    public <T> T readDocForAttr(Class<T> entityklass, String table, String attrname, Object attrvalue) {
        try {
            T result = null;
            MongoCollection<BsonDocument> collection = db.getCollection(table, BsonDocument.class);
            if (collection != null) {
                FindIterable<BsonDocument> response = collection.find(Filters.eq(attrname, attrvalue));
                if (response != null && response.first() != null) {
                    result = fromBindoc(entityklass, response.first());
                }
            }
            return result;
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        } finally {
            //mongoClient.close();
        }
    }

    public long updateDoc(String table, String id, Object data) {
        try {
            long count = 0;
            BsonDocument doc = toBindoc(data);
            doc.remove("_id");
            MongoCollection<BsonDocument> collection = db.getCollection(table, BsonDocument.class);
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

    public long deleteDoc(String table, String id) {
        boolean deleted = false;
        long count = 0;
        MongoCollection<BsonDocument> collection = db.getCollection(table, BsonDocument.class);
        if (collection != null) {
            DeleteResult response = collection.deleteOne(Filters.eq("_id", new ObjectId(id)));
            count = response.getDeletedCount();
        }
        //mongoClient.close();
        return count;
    }

    public boolean dropTable(String table) {
        db.drop();
        //mongoClient.close();
        return true;
    }

    public BsonDocument toBindoc(Object obj) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        binmapper.writeValue(baos, obj);
        RawBsonDocument rawdoc = new RawBsonDocument(baos.toByteArray());

        return rawdoc;
    }

    public <T> T fromBindoc(Class<T> entityklass, BsonDocument doc) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(doc.asBinary().getData());
        logger.info("created input stream: " + bais);

        return binmapper.readValue(bais, entityklass);
    }


}
