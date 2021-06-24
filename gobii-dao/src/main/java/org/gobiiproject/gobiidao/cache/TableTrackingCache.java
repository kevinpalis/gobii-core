package org.gobiiproject.gobiidao.cache;

import java.util.Date;
import org.gobiiproject.gobiidao.GobiiDaoException;
import org.gobiiproject.gobiidao.resultset.core.listquery.DtoListQueryColl;
import org.gobiiproject.gobiimodel.types.GobiiEntityNameType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;

/***
 * The idiom here is not obvious. There is a lot of magic going on in the background via the annotations and the
 * cachemanager configuration in the application-config.xml file, which is where we set up the "tableTracker" cache.
 * The cache is essentially a kev-value pair storage mechanism. Because we specify the gobiiEntityNameType parameter as
 * the key, the values are keyed by that value. If getLasetModified() is called first, the returned
 * value is cached indefinitely. Subsequent calls to getLastModified() will return the value from the cache
 * for that gobiiEntityNameType. This behavior is determined by the @Cacheable annotation. The @CachePut annotation on
 * setLastModified() replaces the existing value for the specified gobiiEntityNameType. If @CachePut is called first,
 * getLastModified() does not replace that value but rather gets it from the store.
 *
 * Two separate caches are needed per the datatype of the item stored. If you have only one cache, both the date and count
 * methods are storing the data in the same cache and the data don't cast properly upon retrieval.
 *
 * NOTE: cached methods DO NOT WORK if you call them from within the class that contains them. For example, if one were to
 * write a wrapper method around getParentChildState() within this class, the method would work, but the result would not
 * be cached. Thus, the consumer of the cache _must_ be in a class outside this class.
 */
public class TableTrackingCache {

    @Autowired
    @SuppressWarnings("unused")
    private DtoListQueryColl dtoListQueryColl;

    @Cacheable(cacheNames = "tableLastUpdateCache", key = "#gobiiEntityNameType")
    public Date getLastModified(GobiiEntityNameType gobiiEntityNameType) {
        return new Date();
    }

    @CachePut(cacheNames = "tableLastUpdateCache", key = "#gobiiEntityNameType")
    public Date setLastModified(GobiiEntityNameType gobiiEntityNameType, Date date) {
        return date;
    }

    @Cacheable(cacheNames = "tableRecordCountCache", key = "#gobiiEntityNameType")
    public EntityCountState getCount(GobiiEntityNameType gobiiEntityNameType) {
        return null; // if we haven't called setCount(), we don't have anything to report
    }

    @CachePut(cacheNames = "tableRecordCountCache", key = "#gobiiEntityNameType")
    public EntityCountState setCount(GobiiEntityNameType gobiiEntityNameType, EntityCountState entityCountState) throws GobiiDaoException {
        return entityCountState;
    }


    @Cacheable(cacheNames = "parentChildCountCache",key = "#gobiiEntityNameTypeParent.toString() + #parentId.toString() + #gobiiEntityNameTypeChild.toString()")
    public ParentChildCountState getParentChildState(GobiiEntityNameType gobiiEntityNameTypeParent,
                                                     Integer parentId,
                                                     GobiiEntityNameType gobiiEntityNameTypeChild) {
        return new ParentChildCountState(gobiiEntityNameTypeParent, null, parentId, gobiiEntityNameTypeChild, null, 0);
    }

}
