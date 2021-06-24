package org.gobii.masticator.aspects;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class AspectParser {

	private static final TransformAspect parseTransformAspect(JsonElement def) {
		String script;

		if (def.isJsonArray()) {
			JsonArray defArray = def.getAsJsonArray();
			TransformAspect aspect = parseTransformAspect(defArray.get(0));
			for (int i = 1 ; i < defArray.size() ; i++) {
				aspect.getArgs().add(defArray.get(i).getAsString());
			}

			return aspect;
		} else if (def.isJsonObject()) {
			JsonObject scriptInlineDef = def.getAsJsonObject();
			String language = scriptInlineDef.get("lang").getAsString();
			script = scriptInlineDef.get("script").getAsString();

			InlineTransformAspect ita = new InlineTransformAspect();
			ita.setLang(language);
			ita.setScript(script);

			return ita;
		} else {
			ReferenceTransformAspect rta = new ReferenceTransformAspect();
			rta.setFname(def.getAsString());

			return rta;
		}
	}

	private static final JsonDeserializer<ElementAspect> aspectParser = new JsonDeserializer<ElementAspect>() {
		@Override
		public ElementAspect deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			if (json.isJsonArray()) {
				JsonArray arr = json.getAsJsonArray();
				if (arr.get(0).isJsonPrimitive() && ((JsonPrimitive) arr.get(0)).isString()) {
					String aspectType = arr.get(0).getAsString();
					if ("CELL".equals(aspectType)) {
						JsonObject coordinates = arr.get(1).getAsJsonObject();
						return new CellAspect(coordinates.get("row").getAsInt(),
								coordinates.get("column").getAsInt());
					} else if ("COLUMN".equals(aspectType)) {
						JsonObject coordinates = arr.get(1).getAsJsonObject();
						return new ColumnAspect(coordinates.get("row").getAsInt(),
								                    coordinates.get("column").getAsInt());
					} else if ("ARRAYCOLUMN".equals(aspectType)) {
						JsonObject coordinates = arr.get(1).getAsJsonObject();
						String separator = ",";
						if(arr.size() > 2) {
							separator = arr.get(2).getAsString();
						} 
						return new ArrayColumnAspect(coordinates.get("row").getAsInt(),
								                    coordinates.get("column").getAsInt(),
													separator);
					} else if ("ROW".equals(aspectType)) {
						JsonObject coordinates = arr.get(1).getAsJsonObject();
						return new RowAspect(coordinates.get("row").getAsInt(),
								coordinates.get("column").getAsInt());
					} else if ("MATRIX".equals(aspectType)) {
						JsonObject coordinates = arr.get(1).getAsJsonObject();
						return new MatrixAspect(
								coordinates.get("row").getAsInt(),
								coordinates.get("column").getAsInt());
					} else if ("CONSTANT".equals(aspectType)) {
						return new ConstantAspect(arr.get(1).getAsString());
					} else if ("RANGE".equals(aspectType)) {
						return new RangeAspect(arr.get(1).getAsInt());
					} else if ("COMPOUND".equals(aspectType)) {
						List<Aspect> compoundAspects = new LinkedList<>();
						for (JsonElement j : json.getAsJsonArray()) {
							compoundAspects.add(deserialize(j, typeOfT, context));
						}

						return new CompoundAspect(compoundAspects);
					} else if ("RESOLVE".equals(aspectType)) {
						JsonObject target = arr.get(1).getAsJsonObject();
						return new ResolveAspect(target.get("table").getAsString(), target.get("column").getAsString());
					} else if ("JSON".equals(aspectType)) {
						return new JsonAspect(parse(json.getAsJsonArray().get(1), TableAspect.class));
					} else if ("TRANSFORM".equals(aspectType)) {
						JsonElement transformDef = arr.get(1);
						JsonElement aspectDef = arr.get(2);
						Aspect subaspect = parse(aspectDef, ElementAspect.class);

						TransformAspect aspect = parseTransformAspect(transformDef);
						aspect.setAspect(subaspect);

						return aspect;
					} else if ("ALIGN".equals(aspectType)) {
						List<Aspect> aspects = new ArrayList<>();
						for (int i = 1 ; i < arr.size() ; i++) {
							aspects.add(parse(arr.get(i), ElementAspect.class));
						}

						return new AlignAspect(aspects);
					}
				}
			} else {
				return new ConstantAspect(json.getAsString());
			}

			throw new RuntimeException(String.format("Error while parsing aspect %s", json));
		}
	};



	private static final JsonDeserializer<TableAspect> tableAspectParser = (json, typeOfT, context) -> {
		Map<String, ElementAspect> m = new HashMap<>();
		for (Map.Entry<String, JsonElement> kv : json.getAsJsonObject().entrySet()) {
			ElementAspect aspect = parse(kv.getValue(), ElementAspect.class);
			aspect.setName(kv.getKey());
			m.put(kv.getKey(), aspect);
		}
		return new TableAspect(null, m);
	};

	private static final JsonDeserializer<FileAspect> fileAspectParser = (json, typeOfT, context) -> {
		Map<String, TableAspect> tableAspects = new HashMap<>();
		for (Map.Entry<String, JsonElement> kv : json.getAsJsonObject().get("aspects").getAsJsonObject().entrySet()) {
			TableAspect tableAspect = parse(kv.getValue(), TableAspect.class);
			tableAspect.setTable(kv.getKey());
			tableAspects.put(kv.getKey(), tableAspect);
		}

		return new FileAspect(tableAspects);
	};

	private static Gson gson = new GsonBuilder()
			.registerTypeAdapter(ElementAspect.class, aspectParser)
			.registerTypeAdapter(TableAspect.class, tableAspectParser)
			.registerTypeAdapter(FileAspect.class, fileAspectParser)
			.create();

	public static <T extends Aspect> T parse(JsonElement json, Class<T> type)  {
		if (json == null) {
			return null;
		} else {
			return gson.fromJson(json, type);
		}
	}

	public static FileAspect parse(String json) {
		if (json == null) {
			return null;
		} else {

			return new GsonBuilder()
					.registerTypeAdapter(ElementAspect.class, aspectParser)
					.registerTypeAdapter(TableAspect.class, tableAspectParser)
					.registerTypeAdapter(FileAspect.class, fileAspectParser)
					.create()
					.fromJson(json, FileAspect.class);
		}
	}

}
