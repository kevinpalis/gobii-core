package org.gobii.masticator.reader.prototype;

import java.io.File;
import java.io.IOException;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.gobii.masticator.reader.MatrixReader;

@Data
@AllArgsConstructor
public class MatrixReaderPrototype implements ReaderPrototype<MatrixReader> {

	private int row;
	private int col;
	private String datasetType;

	@Override
	public MatrixReader build(File file) throws IOException {
		return new MatrixReader(file, row, col,datasetType);
	}
}
