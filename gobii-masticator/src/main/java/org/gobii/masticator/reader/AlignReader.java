package org.gobii.masticator.reader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.gobii.masticator.reader.result.Break;
import org.gobii.masticator.reader.result.End;
import org.gobii.masticator.reader.result.MultiResult;

@Data
@RequiredArgsConstructor
public class AlignReader implements Reader {

	@NonNull
	private List<Reader> readers;

	private Map<Reader, ReaderResult> cache = new HashMap<>();

	@Override
	public ReaderResult read() throws IOException {

		List<ReaderResult> results = new ArrayList<>();

		for (Reader reader : readers) {
			if (cache.containsKey(reader)) {
				results.add(cache.get(reader));
			} else {
				ReaderResult read = reader.read();
				if (read instanceof End) {
					return read;
				}
				else if (read instanceof Break) {
					cache.clear();
				} else if (! (reader instanceof MatrixReader)){
					cache.put(reader, read);
				}
				results.add(read);
			}
		}

		return MultiResult.of(results);
	}
}
