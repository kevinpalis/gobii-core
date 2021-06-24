package org.gobiiproject.gobiimodel.dto.system;

import java.util.List;

import org.gobiiproject.gobiimodel.dto.base.DTOBase;
import org.gobiiproject.gobiimodel.dto.gdmv3.CropsDTO;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ExtractorConfigDTO extends DTOBase {

    private String authUrl;
    private String realm;
    private String client;


    @Override
    public Integer getId() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setId(Integer id) {
        // TODO Auto-generated method stub

    }
    
}
