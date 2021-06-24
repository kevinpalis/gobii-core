package org.gobiiproject.gobiiclient.brapi;

import org.apache.commons.lang.ObjectUtils;
import org.gobiiproject.gobiiapimodel.restresources.common.RestUri;
import org.gobiiproject.gobiiapimodel.types.GobiiControllerType;
import org.gobiiproject.gobiimodel.config.RestResourceId;
import org.gobiiproject.gobiibrapi.calls.studies.search.BrapiRequestStudiesSearch;
import org.gobiiproject.gobiibrapi.calls.studies.search.BrapiResponseStudiesSearch;
import org.gobiiproject.gobiibrapi.core.responsemodel.BrapiResponseEnvelopeMasterDetail;
import org.gobiiproject.gobiiclient.core.brapi.BrapiClientContextAuth;
import org.gobiiproject.gobiiclient.core.common.HttpCore;
import org.gobiiproject.gobiiclient.core.gobii.GobiiClientContext;
import org.gobiiproject.gobiiclient.core.brapi.BrapiEnvelopeRestResource;
import org.gobiiproject.gobiiclient.core.gobii.GobiiClientContextAuth;
import org.gobiiproject.gobiiclient.gobii.Helpers.GlobalPkColl;
import org.gobiiproject.gobiiclient.gobii.dbops.crud.DtoCrudRequestProjectTest;
import org.gobiiproject.gobiimodel.types.GobiiEntityNameType;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

/**
 * Created by Phil on 12/15/2016.
 */
public class BrapiTestSearchStudies {

    private static HttpCore httpCore = null;

    @BeforeClass
    public static void setUpClass() throws Exception {

        // you have to set up the GobiiClient because some native GOBII calls
        // are made to set up data
        Assert.assertTrue(GobiiClientContextAuth.authenticate());

        // but for the BRAPI calls we use the raw httpCore
        httpCore = BrapiClientContextAuth.authenticate();
        Assert.assertNotNull("Could not create http core component",
        httpCore);
    }

    @AfterClass
    public static void tearDownUpClass() throws Exception {
        Assert.assertTrue(GobiiClientContextAuth.deAuthenticate());
    }


    @SuppressWarnings("unused")
    @Test
    public void getStudies() throws Exception {

        List<Integer> projectIds = (new GlobalPkColl<DtoCrudRequestProjectTest>()).getPkVals(DtoCrudRequestProjectTest.class, GobiiEntityNameType.PROJECT, 10);

        RestUri restUriStudiesSearch = GobiiClientContext.getInstance(null, false)
                .getUriFactory(GobiiControllerType.BRAPI)
                .resourceColl(RestResourceId.BRAPI_STUDIES_SEARCH);

        BrapiRequestStudiesSearch brapiRequestStudiesSearch = new BrapiRequestStudiesSearch();
        brapiRequestStudiesSearch.setStudyType("genotype");

        BrapiEnvelopeRestResource<BrapiRequestStudiesSearch, ObjectUtils.Null, BrapiResponseStudiesSearch> brapiEnvelopeRestResource =
                new BrapiEnvelopeRestResource<BrapiRequestStudiesSearch, ObjectUtils.Null, BrapiResponseStudiesSearch>(restUriStudiesSearch,
                        BrapiRequestStudiesSearch.class,
                        ObjectUtils.Null.class,
                        BrapiResponseStudiesSearch.class,
                        BrapiTestSearchStudies.httpCore);

        BrapiResponseEnvelopeMasterDetail<BrapiResponseStudiesSearch> studiesResult = brapiEnvelopeRestResource.postToListResource(brapiRequestStudiesSearch);

        BrapiTestResponseStructure.validatateBrapiResponseStructure(studiesResult.getBrapiMetaData());

        /***
         * TODO
         * Because of the changes for getting just the projects with loaded datasets, this service will always return an empty set.
         * This will have to be changed later on, when there is actual data in the database.
         *
         *
        Assert.assertTrue(studiesResult.getResult().getData().size() > 0);

        for (Integer currentProjectId : projectIds) {

            Assert.assertTrue("A project was not retrieved " + currentProjectId.toString(),
                    studiesResult
                            .getResult()
                            .getData()
                            .stream()
                            .filter(i -> i.getStudyDbId().equals(currentProjectId.toString()))
                            .collect(Collectors.toList())
                            .size() == 1
            );
        }
        **/


    }
}
