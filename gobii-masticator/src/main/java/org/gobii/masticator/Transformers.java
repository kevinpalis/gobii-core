package org.gobii.masticator;

import org.gobii.masticator.reader.transform.compilers.Transformation;
import org.gobii.masticator.reader.transform.types.IUPACTransform;
import org.gobii.masticator.reader.transform.types.NucleotideSeparatorSplitter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Transformers {

	private static IUPACTransform iupac = new IUPACTransform();
	private static NucleotideSeparatorSplitter fourLetter = new NucleotideSeparatorSplitter(4);
	private static NucleotideSeparatorSplitter twoLetter = new NucleotideSeparatorSplitter(2);
	@Transformation
	public String asdf(String value) {
		return "asdf";
	}
	@Transformation
	public String IUPAC2BI(List<String> values, List<Object> args){
		if(values.size()<1)return "";
		String value = values.get(0);
		String retVal = Arrays.stream(value.split(String.valueOf(AspectMapper.delimitter))).map(s -> iupac.process(s)).collect(Collectors.joining("\t"));
			if(retVal==null){
				throw new RuntimeException("Unable to process IUPAC transformation for value " + value);
			}
			return retVal;
	}

	@Transformation
	public String ARRAYIFY(List<String> values, List<Object> args){
		if(values.size()<1)return "";
		String value = values.get(0);
		String retVal = Arrays.stream(value.split(String.valueOf(AspectMapper.delimitter))).map(s -> "{"+s+"}").collect(Collectors.joining("\t"));
		return retVal;
	}


	@Transformation
	public String VCF2BI(List<String> values){
		if(values.size()<1)return "";
		String value = values.get(0);
		//Todo - need to run externally
		throw new RuntimeException("Unable to process VCF. Upstream failed to convert this request");
	}

	@Transformation
	public String FOURLETTERNUCLEOTIDE(List<String> values, List<Object> args){
		if(values.size()<1)return "";
		String value = values.get(0);
		String retVal = Arrays.stream(value.split(String.valueOf(AspectMapper.delimitter))).map(s -> fourLetter.process(s)).collect(Collectors.joining("\t"));

		if(retVal==null){
			throw new RuntimeException("Unable to process four letter nucleotide cleaning transformation for value " + value);
		}
		return retVal;
	}

	@Transformation
	public String TWOLETTERNUCLEOTIDE(List<String> values, List<Object> args){
		if(values.size()<1)return "";
		String value = values.get(0);
		String retVal = Arrays.stream(value.split(String.valueOf(AspectMapper.delimitter))).map(s -> twoLetter.process(s)).collect(Collectors.joining("\t"));

		if(retVal==null){
			throw new RuntimeException("Unable to process two letter nucleotide cleaning transformation for value " + value);
		}
		return retVal;
	}



}
