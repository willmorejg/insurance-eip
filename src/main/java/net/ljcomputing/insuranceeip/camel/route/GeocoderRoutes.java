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

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/** Geocoder route definitions. */
@Component
@Slf4j
public class GeocoderRoutes extends RouteBuilder {
    @Autowired CamelContext camelContext;

    /** Configure route enpoints. */
    @Override
    public void configure() throws Exception {
        from("direct:geocoding")
                .routeId("DirectGeocoding")
                .setBody()
                .jsonpathUnpack("$.address", String.class)
                .log("====>> DirectJsonToSample - body: ${body}")
                .toD(
                        "geocoder:address:${body}?type=NOMINATIM&serverUrl=https://nominatim.openstreetmap.org")
                .process(
                        new Processor() {
                            @Override
                            public void process(Exchange exchange) throws Exception {
                                String in = exchange.getIn().getBody().toString();
                                DocumentContext document = JsonPath.parse(in);
                                String address =
                                        ((String) document.read("$.[0].display_name"))
                                                .replace(",", "");
                                log.debug("address: {}", address);
                                String geoString =
                                        String.format(
                                                "geo:%s,%s",
                                                document.read("$.[0].lat").toString(),
                                                document.read("$.[0].lon").toString());
                                log.debug("geoString: {}", geoString);
                                ProducerTemplate producerTemplate =
                                        camelContext.createProducerTemplate();
                                producerTemplate.sendBody("direct:uspsbarcode", geoString);
                            }
                        })
                .end();
    }
}
