package org.gobiiproject.gobiiclient.gobii.dbops.readonly;

import org.gobiiproject.gobiiapimodel.payload.PayloadEnvelope;
import org.gobiiproject.gobiiapimodel.restresources.common.RestUri;
import org.gobiiproject.gobiimodel.config.RestResourceId;
import org.gobiiproject.gobiiclient.core.gobii.GobiiClientContext;
import org.gobiiproject.gobiiclient.core.gobii.GobiiClientContextAuth;
import org.gobiiproject.gobiiclient.core.gobii.GobiiEnvelopeRestResource;
import org.gobiiproject.gobiiclient.gobii.Helpers.GlobalPkColl;
import org.gobiiproject.gobiiclient.gobii.Helpers.TestUtils;
import org.gobiiproject.gobiiclient.gobii.dbops.crud.DtoCrudRequestContactTest;
import org.gobiiproject.gobiiclient.gobii.dbops.crud.DtoCrudRequestExperimentTest;
import org.gobiiproject.gobiiclient.gobii.dbops.crud.DtoCrudRequestMapsetTest;
import org.gobiiproject.gobiiclient.gobii.dbops.crud.DtoCrudRequestMarkerTest;
import org.gobiiproject.gobiiclient.gobii.dbops.crud.DtoCrudRequestOrganizationTest;
import org.gobiiproject.gobiiclient.gobii.dbops.crud.DtoCrudRequestProjectTest;
import org.gobiiproject.gobiiclient.gobii.dbops.crud.DtoCrudRequestProtocolTest;
import org.gobiiproject.gobiiclient.gobii.dbops.crud.DtoCrudRequestReferenceTest;
import org.gobiiproject.gobiimodel.dto.auditable.ExperimentDTO;
import org.gobiiproject.gobiimodel.dto.auditable.OrganizationDTO;
import org.gobiiproject.gobiimodel.dto.system.EntityStatsDTO;
import org.gobiiproject.gobiimodel.types.GobiiEntityNameType;
import org.gobiiproject.gobiimodel.types.GobiiProcessType;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 *
 */
public class DtoRequestEntityStatsTest {

    List<GobiiEntityNameType> auditedEntityNames = Arrays.asList(
            GobiiEntityNameType.ANALYSIS,
            GobiiEntityNameType.CONTACT,
            GobiiEntityNameType.DATASET,
            GobiiEntityNameType.DISPLAY,
            GobiiEntityNameType.EXPERIMENT,
            GobiiEntityNameType.MANIFEST,
            GobiiEntityNameType.MAPSET,
            GobiiEntityNameType.MARKER_GROUP,
            GobiiEntityNameType.ORGANIZATION,
            GobiiEntityNameType.PLATFORM,
            GobiiEntityNameType.PROJECT,
            GobiiEntityNameType.PROTOCOL,
            GobiiEntityNameType.REFERENCE

    );


    List<GobiiEntityNameType> nonAuditedEntityNames = Arrays.asList(
            GobiiEntityNameType.CV,
            GobiiEntityNameType.CVGROUP,
            GobiiEntityNameType.MARKER,
            GobiiEntityNameType.DISPLAY,
            GobiiEntityNameType.EXPERIMENT,
            GobiiEntityNameType.MANIFEST,
            GobiiEntityNameType.MAPSET,
            GobiiEntityNameType.MARKER_GROUP,
            GobiiEntityNameType.ORGANIZATION,
            GobiiEntityNameType.PLATFORM,
            GobiiEntityNameType.PROJECT,
            GobiiEntityNameType.PROTOCOL,
            GobiiEntityNameType.REFERENCE

    );


    @BeforeClass
    public static void setUpClass() throws Exception {

        Assert.assertTrue(GobiiClientContextAuth.authenticate());

    }

    @AfterClass
    public static void tearDownUpClass() throws Exception {
        Assert.assertTrue(GobiiClientContextAuth.deAuthenticate());
    }

