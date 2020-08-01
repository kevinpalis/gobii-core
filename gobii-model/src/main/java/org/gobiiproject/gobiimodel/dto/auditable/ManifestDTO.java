package org.gobiiproject.gobiimodel.dto.auditable;


import org.gobiiproject.gobiimodel.dto.base.DTOBaseAuditable;
import org.gobiiproject.gobiimodel.dto.annotations.GobiiEntityColumn;
import org.gobiiproject.gobiimodel.dto.annotations.GobiiEntityParam;
import org.gobiiproject.gobiimodel.types.GobiiEntityNameType;


/**
 * Created by Angel on 5/6/2016.
 * Modified by Yanii on 1/27/2017.
 */
public class ManifestDTO extends DTOBaseAuditable {

    public ManifestDTO() {
        super(GobiiEntityNameType.MANIFEST);
    }

    // we are waiting until we a have a view to retirn
    // properties for that property: we don't know how to represent them yet
    private Integer manifestId;
    private String name;
    private String code;
    private String filePath;

    @Override
    public Integer getId() {
        return this.manifestId;
    }

    @Override
    public void setId(Integer id) {
        this.manifestId = id;
    }

    @GobiiEntityParam(paramName = "manifestId")
    public Integer getManifestId() {return manifestId;}
    @GobiiEntityColumn(columnName = "manifest_id")
    public void setManifestId(Integer manifestId) {
        this.manifestId = manifestId;
    }

    @GobiiEntityParam(paramName = "name")
    public String getName() {
        return name;
    }
    @GobiiEntityColumn(columnName = "name")
    public void setName(String name) {
        this.name = name;
    }

    @GobiiEntityParam(paramName = "code")
    public String getCode() {
        return code;
    }
    @GobiiEntityColumn(columnName = "code")
    public void setCode(String code) {
        this.code = code;
    }

    @GobiiEntityParam(paramName = "filePath")
    public String getFilePath() {return filePath;}
    @GobiiEntityColumn(columnName = "file_path")
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }


}
