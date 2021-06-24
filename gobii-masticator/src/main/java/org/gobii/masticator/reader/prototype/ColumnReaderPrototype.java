package org.gobii.masticator.reader.prototype;

import java.io.File;
import java.io.IOException;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.gobii.masticator.reader.ColumnReader;
import org.gobii.masticator.reader.Reader;

@Data
@AllArgsConstructor
public class ColumnReaderPrototype implements ReaderPrototype {

	private final int row;
	private final int col;

	@Override
	public Reader build(File file) throws IOException {
		return new ColumnReader(file, row, col);
	}
}
