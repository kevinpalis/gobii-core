package org.gobii.masticator.reader.transform.compilers;

import java.util.HashMap;
import java.util.Map;

public class Compilers {

	private static final Map<String, Compiler> compilers = new HashMap<>();

	static {
		final Compiler clojure = new ClojureCompiler();
		compilers.put("clojure", clojure);
		compilers.put("clj", clojure);

		final Compiler sed = new SedCompiler();
		compilers.put("sed", sed);
	}

	public static Compiler resolve(String lang) {
		return compilers.get(lang);
	}
}
