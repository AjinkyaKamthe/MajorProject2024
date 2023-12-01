package Priority;

class Task {
    int cloudletIndex;
    int priorityLevel;			// Assigned a value from 1 to 10 (not enforced yet)
    float priority;				// The priority value calculated using priorityLevel and the taskTime
    int taskStartTime;			// When the task started (in ms)
    int taskTime;				// How long the task has been waiting

    // Initialises a task object, sets passed values to object properties
    Task(int index, int p_level){
        this.cloudletIndex = index;			// sets the cloudlet index
        this.priorityLevel = p_level;		// sets the priority level

        this.taskStartTime = (int) System.currentTimeMillis();	// sets the current time (initial task time)
        this.priority = this.priorityLevel * 100;				// doesn't use taskTime as task has just been initialised
    }

    // Finds the time the task has been waiting and recalculates its priority value
    public void refreshTask() {
        // retrieves time waiting
        this.taskTime = (int) (System.currentTimeMillis() - this.taskStartTime);
        // Equation below defines how priority escalates with time
        this.priority = this.priorityLevel * 100 + ((float) (this.taskTime * this.priorityLevel) /1000);
    }

    // Used to nicely format output header
    public void printHeader() {
        System.out.printf("%-17s%-17s%-17s%-17s\n",
                "Cloudlet Index", "Priority Level", "Priority Value", "Time Elapsed");
    }

    // Nicely formats task output
    public void printTask() {
        System.out.printf("%-17d%-17d%-17.2f% -17d\n",
                this.cloudletIndex, this.priorityLevel, this.priority, this.taskTime);
    }
}

