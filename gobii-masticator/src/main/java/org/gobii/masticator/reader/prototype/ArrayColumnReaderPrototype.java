package org.gobii.masticator.reader.prototype;

import java.io.File;
import java.io.IOException;
import lombok.AllArgsConstructor;
import lombok.Data;

import org.gobii.masticator.reader.ArrayColumnReader;
import org.gobii.masticator.reader.Reader;

@Data
@AllArgsConstructor
public class ArrayColumnReaderPrototype implements ReaderPrototype {

	private final int row;
	private final int col;
	private final String sep;

	@Override
	public Reader build(File file) throws IOException {
		return new ArrayColumnReader(file, row, col, sep);
	}
}
