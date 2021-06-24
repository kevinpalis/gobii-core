package org.gobiiproject.gobiiclient.gobii;

import static org.junit.Assert.assertTrue;
import java.util.List;

import org.gobiiproject.gobiiclient.core.gobii.GobiiClientContext;
import org.junit.Test;

public class GobiiClientContextTest {
    
    

    @Test
    public void testGetInstance() throws Exception {
        GobiiClientContext gobiiClientContext = 
            GobiiClientContext.getInstance(
                "http://localhost:8282/gdm/crops/dev/", true);
        //GobiiClientContext context = 
        //    GobiiClientContext
        //        .getInstance("https://gdm-extractor.irri.org/gobii-rice/", true); 
        List<String> crops = gobiiClientContext.getCropTypeTypes();
    }
}
