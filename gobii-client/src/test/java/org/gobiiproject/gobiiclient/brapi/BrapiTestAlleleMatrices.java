package org.gobiiproject.gobiiclient.brapi;

import org.apache.commons.lang.ObjectUtils;
import org.gobiiproject.gobiiapimodel.restresources.common.RestUri;
import org.gobiiproject.gobiiapimodel.types.GobiiControllerType;
import org.gobiiproject.gobiimodel.config.RestResourceId;
import org.gobiiproject.gobiibrapi.calls.markerprofiles.allelematrices.BrapiResponseAlleleMatrices;
import org.gobiiproject.gobiibrapi.core.responsemodel.BrapiResponseEnvelopeMasterDetail;
import org.gobiiproject.gobiiclient.core.brapi.BrapiClientContextAuth;
import org.gobiiproject.gobiiclient.core.brapi.BrapiEnvelopeRestResource;
import org.gobiiproject.gobiiclient.core.common.HttpCore;
import org.gobiiproject.gobiiclient.core.gobii.GobiiClientContext;
import org.gobiiproject.gobiiclient.core.gobii.GobiiClientContextAuth;
import org.gobiiproject.gobiiclient.core.gobii.GobiiTestConfiguration;
import org.gobiiproject.gobiiclient.gobii.Helpers.GlobalPkColl;
import org.gobiiproject.gobiiclient.gobii.dbops.crud.DtoCrudRequestDataSetTest;
import org.gobiiproject.gobiimodel.config.TestExecConfig;
import org.gobiiproject.gobiimodel.types.GobiiEntityNameType;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

/**
 * Created by Phil on 12/15/2016.
 */
@SuppressWarnings("unused")
public class BrapiTestAlleleMatrices {

    private static TestExecConfig testExecConfig = null;

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

        testExecConfig = new GobiiTestConfiguration().getConfigSettings().getTestExecConfig();


    }

    @AfterClass
    public static void tearDownUpClass() throws Exception {
    }


    @Test
    public void getAlleleMatrices() throws Exception {

        List<Integer> dataSetIds = (new GlobalPkColl<DtoCrudRequestDataSetTest>()).getPkVals(DtoCrudRequestDataSetTest.class, GobiiEntityNameType.DATASET, 10);

        RestUri alleleMatrices = GobiiClientContext.getInstance(null, false)
                .getUriFactory(GobiiControllerType.BRAPI)
                .resourceColl(RestResourceId.BRAPI_ALLELE_MATRICES);


        BrapiEnvelopeRestResource<ObjectUtils.Null, ObjectUtils.Null, BrapiResponseAlleleMatrices> brapiEnvelopeRestResource =
                new BrapiEnvelopeRestResource<>(alleleMatrices,
                        ObjectUtils.Null.class,
                        ObjectUtils.Null.class,
                        BrapiResponseAlleleMatrices.class,
                        BrapiTestAlleleMatrices.httpCore);

        BrapiResponseEnvelopeMasterDetail<BrapiResponseAlleleMatrices> matricesResult = brapiEnvelopeRestResource.getFromListResource();
        BrapiTestResponseStructure.validatateBrapiResponseStructure(matricesResult.getBrapiMetaData());


        /***
         * TODO
         * Because of the changes for getting just the experiments with loaded datasets, this service will always return an empty set.
         * This will have to be changed later on, when there is actual data in the database.
         *
         *
            Assert.assertTrue("No matrices were returned",
                    matricesResult.getResult().getData().size() > 0);

            for (Integer currentDatasetId : dataSetIds) {

                Assert.assertTrue("A dataset was not retrieved " + currentDatasetId.toString(),
                        matricesResult
                                .getResult()
                                .getData()
                                .stream()
                                .filter(i -> i.getMatrixDbId().equals(currentDatasetId.toString()))
                                .collect(Collectors.toList())
                                .size() == 1
                );
            }

        ***/
    }

}
