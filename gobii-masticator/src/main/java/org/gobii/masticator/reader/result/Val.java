package org.gobii.masticator.reader.result;

import java.util.function.Function;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.gobii.masticator.reader.ReaderResult;

@Data
@AllArgsConstructor
public class Val implements ReaderResult {

	private final String val;

	public static Val of(String s) {
		return new Val(s);
	}

	@Override
	public String value() {
		return val;
	}

	@Override
	public ReaderResult map(Function<String, String> f) {
		return Val.of(f.apply(val));
	}
}
