package org.gobiiproject.gobiimodel.dto.gdmv3.templates;

import lombok.Data;
import org.gobiiproject.gobiimodel.dto.annotations.GobiiAspectMap;
import org.gobiiproject.gobiimodel.dto.annotations.GobiiAspectMaps;
import org.gobiiproject.gobiimodel.dto.instructions.loader.v3.LinkageGroupTable;
import org.gobiiproject.gobiimodel.dto.instructions.loader.v3.MarkerGroupTable;
import org.gobiiproject.gobiimodel.dto.instructions.loader.v3.MarkerLinkageGroupTable;
import org.gobiiproject.gobiimodel.dto.instructions.loader.v3.MarkerTable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class MarkerTemplateDTO {
    
    private Integer headerLineNumber = 1;

    private String fileSeparator = "\t";

    @GobiiAspectMaps(maps = {
        @GobiiAspectMap(aspectTable = MarkerTable.class),
        @GobiiAspectMap(aspectTable = MarkerLinkageGroupTable.class),
        @GobiiAspectMap(aspectTable = MarkerGroupTable.class)
    })
    private List<String> markerName = new ArrayList<>();

    @GobiiAspectMaps(maps = {@GobiiAspectMap(aspectTable = MarkerTable.class)})
    private  List<String> markerRef = new ArrayList<>();

    @GobiiAspectMaps(maps = {@GobiiAspectMap(aspectTable = MarkerTable.class)})
    private  List<String> markerAlt = new ArrayList<>();

    @GobiiAspectMaps(maps = {
        @GobiiAspectMap(aspectTable = MarkerTable.class),
        @GobiiAspectMap(aspectTable = MarkerLinkageGroupTable.class),
        @GobiiAspectMap(aspectTable = MarkerGroupTable.class)
    })
    private List<String> platformName = new ArrayList<>();

    @GobiiAspectMaps(maps = {@GobiiAspectMap(aspectTable = MarkerLinkageGroupTable.class)})
    private  List<String> markerStart = new ArrayList<>();

    @GobiiAspectMaps(maps = {@GobiiAspectMap(aspectTable = MarkerLinkageGroupTable.class)})
    private  List<String> markerEnd = new ArrayList<>();

    @GobiiAspectMaps(maps = {@GobiiAspectMap(aspectTable = MarkerTable.class)})
    private  List<String> markerSequence = new ArrayList<>();

    @GobiiAspectMaps(maps = {
        @GobiiAspectMap(aspectTable = LinkageGroupTable.class),
        @GobiiAspectMap(aspectTable = MarkerLinkageGroupTable.class)
    })
    private List<String> linkageGroupName = new ArrayList<>();

    @GobiiAspectMaps(maps = {@GobiiAspectMap(aspectTable = LinkageGroupTable.class)})
    private List<String> linkageGroupStart = new ArrayList<>();

    @GobiiAspectMaps(maps = {@GobiiAspectMap(aspectTable = LinkageGroupTable.class)})
    private List<String> linkageGroupStop = new ArrayList<>();

    @GobiiAspectMaps(maps = {@GobiiAspectMap(aspectTable = MarkerTable.class)})
    private Map<String, List<String>> markerProperties = new HashMap<>();

    @GobiiAspectMaps(maps = {@GobiiAspectMap(aspectTable = MarkerTable.class)})
    private List<String> markerStrandName = new ArrayList<>();

    private List<String> markerReferenceName = new ArrayList<>();

    @GobiiAspectMaps(maps = {
        @GobiiAspectMap(paramName = "mapName", aspectTable = MarkerTable.class),
        @GobiiAspectMap(paramName = "mapName", aspectTable = MarkerLinkageGroupTable.class),
    })
    private List<String> genomeMapName = new ArrayList<>();

    @GobiiAspectMaps(maps = {@GobiiAspectMap(aspectTable = MarkerGroupTable.class)})
    private List<String> markerGroupName = new ArrayList<>();

    @GobiiAspectMaps(maps = {@GobiiAspectMap(aspectTable = MarkerGroupTable.class)})
    private List<String> germplasmGroup = new ArrayList<>();

    @GobiiAspectMaps(maps = {@GobiiAspectMap(aspectTable = MarkerGroupTable.class)})
    private List<String> favorableAlleles = new ArrayList<>();
}
