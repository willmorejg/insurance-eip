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
package net.ljcomputing.insuranceeip;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.ljcomputing.insuranceeip.constants.InsuredSql;
import net.ljcomputing.insuranceeip.model.Insured;
import net.ljcomputing.insuranceeip.model.Sample;
import net.ljcomputing.insuranceeip.utils.JsonUtils;
import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.ExchangePattern;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.AdviceWith;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;

@CamelSpringBootTest
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@TestMethodOrder(OrderAnnotation.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class InsuranceEipApplicationTests {
    private static final Logger log = LoggerFactory.getLogger(InsuranceEipApplicationTests.class);

    @LocalServerPort private int port;

    @Autowired private TestRestTemplate testRestTemplate;

    @Autowired private CamelContext camelContext;

    @Autowired private ProducerTemplate producertemplate;

    @EndpointInject("mock:result")
    private MockEndpoint mockEndpoint;

    @Autowired private JdbcTemplate jdbcTemplate;

    @Autowired ObjectMapper mapper;

    private static final Sample getSample() {
        Sample sample = new Sample();
        sample.setKey1("KEY_ONE");
        sample.setKey2("KEY_TWO");
        log.debug("Sample used in test: {}", sample);
        return sample;
    }

    private static final String getExpectedSample(final String filename) throws IOException {
        Path templateXml =
                Path.of(
                        System.getProperty("user.dir"),
                        "src",
                        "test",
                        "resources",
                        "test",
                        filename + ".xml");
        String result = Files.readString(templateXml);
        log.debug("expected: [{}]", result);
        return result;
    }

    private List<Insured> getInsuredRecordAll() {
        final List<Insured> result = new ArrayList<>();

        jdbcTemplate
                .queryForList(InsuredSql.SELECT_ALL.sql())
                .forEach(
                        e -> {
                            Insured element = new Insured();
                            element.setId((Long) e.get("id"));
                            element.setGivenName((String) e.get("given_name"));
                            element.setMiddleName((String) e.get("middle_name"));
                            element.setSurname((String) e.get("surname"));
                            element.setSuffix((String) e.get("suffix"));
                            result.add(element);
                        });

        log.debug("insured all: [{}]", result);
        return result;
    }

    private void insuredInsert() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String nowString = now.format(formatter);

        jdbcTemplate.update(
                "insert into insured (given_name, middle_name, surname) values(?,?,?)",
                "James" + nowString,
                "George" + nowString,
                "Willmore" + nowString);
    }

    private Integer getInsuredRecordCnt() {
        Integer cnt =
                (Integer) jdbcTemplate.queryForObject(InsuredSql.SELECT_COUNT.sql(), Integer.class);
        log.debug("insured cnt: [{}]", cnt);
        return cnt;
    }

    @Test
    @Order(1)
    @Disabled
    void contextLoads() {
        assertNotNull(camelContext);
        assertNotNull(producertemplate);
        assertNotNull(mockEndpoint);
        assertNotNull(jdbcTemplate);
    }

    @Test
    @Order(10)
    @Disabled
    void testDirectSampleTemplate() throws Exception {
        String routeId = "DirectSampleTemplate";
        String routeUri = "direct:sampletemplate";

        AdviceWith.adviceWith(
                camelContext,
                routeId,
                r -> {
                    r.replaceFromWith(routeUri);
                    r.weaveAddLast().to(mockEndpoint);
                    r.weaveAddLast().log("endpoint: ${body}");
                });

        mockEndpoint.expectedBodiesReceived(getExpectedSample(routeId));
        mockEndpoint.setResultWaitTime(600);
        mockEndpoint.setAssertPeriod(600);

        producertemplate.sendBody(routeUri, getSample());

        mockEndpoint.assertIsSatisfied();
    }

    @Test
    @Order(11)
    @Disabled
    void testDirectJsonToSample() throws Exception {
        String routeId = "DirectJsonToSample";
        String routeUri = "direct:jsonToSample";

        AdviceWith.adviceWith(
                camelContext,
                routeId,
                r -> {
                    r.replaceFromWith(routeUri);
                    r.weaveAddLast().to(mockEndpoint);
                    r.weaveAddLast().log("endpoint: ${body}");
                });

        mockEndpoint.expectedBodiesReceived(getSample());
        mockEndpoint.setResultWaitTime(600);
        mockEndpoint.setAssertPeriod(600);

        producertemplate.sendBody(routeUri, JsonUtils.pojoToJson(getSample()));

        mockEndpoint.assertIsSatisfied();
    }

    @Test
    @Order(12)
    @Disabled
    void testDirectXjToXml() throws Exception {
        String routeId = "DirectXjToXml";
        String routeUri = "direct:xjtoxml";

        AdviceWith.adviceWith(
                camelContext,
                routeId,
                r -> {
                    r.replaceFromWith(routeUri);
                    r.weaveAddLast().to(mockEndpoint);
                    r.weaveAddLast().log("endpoint: ${body}");
                });

        mockEndpoint.expectedBodiesReceived(getExpectedSample(routeId));
        mockEndpoint.setResultWaitTime(600);
        mockEndpoint.setAssertPeriod(600);

        producertemplate.sendBody(routeUri, JsonUtils.pojoToJson(getSample()));

        mockEndpoint.assertIsSatisfied();
    }

    @Test
    @Order(13)
    @Disabled
    void testDirectJdbcInsuredSelectCnt() throws Exception {
        String routeId = "DirectJdbcInsuredSelectCnt";
        String routeUri = "direct:jdbcinsuredselectcnt";

        AdviceWith.adviceWith(
                camelContext,
                routeId,
                r -> {
                    r.replaceFromWith(routeUri);
                    r.weaveAddLast().to(mockEndpoint);
                    r.weaveAddLast().log("endpoint: ${body}");
                });

        mockEndpoint.setResultWaitTime(600);
        mockEndpoint.setAssertPeriod(600);

        producertemplate.sendBody(routeUri, ExchangePattern.InOut, "");
        Object received = mockEndpoint.getReceivedExchanges().get(0).getIn().getBody();
        log.debug("received: [{}]", received);
        @SuppressWarnings("unchecked")
        List<Map<String, Long>> receivedList = (List<Map<String, Long>>) received;
        log.debug("receivedList: [{}]", receivedList);
        Integer resultCnt = receivedList.get(0).get("cnt").intValue();
        assertEquals(getInsuredRecordCnt(), resultCnt);
    }

    @Test
    @Order(14)
    @Disabled
    void testDirectJdbcInsuredInsert() throws IOException, InterruptedException {
        Insured insured = new Insured();
        insured.setGivenName("James");
        insured.setMiddleName("George");
        insured.setSurname("Willmore");

        Insured obj =
                (Insured)
                        producertemplate.sendBody(
                                "direct:jdbcinsuredinsert", ExchangePattern.InOut, insured);
        log.debug("-->> obj: {}", obj);

        assertNotNull(obj.getId());
        insured.setId(obj.getId());
        assertEquals(insured, obj);
    }

    @Test
    @Order(20)
    @Disabled
    void testRestSample() throws Exception {
        final String baseUrl = "http://localhost:" + port + "/camel/sample/";
        final URI uri = new URI(baseUrl);

        final HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_XML_VALUE);

        final HttpEntity<Sample> request = new HttpEntity<>(getSample(), headers);
        final ResponseEntity<String> result =
                testRestTemplate.postForEntity(uri, request, String.class);

        log.debug("result - body: [{}]", result.getBody());

        final XmlMapper xmlMapper = new XmlMapper();
        final Sample sampleResult = xmlMapper.readValue(result.getBody(), getSample().getClass());
        log.debug("sample results: [{}]", sampleResult.toString());
    }

    @Test
    @Order(21)
    @Disabled
    void testRestInsuredAll() throws Exception {
        insuredInsert();
        final String baseUrl = "http://localhost:" + port + "/camel/insured";
        final URI uri = new URI(baseUrl);
        final HttpHeaders headers = new HttpHeaders();

        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);

        final HttpEntity<String> request = new HttpEntity<>("", headers);
        ResponseEntity<String> result =
                testRestTemplate.exchange(uri, HttpMethod.GET, request, String.class);

        String json = result.getBody();

        log.debug("result - json: \n{}\n==========", json);

        JsonNode list = mapper.readTree(json);

        log.debug("result - list: \n{}\n==========", list);
    }

    @Test
    @Order(22)
    @Disabled
    void testRestInsuredById() throws Exception {
        insuredInsert();
        final Insured expected = getInsuredRecordAll().get(0);
        final Long expectedId = expected.getId();
        final String baseUrl = "http://localhost:" + port + "/camel/insured/" + expectedId;
        final URI uri = new URI(baseUrl);
        final HttpHeaders headers = new HttpHeaders();

        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);

        final HttpEntity<String> request = new HttpEntity<>("", headers);
        ResponseEntity<String> result =
                testRestTemplate.exchange(uri, HttpMethod.GET, request, String.class);

        String json = result.getBody();

        log.debug("result - json: \n{}\n==========", json);

        JsonNode list = mapper.readTree(json);

        log.debug("result - list: \n{}\n==========", list);
    }
}
