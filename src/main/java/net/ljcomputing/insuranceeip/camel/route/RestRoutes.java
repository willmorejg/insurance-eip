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

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

/** Camel REST route. */
@Component
public class RestRoutes extends RouteBuilder {

    /** Configure the Camel REST routes. */
    @Override
    public void configure() throws Exception {
        restConfiguration().component("servlet").bindingMode(RestBindingMode.auto);

        rest("/geocode")
                .post()
                .routeId("RestGeocode")
                .bindingMode(RestBindingMode.off)
                .consumes(MediaType.APPLICATION_JSON_VALUE)
                .produces(MediaType.APPLICATION_JSON_VALUE)
                // .type(Sample.class)
                .to("direct:geocoding");

        rest("/sample")
                .post()
                .routeId("RestSample")
                .bindingMode(RestBindingMode.off)
                .consumes(MediaType.APPLICATION_JSON_VALUE)
                .produces(MediaType.APPLICATION_XML_VALUE)
                // .type(Sample.class)
                .to("direct:xjtoxml");

        rest("/insured")
                // get all
                .get()
                .routeId("RestGetInsuredAll")
                .bindingMode(RestBindingMode.off)
                .consumes(MediaType.APPLICATION_JSON_VALUE)
                .produces(MediaType.APPLICATION_JSON_VALUE)
                .to("vm:insuredselectall")
                // get by id
                .get("/{id}")
                .routeId("RestGetInsuredById")
                .bindingMode(RestBindingMode.off)
                .consumes(MediaType.APPLICATION_JSON_VALUE)
                .produces(MediaType.APPLICATION_JSON_VALUE)
                .to("vm:insuredselectbyid");
    }
}
