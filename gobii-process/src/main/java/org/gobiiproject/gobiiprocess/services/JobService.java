package org.gobiiproject.gobiiprocess.services;

import org.gobiiproject.gobiimodel.entity.Job;

public interface JobService {
    Job update(Job job);
    Job getByName(String name);
}
