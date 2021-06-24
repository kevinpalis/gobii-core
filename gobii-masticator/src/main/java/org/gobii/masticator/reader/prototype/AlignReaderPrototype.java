package org.gobii.masticator.reader.prototype;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.gobii.masticator.reader.Reader;
import org.gobii.masticator.reader.AlignReader;

@Data
@AllArgsConstructor
public class AlignReaderPrototype implements ReaderPrototype<AlignReader> {

	private List<ReaderPrototype> readers;

	@Override
	public AlignReader build(File file) throws IOException {
		List<Reader> list = new ArrayList<>();
		for (ReaderPrototype<?> rp : readers) {
			Reader build = rp.build(file);
			list.add(build);
		}
		return new AlignReader(list);
	}

}
