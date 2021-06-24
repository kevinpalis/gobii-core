package org.gobiiproject.gobiimodel.utils.email;


import java.io.File;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.gobiiproject.gobiimodel.config.ConfigSettings;
import org.gobiiproject.gobiimodel.dto.children.PropNameId;
import org.gobiiproject.gobiimodel.types.ServerType;
import org.gobiiproject.gobiimodel.utils.HelperFunctions;
import org.gobiiproject.gobiimodel.utils.error.Logger;
import org.gobiiproject.gobiimodel.utils.links.OCLinkHandler;

/*
 *  GOBII - Process mail message format.  (Hopefully to replace DigesterMessage.java)
 */
@SuppressWarnings({"UnusedReturnValue", "WeakerAccess"})
public class ProcessMessage extends MailMessage {
    private String statusLine;
    private String errorLine;
    private String tableLine;
    private String extractCriteriaLine;
    private String entityLine;
    private String identifierLine;
    private String pathsLine;
    private String validationLine;
    //private String confidentialyMessage;
    File fPath;
    private String color;
    final String redColor = "#E74C3C";
    final String greenColor = "#2ECC71";
    final String tableLineWidth = "40";
    final String entityLineWidth = "40";
    final String identifierLineWidth = "40";
    final String extractCriteriaLineWidth = "40";
    final String pathsLineWidth = "65";
    final String validationLineWidth = "40";
    List<HTMLTableEntity> entries=new ArrayList<>();
    List<HTMLTableEntity> identifiers=new ArrayList<>();
    List<HTMLTableEntity> extractCriteria=new ArrayList<>();
    List<HTMLTableEntity> entities=new ArrayList<>();
    List<HTMLTableEntity> paths=new ArrayList<>();
    List<HTMLTableEntity> validations=new ArrayList<>();

    private String environmentName = System.getProperty("org.gobii.environment.name");

    /**
     * Sets the BODY of the mail message with TABLEs
     * @param jobName Name of the JOB ([GOBII - Extractor]: crop - extraction of "xxxx") 
     * @param shortError error message, 100 or less charectors
     * @param success If the job is success/failed (true/false)
     * @param longError Long error message
     * @return handle to this object
     */
    public ProcessMessage setBody(String jobName, String type, long time, String shortError,boolean success, String longError){
        this.setStatus(success);
        this.setSubject(jobName+(success?" Success":" Failed"));
        this.errorLine=shortError;
        this.color = (success ? greenColor:redColor);
        if(!entries.isEmpty()) {
            tableLine = HTMLTableEntity.getHTMLTable(entries, tableLineWidth,"Table","Total in File", "Total Loaded","Total Existing","Total Invalid");
            entries.clear();
        }
        if(!identifiers.isEmpty()) {
            identifierLine = HTMLTableEntity.getHTMLTable(identifiers, identifierLineWidth,"Identifier Type","Name","ID");
            identifiers.clear();
        }
        if(!extractCriteria.isEmpty()) {
            extractCriteriaLine = HTMLTableEntity.getHTMLTable(extractCriteria, extractCriteriaLineWidth, "Extraction Criteria");
        }
        if(!entities.isEmpty()) {
            entityLine = HTMLTableEntity.getHTMLTable(entities, entityLineWidth,"Type","Count");
            entities.clear();
        }
        if(!validations.isEmpty()) {
            validationLine = HTMLTableEntity.getHTMLTable(validations, validationLineWidth,"Validation","Status","Reason","Column","Values");
            validations.clear();
        }
        if(!paths.isEmpty()) {
            pathsLine = HTMLTableEntity.getHTMLTable(paths, pathsLineWidth,"File Type","Path","Size");
            paths.clear();
        }

        String line="<br/>";
        StringBuilder body=new StringBuilder();
        body.append("<html><head><style>table{font-family:arial,sans-serif;border-collapse:collapse;width:60%;}th{background-color:" + color + ";border:1px solid #dddddd;text-align:left;padding:8px;}td{border:1px solid #dddddd;text-align:left;padding:8px;}tr:nth-child(even){background-color:lightblue;}</style></head><body>");

        if(type!=null){
            body.append("<font size = 4><b>"+type+"</b></font> Duration: "+(time>=1000?time/1000+" secs":time+" ms")+"<br/><br/>");
        }
        else{
            body.append("<br/><br/>");
        }

        body.append(statusLine+line);
        if (StringUtils.isNotEmpty(environmentName)) {
            body.append(String.format("<b>Environment Name: %s</b>", environmentName));
        }
        if(errorLine!=null)body.append(errorLine+line);
        body.append(line);
        if(identifierLine!=null)body.append(identifierLine+line);
        if(extractCriteriaLine!=null)body.append(extractCriteriaLine+line);
        if(entityLine!=null)body.append(entityLine+line);
        if(tableLine!=null)body.append(tableLine+line);
        if(validationLine!=null)body.append(validationLine+line);
        if(pathsLine!=null)body.append(pathsLine+line);
        if(longError!=null)body.append(longError);
        body.append("</html>");
        this.setBody(body.toString());
        return this;
    }

