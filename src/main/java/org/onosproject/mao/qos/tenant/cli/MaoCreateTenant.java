package org.onosproject.mao.qos.tenant.cli;

import org.apache.karaf.shell.commands.Argument;
import org.apache.karaf.shell.commands.Command;
import org.onosproject.cli.AbstractShellCommand;
import org.onosproject.mao.qos.api.intf.MaoQosObj;
import org.onosproject.mao.qos.tenant.intf.MaoTenantService;

/**
 * Created by mao on 4/28/16.
 */
@Command(scope = "onos", name = "mao-create-tenant",
        description = "Mao Create Tenant")
public class MaoCreateTenant extends AbstractShellCommand {

    @Argument(index = 0, name = "tenant-name", description = "Tenant Name",
            required = true, multiValued = false)
    private String tenantName;

    @Argument(index = 1, name = "tenant-bandwidth", description = "Tenant BandWidth",
            required = true, multiValued = false)
    private int bandwidth;

    @Argument(index = 2, name = "tenant-bandwidth-unit", description = "Tenant BandWidth Unit",
            required = true, multiValued = false)
    private String bandUnit;

    @Override
    protected void execute() {

        MaoTenantService maoTenantService = AbstractShellCommand.get(MaoTenantService.class);

        MaoQosObj.RATE_UNIT unit;
        switch(bandUnit){
            case "bps":
                unit = MaoQosObj.RATE_UNIT.RATE_BIT;
                break;
            case "kbps":
                unit = MaoQosObj.RATE_UNIT.RATE_KBIT;
                break;
            case "mbps":
                unit = MaoQosObj.RATE_UNIT.RATE_MBIT;
                break;
            case "gbps":
                unit = MaoQosObj.RATE_UNIT.RATE_GBIT;
                break;
            default:
                print("Bandwidth Unit should be: bps/kbps/mbps/gbps");
                return;
        }

        maoTenantService.createTenant(tenantName, bandwidth, unit);


    }
}
