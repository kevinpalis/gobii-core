package org.gobiiproject.gobiimodel.dto.instructions.loader;

import java.io.File;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MasticatorResult {
    private String tableName;
    private File outputFile;
    private int totalLinesWritten;    
}