    @Test
    @SuppressWarnings("unused")
    public void testGetLastModified() throws Exception {


        //make a bunch of organiztion records
        List<Integer> organizationPkVals = (new GlobalPkColl<DtoCrudRequestOrganizationTest>()
                .getFreshPkVals(DtoCrudRequestOrganizationTest.class, GobiiEntityNameType.ORGANIZATION, 10));


        RestUri entityLastModifiedUri = GobiiClientContext.getInstance(null, false)
                .getUriFactory()
                .entityLastModified(GobiiEntityNameType.ORGANIZATION);
        GobiiEnvelopeRestResource<EntityStatsDTO,EntityStatsDTO> gobiiEnvelopeRestResource = new GobiiEnvelopeRestResource<>(entityLastModifiedUri);

        PayloadEnvelope<EntityStatsDTO> resultEnvelope = gobiiEnvelopeRestResource
                .get(EntityStatsDTO.class);

        Assert.assertFalse(TestUtils.checkAndPrintHeaderMessages(resultEnvelope.getHeader()));

        Assert.assertTrue("No entity stats retrieved",
                (resultEnvelope.getPayload().getData().size() > 0));

        EntityStatsDTO entityStatsDTOBeforeAddMore = resultEnvelope.getPayload().getData().get(0);
        Assert.assertEquals("The EntityStateDateType for auditable entities should be "
                        + EntityStatsDTO.EntityStateDateType.INSERT_UPDATE.toString(),
                entityStatsDTOBeforeAddMore.getEntityStateDateType(),
                EntityStatsDTO.EntityStateDateType.INSERT_UPDATE);


        Date beforeAddTime = entityStatsDTOBeforeAddMore.getLastModified();
        Thread.sleep(500);

        // verify that the time is the same without having done an update
        resultEnvelope = gobiiEnvelopeRestResource
                .get(EntityStatsDTO.class);
        entityStatsDTOBeforeAddMore = resultEnvelope.getPayload().getData().get(0);

        Assert.assertEquals("The date time stamp should not have changed without doing an update",
                entityStatsDTOBeforeAddMore.getLastModified(), beforeAddTime);

        //verify that the time is the same when _another_ entity is modified
        List<Integer> contactPkVals = (new GlobalPkColl<DtoCrudRequestContactTest>()
                .getFreshPkVals(DtoCrudRequestContactTest.class, GobiiEntityNameType.CONTACT, 10));
        resultEnvelope = gobiiEnvelopeRestResource
                .get(EntityStatsDTO.class);
        entityStatsDTOBeforeAddMore = resultEnvelope.getPayload().getData().get(0);

        Assert.assertEquals("The date time stamp should not have changed when another entity was created",
                entityStatsDTOBeforeAddMore.getLastModified(), beforeAddTime);

        //now do an update
        Thread.sleep(500);
        Integer arbitrarOrganizationId = organizationPkVals.get(0);
        RestUri restUriOrganizationForGetById = GobiiClientContext.getInstance(null, false)
                .getUriFactory()
                .resourceByUriIdParam(RestResourceId.GOBII_ORGANIZATION);
        restUriOrganizationForGetById.setParamValue("id", arbitrarOrganizationId.toString());
        GobiiEnvelopeRestResource<OrganizationDTO,OrganizationDTO> gobiiEnvelopeRestResourceForGetById = new GobiiEnvelopeRestResource<>(restUriOrganizationForGetById);
        PayloadEnvelope<OrganizationDTO> resultEnvelopeForGetByID = gobiiEnvelopeRestResourceForGetById
                .get(OrganizationDTO.class);

        String newName = UUID.randomUUID().toString();
        OrganizationDTO organizationDTO = resultEnvelopeForGetByID.getPayload().getData().get(0);
        organizationDTO.setName(newName);
        PayloadEnvelope<OrganizationDTO> organizationDTOResponseEnvelopeUpdate = gobiiEnvelopeRestResourceForGetById.put(OrganizationDTO.class,
                new PayloadEnvelope<>(organizationDTO, GobiiProcessType.UPDATE));
        Assert.assertFalse(TestUtils.checkAndPrintHeaderMessages(organizationDTOResponseEnvelopeUpdate.getHeader()));

        resultEnvelopeForGetByID = gobiiEnvelopeRestResourceForGetById
                .get(OrganizationDTO.class);
        Assert.assertFalse(TestUtils.checkAndPrintHeaderMessages(resultEnvelopeForGetByID.getHeader()));
        OrganizationDTO organizationDTOReRetrieved = resultEnvelopeForGetByID.getPayload().getData().get(0);
        Assert.assertEquals(organizationDTOReRetrieved.getName(), newName);

        PayloadEnvelope<EntityStatsDTO> resultEnvelopePostUpdate = gobiiEnvelopeRestResource
                .get(EntityStatsDTO.class);

        Assert.assertFalse(TestUtils.checkAndPrintHeaderMessages(resultEnvelopePostUpdate.getHeader()));
        EntityStatsDTO entityStatsDTOPosteUpdate = resultEnvelopePostUpdate.getPayload().getData().get(0);
        Assert.assertEquals("The EntityStateDateType for auditable entities should be "
                        + EntityStatsDTO.EntityStateDateType.INSERT_UPDATE.toString(),
                entityStatsDTOPosteUpdate.getEntityStateDateType(),
                EntityStatsDTO.EntityStateDateType.INSERT_UPDATE);

        Assert.assertNotNull("The last modified date is null",
                entityStatsDTOPosteUpdate.getLastModified());

        Assert.assertTrue("The new datestamp is not later than from before the update",
                entityStatsDTOPosteUpdate.getLastModified().compareTo(beforeAddTime) > 0);

    }

