package org.gobii.masticator.reader;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FileReader implements Reader {

	private File file;
	private Map<String, TableReader> tableReaders;

	public ReaderResult read(String table) throws IOException {
		return tableReaders.get(table).read();
	}

	@Override
	public ReaderResult read() throws IOException {
		throw new RuntimeException("File Reader needs target table parameter, please use read(String table) instead");
	}
}
