package org.gobii.masticator.reader.prototype;

import java.io.File;
import java.io.IOException;
import org.gobii.masticator.reader.Reader;

public interface ReaderPrototype<T extends Reader> {

	T build(File file) throws IOException;

}