    @Test
    public void testGetCount() throws Exception {

        RestUri entityCountdUri = GobiiClientContext.getInstance(null, false)
                .getUriFactory()
                .entityCount(GobiiEntityNameType.PROJECT);
        GobiiEnvelopeRestResource<EntityStatsDTO,EntityStatsDTO> gobiiEnvelopeRestResource =
                new GobiiEnvelopeRestResource<>(entityCountdUri);

        PayloadEnvelope<EntityStatsDTO> resultEnvelope = gobiiEnvelopeRestResource
                .get(EntityStatsDTO.class);

        Assert.assertFalse(TestUtils.checkAndPrintHeaderMessages(resultEnvelope.getHeader()));

        EntityStatsDTO entityStatsDTOCount = resultEnvelope.getPayload().getData().get(0);
        Assert.assertNotNull("No entity count value was retrieved",
                entityStatsDTOCount.getCount());
        Assert.assertEquals("The EntityStateDateType for auditable entities should be "

                        + EntityStatsDTO.EntityStateDateType.INSERT_UPDATE.toString(),
                entityStatsDTOCount.getEntityStateDateType(),
                EntityStatsDTO.EntityStateDateType.INSERT_UPDATE);

        Integer projectCountBeforeInserts = entityStatsDTOCount.getCount();
        Integer numberOfProjectsToAdd = 5;

        // now create more projects
        (new GlobalPkColl<DtoCrudRequestProjectTest>())
                .getFreshPkVals(DtoCrudRequestProjectTest.class, GobiiEntityNameType.PROJECT, numberOfProjectsToAdd);

        PayloadEnvelope<EntityStatsDTO> resultEnvelopePostAdd = gobiiEnvelopeRestResource
                .get(EntityStatsDTO.class);
        EntityStatsDTO entityStatsDTOPostAdd = resultEnvelopePostAdd.getPayload().getData().get(0);
        Assert.assertEquals("The EntityStateDateType for auditable entities should be "
                        + EntityStatsDTO.EntityStateDateType.INSERT_UPDATE.toString(),
                entityStatsDTOPostAdd.getEntityStateDateType(),
                EntityStatsDTO.EntityStateDateType.INSERT_UPDATE);

        Assert.assertTrue("The count retrieved was not increased by the number of records added",
                entityStatsDTOPostAdd.getCount() == (projectCountBeforeInserts + numberOfProjectsToAdd));

        PayloadEnvelope<EntityStatsDTO> resultEnvelopePostAddRecheck = gobiiEnvelopeRestResource
                .get(EntityStatsDTO.class);
        EntityStatsDTO entityStatsDTOPostAddRecheck = resultEnvelopePostAddRecheck.getPayload().getData().get(0);
        Assert.assertEquals("The EntityStateDateType for auditable entities should be "
                        + EntityStatsDTO.EntityStateDateType.INSERT_UPDATE.toString(),
                entityStatsDTOPostAddRecheck.getEntityStateDateType(),
                EntityStatsDTO.EntityStateDateType.INSERT_UPDATE);

        Assert.assertTrue("The count did not remain the same when no records were added",
                entityStatsDTOPostAddRecheck.getCount() == (projectCountBeforeInserts + numberOfProjectsToAdd));

    }


