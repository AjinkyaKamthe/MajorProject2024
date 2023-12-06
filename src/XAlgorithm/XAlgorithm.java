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

    @Override
    protected void processResourceCharacteristics(SimEvent ev) {
//         Process datacenter characteristics as before
        DatacenterCharacteristics characteristics = (DatacenterCharacteristics) ev.getData();
        getDatacenterCharacteristicsList().put(characteristics.getId(), characteristics);

        if (getDatacenterCharacteristicsList().size() == getDatacenterIdsList().size()) {
            distributeRequestsForNewVmsUsingRoundRobin();
        }
    }

    protected void distributeRequestsForNewVmsUsingRoundRobin() {
        int numberOfVmsAllocated = 0;
        int i = 0;

        final List<Integer> availableDatacenters = getDatacenterIdsList();

        for (Vm vm : getVmList()) {
            int datacenterId = availableDatacenters.get(i++ % availableDatacenters.size());
            String datacenterName = CloudSim.getEntityName(datacenterId);

            if (!getVmsToDatacentersMap().containsKey(vm.getId())) {
                Log.printLine(CloudSim.clock() + ": " + getName() + ": " +
                        "Trying to Create VM #" + vm.getId() + " in " + datacenterName);
                sendNow(datacenterId, CloudSimTags.VM_CREATE_ACK, vm);
                numberOfVmsAllocated++;
            }
        }

        setVmsRequested(numberOfVmsAllocated);
        setVmsAcks(0);
    }


}
