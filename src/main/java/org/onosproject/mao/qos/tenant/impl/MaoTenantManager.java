/*
 * Copyright 2016-present Open Networking Laboratory
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.onosproject.mao.qos.tenant.impl;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.Service;
import org.onosproject.core.ApplicationId;
import org.onosproject.core.CoreService;
import org.onosproject.mao.qos.tenant.intf.MaoTenantService;
import org.onosproject.mao.qos.api.impl.classify.MaoHtbClassObj;
import org.onosproject.mao.qos.api.impl.qdisc.MaoHtbQdiscObj;
import org.onosproject.mao.qos.api.intf.MaoQosObj;
import org.onosproject.mao.qos.intf.MaoQosService;
import org.onosproject.net.DeviceId;
import org.onosproject.store.service.StorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;


/**
 * Skeletal ONOS application component.
 */
@Component(immediate = true)
@Service
public class MaoTenantManager implements MaoTenantService {

    private final Logger log = LoggerFactory.getLogger(getClass());


    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected MaoQosService maoQosService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected StorageService storageService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected CoreService coreService;



    private final String ACCESS_DPID = "of:0001111111111111";
    private final String CORE_DPID = "of:0002222222222222";

    private ApplicationId appId;
    private Map<String, Map<String, Integer>> tenantQosMapping;



    @Activate
    protected void activate() {

        log.info("init...");
        appId = coreService.registerApplication("onos.app.mao.qos.tenant");

//        tenantQosMapping = storageService
//                .Map<String, Map<String, Integer>>consistentMapBuilder()
//                .withPurgeOnUninstall()
//                .withApplicationId(appId)
//                .build()
        tenantQosMapping = new HashMap<>();

        buildDefaultQos();

        log.info("Started");
    }

    @Deactivate
    protected void deactivate() {
        log.info("Stopped");
    }



    private void buildDefaultQos() {
        buildOneDefaultQos(DeviceId.deviceId(ACCESS_DPID), 1);
        buildOneDefaultQos(DeviceId.deviceId(ACCESS_DPID), 2);
        buildOneDefaultQos(DeviceId.deviceId(ACCESS_DPID), 3);
        buildOneDefaultQos(DeviceId.deviceId(CORE_DPID), 1);
        buildOneDefaultQos(DeviceId.deviceId(CORE_DPID), 2);
        buildOneDefaultQos(DeviceId.deviceId(CORE_DPID), 3);

    }

    private void buildOneDefaultQos(DeviceId deviceId, int deviceIntfNumber){

        MaoHtbQdiscObj rootHtb = MaoHtbQdiscObj.builder()
                .add()
                .setParent(MaoQosObj.ROOT)
                .setHandleOrClassId("1:")
                .setDeviceId(deviceId)
                .setDeviceIntfNumber(deviceIntfNumber)
                .setDefaultId(2)
                .build();
        maoQosService.Apply(rootHtb);
    }

    private void buildOneIntfDefaultQos(DeviceId deviceId, int deviceIntfNumber, int bandwidth, MaoQosObj.RATE_UNIT bandUnit) {

        MaoHtbClassObj parentHtbClass = MaoHtbClassObj.builder()
                .add()
                .setParent(MaoHtbQdiscObj.builder().setHandleOrClassId("1:").build())
                .setHandleOrClassId("1:1")
                .setDeviceId(deviceId)
                .setDeviceIntfNumber(deviceIntfNumber)
                .rate(bandwidth, bandUnit)
                .ceil(bandwidth, bandUnit)
                .burst(10, MaoQosObj.SIZE_UNIT.SIZE_KBYTE)
                .cburst(10, MaoQosObj.SIZE_UNIT.SIZE_KBYTE)
                .build();
        maoQosService.Apply(parentHtbClass);

//        if (parentClass == null) {
//            parentClass = parentHtbClass;
//        }

        MaoHtbClassObj leafHtbClass = MaoHtbClassObj.builder()
                .add()
                .setParent(parentHtbClass)
                .setHandleOrClassId("1:2")
                .setDeviceId(deviceId)
                .setDeviceIntfNumber(deviceIntfNumber)
                .rate(1, MaoQosObj.RATE_UNIT.RATE_KBIT)
                .ceil(bandwidth, bandUnit)
                .burst(10, MaoQosObj.SIZE_UNIT.SIZE_KBYTE)
                .cburst(10, MaoQosObj.SIZE_UNIT.SIZE_KBYTE)
                .priority(0)
                .build();
        maoQosService.Apply(leafHtbClass);

        MaoHtbQdiscObj leafHtb = MaoHtbQdiscObj.builder()
                .add()
                .setParent(leafHtbClass)
                .setHandleOrClassId("2:")
                .setDeviceId(deviceId)
                .setDeviceIntfNumber(deviceIntfNumber)
                .build();
        maoQosService.Apply(leafHtb);
    }



    @Override
    public boolean createTenant(String tenantName, int bandwidth, MaoQosObj.RATE_UNIT bandUnit){

        tenantQosMapping.put(tenantName, new HashMap<String, Integer>());

        if(tenantName.equals("A")){

            buildOneIntfDefaultQos(DeviceId.deviceId(ACCESS_DPID), 1,bandwidth,bandUnit);
            buildOneIntfDefaultQos(DeviceId.deviceId(ACCESS_DPID), 3,bandwidth,bandUnit);
            buildOneIntfDefaultQos(DeviceId.deviceId(CORE_DPID), 1,bandwidth,bandUnit);
            buildOneIntfDefaultQos(DeviceId.deviceId(CORE_DPID), 3,bandwidth,bandUnit);


        } else if (tenantName.equals("B")) {

            buildOneIntfDefaultQos(DeviceId.deviceId(ACCESS_DPID), 2,bandwidth,bandUnit);
            buildOneIntfDefaultQos(DeviceId.deviceId(ACCESS_DPID), 3,bandwidth,bandUnit);
            buildOneIntfDefaultQos(DeviceId.deviceId(CORE_DPID), 2,bandwidth,bandUnit);
            buildOneIntfDefaultQos(DeviceId.deviceId(CORE_DPID), 3,bandwidth,bandUnit);

        } else {
            return false;
        }

        return false;
    }
}
