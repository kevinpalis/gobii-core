package org.gobiiproject.gobiimodel.dto.gdmv3.templates;

import lombok.Data;
import org.gobiiproject.gobiimodel.dto.annotations.GobiiAspectMap;
import org.gobiiproject.gobiimodel.dto.annotations.GobiiAspectMaps;
import org.gobiiproject.gobiimodel.dto.instructions.loader.v3.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class DnaRunTemplateDTO {

    private Integer headerLineNumber = 1;

    private String fileSeparator = "\t";

    @GobiiAspectMaps(maps = {
        @GobiiAspectMap(aspectTable = DnaRunTable.class),
    })
    private List<String> dnaRunName = new ArrayList<>();

    @GobiiAspectMaps(maps = {
        @GobiiAspectMap(aspectTable = DnaRunTable.class),
    })
    private Map<String, List<String>> dnaRunProperties = new HashMap<>();

    @GobiiAspectMaps(maps = {
        @GobiiAspectMap(aspectTable = DnaRunTable.class),
        @GobiiAspectMap(aspectTable = DnaSampleTable.class),
    })
    private List<String> dnaSampleName = new ArrayList<>();

    @GobiiAspectMaps(maps = {
        @GobiiAspectMap(aspectTable = DnaRunTable.class),
        @GobiiAspectMap(aspectTable = DnaSampleTable.class),
    })
    private List<String> dnaSampleNum = new ArrayList<>();

    @GobiiAspectMaps(maps = {
        @GobiiAspectMap(aspectTable = DnaSampleTable.class),
    })
    private List<String> dnaSampleWellRow = new ArrayList<>();

    @GobiiAspectMaps(maps = {
        @GobiiAspectMap(aspectTable = DnaSampleTable.class),
    })
    private List<String> dnaSampleWellCol = new ArrayList<>();

    @GobiiAspectMaps(maps = {
        @GobiiAspectMap(aspectTable = DnaSampleTable.class),
    })
    private List<String> dnaSamplePlateName = new ArrayList<>();

    @GobiiAspectMaps(maps = {
        @GobiiAspectMap(aspectTable = DnaSampleTable.class),
    })
    private List<String> dnaSampleUuid = new ArrayList<>();

    @GobiiAspectMaps(maps = {
        @GobiiAspectMap(aspectTable = DnaSampleTable.class),
    })
    private Map<String, List<String>> dnaSampleProperties = new HashMap<>();

    @GobiiAspectMaps(maps = {
        @GobiiAspectMap(aspectTable = GermplasmTable.class),
    })
    private List<String> germplasmName = new ArrayList<>();

    @GobiiAspectMaps(maps = {
        @GobiiAspectMap(aspectTable = GermplasmTable.class),
        @GobiiAspectMap(aspectTable = DnaSampleTable.class),
    })
    private List<String> germplasmExternalCode = new ArrayList<>();

    @GobiiAspectMaps(maps = {
        @GobiiAspectMap(aspectTable = GermplasmTable.class),
    })
    private List<String> germplasmSpeciesName = new ArrayList<>();

    @GobiiAspectMaps(maps = {
        @GobiiAspectMap(aspectTable = GermplasmTable.class),
    })
    private List<String> germplasmType = new ArrayList<>();

    @GobiiAspectMaps(maps = {
        @GobiiAspectMap(aspectTable = GermplasmTable.class),
    })
    private Map<String, List<String>> germplasmProperties = new HashMap<>();

}