    /**
     * Add entry to the validation results table FAILURE scenario
     * @param fileName Validation JSON "fileName" : "dataset_dnarun"
     * @param status "status" : "FAILURE",
     * @param reason
     * @param columns
     * @param values
     * @return
     */
    public ProcessMessage addValidateTableElement(String fileName, String status, String reason, List<String> columns, List<String> values){
        if(reason.equals("")) {
            validations.add(new HTMLTableEntity(fileName, status, "" , "" , ""));
        }
        else{
            validations.add(new HTMLTableEntity(fileName, status, reason, (columns.isEmpty()) ? "" : String.join(", ", columns), (values.isEmpty()) ? "" : String.join(", ", values)));
        }
        return this;
    }

    /**
     * Add entry to the validation results table SUCCESS scenario
     * @param fileName
     * @param status
     * @return
     */
    public ProcessMessage addValidateTableElement(String fileName, String status){
        addValidateTableElement(fileName, status, "" , null, null);
        return this;
    }

    /**
     * Add an entry to the intermediate File table
     * @param tableName Name of table
     * @param fileCount Unique entry count in file
     * @param loadCount Count of loaded entries
     * @param existCount Count of duplicate entries
     * @return this object
     */
    public ProcessMessage addEntry(String tableName,String fileCount, String loadCount, String existCount, String invalidCount){
        entries.add(new HTMLTableEntity(tableName,fileCount,loadCount,existCount, invalidCount));
        return this;
    }
    
    /**
     * Add an entry to the Identifiers table
     * @param type Type of entry (Platform, Dataset, etc.)
     * @param name Name of entry
     * @param id Identifier of entry
     * @return this object
     */
    public ProcessMessage addIdentifier(String type,String name, String id){
        if((name==null) && ((id==null || id.equals("null"))))return this;
        if(id==null){
            identifiers.add(new HTMLTableEntity(type,escapeHTML(name),""));
        }
        else {
            identifiers.add(new HTMLTableEntity(type, escapeHTML(name), id));
        }
         return this;
    }
    
    public ProcessMessage addIdentifier(String type, PropNameId identifier){
        if(identifier==null)return this;//Don't add a null ID to the table
        return addIdentifier(type,escapeHTML(identifier.getName()),identifier.getId()+"");
    }
    
     /**
     * Add an entry to the Entity table (just with two strings)
     * @param type Type of entry (Platform, Dataset, etc.)
     * @param name Name of entry
     * @return this object
     */
    public ProcessMessage addEntity(String type,String name){
        if(name==null)return this;
        entities.add(new HTMLTableEntity(type,escapeHTML(name)));
        return this;
    }

    public ProcessMessage addCriteria(String type, String name){
        if(name == null)return this;
        extractCriteria.add(new HTMLTableEntity(type, name));
        return this;
    }

    public ProcessMessage addCriteria(String type, PropNameId criteria){
        if(criteria == null)return this;
        if(criteria.getName() == null) return this; // Don't add null values to the table
        extractCriteria.add(new HTMLTableEntity(type, criteria.getName()));
        return this;
    }

