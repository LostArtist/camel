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
package org.apache.camel.dsl.jbang.core.commands.process;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;

import com.github.freva.asciitable.AsciiTable;
import com.github.freva.asciitable.Column;
import com.github.freva.asciitable.HorizontalAlign;
import com.github.freva.asciitable.OverflowBehaviour;
import org.apache.camel.dsl.jbang.core.commands.CamelJBangMain;
import org.apache.camel.dsl.jbang.core.common.PidNameAgeCompletionCandidates;
import org.apache.camel.dsl.jbang.core.common.ProcessHelper;
import org.apache.camel.util.TimeUtils;
import org.apache.camel.util.json.JsonArray;
import org.apache.camel.util.json.JsonObject;
import picocli.CommandLine;
import picocli.CommandLine.Command;

@Command(name = "service",
         description = "Get services of Camel integrations", sortOptions = false, showDefaultValues = true)
public class ListService extends ProcessWatchCommand {

    @CommandLine.Parameters(description = "Name or pid of running Camel integration", arity = "0..1")
    String name = "*";

    @CommandLine.Option(names = { "--sort" }, completionCandidates = PidNameAgeCompletionCandidates.class,
                        description = "Sort by pid, name or age", defaultValue = "pid")
    String sort;

    @CommandLine.Option(names = { "--metadata" },
                        description = "Show service metadata (only available for some services)")
    boolean metadata;

    @CommandLine.Option(names = { "--short-uri" },
                        description = "List endpoint URI without query parameters (short)")
    boolean shortUri;

    @CommandLine.Option(names = { "--wide-uri" },
                        description = "List endpoint URI in full details")
    boolean wideUri;

    public ListService(CamelJBangMain main) {
        super(main);
    }

    @Override
    public Integer doProcessWatchCall() throws Exception {
        List<Row> rows = new ArrayList<>();

        List<Long> pids = findPids(name);
        ProcessHandle.allProcesses()
                .filter(ph -> pids.contains(ph.pid()))
                .forEach(ph -> {
                    JsonObject root = loadStatus(ph.pid());
                    // there must be a status file for the running Camel integration
                    if (root != null) {
                        Row row = new Row();
                        JsonObject context = (JsonObject) root.get("context");
                        if (context == null) {
                            return;
                        }
                        row.name = context.getString("name");
                        if ("CamelJBang".equals(row.name)) {
                            row.name = ProcessHelper.extractName(root, ph);
                        }
                        row.pid = Long.toString(ph.pid());
                        row.uptime = extractSince(ph);
                        row.age = TimeUtils.printSince(row.uptime);

                        JsonObject jo = (JsonObject) root.get("services");
                        if (jo != null) {
                            JsonArray arr = (JsonArray) jo.get("services");
                            if (arr != null) {
                                for (int i = 0; i < arr.size(); i++) {
                                    row = row.copy();
                                    jo = (JsonObject) arr.get(i);
                                    row.component = jo.getString("component");
                                    row.direction = jo.getString("direction");
                                    row.hosted = jo.getBooleanOrDefault("hosted", false);
                                    row.protocol = jo.getString("protocol");
                                    row.serviceUrl = jo.getString("serviceUrl");
                                    row.endpointUri = jo.getString("endpointUri");
                                    row.hits = jo.getLongOrDefault("hits", 0);
                                    row.routeId = jo.getString("routeId");
                                    row.metadata = jo.getMap("metadata");
                                    rows.add(row);
                                }
                            }
                        }
                    }
                });

        // sort rows
        rows.sort(this::sortRow);

        if (!rows.isEmpty()) {
            printer().println(AsciiTable.getTable(AsciiTable.NO_BORDERS, rows, Arrays.asList(
                    new Column().header("PID").headerAlign(HorizontalAlign.CENTER).with(r -> r.pid),
                    new Column().header("NAME").dataAlign(HorizontalAlign.LEFT).maxWidth(30, OverflowBehaviour.ELLIPSIS_RIGHT)
                            .with(r -> r.name),
                    new Column().header("COMPONENT").dataAlign(HorizontalAlign.LEFT).with(r -> r.component),
                    new Column().header("DIR").dataAlign(HorizontalAlign.LEFT).with(r -> r.direction),
                    new Column().header("ROUTE").dataAlign(HorizontalAlign.LEFT).with(this::getRouteId),
                    new Column().header("PROTOCOL").dataAlign(HorizontalAlign.LEFT).with(this::getProtocol),
                    new Column().header("SERVICE").dataAlign(HorizontalAlign.LEFT).with(this::getService),
                    new Column().header("METADATA").visible(metadata).dataAlign(HorizontalAlign.LEFT).with(this::getMetadata),
                    new Column().header("TOTAL").dataAlign(HorizontalAlign.RIGHT).with(r -> "" + r.hits),
                    new Column().header("ENDPOINT").visible(!wideUri).dataAlign(HorizontalAlign.LEFT)
                            .maxWidth(90, OverflowBehaviour.ELLIPSIS_RIGHT)
                            .with(this::getUri),
                    new Column().header("ENDPOINT").visible(wideUri).dataAlign(HorizontalAlign.LEFT)
                            .maxWidth(140, OverflowBehaviour.NEWLINE)
                            .with(this::getUri))));
        }

        return 0;
    }

    private String getRouteId(Row r) {
        if (r.routeId != null) {
            return r.routeId;
        }
        return "";
    }

    protected int sortRow(Row o1, Row o2) {
        String s = sort;
        int negate = 1;
        if (s.startsWith("-")) {
            s = s.substring(1);
            negate = -1;
        }
        switch (s) {
            case "pid":
                return Long.compare(Long.parseLong(o1.pid), Long.parseLong(o2.pid)) * negate;
            case "name":
                return o1.name.compareToIgnoreCase(o2.name) * negate;
            case "age":
                return Long.compare(o1.uptime, o2.uptime) * negate;
            default:
                return 0;
        }
    }

    private String getUri(Row r) {
        String u = r.endpointUri;
        if (shortUri) {
            int pos = u.indexOf('?');
            if (pos > 0) {
                u = u.substring(0, pos);
            }
        }
        return u;
    }

    private String getProtocol(Row r) {
        return r.protocol;
    }

    private String getService(Row r) {
        return r.serviceUrl;
    }

    private String getMetadata(Row r) {
        if (r.metadata != null) {
            StringJoiner sj = new StringJoiner(" ");
            r.metadata.forEach((k, v) -> sj.add(k + "=" + v));
            return sj.toString();
        }
        return "";
    }

    private static class Row implements Cloneable {
        String pid;
        String name;
        String age;
        long uptime;
        String component;
        String direction;
        boolean hosted;
        String protocol;
        String serviceUrl;
        String endpointUri;
        long hits;
        String routeId;
        JsonObject metadata;

        Row copy() {
            try {
                return (Row) clone();
            } catch (CloneNotSupportedException e) {
                return null;
            }
        }
    }

}
