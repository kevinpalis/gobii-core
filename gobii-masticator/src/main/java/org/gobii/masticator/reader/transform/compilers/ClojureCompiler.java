package org.gobii.masticator.reader.transform.compilers;

import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Var;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.gobii.Util;
import org.gobii.masticator.reader.transform.Namespace;
import org.gobii.masticator.reader.transform.TransformerPrototype;


public class ClojureCompiler implements Compiler {

	public ClojureCompiler() {
		try {
			String cljTransforms = null;
			//shamelessly stolen from java tutorial site
			try (InputStream inputStream = getClass().getResourceAsStream("transformations.clj");
				 BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
				cljTransforms = reader.lines()
						.collect(Collectors.joining("\n"));
			}
			compile(cljTransforms);
			//compile(Util.slurpResource("transformations.clj"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public TransformerPrototype<Object, String> compile(String script) {

		IFn fn = (IFn) RT.var("clojure.core","load-string")
						.invoke(String.format("(ns transformations (:require [clojure.string :as str])) %s", script));

		return (args) -> (val) ->
		{
			List<Object> allArgs = new ArrayList<>();
			allArgs.addAll(val);
			allArgs.addAll(args);
			return Objects.toString(RT.var("clojure.core", "apply").invoke(fn, allArgs));
		};
	}

	@Override
	@SuppressWarnings("unchecked")
	public void load(String script) {
		List<Var> exported = (List<Var>) RT.var("clojure.core", "load-string")
						.invoke(script + " (->> *ns* ns-interns vals (filter #(-> % meta :export)))");

		exported.forEach(var -> Namespace.intern(var.sym.toString(),
				(args) -> (val) -> {
					List<Object> allArgs = new ArrayList<>();
					allArgs.addAll(val);
					allArgs.addAll(args);
					return Objects.toString(RT.var("clojure.core", "apply")
								.invoke((IFn) var.deref(), allArgs));
				}));
	}
}
