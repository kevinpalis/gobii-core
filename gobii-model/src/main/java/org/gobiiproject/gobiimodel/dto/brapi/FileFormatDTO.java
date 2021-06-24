package org.gobiiproject.gobiimodel.dto.brapi;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class FileFormatDTO {

    private String dataFormat;

    private String fileFormat;

    private String fileURL;

    private String sepPhased;

    private String sepUnphased;

    private String unknownString;

    public String getDataFormat() {
        return dataFormat;
    }

    public void setDataFormat(String dataFormat) {
        this.dataFormat = dataFormat;
    }

    public String getFileFormat() {
        return fileFormat;
    }

    public void setFileFormat(String fileFormat) {
        this.fileFormat = fileFormat;
    }

    public String getFileURL() {
        return fileURL;
    }

    public void setFileURL(String fileURL) {
        this.fileURL = fileURL;
    }

    public String getSepPhased() {
        return sepPhased;
    }

    public void setSepPhased(String sepPhased) {
        this.sepPhased = sepPhased;
    }

    public String getSepUnphased() {
        return sepUnphased;
    }

    public void setSepUnphased(String sepUnphased) {
        this.sepUnphased = sepUnphased;
    }

    public String getUnknownString() {
        return unknownString;
    }

    public void setUnknownString(String unknownString) {
        this.unknownString = unknownString;
    }
}
