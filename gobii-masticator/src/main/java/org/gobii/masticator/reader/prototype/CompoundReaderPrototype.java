package org.gobii.masticator.reader.prototype;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.gobii.masticator.reader.Reader;
import org.gobii.masticator.reader.ReaderResult;
import org.gobii.masticator.reader.result.Break;
import org.gobii.masticator.reader.result.End;
import org.gobii.masticator.reader.result.Val;

@Data
@AllArgsConstructor
public class CompoundReaderPrototype implements ReaderPrototype {

	private List<ReaderPrototype> prototypes;

	@Override
	public Reader build(File file) throws IOException {

		List<Reader> readers = new ArrayList<>(prototypes.size());
		for (ReaderPrototype prototype : prototypes) {
			readers.add(prototype.build(file));
		}

		return () -> {
			StringBuilder sb = new StringBuilder();
			for (Reader r : readers) {

				final ReaderResult result = r.read();
				if (result instanceof End || result instanceof Break) {
					return result;
				}

				sb.append(result.value());
			}

			return new Val(sb.toString());
		};
	}
}
