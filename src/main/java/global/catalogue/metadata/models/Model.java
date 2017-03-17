package global.catalogue.metadata.models;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

import javax.persistence.MappedSuperclass;

import com.fasterxml.jackson.annotation.JsonIgnore;

@MappedSuperclass
public abstract class Model implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
    private String id;
    private String name;
    private String textFileName;
    private String originalFileName;
    private Set<String> feedIds = new TreeSet<String>();
    
    public String getName() {
    	return name;
    }
    
    public void setName(String name) {
    	this.name = name;
    }
    
    @JsonIgnore
    public File getTextFile() {
    	return new File(textFileName);
    }
    
    public void setTextFile(File textFile) throws IOException {
    	this.textFileName = textFile.getCanonicalPath();
    }
    
    public String getOriginalFileName() {
    	return originalFileName;
    }
    
    public void setOriginalFileName(String originalFileName) {
    	this.originalFileName = originalFileName;
    }
    
    public Set<String> getFeedIds() {
    	return feedIds;
    }
    
    public boolean addFeedId(String feedId) {
    	removeFeedIdFromOthers(feedId);
    	return feedIds.add(feedId);
    }
    
    public boolean removeFeedId(String feedId) {
    	return feedIds.remove(feedId);
    }
    
    public boolean containsFeedId(String feedId) {
    	return feedIds.contains(feedId);
    }

	public void addFeedIds(String[] _feedIds) {
		for(String feedId : _feedIds)
			addFeedId(feedId);
	}

	public void removeFeedIds(String[] _feedIds) {
		for(String feedId : _feedIds)
			removeFeedId(feedId);
	}

	public Model () {
        this.id = UUID.randomUUID().toString();
    }
    
    public String getId() {
    	return id;
    }
    
    protected abstract void removeFeedIdFromOthers(String feedId);
}