    @Test
    public void testGetCountOfChildren() throws Exception {

        Integer projectId = (new GlobalPkColl<DtoCrudRequestProjectTest>())
                .getFreshPkVals(DtoCrudRequestProjectTest.class, GobiiEntityNameType.PROJECT, 1)
                .get(0);

        RestUri entityCountdUri = GobiiClientContext.getInstance(null, false)
                .getUriFactory()
                .entityChildCount(GobiiEntityNameType.PROJECT, GobiiEntityNameType.EXPERIMENT, projectId);

        GobiiEnvelopeRestResource<EntityStatsDTO,EntityStatsDTO> gobiiEnvelopeRestResource =
                new GobiiEnvelopeRestResource<>(entityCountdUri);
        PayloadEnvelope<EntityStatsDTO> resultEnvelopeIntialCount = gobiiEnvelopeRestResource
                .get(EntityStatsDTO.class);


        Assert.assertFalse(TestUtils.checkAndPrintHeaderMessages(resultEnvelopeIntialCount.getHeader()));

        EntityStatsDTO entityStatsDTOInitialCount = resultEnvelopeIntialCount.getPayload().getData().get(0);
        Assert.assertNotNull("Null count retrieved",
                entityStatsDTOInitialCount.getCount()
        );
        Assert.assertEquals("The EntityStateDateType for auditable entities should be "
                        + EntityStatsDTO.EntityStateDateType.INSERT_UPDATE.toString(),
                entityStatsDTOInitialCount.getEntityStateDateType(),
                EntityStatsDTO.EntityStateDateType.INSERT_UPDATE);

        Integer countBeforeAddChildren = entityStatsDTOInitialCount.getCount();

        Integer numberoOfChildrenToAdd = 5;

        List<Integer> experimentPks = (new GlobalPkColl<DtoCrudRequestExperimentTest>())
                .getFreshPkVals(DtoCrudRequestExperimentTest.class, GobiiEntityNameType.EXPERIMENT, numberoOfChildrenToAdd);

        for (Integer currentExperimentId : experimentPks) {
            RestUri experimentsUriById = GobiiClientContext.getInstance(null, false)
                    .getUriFactory()
                    .resourceByUriIdParam(RestResourceId.GOBII_EXPERIMENTS);

            experimentsUriById.setParamValue("id", currentExperimentId.toString());
            GobiiEnvelopeRestResource<ExperimentDTO,ExperimentDTO> gobiiEnvelopeRestResourceForExperimentsById = new GobiiEnvelopeRestResource<>(experimentsUriById);
            PayloadEnvelope<ExperimentDTO> resultEnvelopeExperimentGet = gobiiEnvelopeRestResourceForExperimentsById
                    .get(ExperimentDTO.class);

            ExperimentDTO currentExperimentDto = resultEnvelopeExperimentGet.getPayload().getData().get(0);
            currentExperimentDto.setProjectId(projectId);

            PayloadEnvelope<ExperimentDTO> putRequestEnvelope = new PayloadEnvelope<>(currentExperimentDto, GobiiProcessType.UPDATE);

            gobiiEnvelopeRestResourceForExperimentsById.put(ExperimentDTO.class, putRequestEnvelope);
        }

        PayloadEnvelope<EntityStatsDTO> postAddChildrenCountResult = gobiiEnvelopeRestResource
                .get(EntityStatsDTO.class);

        EntityStatsDTO entityStatsDTOPostAddChildren = postAddChildrenCountResult.getPayload().getData().get(0);
        Assert.assertEquals("The EntityStateDateType for auditable entities should be "
                        + EntityStatsDTO.EntityStateDateType.INSERT_UPDATE.toString(),
                entityStatsDTOPostAddChildren.getEntityStateDateType(),
                EntityStatsDTO.EntityStateDateType.INSERT_UPDATE);

        Assert.assertTrue("Child count after adding new children was not incremented",
                entityStatsDTOPostAddChildren.getCount().equals(countBeforeAddChildren + numberoOfChildrenToAdd));

        PayloadEnvelope<EntityStatsDTO> noAddAddChildrenCountResult = gobiiEnvelopeRestResource
                .get(EntityStatsDTO.class);

        EntityStatsDTO entityStatsDTONoAddChildren = noAddAddChildrenCountResult.getPayload().getData().get(0);
        Assert.assertTrue("Child count without adding new children should have remained the same",
                entityStatsDTONoAddChildren.getCount().equals(countBeforeAddChildren + numberoOfChildrenToAdd));


    }

