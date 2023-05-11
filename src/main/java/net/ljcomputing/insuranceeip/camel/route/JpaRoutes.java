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

import java.util.ArrayList;
import net.ljcomputing.insured.entity.Insured;
import net.ljcomputing.insured.service.InsuredService;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jackson.JacksonDataFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/** JPA route definitions. */
@Component
public class JpaRoutes extends RouteBuilder {

    /** Insurance data source. */
    // @Autowired
    // @Qualifier("insurance")
    // private DataSource insurance;
    @Autowired InsuredService insuredService;

    /** Configure route enpoints. */
    @Override
    public void configure() throws Exception {
        from("vm:insuredselectall")
                .routeId("DirectInsuredSelectAll")
                .bean(insuredService, "findAll")
                .marshal(getJacksonDataFormat(Insured.class))
                .convertBodyTo(String.class)
                .end();

        from("vm:insuredselectbyid")
                .routeId("DirectInsuredSelectById")
                .log("${headers}")
                .bean(insuredService, "findById(${headers.id})")
                .marshal(getJacksonDataFormat(Insured.class))
                .convertBodyTo(String.class)
                .end();
    }

    private JacksonDataFormat getJacksonDataFormat(Class<?> unmarshalType) {
        JacksonDataFormat format = new JacksonDataFormat();
        format.setCollectionType(ArrayList.class);
        format.setPrettyPrint(true);
        format.setUnmarshalType(unmarshalType);
        return format;
    }
}
