package org.gobii.masticator.reader;

import java.io.IOException;

public interface Reader {

	ReaderResult read() throws IOException;

	default int dimension() {
		return 1;
	};
}
