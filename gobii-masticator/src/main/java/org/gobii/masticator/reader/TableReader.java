package org.gobii.masticator.reader;

import java.io.IOException;
import java.util.List;
import java.util.StringJoiner;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import org.gobii.masticator.AspectMapper;
import org.gobii.masticator.reader.result.Break;
import org.gobii.masticator.reader.result.End;
import org.gobii.masticator.reader.result.Val;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Accessors(chain = true)
public class TableReader implements Reader {

	private String delimiter = "\t";

	@NonNull
	private List<String> header;
	@NonNull
	private List<Reader> readers;


	public String readHeader() {
		delimiter = String.valueOf(AspectMapper.delimitter);
		return String.join(delimiter, header);
	}

	@Override
	public ReaderResult read() throws IOException {

		StringJoiner joiner = new StringJoiner(delimiter);
		for (Reader reader : readers) {
			ReaderResult read = reader.read();
			if (read instanceof End) {
				return read;
			} else if (read instanceof Break) {
				return read;
			}
			joiner.add(read.value());
		}
		return new Val(joiner.toString());
	}
}
