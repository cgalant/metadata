package global.catalogue.metadata.controllers.api;

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
import global.catalogue.metadata.models.MiscData;
import spark.Request;
import spark.Response;

public class MiscDataController {

	public static JsonManager<MiscData> json = new JsonManager<>(MiscData.class, JsonViews.UserInterface.class);
    private static ObjectMapper mapper = new ObjectMapper();
    
    public static Collection<MiscData> getAllMiscData(Request req, Response res) throws JsonProcessingException {
    	return MiscData.getAll();
    }

    public static MiscData getMiscData(Request req, Response res) {
    	String id = req.params("id");
    	return MiscData.get(id);
    }

    public static MiscData createMiscData(Request req, Response res) throws IOException {
    	MiscData miscData = new MiscData();
    	applyJsonToMiscData(miscData, req.body(), true);
    	miscData.save();
    	return miscData;
    }

    public static MiscData updateMiscData(Request req, Response res) throws IOException {
        String id = req.params("id");
        MiscData miscData = MiscData.get(id);
        String body = req.body();
        JsonNode node = mapper.readTree(body);
        JsonNode action = node.findValue("action");
        if (action != null && "remove".equals(action.asText()))
        	applyJsonToMiscData(miscData, req.body(), false);
        else
        	applyJsonToMiscData(miscData, req.body(), true);
        miscData.save();
        return miscData;
    }

    public static MiscData deleteMiscData(Request req, Response res) throws IOException {
        String id = req.params("id");
        MiscData miscData = MiscData.get(id);
        miscData.delete();
        return miscData;
    }
    
    private static void applyJsonToMiscData(MiscData miscData, String json, boolean add) throws IOException {
    	JsonNode node = mapper.readTree(json);
    	Iterator<Map.Entry<String, JsonNode>> fieldsIter = node.fields();
    	while (fieldsIter.hasNext()) {
    		Map.Entry<String, JsonNode> entry = fieldsIter.next();
    		if ("name".equals(entry.getKey())) { // TODO: Adding fileZip
    			;//miscData.setName(entry.getValue().asText());
    		}
            else if ("feedIds".equals(entry.getKey())) {
            	JsonNode feedIdsNode = entry.getValue();
            	if (feedIdsNode.isArray())
            		for (int i = 0; i < feedIdsNode.size(); i++)
            			if (add) {
            				for (MiscData li : MiscData.getAll()) // remove feedIdsNode.get(i) from any other miscData
            					if (li.removeFeedId(feedIdsNode.get(i).asText())) {
            						li.save();
            						break;
            					}
            				miscData.addFeedId(feedIdsNode.get(i).asText());	
            			} else {
            				miscData.removeFeedId(feedIdsNode.get(i).asText());
            			}
            }
    	}
    }

    public static void register(String apiPrefix) {
		options(apiPrefix + "secure/miscdata", (q, s) -> "");
		options(apiPrefix + "secure/miscdata/:id", (q, s) -> "");
		get(apiPrefix + "secure/miscdata", MiscDataController::getAllMiscData, json::write);
		get(apiPrefix + "secure/miscdata/:id", MiscDataController::getMiscData, json::write);
        post(apiPrefix + "secure/miscdata", MiscDataController::createMiscData, json::write);
        put(apiPrefix + "secure/miscdata/:id", MiscDataController::updateMiscData, json::write);
        delete(apiPrefix + "secure/miscdata/:id", MiscDataController::deleteMiscData, json::write);
	}
}
