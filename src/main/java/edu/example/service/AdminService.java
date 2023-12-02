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
    private final Trigger trigger;
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
     * @param seconds Time interval in seconds
     * @throws SchedulerException If seconds value is invalid, or other scheduling error accrued
     */
    public synchronized void changeOldCommentsDeletionTime(int seconds) throws SchedulerException {
        Trigger newTrigger = TriggerBuilder.newTrigger().forJob(jobDetail)
                .withIdentity(trigger.getKey().getName())
                .withSchedule(SimpleScheduleBuilder.repeatSecondlyForever(seconds))
                .build();
        scheduler.rescheduleJob(trigger.getKey(), newTrigger);

        logger.info(String.format("Old comments deletion time changed to: %dh %dm %ds",
                seconds / 60 / 60,
                (seconds / 60) % 60,
                seconds % 60));
    }

}
