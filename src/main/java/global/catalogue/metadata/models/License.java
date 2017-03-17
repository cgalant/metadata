package global.catalogue.metadata.models;

import global.catalogue.metadata.persistence.DataStore;

import java.io.Serializable;
import java.util.Collection;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;


@JsonInclude(Include.ALWAYS)
@JsonIgnoreProperties(ignoreUnknown = true)
public class License extends Model implements Serializable {
    
    private static final long serialVersionUID = 1L;
    private static DataStore<License> licenseStore = new DataStore<>("licenses");
    
	@Override
	protected void removeFeedIdFromOthers(String feedId) {
		for (License license : License.getAll()) {
			if (license.containsFeedId(feedId)) {
				license.removeFeedId(feedId);
				license.save();
				break;
			}
		}
	}

	public void save() {
		licenseStore.save(this.getId(), this);
	}

	public void delete() {
		if (getTextFile().exists())
			getTextFile().delete();
		licenseStore.delete(this.getId());
	}
    
    public static Collection<License> getAll () {
        return licenseStore.getAll();
    }
    
    public static License get(String id) {
    	return licenseStore.getById(id);
    }
}
