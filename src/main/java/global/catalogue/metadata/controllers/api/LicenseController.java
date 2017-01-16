package global.catalogue.metadata.controllers.api;

import spark.Request;
import spark.Response;
import static spark.Spark.delete;
import static spark.Spark.get;
import static spark.Spark.options;
import static spark.Spark.post;
import static spark.Spark.put;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import global.catalogue.commons.JsonManager;
import global.catalogue.metadata.models.JsonViews;
import global.catalogue.metadata.models.License;

public class LicenseController {
	
	public static JsonManager<License> json = new JsonManager<>(License.class, JsonViews.UserInterface.class);
    private static ObjectMapper mapper = new ObjectMapper();

    public static Collection<License> getAllLicenses(Request req, Response res) throws JsonProcessingException {
    	return License.getAll();
    }

    public static License getLicense(Request req, Response res) {
    	String id = req.params("id");
    	return License.get(id);
    }

    public static License createLicense(Request req, Response res) throws IOException {
    	License license = new License();
    	applyJsonToLicense(license, req.body(), true);
    	license.save();
    	return license;
    }

    public static License updateLicense(Request req, Response res) throws IOException {
        String id = req.params("id");
        License license = License.get(id);
        String body = req.body();
        JsonNode node = mapper.readTree(body);
        JsonNode action = node.findValue("action");
        if (action != null && "remove".equals(action.asText()))
        	applyJsonToLicense(license, req.body(), false);
        else
        	applyJsonToLicense(license, req.body(), true);
        license.save();
        return license;
    }

    public static License deleteLicense(Request req, Response res) throws IOException {
        String id = req.params("id");
        License license = License.get(id);
        license.delete();
        return license;
    }
    
    private static void applyJsonToLicense(License license, String json, boolean add) throws IOException {
    	JsonNode node = mapper.readTree(json);
    	Iterator<Map.Entry<String, JsonNode>> fieldsIter = node.fields();
    	while (fieldsIter.hasNext()) {
    		Map.Entry<String, JsonNode> entry = fieldsIter.next();
    		if("name".equals(entry.getKey())) {
    			license.setName(entry.getValue().asText());
    		}
            else if("text".equals(entry.getKey())) {
            	license.setText(entry.getValue().asText());
            }
            else if ("feedIds".equals(entry.getKey())) {
            	JsonNode feedIdsNode = entry.getValue();
            	if (feedIdsNode.isArray())
            		for (int i = 0; i < feedIdsNode.size(); i++)
            			if (add) {
            				for (License li : License.getAll()) // remove feedIdsNode.get(i) from any other License
            					if (li.removeFeedId(feedIdsNode.get(i).asText())) {
            						li.save();
            						break;
            					}
            				license.addFeedId(feedIdsNode.get(i).asText());	
            			} else {
            				license.removeFeedId(feedIdsNode.get(i).asText());
            			}
            }
    	}
    }
    
	public static void register(String apiPrefix) {
		options(apiPrefix + "secure/license", (q, s) -> "");
		options(apiPrefix + "secure/license/:id", (q, s) -> "");
		get(apiPrefix + "secure/license", LicenseController::getAllLicenses, json::write);
		get(apiPrefix + "secure/license/:id", LicenseController::getLicense, json::write);
        post(apiPrefix + "secure/license", LicenseController::createLicense, json::write);
        put(apiPrefix + "secure/license/:id", LicenseController::updateLicense, json::write);
        delete(apiPrefix + "secure/license/:id", LicenseController::deleteLicense, json::write);
	}
}
