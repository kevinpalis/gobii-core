package org.gobiiproject.gobiimodel.dto.gdmv3.templates;

import lombok.Data;


@Data
public class GenotypeMatrixTemplateDTO {

    private boolean areLeftLabelsVariantLabels = true;
    
    // line number that indicates header line
    private Integer headerLineNumber;

    // ignored if headerLineNumber is provided.
    // String that distinctively identifies header line.
    private String headerStartsWith;

    private Integer leftLabelIdHeaderPosition;

    // name of the column which is left side label for matrix.
    private String leftLabelIdHeaderColumnName;

    private Integer topLabelIdHeaderStartPosition;

    private String topLabelIdHeaderColumnName;

    // file values separator. tab is default separator
    private String fileSeparator = "\t";

}
