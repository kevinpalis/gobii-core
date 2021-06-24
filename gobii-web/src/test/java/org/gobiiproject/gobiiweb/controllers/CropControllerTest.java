package org.gobiiproject.gobiiweb.controllers;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.util.ArrayList;
import java.util.List;

import org.gobiiproject.gobiidomain.services.gdmv3.CropService;
import org.gobiiproject.gobiimodel.dto.gdmv3.CropsDTO;
import org.gobiiproject.gobiimodel.dto.system.PagedResult;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@WebAppConfiguration
public class CropControllerTest {

    @InjectMocks
    private CropsController cropsController;

    @Mock
    private CropService cropService;

    private MockMvc mockMvc;

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(cropsController).build();
    }

    @Test
    public void testGetCrops() throws Exception {

        List<CropsDTO> crops = new ArrayList<>();

        CropsDTO crop1 = new CropsDTO();
        crop1.setUserAuthorized(true);
        crop1.setCropType("test1");
        crops.add(crop1);

        PagedResult<CropsDTO> cropsPagedResult = PagedResult.createFrom(0, crops);

        Mockito.when(cropService.getCrops()).thenReturn(cropsPagedResult);

        mockMvc.perform(MockMvcRequestBuilders.get("/gobii/crops").contextPath("/gobii"))
            .andDo(print()).andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.metadata.pagination.currentPage").value(0))
            .andExpect(jsonPath("$.metadata.pagination.pageSize").value(1))
            .andExpect(jsonPath("$.result.data[0].cropType").value(crop1.getCropType()))
            .andExpect(jsonPath("$.result.data[0].userAuthorized").value(crop1.isUserAuthorized()));

    }
}
