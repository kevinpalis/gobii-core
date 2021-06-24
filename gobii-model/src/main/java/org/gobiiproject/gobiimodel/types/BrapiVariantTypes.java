package org.gobiiproject.gobiimodel.types;

public enum BrapiVariantTypes {

    MARKER("Marker"),
    SNP("Single Nucleotide Polymorphism");

    String variantType;

    BrapiVariantTypes(String variantType) {
       this.variantType = variantType;
    }

    public String toString() {
        return this.variantType;
    }

}
