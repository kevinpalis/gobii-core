package org.gobiiproject.gobiiclient.brapi;


import org.apache.commons.lang.ObjectUtils;
import org.gobiiproject.gobiiapimodel.restresources.common.RestUri;
import org.gobiiproject.gobiiapimodel.types.GobiiControllerType;
import org.gobiiproject.gobiimodel.config.RestResourceId;
import org.gobiiproject.gobiibrapi.calls.germplasm.BrapiResponseGermplasmByDbId;
import org.gobiiproject.gobiibrapi.core.responsemodel.BrapiResponseDataList;
import org.gobiiproject.gobiibrapi.core.responsemodel.BrapiResponseEnvelopeMaster;
import org.gobiiproject.gobiiclient.core.brapi.BrapiClientContextAuth;
import org.gobiiproject.gobiiclient.core.common.HttpCore;
import org.gobiiproject.gobiiclient.core.gobii.GobiiClientContext;
import org.gobiiproject.gobiiclient.core.brapi.BrapiEnvelopeRestResource;
import org.gobiiproject.gobiiclient.core.gobii.GobiiClientContextAuth;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Created by Phil on 12/15/2016.
 */
public class BrapiTestGermplasm {

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


    @SuppressWarnings({"rawtypes"})
    @Test
    public void getGermplasmByDbid() throws Exception {


        RestUri restUriGermplasm = GobiiClientContext.getInstance(null, false)
                .getUriFactory(GobiiControllerType.BRAPI)
                .resourceByUriIdParam(RestResourceId.GOBII_GERMPLASM);
        restUriGermplasm.setParamValue("id", "1");

        BrapiEnvelopeRestResource<ObjectUtils.Null, BrapiResponseGermplasmByDbId, BrapiResponseDataList> brapiEnvelopeRestResource =
                new BrapiEnvelopeRestResource<>(restUriGermplasm,
                        ObjectUtils.Null.class,
                        BrapiResponseGermplasmByDbId.class,
                        BrapiResponseDataList.class,
                        httpCore);

        BrapiResponseEnvelopeMaster<BrapiResponseGermplasmByDbId> brapiResponseEnvelopeMaster = brapiEnvelopeRestResource.getFromMasterResource();

        BrapiTestResponseStructure.validatateBrapiResponseStructure(brapiResponseEnvelopeMaster.getBrapiMetaData());

        BrapiResponseGermplasmByDbId brapiRequestStudiesSearch = brapiResponseEnvelopeMaster.getResult();

        Assert.assertNotNull(brapiRequestStudiesSearch.getGermplasmDbId());
        Assert.assertNotNull(brapiRequestStudiesSearch.getGermplasmName());
        Assert.assertTrue(brapiRequestStudiesSearch.getDonors().size() > 0);

    }
}
