package com.nova.groupxercise.DBObjects;

import org.joda.time.DateTime;


public class WalkingPlanGoalDBObject {
    public String plan;
    public int progress;
    public int todays_step_goal;
    public long start_time;
    public long last_walk_time;
    public long last_time_step_goal_reset;
    public long increment;

    public WalkingPlanGoalDBObject( String plan, int todays_step_goal, int increment ) {
        this.plan = plan;
        this.todays_step_goal = todays_step_goal;
        this.increment = increment;
        this.progress = 0;
        this.last_walk_time = 0;
        this.start_time = DateTime.now().getMillis();
        this.last_time_step_goal_reset = 0;
    }
}
