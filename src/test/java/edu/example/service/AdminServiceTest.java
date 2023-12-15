package edu.example.service;

import edu.example.TestContext;
import edu.example.scheduling.SchedulingConfiguration;
import org.junit.jupiter.api.Test;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AdminServiceTest extends TestContext {

    @Autowired
    AdminService adminService;

    @Autowired
    Scheduler scheduler;
    @Autowired
    JobDetail jobDetail;

    @Test
    void changeOldCommentsDeletionInterval() throws SchedulerException {
        // when
        adminService.changeOldCommentsDeletionInterval(100);

        // then
        var triggers = scheduler.getTriggersOfJob(jobDetail.getKey());
        assertEquals(1, triggers.size());
        var trigger = triggers.get(0);
        var previous = trigger.getPreviousFireTime();
        var next = trigger.getNextFireTime();
        assertEquals(100, (next.getTime() - previous.getTime()) / 1000);
    }

    @Test
    void changeAgeOfCommentsToDelete() throws SchedulerException {
        // when
        adminService.changeAgeOfCommentsToDelete(7);

        // then
        var jobDetail = scheduler.getJobDetail(JobKey.jobKey(SchedulingConfiguration.DELETE_OLD_COMMENTS_JOB_NAME));
        assertEquals(7, Integer.valueOf((String) jobDetail.getJobDataMap().get("ageOfComments")));
    }

}
