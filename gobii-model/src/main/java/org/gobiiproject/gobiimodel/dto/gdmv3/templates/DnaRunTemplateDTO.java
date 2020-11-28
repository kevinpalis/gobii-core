package org.gobiiproject.gobiimodel.dto.gdmv3.templates;

import org.gobiiproject.gobiimodel.dto.annotations.GobiiAspectMap;
import org.gobiiproject.gobiimodel.dto.annotations.GobiiAspectMaps;
import org.gobiiproject.gobiimodel.dto.instructions.loader.v3.LinkageGroupTable;
import org.gobiiproject.gobiimodel.dto.instructions.loader.v3.MarkerLinkageGroupTable;
import org.gobiiproject.gobiimodel.dto.instructions.loader.v3.MarkerTable;
import org.gobiiproject.gobiimodel.entity.LinkageGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DnaRunTemplateDTO {

    private List<String> dnaRunName = new ArrayList<>();

    private Map<String, List<String>> dnaRunProperties = new HashMap<>();

    private List<String> dnaSampleName = new ArrayList<>();

    private List<String> dnaSampleWellRow = new ArrayList<>();

    private List<String> dnaSampleWellCol = new ArrayList<>();

    private List<String> dnaSamplePlateName = new ArrayList<>();

    private List<String> dnaSampleUuid = new ArrayList<>();

    private Map<String, List<String>> dnaSampleProperties = new HashMap<>();

    private List<String> germplasmName = new ArrayList<>();

    private List<String> germplasmExternalCode = new ArrayList<>();

    private List<String> germplasmSpeciesName = new ArrayList<>();

    private List<String> germplasmType = new ArrayList<>();

    private Map<String, List<String>> germplasmProperties = new HashMap<>();

    public List<String> getDnaRunName() {
        return dnaRunName;
    }

    public void setDnaRunName(List<String> dnaRunName) {
        this.dnaRunName = dnaRunName;
    }

    public Map<String, List<String>> getDnaRunProperties() {
        return dnaRunProperties;
    }

    public void setDnaRunProperties(Map<String, List<String>> dnaRunProperties) {
        this.dnaRunProperties = dnaRunProperties;
    }

    public List<String> getDnaSampleName() {
        return dnaSampleName;
    }

    public void setDnaSampleName(List<String> dnaSampleName) {
        this.dnaSampleName = dnaSampleName;
    }

    public List<String> getDnaSampleWellRow() {
        return dnaSampleWellRow;
    }

    public void setDnaSampleWellRow(List<String> dnaSampleWellRow) {
        this.dnaSampleWellRow = dnaSampleWellRow;
    }

    public List<String> getDnaSampleWellCol() {
        return dnaSampleWellCol;
    }

    public void setDnaSampleWellCol(List<String> dnaSampleWellCol) {
        this.dnaSampleWellCol = dnaSampleWellCol;
    }

    public List<String> getDnaSamplePlateName() {
        return dnaSamplePlateName;
    }

    public void setDnaSamplePlateName(List<String> dnaSamplePlateName) {
        this.dnaSamplePlateName = dnaSamplePlateName;
    }

    public List<String> getDnaSampleUuid() {
        return dnaSampleUuid;
    }

    public void setDnaSampleUuid(List<String> dnaSampleUuid) {
        this.dnaSampleUuid = dnaSampleUuid;
    }

    public Map<String, List<String>> getDnaSampleProperties() {
        return dnaSampleProperties;
    }

    public void setDnaSampleProperties(Map<String, List<String>> dnaSampleProperties) {
        this.dnaSampleProperties = dnaSampleProperties;
    }

    public List<String> getGermplasmName() {
        return germplasmName;
    }

    public void setGermplasmName(List<String> germplasmName) {
        this.germplasmName = germplasmName;
    }

    public List<String> getGermplasmExternalCode() {
        return germplasmExternalCode;
    }

    public void setGermplasmExternalCode(List<String> germplasmExternalCode) {
        this.germplasmExternalCode = germplasmExternalCode;
    }

    public List<String> getGermplasmSpeciesName() {
        return germplasmSpeciesName;
    }

    public void setGermplasmSpeciesName(List<String> germplasmSpeciesName) {
        this.germplasmSpeciesName = germplasmSpeciesName;
    }

    public List<String> getGermplasmType() {
        return germplasmType;
    }

    public void setGermplasmType(List<String> germplasmType) {
        this.germplasmType = germplasmType;
    }

    public Map<String, List<String>> getGermplasmProperties() {
        return germplasmProperties;
    }

    public void setGermplasmProperties(Map<String, List<String>> germplasmProperties) {
        this.germplasmProperties = germplasmProperties;
    }
}