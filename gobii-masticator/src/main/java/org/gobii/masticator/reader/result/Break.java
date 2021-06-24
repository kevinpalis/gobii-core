package org.gobii.masticator.reader.result;

import java.util.function.Function;
import org.gobii.masticator.reader.ReaderResult;

public class Break implements ReaderResult {

	public static final Break inst = new Break();

	@Override
	public String value() {
		return "\n";
	}

	@Override
	public ReaderResult map(Function<String, String> f) {
		return inst;
	}

}
