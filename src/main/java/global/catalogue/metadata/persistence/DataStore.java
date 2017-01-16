package global.catalogue.metadata.persistence;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import org.mapdb.BTreeMap;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.Serializer;

import global.catalogue.metadata.MetadataManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataStore<T> {
	
	static final Logger LOG = LoggerFactory.getLogger(DataStore.class);
	private DB db;
    private BTreeMap<String, T> map;

    @SuppressWarnings("unchecked")
	public DataStore(String dataFile) {
    	File directory = new File(MetadataManager.storeRepos);
    	if (!directory.exists())
    		directory.mkdirs();
    	try {
            LOG.info("Store repository path: "+directory.getCanonicalPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    	db = DBMaker.fileDB(new File(directory, dataFile + ".db")).fileMmapEnable().closeOnJvmShutdown().make();
    	map = db.treeMap(dataFile).keySerializer(Serializer.STRING).keySerializer(Serializer.JAVA).createOrOpen();
    }

    public T getById(String id) {
    	return map.get(id);
    }
    
    @SuppressWarnings("unchecked")
	public Collection<T> getAll() {
    	return map.values();
    }
    
    public void save(String id, T obj) {
        map.put(id, obj);
        db.commit();
    }
    
    public void delete(String id) {
        map.remove(id);
        db.commit();
    }
}
