package org.gobii.masticator.reader.transform.compilers;

import org.gobii.masticator.reader.transform.TransformerPrototype;

public interface Compiler {

	TransformerPrototype<Object, String> compile(String script);

	void load(String script);

}
