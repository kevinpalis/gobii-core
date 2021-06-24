package org.gobiiproject.gobiidomain.services.gdmv3;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.AssertionErrors.assertTrue;

import java.util.List;
import java.util.Random;

import org.apache.commons.lang.RandomStringUtils;
import org.gobiiproject.gobiidomain.services.brapi.MockSetup;
import org.gobiiproject.gobiimodel.dto.gdmv3.ProtocolDTO;
import org.gobiiproject.gobiimodel.dto.system.PagedResult;
import org.gobiiproject.gobiimodel.entity.Protocol;
import org.gobiiproject.gobiimodel.modelmapper.ModelMapper;
import org.gobiiproject.gobiisampletrackingdao.ContactDao;
import org.gobiiproject.gobiisampletrackingdao.PlatformDao;
import org.gobiiproject.gobiisampletrackingdao.ProtocolDao;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.web.WebAppConfiguration;

@WebAppConfiguration
public class ProtocolServiceImplTest {

    @InjectMocks
    private ProtocolServiceImpl protocolService;

    @Mock
    private ProtocolDao protocolDao;

    @Mock
    private ContactDao contactDao;

    @Mock
    private PlatformDao platformDao;

    MockSetup mockSetup;

    Random random = new Random();

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        mockSetup  = new MockSetup();
    }

    @Test
    public void getProtocolByIdTest() {
        int testPageSize = 10;
        mockSetup.createMockProtocols(testPageSize);

        Protocol testProtocol = mockSetup.mockProtocols
            .get(random.nextInt(mockSetup.mockProtocols.size() - 1));

        when(protocolDao.getProtocolById(testProtocol.getProtocolId())).thenReturn(testProtocol);

        ProtocolDTO protocolDTO = protocolService.getProtocolById(testProtocol.getProtocolId());

        testFieldMappings(protocolDTO, testProtocol);

    }

    @Test
    public void getProtocolsTest() {
        int testPageSize = 10;
        mockSetup.createMockProtocols(testPageSize);

        List<Protocol> testProtocols = mockSetup.mockProtocols.subList(0, testPageSize-2);

        when(protocolDao.getProtocols(testPageSize, 0, null)).thenReturn(testProtocols);

        PagedResult<ProtocolDTO> protocolPagedResult =
            protocolService.getProtocols(testPageSize, 0, null);

        assertTrue("PagedResult page size test failed",
            protocolPagedResult.getCurrentPageSize() == testPageSize-2);

        assertTrue("PagedResult page num test failed",
            protocolPagedResult.getCurrentPageNum() == 0);

        for(int i = 0; i < protocolPagedResult.getResult().size(); i++) {
            ProtocolDTO protocolDTO = protocolPagedResult.getResult().get(i);
            Protocol testProtocol = testProtocols.get(i);
            testFieldMappings(protocolDTO, testProtocol);
        }
    }

    @Test
    public void createProtocolTest() throws Exception {

        mockSetup.createMockProtocols(1);
        mockSetup.createMockContacts(1);

        Protocol protocolToCreate = new Protocol();
        protocolToCreate.setName(mockSetup.mockProtocols.get(0).getName());
        protocolToCreate.setDescription(mockSetup.mockProtocols.get(0).getDescription());
        protocolToCreate.setPlatform(mockSetup.mockProtocols.get(0).getPlatform());

        when(protocolDao.createProtocol(any(Protocol.class)))
            .thenReturn(mockSetup.mockProtocols.get(0));


        when(contactDao.getContactByUsername(any())).thenReturn(mockSetup.mockContacts.get(0));

        ProtocolDTO protocolDTOToCreate = new ProtocolDTO();
        ModelMapper.mapEntityToDto(protocolToCreate, protocolDTOToCreate);

        //To check the protocolId is ignored.
        protocolDTOToCreate.setProtocolId(mockSetup.mockProtocols.get(0).getProtocolId()+1);

        ProtocolDTO protocolDTO = protocolService.createProtocol(protocolDTOToCreate);

        testFieldMappings(protocolDTO, mockSetup.mockProtocols.get(0));
    }

    @Test
    public void patchProtocolTest() throws Exception {

        mockSetup.createMockProtocols(1);
        mockSetup.createMockContacts(1);

        Protocol testProtocol = mockSetup.mockProtocols.get(0);

        Protocol updatedTestProtocol = new Protocol();

        ProtocolDTO protocolDTOToBeUpdated = new ProtocolDTO();
        ModelMapper.mapEntityToDto(testProtocol, protocolDTOToBeUpdated);
        protocolDTOToBeUpdated.setProtocolDescription(RandomStringUtils.random(7, true, true));
        ModelMapper.mapDtoToEntity(protocolDTOToBeUpdated, updatedTestProtocol);
        protocolDTOToBeUpdated.setProtocolId(null);

        when(contactDao.getContactByUsername(any())).thenReturn(mockSetup.mockContacts.get(0));

        when(platformDao.getPlatform(any())).thenReturn(testProtocol.getPlatform());

        when(protocolDao.getProtocolById(testProtocol.getProtocolId()))
            .thenReturn(testProtocol);

        when(protocolDao.patchProtocol(any(Protocol.class)))
            .thenReturn(updatedTestProtocol);

        ProtocolDTO updatedProtocol =
            protocolService.patchProtocol(testProtocol.getProtocolId(), protocolDTOToBeUpdated);

        testFieldMappings(updatedProtocol, updatedTestProtocol);
    }

    private void testFieldMappings(ProtocolDTO protocolDTO, Protocol testProtocol) {

        assertTrue("protocolId mapping failed",
            protocolDTO.getProtocolId() == testProtocol.getProtocolId());

        assertTrue("protocolName mapping failed",
            protocolDTO.getProtocolName().equals(testProtocol.getName()));

        assertTrue("protocolDescription mapping failed",
            protocolDTO.getProtocolDescription().equals(testProtocol.getDescription()));

        assertTrue("platformId mapping failed",
            protocolDTO.getPlatformId() == testProtocol.getPlatform().getPlatformId());

    }
}
