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
import org.apache.camel.spi.DataFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/** Barcode related routes. */
@Component
public class BarcodeRoutes extends RouteBuilder {
    @Autowired private Path outputDirectory;

    @Autowired private DataFormat barcode;

    /** Configure route enpoints. */
    @Override
    public void configure() throws Exception {

        from("direct:uspsbarcode")
                .routeId("DirectUSPSBarcode")
                .marshal(barcode)
                .toD("file://" + outputDirectory.toAbsolutePath() + "?fileName=test.png")
                .end();
    }
}
