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
    protected void processResourceCharacteristics(SimEvent ev) {

        DatacenterCharacteristics characteristics = (DatacenterCharacteristics) ev.getData();
        getDatacenterCharacteristicsList().put(characteristics.getId(), characteristics);

        if (getDatacenterCharacteristicsList().size() == getDatacenterIdsList().size()) {
            distributeRequestsForNewVmsUsingWeightedRoundRobin();
        }
    }


    @Override
    protected void submitCloudlets() {
        List<Vm> vmList = getVmsCreatedList();
        List<Cloudlet> cloudlets = getCloudletList();
        PriorityQueue<Vm> vmQueue = new PriorityQueue<>(
                Comparator.comparingDouble(vm -> vm.getTotalUtilizationOfCpu(CloudSim.clock()))
        );
        PriorityQueue<Cloudlet> cloudletQueue = new PriorityQueue<>(
                Comparator.comparingDouble(Cloudlet::getCloudletLength).reversed()
        );
        cloudletQueue.addAll(cloudlets);
        vmQueue.addAll(vmList);

        while (!cloudletQueue.isEmpty()) {
            Cloudlet cloudlet = cloudletQueue.poll();
            System.out.println(cloudlet.getCloudletLength());
            Vm leastLoadedVm = vmQueue.poll();
            bindCloudletToVm(cloudlet.getCloudletId(), leastLoadedVm.getId());
            sendNow(getVmsToDatacentersMap().get(leastLoadedVm.getId()), CloudSimTags.CLOUDLET_SUBMIT, cloudlet);
            vmQueue.add(leastLoadedVm);
        }
    }


    protected void distributeRequestsForNewVmsUsingWeightedRoundRobin() {
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
