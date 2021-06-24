package org.gobii.masticator.aspects;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class InlineTransformAspect extends TransformAspect {

	private String lang;
	private String script;

}
