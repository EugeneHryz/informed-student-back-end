package edu.example.scheduling;

import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import java.text.MessageFormat;

@Configuration
public class SchedulingConfiguration {

    public static final String DELETE_OLD_COMMENTS_JOB_NAME = "delete_old_comments_job";
    public static final String DELETE_OLD_COMMENTS_TRIGGER_NAME = "delete_old_comments_trigger";

    public static final String OLD_COMMENTS_DELETION_AGE_KEY = "ageOfComments";

    // in days (2 years)
    private static final int INITIAL_AGE_OF_COMMENTS_TO_DELETE = 730;
    // in seconds (1 day)
    private static final int INITIAL_INTERVAL_OF_COMMENTS_DELETION = 86400;

    @Bean
    public JobDetail jobDetail() {
        return JobBuilder.newJob(DeleteOldCommentsJob.class)
                .storeDurably()
                .withIdentity(DELETE_OLD_COMMENTS_JOB_NAME)
                .withDescription(MessageFormat.format("Deletes comments older than {0} days", INITIAL_AGE_OF_COMMENTS_TO_DELETE))
                .requestRecovery(true)
                .usingJobData(OLD_COMMENTS_DELETION_AGE_KEY, Integer.toString(INITIAL_AGE_OF_COMMENTS_TO_DELETE))
                .build();
    }

    @Bean
    public Trigger trigger() {
        return TriggerBuilder.newTrigger()
                .forJob(jobDetail())
                .withIdentity(DELETE_OLD_COMMENTS_TRIGGER_NAME)
                .withSchedule(SimpleScheduleBuilder.repeatSecondlyForever(INITIAL_INTERVAL_OF_COMMENTS_DELETION))
                .build();
    }

    @Bean
    public Scheduler scheduler(SchedulerFactoryBean factory, Trigger trigger) throws SchedulerException {
        factory.setWaitForJobsToCompleteOnShutdown(true);
        var scheduler = factory.getScheduler();
        if (!scheduler.checkExists(trigger.getKey()))
            scheduler.scheduleJob(trigger);
        scheduler.start();
        return scheduler;
    }
}