    @Test
    public void testGetAll() throws Exception {

        RestUri allEntityStatsUri = GobiiClientContext.getInstance(null, false)
                .getUriFactory()
                .resourceColl(RestResourceId.GOBII_ENTITIES);

        GobiiEnvelopeRestResource<EntityStatsDTO,EntityStatsDTO> gobiiEnvelopeRestResource =
                new GobiiEnvelopeRestResource<>(allEntityStatsUri);
        PayloadEnvelope<EntityStatsDTO> resultEnvelopeIntialCount = gobiiEnvelopeRestResource
                .get(EntityStatsDTO.class);


        Assert.assertFalse(TestUtils.checkAndPrintHeaderMessages(resultEnvelopeIntialCount.getHeader()));

        List<EntityStatsDTO> allEnttiesStatsInitial = resultEnvelopeIntialCount.getPayload().getData();

        Assert.assertTrue("Not all entity states are of EntityStateDateType"
                        + EntityStatsDTO.EntityStateDateType.INSERT_UPDATE.toString(),
                allEnttiesStatsInitial
                        .stream()
                        .filter(es -> es.getEntityStateDateType().equals(EntityStatsDTO.EntityStateDateType.INSERT_UPDATE))
                        .count() == allEnttiesStatsInitial.size());


        for (GobiiEntityNameType currentEntityName : auditedEntityNames) {

            if (currentEntityName != GobiiEntityNameType.UNKNOWN) {

                Assert.assertTrue("There is not one of the entity type in the collection returned",
                        allEnttiesStatsInitial
                                .stream()
                                .filter(es -> es.getEntityNameType().equals(currentEntityName))
                                .count() == 1);
            }

        }


        //Now "manually" update a few known entities to flip their modified time and count
        Integer setCountVal = 3;
        List<GobiiEntityNameType> modifiedTypeNames = Arrays.asList(GobiiEntityNameType.PROJECT,
                GobiiEntityNameType.MAPSET,
                GobiiEntityNameType.CONTACT,
                GobiiEntityNameType.REFERENCE,
//                GobiiEntityNameType.DATASET,
                GobiiEntityNameType.PROTOCOL);

        // These need to be excluded because they are creatd as side effects of the crud tests we are using explicitly.
        // Their counts are indeterminate and so we can't just add them to the items we test for
        // ANALYSIS, PLATFORM, and EXPERIMENT records are created by the DATASET crud test,
        // and ORGANIZATION  is created by CONTACT crud
        // The PROTOCOL test is creating a MANIFEST
        List<GobiiEntityNameType> sideEffectRecordsToExclude = Arrays.asList(GobiiEntityNameType.ANALYSIS,
                GobiiEntityNameType.PLATFORM,
                GobiiEntityNameType.EXPERIMENT,
                GobiiEntityNameType.ORGANIZATION,
                GobiiEntityNameType.MANIFEST);

        (new GlobalPkColl<DtoCrudRequestProjectTest>()).getFreshPkVals(DtoCrudRequestProjectTest.class, GobiiEntityNameType.PROJECT, setCountVal);
        (new GlobalPkColl<DtoCrudRequestMapsetTest>()).getFreshPkVals(DtoCrudRequestMapsetTest.class, GobiiEntityNameType.MAPSET, setCountVal);
        (new GlobalPkColl<DtoCrudRequestContactTest>()).getFreshPkVals(DtoCrudRequestContactTest.class, GobiiEntityNameType.CONTACT, setCountVal);
        (new GlobalPkColl<DtoCrudRequestReferenceTest>()).getFreshPkVals(DtoCrudRequestReferenceTest.class, GobiiEntityNameType.REFERENCE, setCountVal);
//        (new GlobalPkColl<DtoCrudRequestDataSetTest>()).getFreshPkVals(DtoCrudRequestDataSetTest.class, GobiiEntityNameType.DATASET, setCountVal);
        (new GlobalPkColl<DtoCrudRequestProtocolTest>()).getFreshPkVals(DtoCrudRequestProtocolTest.class, GobiiEntityNameType.PROTOCOL, setCountVal);


        PayloadEnvelope<EntityStatsDTO> resultEnvelopePostUpdate = gobiiEnvelopeRestResource
                .get(EntityStatsDTO.class);
        List<EntityStatsDTO> allEnttiesStatsPost = resultEnvelopePostUpdate.getPayload().getData();

        Assert.assertTrue("Not all entity states are of EntityStateDateType"
                        + EntityStatsDTO.EntityStateDateType.INSERT_UPDATE.toString(),
                allEnttiesStatsPost
                        .stream()
                        .filter(es -> es.getEntityStateDateType().equals(EntityStatsDTO.EntityStateDateType.INSERT_UPDATE))
                        .count() == allEnttiesStatsInitial.size());



        for (EntityStatsDTO currentEntityStatsDto : allEnttiesStatsPost) {
            EntityStatsDTO preUpdateEntityStatsDTO = allEnttiesStatsInitial
                    .stream()
                    .filter(es -> es.getEntityNameType().equals(currentEntityStatsDto.getEntityNameType()))
                    .collect(Collectors.toList())
                    .get(0);

            if (modifiedTypeNames.contains(currentEntityStatsDto.getEntityNameType())) {

                // count can be greater than what we added because additional records are added as side effects of other crud tests
                Assert.assertTrue("entity stat count was not updated for entity: " + currentEntityStatsDto.getEntityNameType().toString(),
                        currentEntityStatsDto.getCount() >= (preUpdateEntityStatsDTO.getCount() + setCountVal));

                Assert.assertTrue("entity stat lastmodified was not updated for entity: " + currentEntityStatsDto.getEntityNameType().toString(),
                        currentEntityStatsDto.getLastModified().compareTo(preUpdateEntityStatsDTO.getLastModified()) > 0);
            } else {
                if (!sideEffectRecordsToExclude.contains(currentEntityStatsDto.getEntityNameType())) {
                    Assert.assertTrue("entity stat count for unchanged entity should not have changed: " + currentEntityStatsDto.getEntityNameType().toString(),
                            currentEntityStatsDto.getCount().equals(preUpdateEntityStatsDTO.getCount()));

                    Assert.assertTrue("entity stat lastmodified for unchanged entity should not have changed: " + currentEntityStatsDto.getEntityNameType().toString(),
                            currentEntityStatsDto.getLastModified().equals(preUpdateEntityStatsDTO.getLastModified()));
                }
            }

        }


    }

