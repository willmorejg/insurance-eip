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

import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import net.ljcomputing.insuranceeip.constants.InsuredSql;
import net.ljcomputing.insuranceeip.model.Insured;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.sql.SqlConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/** JDBC route definitions. */
@Component
@Slf4j
public class JdbcRoutes extends RouteBuilder {
    private static final String[] ID_HEADER_VALUE = new String[] {"id"};

    /** Insurance data source. */
    @Autowired
    @Qualifier("insurance")
    private DataSource insurance;

    /** Configure route enpoints. */
    @Override
    public void configure() throws Exception {
        from("direct:jdbcinsuredselectcnt")
                .routeId("DirectJdbcInsuredSelectCnt")
                .to("sql:" + InsuredSql.SELECT_COUNT.sql("insurance"))
                .log("Out: ${body}");

        from("direct:jdbcinsuredselectall")
                .routeId("DirectJdbcInsuredSelectAll")
                .to("sql:" + InsuredSql.SELECT_ALL.sql("insurance"))
                .log("Out: ${body}");

        from("direct:jdbcinsuredinsert")
                .routeId("DirectJdbcInsuredInsert")
                .setHeader(SqlConstants.SQL_RETRIEVE_GENERATED_KEYS, constant(true))
                .setHeader(SqlConstants.SQL_GENERATED_COLUMNS, constant(ID_HEADER_VALUE))
                .inputType(Insured.class)
                .log("body: ${body}")
                .to(
                        "sql:"
                                + InsuredSql.INSERT.sql("insurance")
                                + "&outputType=SelectOne&exchangePattern=InOut&outputHeader=id")
                .process(
                        new Processor() {
                            public void process(Exchange exchange) throws Exception {
                                @SuppressWarnings("unchecked")
                                List<Object> camelSqlGeneratedKeyRows =
                                        (List<Object>)
                                                exchange.getIn()
                                                        .getHeader("CamelSqlGeneratedKeyRows");
                                @SuppressWarnings("unchecked")
                                Map<String, Object> rowsMap =
                                        (Map<String, Object>) camelSqlGeneratedKeyRows.get(0);
                                Long id = (Long) rowsMap.get("id");
                                Insured obj = (Insured) exchange.getIn().getBody();
                                obj.setId(id);
                                log.debug("obj: {}", obj);
                                log.debug("msg: {}", exchange.getMessage().getBody());
                                exchange.getMessage().setBody(obj);
                            }
                        });
    }
}
