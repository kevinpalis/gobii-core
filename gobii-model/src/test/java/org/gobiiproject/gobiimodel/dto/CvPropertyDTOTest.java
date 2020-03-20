package org.gobiiproject.gobiimodel.dto;

import org.gobiiproject.gobiimodel.entity.Cv;
import org.gobiiproject.gobiimodel.entity.CvGroup;
import org.gobiiproject.gobiimodel.modelmapper.ModelMapper;
import org.gobiiproject.gobiimodel.dto.children.CvPropertyDTO;
import org.junit.Test;

public class CvPropertyDTOTest {

    @Test
    public void testCvPropertyDTOMapping() {
        Cv entity = new Cv();
        CvGroup group = new CvGroup();
        group.setCvGroupType(1);
        group.setCvGroupId(1);
        group.setCvGroupName("test-name");

        entity.setCvGroup(group);

        CvPropertyDTO dto = new CvPropertyDTO();

        ModelMapper.mapEntityToDto(entity, dto);
        assert dto.getPropertyGroupType() == 1;
        assert dto.getPropertyType() == "system defined";

        
    }
}