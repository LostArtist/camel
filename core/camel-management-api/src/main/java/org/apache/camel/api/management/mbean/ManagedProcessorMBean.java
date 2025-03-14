/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.api.management.mbean;

import org.apache.camel.api.management.ManagedAttribute;
import org.apache.camel.api.management.ManagedOperation;

public interface ManagedProcessorMBean extends ManagedPerformanceCounterMBean {

    @ManagedAttribute(description = "Processor State")
    String getState();

    @ManagedAttribute(description = "Camel ID")
    String getCamelId();

    @ManagedAttribute(description = "Camel ManagementName")
    String getCamelManagementName();

    @ManagedAttribute(description = "Route ID")
    String getRouteId();

    @ManagedAttribute(description = "Node Prefix ID")
    String getNodePrefixId();

    @ManagedAttribute(description = "Step ID")
    String getStepId();

    @ManagedAttribute(description = "Processor ID")
    String getProcessorId();

    @ManagedAttribute(description = "Processor Name (Short)")
    String getProcessorName();

    @ManagedAttribute(description = "Processor Description")
    String getDescription();

    @ManagedAttribute(description = "Processor Index")
    Integer getIndex();

    @ManagedAttribute(description = "Processor Level in the route tree")
    int getLevel();

    @ManagedAttribute(description = "Source file Location")
    String getSourceLocation();

    @ManagedAttribute(description = "Line number of this node in the source file (when loaded from a line number aware parser)")
    Integer getSourceLineNumber();

    @ManagedAttribute(description = "Whether this processor supports extended JMX information")
    Boolean getSupportExtendedInformation();

    @ManagedOperation(description = "Start Processor")
    void start() throws Exception;

    @ManagedOperation(description = "Stop Processor")
    void stop() throws Exception;

    @ManagedOperation(description = "Dumps the processor as XML")
    String dumpProcessorAsXml() throws Exception;

}
