package global.catalogue.metadata.models;

import java.io.Serializable;
import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.zip.ZipFile;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import global.catalogue.metadata.persistence.DataStore;

@JsonInclude(Include.ALWAYS)
@JsonIgnoreProperties(ignoreUnknown = true)
public class MiscData implements Serializable {

	private static final long serialVersionUID = 1L;
    private static DataStore<MiscData> miscDataStore = new DataStore<>("miscdata");
    
    private String id;
    private ZipFile zipFile;
    private Set<String> feedIds = new TreeSet<String>();

    public MiscData() {
    	this.id = UUID.randomUUID().toString();
    }
    
    public String getId() {
    	return id;
    }
    
    public ZipFile getZipFile() {
    	return zipFile;
    }
    
    public void setZipFile(ZipFile zipFile) {
    	this.zipFile = zipFile;
    }

    public Set<String> getFeedIds() {
    	return feedIds;
    }
    
    public boolean addFeedId(String feedId) {
    	return feedIds.add(feedId);
    }
    
    public boolean removeFeedId(String feedId) {
    	return feedIds.remove(feedId);
    }
    
    public boolean containsFeedId(String feedId) {
    	return feedIds.contains(feedId);
    }

	public static Collection<MiscData> getAll() {
		return miscDataStore.getAll();
	}

	public static MiscData get(String id) {
		// Add zipFile
		return miscDataStore.getById(id);
	}

	public void save() {
		// save zipFile
		miscDataStore.save(this.id, this);
	}

	public void delete() {
		// delete zipFile
		miscDataStore.delete(this.id);
	}
}
