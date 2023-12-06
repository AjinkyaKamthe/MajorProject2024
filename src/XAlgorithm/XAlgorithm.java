package XAlgorithm;

import org.cloudbus.cloudsim.*;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.CloudSimTags;
import org.cloudbus.cloudsim.core.SimEvent;

import java.util.List;
import java.util.*;

public class XAlgorithm extends DatacenterBroker {

    public XAlgorithm(String name) throws Exception {
        super(name);
    }

    @Override
    public void submitCloudletList(List<? extends Cloudlet> list) {
        super.submitCloudletList(list);

        for (Cloudlet cloudlet : getCloudletList()) {
            Vm leastLoadedVm = getLeastLoadedVm();
            bindCloudletToVm(cloudlet.getCloudletId(), leastLoadedVm.getId());
        }
    }

    private Vm getLeastLoadedVm() {
        List<Vm> vmList = getVmList();

        Vm leastLoadedVm = vmList.get(0);
        double minExpectedTime = getExpectedCompletionTime(leastLoadedVm);

        for (Vm vm : vmList) {
            double expectedTime = getExpectedCompletionTime(vm);
            if (expectedTime < minExpectedTime) {
                minExpectedTime = expectedTime;
                leastLoadedVm = vm;
            }
        }

        return leastLoadedVm;
    }

    private double getExpectedCompletionTime(Vm vm) {
        double vmMips = vm.getMips();
        double totalCloudletLength = 0;

        for (Cloudlet cloudlet : getCloudletList()) {
            if (cloudlet.getVmId() == vm.getId()) {
                totalCloudletLength += cloudlet.getCloudletLength();
            }
        }

        return totalCloudletLength / vmMips;
    }
}
