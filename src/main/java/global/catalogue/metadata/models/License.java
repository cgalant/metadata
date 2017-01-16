package global.catalogue.metadata.models;

import global.catalogue.metadata.persistence.DataStore;

import java.io.Serializable;
import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

//import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;


@JsonInclude(Include.ALWAYS)
@JsonIgnoreProperties(ignoreUnknown = true)
public class License implements Serializable {
    
    private static final long serialVersionUID = 1L;
    private static DataStore<License> licenseStore = new DataStore<>("licenses");
    
    private String id;
    private String name;
    private String text;
    private Set<String> feedIds = new TreeSet<String>();
    
    public License() {
    	this.id = UUID.randomUUID().toString();
    }
    
    public String getId() {
    	return id;
    }
    
    public String getName() {
    	return name;
    }
    
    public void setName(String name) {
    	this.name = name;
    }
    
    public String getText() {
    	return text;
    }
    
    public void setText(String text) {
    	this.text = text;
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
    
    public static Collection<License> getAll () {
        return licenseStore.getAll();
    }
    
    public static License get(String id) {
    	return licenseStore.getById(id);
    }

	public void save() {
		licenseStore.save(this.id, this);
	}

	public void delete() {
		licenseStore.delete(this.id);
	}
}
