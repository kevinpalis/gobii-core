package org.gobiiproject.gobiimodel.dto.instructions.extractor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.gobiiproject.gobiimodel.cvnames.JobProgressStatusType;
import org.gobiiproject.gobiimodel.dto.children.PropNameId;
import org.gobiiproject.gobiimodel.types.GobiiExtractFilterType;
import org.gobiiproject.gobiimodel.types.GobiiFileType;
import org.gobiiproject.gobiimodel.types.GobiiSampleListType;

/**
 * Data Set-Specific extract. Each represents a single data set extracting to a unique file name.
 * Created by Phil on 6/6/2016.
 */
public class GobiiDataSetExtract {

    //Type of file to export (or meta data without separate data entries
    private GobiiFileType gobiiFileType = null;
    //Combine data sets into a single output file (Unused/unsupported)
    private boolean accolate = false;
    //Descriptive name of the data set. Used in reporting
    private JobProgressStatusType gobiiJobStatus;
    //Internal ID of the data set. Used for internal lookups.
    private String extractDestinationDirectory = null;


    private GobiiExtractFilterType gobiiExtractFilterType;
    private List<String> markerList = new ArrayList<>();
    private List<String> sampleList = new ArrayList<>();
    private String listFileName;
    private PropNameId gobiiDatasetType = new PropNameId();
    private PropNameId principleInvestigator = new PropNameId();
    private PropNameId project = new PropNameId();
    private PropNameId dataSet = new PropNameId();
    private List<PropNameId> platforms = new ArrayList<>();
    private GobiiSampleListType gobiiSampleListType;
    private List<PropNameId> markerGroups = new ArrayList<>();
    private List<File> extractedFiles = new ArrayList<>();
    private String logMessage;

    public GobiiExtractFilterType getGobiiExtractFilterType() {
        return gobiiExtractFilterType;
    }

    public void setGobiiExtractFilterType(GobiiExtractFilterType gobiiExtractFilterType) {
        this.gobiiExtractFilterType = gobiiExtractFilterType;
    }

    public List<String> getMarkerList() {
        return markerList;
    }

    public void setMarkerList(List<String> markerList) {
        this.markerList = markerList;
    }

    public List<String> getSampleList() {
        return sampleList;
    }

    public void setSampleList(List<String> sampleList) {
        this.sampleList = sampleList;
    }

    public String getListFileName() {
        return listFileName;
    }

    public void setListFileName(String listFileName) {
        this.listFileName = listFileName;
    }

    public GobiiSampleListType getGobiiSampleListType() {
        return gobiiSampleListType;
    }

    public void setGobiiSampleListType(GobiiSampleListType gobiiSampleListType) {
        this.gobiiSampleListType = gobiiSampleListType;
    }

    public PropNameId getGobiiDatasetType() {
        return gobiiDatasetType;
    }

    public void setGobiiDatasetType(PropNameId gobiiDatasetType) {
        this.gobiiDatasetType = gobiiDatasetType;
    }

    public List<PropNameId> getPlatforms() {
        return platforms;
    }

    public void setPlatforms(List<PropNameId> platforms) {
        this.platforms = platforms;
    }

    public GobiiFileType getGobiiFileType() {
        return gobiiFileType;
    }

    public void setGobiiFileType(GobiiFileType gobiiFileType) {
        this.gobiiFileType = gobiiFileType;
    }

    public boolean isAccolate() {
        return accolate;
    }

    public void setAccolate(boolean accolate) {
        this.accolate = accolate;
    }

    public String getExtractDestinationDirectory() {
        return extractDestinationDirectory;
    }

    public void setExtractDestinationDirectory(String extractDestinationDirectory) {
        this.extractDestinationDirectory = extractDestinationDirectory;
    }

    public JobProgressStatusType getGobiiJobStatus() {
        return gobiiJobStatus;
    }

    public void setGobiiJobStatus(JobProgressStatusType gobiiJobStatus) {
        this.gobiiJobStatus = gobiiJobStatus;
    }


    public PropNameId getPrincipleInvestigator() {
        return principleInvestigator;
    }

    public void setPrincipleInvestigator(PropNameId principleInvestigator) {
        this.principleInvestigator = principleInvestigator;
    }

    public PropNameId getProject() {
        return project;
    }

    public void setProject(PropNameId project) {
        this.project = project;
    }

    public PropNameId getDataSet() {
        return dataSet;
    }

    public void setDataSet(PropNameId dataSet) {
        this.dataSet = dataSet;
    }

    public List<PropNameId> getMarkerGroups() {
        return markerGroups;
    }

    public void setMarkerGroups(List<PropNameId> markerGroups) {
        this.markerGroups = markerGroups;
    }

    public List<File> getExtractedFiles() {
        return extractedFiles;
    }

    public void setExtractedFiles(List<File> extractedFiles) {
        this.extractedFiles = extractedFiles;
    }

    public String getLogMessage() { return logMessage; }

    public void setLogMessage(String logMessage) { this.logMessage = logMessage; }

}