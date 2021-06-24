package org.gobiiproject.gobiimodel.cvnames;

/**
 * Created by Phil on 5/13/2016.
 * These are intended to codify cv term names. You cannot add new values unless there exists a corresponding cv term in the database
 */


public enum DatasetType {

    CV_DATASETTYPE_NUCLEOTIDE_2_LETTER("nucleotide_2_letter"),
    CV_DATASETTYPE_NUCLEOTIDE_4_LETTER("nucleotide_4_letter"),
    CV_DATASETTYPE_IUPAC("iupac"),
    CV_DATASETTYPE_DOMINANT_NON_NUCLEOTYPE("dominant_non_nucleotide"),
    CV_DATASETTYPE_CO_DOMINANT_NON_NUCLEOTIDE("co_dominant_non_nucleotide"),
    CV_DATASETTYPE_SSR_ALLELE_SIZE("ssr_allele_size");

    private String datasetTypeName;

    DatasetType(String datasetTypeName) {
        this.datasetTypeName = datasetTypeName;
    }

    public String getDatasetTypeName() {
        return this.datasetTypeName;
    }


}
