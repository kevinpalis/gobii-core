package org.gobiiproject.gobiidomain.services.gdmv3;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.PersistenceException;

import org.gobiiproject.gobiimodel.config.GobiiException;
import org.gobiiproject.gobiimodel.dto.gdmv3.ReferenceDTO;
import org.gobiiproject.gobiimodel.dto.system.PagedResult;
import org.gobiiproject.gobiimodel.entity.Contact;
import org.gobiiproject.gobiimodel.entity.Reference;
import org.gobiiproject.gobiisampletrackingdao.ContactDao;
import org.gobiiproject.gobiisampletrackingdao.ReferenceDao;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.web.WebAppConfiguration;

@WebAppConfiguration
public class ReferenceServiceImplTest {
    
    @Mock
    private ReferenceDao referenceDao;

    @Mock
    private ContactDao contactDao;

    @InjectMocks
    private ReferenceServiceImpl referenceServiceImpl;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetReferencesOk() throws Exception {
        List<Reference> mockList = new ArrayList<>();
        mockList.add(new Reference());
        when(referenceDao.getReferences(0, 1000)).thenReturn(mockList);

        PagedResult<ReferenceDTO> results = referenceServiceImpl.getReferences(0, 1000);
        assertTrue( results.getCurrentPageNum() == 0);
        assertTrue( results.getCurrentPageSize() == 1);
        assertTrue( results.getResult().size() == 1);
        
    }

    @Test
    public void testCreateReferenceOk() throws Exception {
        ReferenceDTO request = new ReferenceDTO();

        request.setReferenceName("referenceName-test");
        request.setVersion("test-version");

        Contact mockContact = new Contact();
        mockContact.setContactId(1);
        mockContact.setUsername("test-editor");

        when(contactDao.getContactByUsername("test-editor")).thenReturn(mockContact);
        when(referenceDao.createReference(any(Reference.class))).thenReturn(new Reference());

        ArgumentCaptor<Reference> arg = ArgumentCaptor.forClass(Reference.class);
        referenceServiceImpl.createReference(request, "test-editor");

        verify(referenceDao).createReference(arg.capture());

        assertTrue( arg.getValue().getReferenceName().equals("referenceName-test"));
        assertTrue( arg.getValue().getVersion().equals("test-version"));
    }

    @Test
    public void testGetReferenceOk() throws Exception {
        Reference mockRef = new Reference();
        mockRef.setReferenceName("test-ref");
        mockRef.setVersion("test-version");
        when(referenceDao.getReference(123)).thenReturn(mockRef);
        ReferenceDTO dto = referenceServiceImpl.getReference(123);

        verify(referenceDao, times(1)).getReference(123);
        assertTrue(dto.getReferenceName().equals("test-ref"));
        assertTrue(dto.getVersion().equals("test-version"));
    }

    @Test(expected = GobiiException.class)
    public void testGetReferenceNotFound() throws Exception {
        when(referenceDao.getReference(123)).thenReturn(null);
        referenceServiceImpl.getReference(123);
        verify(referenceDao, times(1)).getReference(123);
    }


    @Test
    public void testUpdateReferenceOk() throws Exception {
        Reference mockRef = new Reference();
        mockRef.setReferenceName("test-ref");
        mockRef.setVersion("test-version");

        when(referenceDao.getReference(123)).thenReturn(mockRef);

        ReferenceDTO request = new ReferenceDTO();
        request.setReferenceName("new-reference-name");
        request.setVersion("new-version");

        Contact mockContact = new Contact();
        mockContact.setContactId(1);
        mockContact.setUsername("test-editor");

        when(contactDao.getContactByUsername("test-editor")).thenReturn(mockContact);

        ArgumentCaptor<Reference> arg = ArgumentCaptor.forClass(Reference.class);
        
        when(referenceDao.updateReference(any(Reference.class))).thenReturn(new Reference());

        referenceServiceImpl.updateReference(123, request, "test-editor");

        verify(referenceDao, times(1)).updateReference(any(Reference.class));
        verify(referenceDao).updateReference(arg.capture());

        assertTrue( arg.getValue().getReferenceName().equals("new-reference-name"));
        assertTrue( arg.getValue().getVersion().equals("new-version"));

    }

    @Test
    public void testUpdateReferenceOkNoUpdate() throws Exception {
        Reference mockRef = new Reference();
        mockRef.setReferenceName("test-ref");
        mockRef.setVersion("test-version");

        when(referenceDao.getReference(123)).thenReturn(mockRef);

        ReferenceDTO request = new ReferenceDTO();

        Contact mockContact = new Contact();
        mockContact.setContactId(1);
        mockContact.setUsername("test-editor");

        when(contactDao.getContactByUsername("test-editor")).thenReturn(mockContact);

        referenceServiceImpl.updateReference(123, request, "test-editor");

        verify(referenceDao, times(0)).updateReference(any(Reference.class));
        
    }

    @Test
    public void testDeleteOk() throws Exception {
        Reference mockRef = new Reference();
        mockRef.setReferenceName("test-ref");
        mockRef.setVersion("test-version");
        mockRef.setReferenceId(123);

        when(referenceDao.getReference(123)).thenReturn(mockRef);
        referenceServiceImpl.deleteReference(123);

        verify(referenceDao, times(1)).deleteReference(any(Reference.class));
    }

    @Test(expected = GobiiException.class)
    public void testDeleteNotOk() throws Exception {
        when(referenceDao.getReference(123)).thenReturn(new Reference());
        doThrow(new PersistenceException()).when(referenceDao).deleteReference(any(Reference.class));
        
        referenceServiceImpl.deleteReference(123);
        verify(referenceDao, times(1)).deleteReference(any(Reference.class));

    }
}