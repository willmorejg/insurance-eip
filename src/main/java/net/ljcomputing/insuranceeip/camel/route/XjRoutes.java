/*
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.

James G Willmore - LJ Computing - (C) 2023
*/
package net.ljcomputing.insuranceeip.camel.route;

import java.nio.file.Path;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

/** XJ (XML and JSON document conversion) route definitions. */
@Component
public class XjRoutes extends RouteBuilder {

    /** Configure route enpoints. */
    @Override
    public void configure() throws Exception {
        String homeDir = System.getProperty("user.home");
        Path outPath = Path.of(homeDir, "out");
        String outFile =
                String.format("file://%s/?fileName=xjtoxml-template.xml", outPath.toString());

        from("direct:xjtoxml")
                .routeId("DirectXjToXml")
                .pipeline(
                        "xj:identity?transformDirection=JSON2XML", "xslt:xslt/xjToXml.xsl", outFile)
                .end();
        ;
    }
}