    @Test
    public void testGetCountOfNonAuditable() throws Exception {

        RestUri entityCountdUri = GobiiClientContext.getInstance(null, false)
                .getUriFactory()
                .entityCount(GobiiEntityNameType.MARKER);
        GobiiEnvelopeRestResource<EntityStatsDTO,EntityStatsDTO> gobiiEnvelopeRestResource =
                new GobiiEnvelopeRestResource<>(entityCountdUri);

        PayloadEnvelope<EntityStatsDTO> resultEnvelope = gobiiEnvelopeRestResource
                .get(EntityStatsDTO.class);

        Assert.assertFalse(TestUtils.checkAndPrintHeaderMessages(resultEnvelope.getHeader()));

        EntityStatsDTO entityStatsDTOCount = resultEnvelope.getPayload().getData().get(0);
        Assert.assertEquals("The EntityStateDateType for non-auditble entities should be "
                        + EntityStatsDTO.EntityStateDateType.INSERT_ONLY.toString(),
                entityStatsDTOCount.getEntityStateDateType(),
                EntityStatsDTO.EntityStateDateType.INSERT_ONLY);


        Assert.assertNotNull("No entity count value was retrieved",
                entityStatsDTOCount.getCount());

        Integer markerCountBeforeInserts = entityStatsDTOCount.getCount();
        Date updateDateBeforeInserts = entityStatsDTOCount.getLastModified();
        Integer numberOfMarkers = 5;

        // now create more projects
        (new GlobalPkColl<DtoCrudRequestMarkerTest>())
                .getFreshPkVals(DtoCrudRequestMarkerTest.class, GobiiEntityNameType.MARKER, numberOfMarkers);

        PayloadEnvelope<EntityStatsDTO> resultEnvelopePostAdd = gobiiEnvelopeRestResource
                .get(EntityStatsDTO.class);
        EntityStatsDTO entityStatsDTOPostAdd = resultEnvelopePostAdd.getPayload().getData().get(0);
        Assert.assertEquals("The EntityStateDateType for non-auditble entities should be "
                        + EntityStatsDTO.EntityStateDateType.INSERT_ONLY.toString(),
                entityStatsDTOPostAdd.getEntityStateDateType(),
                EntityStatsDTO.EntityStateDateType.INSERT_ONLY);

        Assert.assertTrue("The count retrieved was not increased by the number of records added",
                entityStatsDTOPostAdd.getCount() == (markerCountBeforeInserts + numberOfMarkers));

        Assert.assertTrue("The new datestamp is not later than from before the update",
                entityStatsDTOPostAdd.getLastModified().compareTo(updateDateBeforeInserts) > 0);

        PayloadEnvelope<EntityStatsDTO> resultEnvelopePostAddRecheck = gobiiEnvelopeRestResource
                .get(EntityStatsDTO.class);
        EntityStatsDTO entityStatsDTOPostAddRecheck = resultEnvelopePostAddRecheck.getPayload().getData().get(0);
        Assert.assertEquals("The EntityStateDateType for non-auditble entities should be "
                        + EntityStatsDTO.EntityStateDateType.INSERT_ONLY.toString(),
                entityStatsDTOPostAddRecheck.getEntityStateDateType(),
                EntityStatsDTO.EntityStateDateType.INSERT_ONLY);

        Assert.assertTrue("The count did not remain the same when no records were added",
                entityStatsDTOPostAddRecheck.getCount() == (markerCountBeforeInserts + numberOfMarkers));

        Assert.assertTrue("The new datestamp is not the same as when no records were added",
                entityStatsDTOPostAdd.getLastModified().compareTo(entityStatsDTOPostAddRecheck.getLastModified()) == 0);
    }

}
