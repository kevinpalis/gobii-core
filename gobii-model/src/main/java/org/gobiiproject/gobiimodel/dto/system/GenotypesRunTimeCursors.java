package org.gobiiproject.gobiimodel.dto.system;

import org.gobiiproject.gobiimodel.dto.brapi.GenotypeCallsDTO;
import org.gobiiproject.gobiimodel.entity.DnaRun;
import org.gobiiproject.gobiimodel.entity.Marker;

import java.util.*;

public class GenotypesRunTimeCursors {


    public Integer markerIdCursor = 0;

    public Integer dnaRunIdCursor = 0;

    public Integer startDatasetId = 0;

    public Integer pageOffset = 0;

    public Integer nextPageOffset = 0;

    public Integer dnaRunOffset = 0;

    public Integer markerPageSize = 0;

    public Integer nextDnaRunOffset = 0;

    public Integer columnOffset = 0;

    public Set<String> markerDatasetIds;
    public Set<String> dnaRunDatasetIds;

    public Integer datasetIdCursor = 0;

    public Integer dnaRunBinCursor = 0;
    public Integer markerBinCursor = 0;

    public Map<String, List<String>> markerHdf5IndexMap = new HashMap<>();

    public Map<String, List<String>> dnarunHdf5IndexMap = new HashMap<>();

    public Map<String, List<Marker>> markersByDatasetId= new HashMap<>();

    public Map<String, List<DnaRun>> dnarunsByDatasetId = new HashMap<>();

    public Map<String, Integer> dnaRunOrderIndexMap = new HashMap<>();

    public SortedMap<Integer, Integer> dnarunHdf5OrderMap = new TreeMap<>();

}
