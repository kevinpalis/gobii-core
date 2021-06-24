package org.gobii.masticator.aspects;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ConstantAspect extends ElementAspect {

	private String constant;
}
