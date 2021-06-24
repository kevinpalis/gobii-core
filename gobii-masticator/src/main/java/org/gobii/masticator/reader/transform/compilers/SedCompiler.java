package org.gobii.masticator.reader.transform.compilers;

import java.util.Objects;
import java.util.stream.Collectors;
import org.gobii.masticator.reader.transform.TransformerPrototype;
import org.unix4j.Unix4j;

public class SedCompiler implements Compiler {

	public SedCompiler() {

	}

	@Override
	public TransformerPrototype<Object, String> compile(String script) {

		return args -> vals ->
				Unix4j.fromString(String.join("\t",
					vals.stream()
							.map(Objects::toString)
							.collect(Collectors.toList())))
				.sed(script).toStringResult();

	}

	@Override
	public void load(String script) {

	}

}
