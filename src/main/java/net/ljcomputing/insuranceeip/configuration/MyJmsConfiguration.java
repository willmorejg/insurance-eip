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
package net.ljcomputing.insuranceeip.configuration;

/**
 * @author James G. Willmore
 */
// @Configuration
// @EnableJms
public class MyJmsConfiguration {
    // private static final String BROKER_URL = "tcp://localhost:61616";
    // private static final String BROKER_USERNAME = "jim";
    // private static final String BROKER_PASSWORD = "Wiomm$001";

    // @Bean
    // public ActiveMQConnectionFactory connectionFactory() {
    //     ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
    //     connectionFactory.setBrokerURL(BROKER_URL);
    //     connectionFactory.setPassword(BROKER_USERNAME);
    //     connectionFactory.setUserName(BROKER_PASSWORD);
    //     return connectionFactory;
    // }

    // @Bean(name = "activemq")
    // public ActiveMQComponent createComponent() {
    //     ActiveMQComponent activeMQComponent = new ActiveMQComponent();
    //     activeMQComponent.setConnectionFactory(connectionFactory());
    //     return activeMQComponent;
    // }

    // @Bean
    // public JmsTemplate jmsTemplate() {
    //     JmsTemplate template = new JmsTemplate();
    //     template.setConnectionFactory(connectionFactory());
    //     return template;
    // }

    // @Bean
    // public DefaultJmsListenerContainerFactory jmsListenerContainerFactory() {
    //     DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
    //     factory.setConnectionFactory(connectionFactory());
    //     factory.setConcurrency("1-1");
    //     return factory;
    // }
}
