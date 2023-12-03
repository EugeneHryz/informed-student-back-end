package edu.example.scheduling;

import org.quartz.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import java.text.MessageFormat;

@Configuration
public class SchedulingConfiguration {

    public static final String DELETE_OLD_COMMENTS_JOB_NAME = "delete_old_comments_job";
    public static final String DELETE_OLD_COMMENTS_TRIGGER_NAME = "delete_old_comments_trigger";

    @Value("${scheduling.cleaning.comment.defaultAge}")
    private int ageOfCommentsToDelete;

    @Bean
    public JobDetail jobDetail() {
        return JobBuilder.newJob(DeleteOldCommentsJob.class)
                .storeDurably()
                .withIdentity(DELETE_OLD_COMMENTS_JOB_NAME)
                .withDescription(MessageFormat.format("Deletes comments older than {0} days", ageOfCommentsToDelete))
                .requestRecovery(true)
                .usingJobData("ageOfComments", Integer.toString(ageOfCommentsToDelete))
                .build();
    }

    @Bean
    public Trigger trigger() {
        return TriggerBuilder.newTrigger()
                .forJob(jobDetail())
                .withIdentity(DELETE_OLD_COMMENTS_TRIGGER_NAME)
                .withSchedule(SimpleScheduleBuilder.repeatMinutelyForever(15))
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