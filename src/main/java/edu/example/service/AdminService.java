package edu.example.service;

import edu.example.Application;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class AdminService {

    private final JobDetail jobDetail;
    private Trigger trigger;
    private final Scheduler scheduler;
    private final Logger logger;

    public AdminService(JobDetail jobDetail, Trigger trigger, Scheduler scheduler) {
        this.jobDetail = jobDetail;
        this.trigger = trigger;
        this.scheduler = scheduler;
        logger = LoggerFactory.getLogger(Application.class);
    }

    /**
     * Changes time interval at which old comments are deleted
     */
    public synchronized void changeOldCommentsDeletionTime(int hours, int minutes, int seconds) throws SchedulerException {
        Trigger newTrigger = TriggerBuilder.newTrigger().forJob(jobDetail)
                .withIdentity("SomeJobTrigger")
                .withSchedule(SimpleScheduleBuilder.repeatSecondlyForever(
                        (int) (TimeUnit.HOURS.toSeconds(hours)
                                + TimeUnit.MINUTES.toSeconds(minutes)
                                + TimeUnit.SECONDS.toSeconds(seconds))
                )).build();

        scheduler.unscheduleJob(trigger.getKey());
        scheduler.deleteJob(jobDetail.getKey());
        scheduler.scheduleJob(jobDetail, newTrigger);
        trigger = newTrigger;

        scheduler.start();
        logger.info(String.format("Old comments deletion time changed to: %dh %dm %ds",
                hours, minutes, seconds));
    }

}