package edu.example.service;

import edu.example.model.Role;
import edu.example.scheduling.DeleteOldCommentsJob;
import lombok.RequiredArgsConstructor;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;

import static edu.example.scheduling.SchedulingConfiguration.DELETE_OLD_COMMENTS_JOB_NAME;
import static edu.example.scheduling.SchedulingConfiguration.OLD_COMMENTS_DELETION_AGE_KEY;

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
     * @throws SchedulerException If seconds value is invalid, or other scheduling error occurred
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

    /**
     * Changes the age of comments that are considered old and will be deleted.
     * This method creates new job definition object with new 'ageOfComments' param
     * and replaces existing job definition with the same name.
     * @param newAgeValue age of comments to delete (in days)
     * @throws SchedulerException If there is a scheduler error
     */
    public synchronized void changeAgeOfCommentsToDelete(int newAgeValue) throws SchedulerException {
        JobDetail deleteCommentsJobDetail = JobBuilder.newJob(DeleteOldCommentsJob.class)
                .storeDurably()
                .withIdentity(DELETE_OLD_COMMENTS_JOB_NAME)
                .withDescription(MessageFormat.format("Deletes comments older than {0} days", newAgeValue))
                .requestRecovery(true)
                .usingJobData(OLD_COMMENTS_DELETION_AGE_KEY, Integer.toString(newAgeValue))
                .build();
        scheduler.addJob(deleteCommentsJobDetail, true);
    }

    public List<Role> getAllRoles() {
        return Arrays.asList(Role.values());
    }
}
