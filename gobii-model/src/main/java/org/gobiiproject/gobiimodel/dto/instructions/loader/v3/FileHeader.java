package org.gobiiproject.gobiimodel.dto.instructions.loader.v3;

import lombok.Data;

@Data
public class FileHeader {
   private int headerLineNumber;
   private String[] headers; 
}
