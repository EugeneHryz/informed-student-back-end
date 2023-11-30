package edu.example.scheduling;

import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

@Configuration
public class SchedulingConfiguration {

    @Bean
    public JobDetail jobDetail() {
        return JobBuilder.newJob(DeleteOldCommentsJob.class)
                .storeDurably()
                .withIdentity("Delete_old_comments_job")
                .withDescription("Deletes comments older then 2 years")
                .requestRecovery(true)
                .build();
    }

    @Bean
    public Trigger trigger() {
        return TriggerBuilder.newTrigger()
                .forJob(jobDetail())
                .withIdentity("JobTrigger")
                .withSchedule(SimpleScheduleBuilder.repeatMinutelyForever(15))
                .build();
    }

    @Bean
    public Scheduler scheduler(SchedulerFactoryBean factory, Trigger trigger) throws SchedulerException {
        factory.setWaitForJobsToCompleteOnShutdown(true);
        var scheduler = factory.getScheduler();
        scheduler.unscheduleJob(trigger.getKey());
        scheduler.scheduleJob(trigger);
        scheduler.start();
        return scheduler;
    }

}
