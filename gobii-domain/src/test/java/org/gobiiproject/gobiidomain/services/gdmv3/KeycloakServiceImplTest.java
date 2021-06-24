package org.gobiiproject.gobiidomain.services.gdmv3;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gobiiproject.gobiidomain.services.gdmv3.KeycloakServiceImpl;
import org.gobiiproject.gobiidomain.services.gdmv3.exceptions.EntityDoesNotExistException;
import org.gobiiproject.gobiimodel.config.KeycloakConfig;
import org.gobiiproject.gobiimodel.config.Roles;
import org.gobiiproject.gobiimodel.dto.gdmv3.ContactDTO;
import org.junit.Before;
import org.junit.Test;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.GroupResource;
import org.keycloak.admin.client.resource.GroupsResource;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.web.WebAppConfiguration;


@WebAppConfiguration
public class KeycloakServiceImplTest {

    @Mock
    KeycloakConfig keycloakConfig;

    @InjectMocks
    KeycloakServiceImpl keycloakServiceImpl;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetUser() throws Exception {
        // mock the builder
        KeycloakServiceImpl implSpy = spy(keycloakServiceImpl);


        //mock realmres
        Keycloak mockKeycloak = mock(Keycloak.class);
        RealmResource mockRealm = mock(RealmResource.class);
        UsersResource mockUsers = mock(UsersResource.class);
        UserResource mockUser = mock(UserResource.class);
        UserRepresentation mockRep  = new UserRepresentation();
        mockRep.setCreatedTimestamp(12345L);


        when(keycloakConfig.getRealm()).thenReturn("test-realm");
        when(mockKeycloak.realm("test-realm")).thenReturn(mockRealm);
        when(mockRealm.users()).thenReturn(mockUsers);
        when(mockUsers.get("some-uuid-id")).thenReturn(mockUser);
        when(mockUser.toRepresentation()).thenReturn(mockRep);

        doReturn(mockKeycloak).when(implSpy).getKeycloakAdminClient();

        ContactDTO dto = implSpy.getUser("some-uuid-id");
        assertNotNull(dto);
        //mock 

    }

    @Test(expected = EntityDoesNotExistException.class)
    public void testGetUserNotFound() throws Exception {
        // mock the builder
        KeycloakServiceImpl implSpy = spy(keycloakServiceImpl);


        //mock realmres
        Keycloak mockKeycloak = mock(Keycloak.class);
        RealmResource mockRealm = mock(RealmResource.class);
        UsersResource mockUsers = mock(UsersResource.class);
        UserResource mockUser = mock(UserResource.class);
        UserRepresentation mockRep  = new UserRepresentation();
        mockRep.setCreatedTimestamp(12345L);


        when(keycloakConfig.getRealm()).thenReturn("test-realm");
        when(mockKeycloak.realm("test-realm")).thenReturn(mockRealm);
        when(mockRealm.users()).thenReturn(mockUsers);
        when(mockUsers.get("some-uuid-id")).thenReturn(mockUser);
        when(mockUser.toRepresentation()).thenReturn(null);

        doReturn(mockKeycloak).when(implSpy).getKeycloakAdminClient();

        implSpy.getUser("some-uuid-id");

    }

    @Test
    public void testGetUsersOk() throws Exception {
        // mock the builder
        KeycloakServiceImpl implSpy = setupMockKeycloak();

        List<ContactDTO> dtos = implSpy.getKeycloakUsers("dev", Roles.PI, 0, 1000);
        assertNotNull(dtos);
        assertTrue(dtos.size() == 1);
        //mock 

    }


    @Test
    public void testGetUsersOk2() throws Exception {
        // mock the builder
        KeycloakServiceImpl implSpy = setupMockKeycloak();

        List<ContactDTO> dtos = implSpy.getKeycloakUsers("dev", Roles.USER, 0, 1000);
        assertNotNull(dtos);
        assertTrue(dtos.size() == 0);
        //mock 

    }

    @Test
    public void testGetUsersOk3() throws Exception {
        // mock the builder
        KeycloakServiceImpl implSpy = setupMockKeycloak();

        List<ContactDTO> dtos = implSpy.getKeycloakUsers("dev", "foo", 0, 1000);
        assertNotNull(dtos);
        assertTrue(dtos.size() == 0);


        dtos = implSpy.getKeycloakUsers("dev", null, 0, 1000);
        assertNotNull(dtos);
        assertTrue(dtos.size() == 0);
        //mock 

        dtos = implSpy.getKeycloakUsers("foo", null, 0, 1000);
        assertNotNull(dtos);
        assertTrue(dtos.size() == 0);

        dtos = implSpy.getKeycloakUsers("bar", null, 0, 1000);
        assertNotNull(dtos);
        assertTrue(dtos.size() == 0);

    }


    private KeycloakServiceImpl  setupMockKeycloak() {
        KeycloakServiceImpl implSpy = spy(keycloakServiceImpl);
        //mock realmres
        Keycloak mockKeycloak = mock(Keycloak.class);
        RealmResource mockRealm = mock(RealmResource.class);
        GroupsResource mockGroups = mock(GroupsResource.class);
        
        List<GroupRepresentation> groups = new ArrayList<>();
        
        GroupRepresentation devPi = new GroupRepresentation();
        devPi.setId("pi-id");
        devPi.setPath("/dev/pi");

        GroupRepresentation devCu = new GroupRepresentation();
        devCu.setId("cu-id");
        devCu.setPath("/dev/curator");

        List<GroupRepresentation> subGroups = new ArrayList<>();
        subGroups.add(devPi);
        subGroups.add(devCu);
        
        GroupRepresentation dev = new GroupRepresentation();
        dev.setId("dev");
        dev.setPath("/dev");
        dev.setSubGroups(subGroups);

        groups.add(dev);

        when(mockGroups.groups(eq("dev"), any(Integer.class), any(Integer.class))).thenReturn(groups);
        when(mockGroups.groups(eq("foo"), any(Integer.class), any(Integer.class))).thenReturn(null);
        when(mockGroups.groups(eq("bar"), any(Integer.class), any(Integer.class))).thenReturn(new ArrayList<GroupRepresentation>());

        UserRepresentation mockUser1 = new UserRepresentation();
        mockUser1.setCreatedTimestamp(12345L);
        mockUser1.setId("user-1");

        List<String> orgs = new ArrayList<>();
        orgs.add("test-org");

        Map<String, List<String>> attrs = new HashMap<>();
        attrs.put("organization", orgs);
        mockUser1.setAttributes(attrs);

        List<UserRepresentation> users = new ArrayList<>();
        users.add(mockUser1);

        GroupResource piGroup = mock(GroupResource.class);
        when(mockGroups.group("pi-id")).thenReturn(piGroup);

        GroupResource devGroup = mock(GroupResource.class);
        when(mockGroups.group("dev")).thenReturn(devGroup);


        when(piGroup.members(any(Integer.class), any(Integer.class))).thenReturn(users);
        

        when(devGroup.members(any(Integer.class), any(Integer.class))).thenReturn(new ArrayList<UserRepresentation>());
        
        when(mockRealm.groups()).thenReturn(mockGroups);

        when(keycloakConfig.getRealm()).thenReturn("test-realm");
        when(mockKeycloak.realm("test-realm")).thenReturn(mockRealm);


        doReturn(mockKeycloak).when(implSpy).getKeycloakAdminClient();
        return implSpy;
    }

}