package org.gobii.masticator.reader;

import java.io.IOException;
import java.util.Iterator;
import java.util.StringJoiner;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;
import org.gobii.masticator.reader.result.Break;
import org.gobii.masticator.reader.result.End;
import org.gobii.masticator.reader.result.Val;

@Data
@Accessors(chain = true)
@AllArgsConstructor
public class JsonReader implements Reader {

	private TableReader reader;

	@Override
	public ReaderResult read() throws IOException {
		StringJoiner joiner = new StringJoiner(",");
		Iterator<String> headerIterator = reader.header.iterator();
		Iterator<Reader> entryIterator = reader.readers.iterator();
		while(headerIterator.hasNext() && entryIterator.hasNext()) {
			ReaderResult read = entryIterator.next().read();
			String key = headerIterator.next();

			//If anyone's EoF, we're EoF
			if (read instanceof End) {
				return read;
			} else if (read instanceof Break) {
				return read;
			}

			// "key":"value","key2":"value2"
			joiner.add(String.format("\"%s\":\"%s\"", key, read.value()));
		}
		return new Val(joiner.toString());
	}
}