    public ProcessMessage addFolderPath(String type, String path, ConfigSettings config) throws Exception {
        String pathLine=path;
        if (config.getGlobalServer(ServerType.OWN_CLOUD).isActive()) {
            try {
                String urlpath= OCLinkHandler.getOwncloudURL(path, config);
                if(urlpath!=null&&!urlpath.isEmpty()) {
                    pathLine = path + "\n" + "<hr>" + "\n" + hyperlink(OCLinkHandler.getOwncloudURL(path, config));
                }
            }catch (ConnectException e) {
                Logger.logWarning("ProcessMessage", "Unable to connect to OwnCloud Server to obtain live links. Defaulting to only generating file system links", e);
                //Note - if catch is caught - no modification was made above to pathLine - thus below block will work as if OWN_CLOUD.isActive() returned false
            }
        }
        paths.add(new HTMLTableEntity(type,pathLine,""));
        return this;
    }

    /**
     * Adds a hyperlinking tag to a string that contains a valid hyperlink.
     * http://example.com becomes
     * <a href="http://example.com">http://example.com</a>
     * @param toHyperlink
     * @return
     */
    private String hyperlink(String toHyperlink){
        if(toHyperlink==null || toHyperlink.equals("")){
            return toHyperlink;
        }
        return "<a href=\""+toHyperlink+"\">"+toHyperlink + "</a>";
    }

        /**
         * Add item to the filepaths entry
         * @param type type of file
         * @param path filepath
         * #param alwaysShow to always show the path, even if no object is there
         * @return this object
         */
    public ProcessMessage addPath(String type,String path, boolean alwaysShow, ConfigSettings config, boolean publicUrl) throws Exception {

        if(path==null){
            //Do nothing if path is not specified
            return this;
        }
        // As per LiveLinks 1.c., if no preview is available, make a link to the folder.
        // Convert this into a folder path by cutting off the file, so path will end with /

        if(!publicUrl){
            if(!path.endsWith("/") && path.contains("/")){
                path = path.substring(0,path.lastIndexOf("/")+1);
            }
        }


        String pathLine = path;
        if (config.getGlobalServer(ServerType.OWN_CLOUD).isActive()) {
            String urlpath = null;
            try {
                urlpath = path.endsWith("/") ? OCLinkHandler.getOwncloudURL(path, config) : OCLinkHandler.getLink(path, config, publicUrl);
            } catch (ConnectException e) {
                Logger.logWarning("ProcessMessage", "Unable to connect to OwnCloud Server to obtain live links. Defaulting to only generating file system links", e);
                //Note - if catch is caught - no modification was made above to pathLine - thus below block will work as if OWN_CLOUD.isActive() returned false
            }
            if (urlpath == null || urlpath.isEmpty()) {
                //return normal path
                Logger.logWarning("ProcessMessage", "Unable to generate URL link for " + path);

            } else {
                pathLine = path + "\n" + "<hr>" + "\n" + hyperlink(urlpath);
            }
        }
    	if(new File(path).length() > 1){
    		paths.add(new HTMLTableEntity(type, pathLine, HelperFunctions.sizeToReadable(new File(path).length())));
    	}
    	else if(alwaysShow){
    	    paths.add(new HTMLTableEntity(type,pathLine,""));
        }
        return this;
    }

    /**
     * As ProcessMessage(type,path,False)
     * @param type type of file
     * @param path filepath
     * @return
     */
    public ProcessMessage addPath(String type, String path, ConfigSettings config, boolean publicUrl) throws Exception {
        return addPath(type,path,false,config, publicUrl);
    }

    
    /**
     * Set status line in HTML format. format includes font size and color
     * @param status
     * @return
     */
    private ProcessMessage setStatus(boolean status) {
        statusLine = "Status: " + (status ?
                "<font color="+greenColor+" size=4><b>SUCCESS</b></font>" :
                "<font color="+redColor+" size=4><b>Failed</b></font>");
        return this;
    }

    public ProcessMessage addConfidentialityMessage(String confidentialyMessage){
        this.setConfidentialityMessage("<font color="+redColor+"><b></br>"+escapeHTML(confidentialyMessage)+"</br></b></font>");
        return this;
    }

    public String escapeHTML(String strToHTML){
        return StringEscapeUtils.escapeHtml(strToHTML);
    }
}

