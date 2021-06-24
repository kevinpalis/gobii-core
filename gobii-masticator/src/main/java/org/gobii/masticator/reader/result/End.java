package org.gobii.masticator.reader.result;

import java.util.function.Function;
import org.gobii.masticator.reader.ReaderResult;

public class End implements ReaderResult {

	public static End inst = new End();

	@Override
	public String value() {
		return null;
	}

	@Override
	public ReaderResult map(Function<String, String> f) {
		return inst;
	}

}
