package org.gobii.masticator.reader.prototype;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.gobii.masticator.reader.FileReader;
import org.gobii.masticator.reader.TableReader;

@Data
@AllArgsConstructor
public class FileReaderPrototype implements ReaderPrototype<FileReader> {

	private Map<String, TableReaderPrototype> prototypes;

	@Override
	public FileReader build(File file) throws IOException {

		Map<String, TableReader> readers = new HashMap<>();
		for (Map.Entry<String, TableReaderPrototype> kv : prototypes.entrySet()) {
			readers.put(kv.getKey(), kv.getValue().build(file));
		}

		return new FileReader(file, readers);
	}
}
