package org.gobii.masticator.reader.prototype;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.gobii.masticator.reader.Reader;
import org.gobii.masticator.reader.TableReader;

@Data
@AllArgsConstructor
public class TableReaderPrototype implements ReaderPrototype<TableReader> {

	private Map<String, ? extends ReaderPrototype> prototypes;

	@Override
	public TableReader build(File file) throws IOException {
		List<String> header = new ArrayList<>();

		List<Reader> readers = new ArrayList<>();
		for (Map.Entry<String, ? extends ReaderPrototype> kv : prototypes.entrySet()) {
			header.add(kv.getKey());
			readers.add(kv.getValue().build(file));
		}

		return new TableReader(header, readers);
	}
}
