package org.onosproject.mao.qos.tenant.intf;

import org.onosproject.mao.qos.api.intf.MaoQosObj;

/**
 * Created by mao on 4/24/16.
 */
public interface MaoTenantService {

    boolean createTenant(String tenantName, int bandwidth, MaoQosObj.RATE_UNIT bandUnit);
}
