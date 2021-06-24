package org.gobii.masticator.reader.prototype;

import java.io.File;
import java.io.IOException;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.gobii.masticator.reader.CellReader;

@Data
@AllArgsConstructor
public class CellReaderPrototype implements ReaderPrototype<CellReader> {

	private int row;
	private int col;

	@Override
	public CellReader build(File file) throws IOException {
		return new CellReader(file, row, col);
	}

}
