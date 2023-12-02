package edu.example.service;

import edu.example.scheduling.DeleteOldCommentsJob;
import lombok.RequiredArgsConstructor;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;

import static edu.example.scheduling.SchedulingConfiguration.DELETE_OLD_COMMENTS_JOB_NAME;

@RequiredArgsConstructor
@Service
public class AdminService {

    private static final Logger logger = LoggerFactory.getLogger(AdminService.class);

    private final JobDetail jobDetail;
    private final Trigger trigger;
    private final Scheduler scheduler;

    /**
     * Changes time interval at which old comments are deleted
     * @param seconds Time interval in seconds
     * @throws SchedulerException If seconds value is invalid, or other scheduling error accrued
     */
    public synchronized void changeOldCommentsDeletionInterval(int seconds) throws SchedulerException {
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

    public synchronized void changeAgeOfCommentsToDelete(int newAgeValue) throws SchedulerException {
        JobDetail deleteCommentsJobDetail = JobBuilder.newJob(DeleteOldCommentsJob.class)
                .storeDurably()
                .withIdentity(DELETE_OLD_COMMENTS_JOB_NAME)
                .withDescription(MessageFormat.format("Deletes comments older than {0} days", newAgeValue))
                .requestRecovery(true)
                .usingJobData("ageOfComments", Integer.toString(newAgeValue))
                .build();
        scheduler.addJob(deleteCommentsJobDetail, true);
    }

}
