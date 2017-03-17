package global.catalogue.metadata.models;

import global.catalogue.metadata.persistence.DataStore;

import java.io.Serializable;
import java.util.Collection;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;


@JsonInclude(Include.ALWAYS)
@JsonIgnoreProperties(ignoreUnknown = true)
public class MiscData extends Model implements Serializable {

	private static final long serialVersionUID = 1L;
    private static DataStore<MiscData> miscDataStore = new DataStore<>("miscdata");
    
    @Override
    protected void removeFeedIdFromOthers(String feedId) {
    	for (MiscData miscData : MiscData.getAll()) {
    		if (miscData.containsFeedId(feedId)) {
    			miscData.removeFeedId(feedId);
    			miscData.save();
    			break;
    		}
    	}
    }

	public void save() {
		miscDataStore.save(this.getId(), this);
	}

	public void delete() {
		if (getTextFile().exists())
			getTextFile().delete();
		miscDataStore.delete(this.getId());
	}
    
    public static Collection<MiscData> getAll () {
        return miscDataStore.getAll();
    }
    
    public static MiscData get(String id) {
    	return miscDataStore.getById(id);
    }
}
