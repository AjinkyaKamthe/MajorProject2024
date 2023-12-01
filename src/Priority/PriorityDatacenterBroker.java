package Priority;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.DatacenterBroker;
import org.cloudbus.cloudsim.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class PriorityDatacenterBroker extends DatacenterBroker {

    public PriorityDatacenterBroker(String name) throws Exception {
        super(name);
    }


    public void runPriority() {
        try {
            Scanner scanner = new Scanner(System.in);
            List<Cloudlet> submissionList = new ArrayList<Cloudlet>();
            List<Task> priorityList = new ArrayList<Task>();

            int[] priorityTestVals = new int[getCloudletList().size()];
            Log.printLine("Assigning random priorities (1-10) to tasks");

            Random random = new Random();
            for (int i = 0; i < getCloudletList().size(); i++) {
                priorityTestVals[i] = random.nextInt(10);
            }

            for (int id = 0; id < getCloudletList().size(); id++) {
                priorityList.add(new Task(id, priorityTestVals[id]));
            }

            for (int i = 0; i < getCloudletList().size(); i++) {
                TimeUnit.SECONDS.sleep(2);
                priorityList.get(i).refreshTask();
            }

            priorityList.sort((Task t1, Task t2) -> Float.compare(t2.priority, t1.priority));
            priorityList.forEach((t) -> submissionList.add(cloudletList.get(t.cloudletIndex)));

            getCloudletList().clear();
            getCloudletList().addAll(submissionList);
        } catch (Exception e) {
            e.printStackTrace();
            Log.printLine("The simulation has been terminated due to an unexpected error");
        }
    }

    @Override
    public void submitCloudletList(List<? extends Cloudlet> list) {
        getCloudletList().addAll(list);
        runPriority();
    }
}
