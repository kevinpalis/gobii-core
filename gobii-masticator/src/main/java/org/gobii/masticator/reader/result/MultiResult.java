package org.gobii.masticator.reader.result;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.Getter;
import org.gobii.Util;
import org.gobii.masticator.reader.ReaderResult;


public class MultiResult implements ReaderResult {

	private String delimiter = "\t";

	@Getter
	private List<ReaderResult> results;

	private MultiResult(List<ReaderResult> results) {
		this.results = results;
	}

	@Override
	public String value() {
		return String.join(delimiter, Util.map(ReaderResult::value, results));
	}

	@Override
	public ReaderResult map(Function<String, String> f) {
		return of(results.stream().map(r -> r.map(f)).collect(Collectors.toList()));
	}

	public static ReaderResult of(List<ReaderResult> results) {
		if (results.stream().anyMatch(r -> r instanceof End)) {
			return End.inst;
		} else if (results.stream().anyMatch(r -> r instanceof Break)) {
			return Break.inst;
		} else {
			return new MultiResult(results);
		}
	}
}
