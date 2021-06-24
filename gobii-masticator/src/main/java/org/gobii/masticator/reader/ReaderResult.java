package org.gobii.masticator.reader;

import java.util.function.Function;

public interface ReaderResult {

	String value();

	ReaderResult map(Function<String, String> f);

}
