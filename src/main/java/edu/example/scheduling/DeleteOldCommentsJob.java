package edu.example.scheduling;

import edu.example.service.CommentService;
import org.jetbrains.annotations.NotNull;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;

@Component
@DisallowConcurrentExecution
public class DeleteOldCommentsJob extends QuartzJobBean {

    private static final Logger logger = LoggerFactory.getLogger(DeleteOldCommentsJob.class);
    private final CommentService commentService;

    private Integer ageOfComments;

    @Autowired
    public DeleteOldCommentsJob(CommentService commentService) {
        this.commentService = commentService;
    }

    @Override
    protected void executeInternal(@NotNull JobExecutionContext context) throws JobExecutionException {
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
