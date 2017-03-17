package global.catalogue.metadata.controllers.api;

import spark.Request;
import spark.Response;
import static spark.Spark.delete;
import static spark.Spark.get;
import static spark.Spark.halt;
import static spark.Spark.options;
import static spark.Spark.post;
import static spark.Spark.put;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;

import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletException;
import javax.servlet.http.Part;

import com.fasterxml.jackson.core.JsonProcessingException;

import global.catalogue.commons.JsonManager;
import global.catalogue.metadata.MetadataManager;
import global.catalogue.metadata.models.JsonViews;
import global.catalogue.metadata.models.MiscData;

public class MiscDataController {

	public static JsonManager<MiscData> json = new JsonManager<>(MiscData.class, JsonViews.UserInterface.class);
    
    public static Collection<MiscData> getAllMiscData(Request req, Response res) throws JsonProcessingException {
    	return MiscData.getAll();
    }

    public static MiscData getMiscData(Request req, Response res) throws IOException {
    	String id = req.params("id");
    	MiscData miscData = MiscData.get(id);
    	res.raw().setContentType("application/octet-stream");
        res.raw().setHeader("Content-Disposition", "attachment; filename=" + miscData.getOriginalFileName());
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(res.raw().getOutputStream());
        BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(miscData.getTextFile()));
        byte[] buffer = new byte[1024];
        int len;
        while ((len = bufferedInputStream.read(buffer)) > 0) {
            bufferedOutputStream.write(buffer, 0, len);
        }
        bufferedOutputStream.flush();
        bufferedOutputStream.close();
        bufferedInputStream.close();
    	return miscData;
    }

    public static MiscData createMiscData(Request req, Response res) throws IOException, ServletException {
    	MiscData miscData = new MiscData();
    	try {
    		handleMiscDataFile(req, miscData, true);
    		miscData.save();
    	} catch(Exception e) {
    		e.printStackTrace();
    		throw e;
    	}
    	return miscData;
    }

    public static MiscData updateMiscData(Request req, Response res) throws IOException, ServletException {
        String id = req.params("id");
        MiscData miscData = MiscData.get(id);
    	if (miscData != null) {
        	try {
        		handleMiscDataFile(req, miscData, false);
        		miscData.save();
        	} catch(Exception e) {
        		e.printStackTrace();
        		throw e;
        	}
    	}
        return miscData;
    }

    public static MiscData deleteMiscData(Request req, Response res) throws IOException {
        String id = req.params("id");
        MiscData miscData = MiscData.get(id);
        miscData.delete();
        return miscData;
    }
    
    private static void handleMiscDataFile(Request req, MiscData miscData, boolean create) throws IOException, ServletException {
    	if (req.raw().getAttribute("org.eclipse.jetty.multipartConfig") == null) {
            MultipartConfigElement multipartConfigElement = new MultipartConfigElement(System.getProperty("java.io.tmpdir"));
            req.raw().setAttribute("org.eclipse.jetty.multipartConfig", multipartConfigElement);
        }
    	
    	Part part = null;
    	if (create)
    		part = req.raw().getPart("file");
    	else {
    		try {
    			part = req.raw().getPart("file");
    		} catch(Exception e) {
    			part = null;
    		}
    	}
    	
    	String originalFileName = getFileName(part);
    	if (originalFileName != null && !originalFileName.trim().isEmpty())
    		miscData.setOriginalFileName(originalFileName);
    	
    	String licenseName = req.queryParams("name");
    	if (licenseName == null || licenseName.trim().isEmpty()) {
    		licenseName = originalFileName;
    		if (licenseName == null || licenseName.trim().isEmpty()) {
    			licenseName = "LICENSE";
    		}
    	} else
    		miscData.setName(licenseName);
    	if (miscData.getName() == null)
    		miscData.setName(licenseName);
    	
    	String action = req.queryParams("action");
    	boolean addFeeds = true;
    	if (action != null && "remove".equals(action.trim()))
    		addFeeds = false;
    	
    	String feedids = req.queryParams("feeds");
    	if (feedids != null)
    		if (addFeeds)
    			miscData.addFeedIds(feedids.split(","));
    		else
    			miscData.removeFeedIds(feedids.split(","));

    	if (part == null)
    		return;
    	
    	InputStream inputStream;
        OutputStream outputStream;
        File file = null;
        try {
        	inputStream = part.getInputStream();
        	File path = new File(MetadataManager.storeRepos + File.separatorChar + "miscdata_files");
        	if (!path.exists())
        		path.mkdirs();
        	String fileName = miscData.getId();
        	file = new File(path, fileName);
        	if (file.exists()) {
        		file.delete();
            	file = new File(MetadataManager.storeRepos + File.separatorChar + "miscdata_files", fileName);
        	}
        	outputStream = new FileOutputStream(file);
        	byte[] b = new byte[1024];
        	int len = inputStream.read(b);
        	while (len > -1) {
        		outputStream.write(b, 0, len);
        		len = inputStream.read(b);
        	}
        	outputStream.flush();
        	outputStream.close();
        	inputStream.close();
        	miscData.setTextFile(file);
        } catch(Exception e) {
        	e.printStackTrace();
        	halt(400, "Unable to read uploaded license file");
        }
    }
    
    private static String getFileName(final Part part) {
    	if (part == null)
    		return null;
        final String partHeader = part.getHeader("content-disposition");
        for (String content : partHeader.split(";")) {
            if (content.trim().startsWith("filename")) {
                return content.substring(content.indexOf('=') + 1).trim().replace("\"", "");
            }
        }
        return null;
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
