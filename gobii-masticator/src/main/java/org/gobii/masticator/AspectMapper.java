package org.gobii.masticator;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.gobii.Util;
import org.gobii.masticator.aspects.AlignAspect;
import org.gobii.masticator.aspects.ArrayColumnAspect;
import org.gobii.masticator.aspects.Aspect;
import org.gobii.masticator.aspects.CellAspect;
import org.gobii.masticator.aspects.ColumnAspect;
import org.gobii.masticator.aspects.CompoundAspect;
import org.gobii.masticator.aspects.ConstantAspect;
import org.gobii.masticator.aspects.FileAspect;
import org.gobii.masticator.aspects.InlineTransformAspect;
import org.gobii.masticator.aspects.JsonAspect;
import org.gobii.masticator.aspects.MatrixAspect;
import org.gobii.masticator.aspects.RangeAspect;
import org.gobii.masticator.aspects.ReferenceTransformAspect;
import org.gobii.masticator.aspects.RowAspect;
import org.gobii.masticator.aspects.TableAspect;
import org.gobii.masticator.reader.prototype.AlignReaderPrototype;
import org.gobii.masticator.reader.prototype.ArrayColumnReaderPrototype;
import org.gobii.masticator.reader.prototype.CellReaderPrototype;
import org.gobii.masticator.reader.prototype.ColumnReaderPrototype;
import org.gobii.masticator.reader.prototype.CompoundReaderPrototype;
import org.gobii.masticator.reader.prototype.ConstantReaderPrototype;
import org.gobii.masticator.reader.prototype.FileReaderPrototype;
import org.gobii.masticator.reader.prototype.InlineTransformReaderPrototype;
import org.gobii.masticator.reader.prototype.JsonReaderPrototype;
import org.gobii.masticator.reader.prototype.MatrixReaderPrototype;
import org.gobii.masticator.reader.prototype.RangeReaderPrototype;
import org.gobii.masticator.reader.prototype.ReaderPrototype;
import org.gobii.masticator.reader.prototype.ReferenceTransformReaderPrototype;
import org.gobii.masticator.reader.prototype.RowReaderPrototype;
import org.gobii.masticator.reader.prototype.TableReaderPrototype;


public class AspectMapper {

	public static char delimitter;

	private static Map<Class<? extends Aspect>, Function<? extends Aspect, ReaderPrototype>> mappers = new HashMap<>();
	static {
		mappers.put(CellAspect.class,
				(CellAspect aspect) -> new CellReaderPrototype(aspect.getRow(), aspect.getCol()));

		mappers.put(ColumnAspect.class,
				(ColumnAspect aspect) -> new ColumnReaderPrototype(aspect.getRow(), aspect.getCol()));
		
		mappers.put(ArrayColumnAspect.class,
				(ArrayColumnAspect aspect) -> 
					new ArrayColumnReaderPrototype(aspect.getRow(), 
												   aspect.getCol(), 
												   aspect.getSeparator()));

		mappers.put(RowAspect.class,
				(RowAspect aspect) -> new RowReaderPrototype(aspect.getRow(), aspect.getCol()));

		mappers.put(MatrixAspect.class,
				(MatrixAspect aspect) -> new MatrixReaderPrototype(aspect.getRow(), aspect.getCol()));

		mappers.put(ConstantAspect.class,
				(ConstantAspect aspect) -> new ConstantReaderPrototype(aspect.getConstant()));

		mappers.put(RangeAspect.class,
				(RangeAspect aspect) -> new RangeReaderPrototype(aspect.getFrom()));

		mappers.put(CompoundAspect.class,
				(CompoundAspect aspect) -> new CompoundReaderPrototype(aspect.getCompoundAspects()
						.stream()
						.map(AspectMapper::map)
						.collect(Collectors.toList())));

		mappers.put(TableAspect.class,
				(TableAspect aspect) ->
					new TableReaderPrototype(Util.mapVals(aspect.getAspects(), AspectMapper::map)));

		mappers.put(FileAspect.class,
				(FileAspect aspect) ->
					new FileReaderPrototype(Util.mapVals(aspect.getAspects(), AspectMapper::map)));

		mappers.put(JsonAspect.class,
				(JsonAspect aspect) ->
					new JsonReaderPrototype(AspectMapper.map(aspect.getAspect())));

		mappers.put(InlineTransformAspect.class,
				(InlineTransformAspect aspect) ->
					new InlineTransformReaderPrototype(aspect.getLang(), aspect.getScript(), aspect.getArgs(), map(aspect.getAspect())));

		mappers.put(ReferenceTransformAspect.class,
				(ReferenceTransformAspect aspect) ->
					new ReferenceTransformReaderPrototype(aspect.getFname(), aspect.getArgs(), map(aspect.getAspect())));

		mappers.put(AlignAspect.class,
				(AlignAspect aspect) ->
				new AlignReaderPrototype(Util.map(AspectMapper::map, aspect.getAspects())));
	}

	@SuppressWarnings("unchecked")
	private static Function<Aspect, ReaderPrototype> getMapper(Class<? extends Aspect> aspect) {
		Function<Aspect,ReaderPrototype> mapper=(Function<Aspect, ReaderPrototype>) mappers.get(aspect);
		if(mapper == null){
			//TODO - this should probably be a catchable exception at some point, but for now I just need to know what went bonk
			throw new RuntimeException("Missing mapper for aspect " + aspect.getName());
		}
		return mapper;
	}

	public static <A extends Aspect> ReaderPrototype map(A aspect) {
		return getMapper(aspect.getClass()).apply(aspect);
	}

	public static TableReaderPrototype map(TableAspect aspect) {
		return (TableReaderPrototype) getMapper(aspect.getClass()).apply(aspect);
	}
	
	public static TableReaderPrototype map(TableAspect aspect, char delimitter) {
		AspectMapper.delimitter = delimitter;
		return (TableReaderPrototype) getMapper(aspect.getClass()).apply(aspect);
	}

	public static FileReaderPrototype map(FileAspect aspect) {
		return (FileReaderPrototype) getMapper(aspect.getClass()).apply(aspect);
	}
}
