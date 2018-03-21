/*
 * Copyright (C) 2017-Present Pivotal Software, Inc. All rights reserved.
 *
 * This program and the accompanying materials are made available under the terms of the under the Apache License,
 * Version 2.0 (the "License”); you may not use this file except in compliance with the License. You may obtain a copy
 * of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package io.pivotal.ecosystem.mssqlserver.broker;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.servicebroker.model.instance.CreateServiceInstanceRequest;
import org.springframework.cloud.servicebroker.model.instance.GetServiceInstanceRequest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.*;

/**
 * test will create and delete a cluster on a SQL Server. @Ignore tests unless you are doing integration testing and
 * have a test SQL Server available. You will need to edit the application.properties file in src/test/resources to
 * add your SQL Server environment data for this test to work.
 */

@RunWith(SpringRunner.class)
@SpringBootTest
public class ServiceInstanceTest {

    @Autowired
    private SqlServerClient sqlServerClient;

    @Autowired
    private ServiceInstanceRepository serviceInstanceRepository;


    @Autowired
    private CreateServiceInstanceRequest createServiceInstanceRequest;

    @Autowired
    private GetServiceInstanceRequest getServiceInstanceRequest;

    @Before
    public void setUp() {
        reset();
    }

    @After
    public void cleanUp() {
        reset();
    }

    private void reset() {
        Optional<ServiceInstance> si = serviceInstanceRepository.findById(TestConfig.SI_ID);
        if (si.isPresent()) {
            serviceInstanceRepository.delete(si.get());
        }
    }

    @Test
    public void testCrud() {
        ServiceInstance si = new ServiceInstance(createServiceInstanceRequest);
        assertNotNull(si);
        assertEquals(1, si.getParameters().size());

        assertNotNull(serviceInstanceRepository.save(si));

        assertTrue(serviceInstanceRepository.existsById(si.getId()));

        Optional<ServiceInstance> si3 = serviceInstanceRepository.findById(si.getId());
        assertTrue(si3.isPresent());

        assertEquals(1, si3.get().getParameters().size());

        si3.get().getParameters().put("foo", "bar");
        si3.get().getParameters().put("bizz", "bazz");
        serviceInstanceRepository.save(si3.get());

        Optional<ServiceInstance> si4 = serviceInstanceRepository.findById(si.getId());
        assertTrue(si4.isPresent());
        assertEquals(3, si4.get().getParameters().size());

        serviceInstanceRepository.delete(si4.get());

        assertFalse(serviceInstanceRepository.existsById(si.getId()));
    }
}