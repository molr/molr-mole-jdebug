/*
 * © Copyright 2016 CERN. This software is distributed under the terms of the Apache License Version 2.0, copied
 * verbatim in the file “COPYING”. In applying this licence, CERN does not waive the privileges and immunities granted
 * to it by virtue of its status as an Intergovernmental Organization or submit itself to any jurisdiction.
 */

package cern.jarrace.commons.instantiation;

import cern.jarrace.commons.domain.Service;

/**
 * A request to instantiate an inspector with a given classpath and a {@link Service} to run.
 */
public interface InspectorInstantiationRequest {

    String getClassPath();

    Service getEntryPoints();

}
