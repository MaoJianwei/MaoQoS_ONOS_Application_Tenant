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
package org.onosproject.mao.push.qos.impl;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.Service;
import org.onosproject.mao.push.qos.intf.MaoPushQosService;
import org.onosproject.mao.qos.api.impl.qdisc.MaoHtbQdiscObj;
import org.onosproject.mao.qos.api.intf.MaoQosObj;
import org.onosproject.mao.qos.intf.MaoQosService;
import org.onosproject.net.DeviceId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Skeletal ONOS application component.
 */
@Component(immediate = true)
@Service
public class MaoPushQos implements MaoPushQosService {

    private final Logger log = LoggerFactory.getLogger(getClass());


    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected MaoQosService maoQosService;





    @Activate
    protected void activate() {
        log.info("Started");
    }

    @Deactivate
    protected void deactivate() {
        log.info("Stopped");
    }



    @Override
    public boolean pushQos(){

        MaoHtbQdiscObj.Builder maoHtbQdiscObjBuilder = MaoHtbQdiscObj.builder();

        maoHtbQdiscObjBuilder
                .add()
                .setDeviceId(DeviceId.deviceId("of:0001111111111111"))
                .setDeviceIntfNumber(3)
                .setParent(MaoQosObj.ROOT)
                .setHandleOrClassId("1")
                .setDefaultId(1);


        return maoQosService.Apply(maoHtbQdiscObjBuilder.build());
//        maoQosService.Apply(null);
//        return true;
    }


}
