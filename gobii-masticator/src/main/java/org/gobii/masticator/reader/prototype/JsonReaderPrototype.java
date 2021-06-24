package org.gobii.masticator.reader.prototype;

import java.io.File;
import java.io.IOException;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.gobii.masticator.reader.JsonReader;

@Data
@AllArgsConstructor
public class JsonReaderPrototype implements ReaderPrototype<JsonReader> {

	private final TableReaderPrototype reader;

	@Override
	public JsonReader build(File file) throws IOException {
		return new JsonReader(reader.build(file));
	}

}
