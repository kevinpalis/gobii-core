package org.gobii.masticator.reader;

import java.io.IOException;
import java.util.Collections;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.gobii.Util;
import org.gobii.masticator.reader.result.Break;
import org.gobii.masticator.reader.result.End;
import org.gobii.masticator.reader.result.MultiResult;
import org.gobii.masticator.reader.result.Val;
import org.gobii.masticator.reader.transform.Transformer;


@Data
@AllArgsConstructor
public class TransformReader implements Reader {

	private Transformer<Object, String> transformer;
	private Reader reader;

	@Override
	public ReaderResult read() throws IOException {

		ReaderResult result = reader.read();

		if (result instanceof End || result instanceof Break) {
			return result;
		} else if (result instanceof MultiResult) {
			return Val.of(transformer.apply(Util.map(ReaderResult::value, ((MultiResult) result).getResults())));
		} else if (result instanceof Val) {
			return Val.of(transformer.apply(Collections.singletonList(result.value())));
		} else {
			return null;
		}

	}

}
