package org.gobiiproject.gobiidomain.services.gdmv3;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.gobiiproject.gobiidomain.GobiiDomainException;
import org.gobiiproject.gobiimodel.config.GobiiException;
import org.gobiiproject.gobiimodel.dto.gdmv3.VendorProtocolDTO;
import org.gobiiproject.gobiimodel.dto.system.PagedResult;
import org.gobiiproject.gobiimodel.entity.VendorProtocol;
import org.gobiiproject.gobiisampletrackingdao.VendorProtocolDao;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.web.WebAppConfiguration;

@WebAppConfiguration
public class VendorProtocolServiceImplTest {

    @Mock
    private VendorProtocolDao vendorProtocolDao;

    @InjectMocks
    private VendorProtocolServiceImpl vendorProtocolServiceImpl;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetVendorProtocolsOk() throws Exception {
        List<VendorProtocol> mockList = new ArrayList<>();
        mockList.add(new VendorProtocol());
        when(vendorProtocolDao.getVendorProtocols(0, 1000)).thenReturn(mockList);

        PagedResult<VendorProtocolDTO> result = vendorProtocolServiceImpl.getVendorProtocols(0, 1000);
        assertTrue( result.getCurrentPageNum() == 0);
        assertTrue( result.getCurrentPageSize() == 1);
        assertTrue( result.getResult().size() == 1);
    }

    @Test( expected = GobiiException.class)
    public void testGetVPsNotOk1() throws Exception {

        when(vendorProtocolDao.getVendorProtocols(0, 1000)).thenThrow(new GobiiException("test"));

        vendorProtocolServiceImpl.getVendorProtocols(0, 1000);
    }

    @Test( expected = GobiiDomainException.class)
    public void testGetVPsNotOk2() throws Exception {
        vendorProtocolServiceImpl.getVendorProtocols(null, null);
    }
    

}