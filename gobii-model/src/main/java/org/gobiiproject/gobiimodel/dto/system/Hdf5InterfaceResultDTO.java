package org.gobiiproject.gobiimodel.dto.system;

public class Hdf5InterfaceResultDTO {

    private String outputFolder;

    private String genotypeFile;

    private String errorFile;

    public String getOutputFolder() {
        return outputFolder;
    }

    public void setOutputFolder(String outputFolder) {
        this.outputFolder = outputFolder;
    }

    public String getGenotypeFile() {
        return genotypeFile;
    }

    public void setGenotypeFile(String genotypeFile) {
        this.genotypeFile = genotypeFile;
    }

    public String getErrorFile() {
        return errorFile;
    }

    public void setErrorFile(String errorFile) {
        this.errorFile = errorFile;
    }
}
