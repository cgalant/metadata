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
import global.catalogue.metadata.models.License;

public class LicenseController {
	
	public static JsonManager<License> json = new JsonManager<>(License.class, JsonViews.UserInterface.class);

    public static Collection<License> getAllLicenses(Request req, Response res) throws JsonProcessingException {
    	return License.getAll();
    }

    public static License getLicense(Request req, Response res) throws IOException {
    	String id = req.params("id");
    	License license = License.get(id);
    	res.raw().setContentType("application/octet-stream");
        res.raw().setHeader("Content-Disposition", "attachment; filename=" + license.getOriginalFileName());
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(res.raw().getOutputStream());
        BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(license.getTextFile()));
        byte[] buffer = new byte[1024];
        int len;
        while ((len = bufferedInputStream.read(buffer)) > 0) {
            bufferedOutputStream.write(buffer, 0, len);
        }
        bufferedOutputStream.flush();
        bufferedOutputStream.close();
        bufferedInputStream.close();
        return license;
    }

    public static License createLicense(Request req, Response res) throws IOException, ServletException {
    	License license = new License();
    	try {
    		handleLicenseFile(req, license, true);
    		license.save();
    	} catch(Exception e) {
    		e.printStackTrace();
    		throw e;
    	}
    	return license;
    }
    
    public static License updateLicense(Request req, Response res) throws IOException, ServletException {
    	String id = req.params("id");
    	License license = License.get(id);
    	if (license != null) {
        	try {
        		handleLicenseFile(req, license, false);
        		license.save();
        	} catch(Exception e) {
        		e.printStackTrace();
        		throw e;
        	}
    	}
    	return license;
    }

    public static License deleteLicense(Request req, Response res) throws IOException {
        String id = req.params("id");
        License license = License.get(id);
        license.delete();
        return license;
    }

    public static License deleteLicenses(Request req, Response res) throws IOException {
        for (License license: License.getAll())
	    license.delete();
        return null;
    }
    
    private static void handleLicenseFile(Request req, License license, boolean create) throws IOException, ServletException {
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
    		license.setOriginalFileName(originalFileName);
    	
    	String licenseName = req.queryParams("name");
    	if (licenseName == null || licenseName.trim().isEmpty()) {
    		licenseName = originalFileName;
    		if (licenseName == null || licenseName.trim().isEmpty()) {
    			licenseName = "LICENSE";
    		}
    	} else
    		license.setName(licenseName);
    	if (license.getName() == null)
    		license.setName(licenseName);
    	
    	String action = req.queryParams("action");
    	boolean addFeeds = true;
    	if (action != null && "remove".equals(action.trim()))
    		addFeeds = false;
    	
    	String feedids = req.queryParams("feeds");
    	if (feedids != null)
    		if (addFeeds)
    			license.addFeedIds(feedids.split(","));
    		else
    			license.removeFeedIds(feedids.split(","));

    	if (part == null)
    		return;
    	
    	InputStream inputStream;
        OutputStream outputStream;
        File file = null;
        try {
        	inputStream = part.getInputStream();
        	File path = new File(MetadataManager.storeRepos + File.separatorChar + "license_files");
        	if (!path.exists())
        		path.mkdirs();
        	String fileName = license.getId();
        	file = new File(path, fileName);
        	if (file.exists()) {
        		file.delete();
            	file = new File(MetadataManager.storeRepos + File.separatorChar + "license_files", fileName);
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
        	license.setTextFile(file);
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
		options(apiPrefix + "secure/licenses", (q, s) -> "");
		options(apiPrefix + "secure/license", (q, s) -> "");
		options(apiPrefix + "secure/license/:id", (q, s) -> "");
		get(apiPrefix + "secure/license", LicenseController::getAllLicenses, json::write);
		get(apiPrefix + "secure/license/:id", LicenseController::getLicense, json::write);
        post(apiPrefix + "secure/license", LicenseController::createLicense, json::write);
        put(apiPrefix + "secure/license/:id", LicenseController::updateLicense, json::write);
        delete(apiPrefix + "secure/license/:id", LicenseController::deleteLicense, json::write);
        delete(apiPrefix + "secure/licenses", LicenseController::deleteLicenses, json::write);
	}
}
