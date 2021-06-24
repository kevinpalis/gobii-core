package org.gobiiproject.gobiisampletrackingdao;

import java.util.List;
import java.util.Set;

import org.gobiiproject.gobiimodel.entity.Reference;

public interface ReferenceDao {

    Reference getReference(Integer id);

	List<Reference> getReferences(Integer page, Integer offset);
    List<Reference> getReferences(Set<String> referenceNames,
                                  Integer pageSize,
                                  Integer offset);

	Reference createReference(Reference reference) throws Exception;

	Reference updateReference(Reference reference) throws Exception;

	void deleteReference(Reference reference);
}