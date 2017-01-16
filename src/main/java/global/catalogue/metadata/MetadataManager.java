package global.catalogue.metadata;

import global.catalogue.commons.CORSFilter;
import global.catalogue.metadata.controllers.api.LicenseController;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import static spark.Spark.before;
import static spark.Spark.port;

public class MetadataManager {
    
    private static JsonNode config;
    private static String apiPrefix = "/api/metadata/";
    public static String storeRepos = "/opt/catalogue/metadata/";
    
    public static void main(String[] args) throws IOException {
        initConfig(args);
        initHttpServer();
    }
    
    private static void initConfig(String[] args) throws IOException {
        FileInputStream in;
        if (args.length == 0)
            in = new FileInputStream(new File("config.yml"));
        else
            in = new FileInputStream(new File(args[0]));
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        config = mapper.readTree(in);
        if (config.get("metadata").has("store_repos"))
        	storeRepos = config.get("metadata").get("store_repos").asText();
    }
    
    private static void initHttpServer() {
        if (config.get("metadata").has("port"))
            port(Integer.parseInt(config.get("metadata").get("port").asText()));
        CORSFilter.apply();
        if (config.get("metadata").has("version"))
            apiPrefix += config.get("metadata").get("version") + "/";
        else
            apiPrefix += "v1/";
        LicenseController.register(apiPrefix);
        
        before(apiPrefix + "secure/*", (request, response) -> {
        	if (request.requestMethod().equals("OPTIONS"))
        		return;
        	//Auth0Connection.checkUser(request); // halt with a code and a message!! What if this can be done with the help 
        	// of an external microservice??? Good
        });
    }
}
