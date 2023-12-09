package edu.example.scheduling;

import edu.example.service.CommentService;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;

@DisallowConcurrentExecution
public class DeleteOldCommentsJob implements Job {

    private static final Logger logger = LoggerFactory.getLogger(DeleteOldCommentsJob.class);
    private final CommentService commentService;

    private Integer ageOfComments;

    public DeleteOldCommentsJob(CommentService commentService) {
        this.commentService = commentService;
        logger.info("Created job instance");
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        commentService.deleteCommentsOlderThen(0, 0, ageOfComments);

        logger.info(MessageFormat.format("Deleted comments that are {0} days old", ageOfComments));
    }

    /**
     * This method is used by JobFactory to inject data map value {@code ageOfComments}
     * @param ageOfComments age of comments to be deleted
     */
    public void setAgeOfComments(Integer ageOfComments) {
        this.ageOfComments = ageOfComments;
    }
}
