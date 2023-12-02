package edu.example.scheduling;

import edu.example.Application;
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

@Component
@DisallowConcurrentExecution
public class DeleteOldCommentsJob extends QuartzJobBean {

    private final CommentService commentService;
    private final Logger logger;

    @Autowired
    public DeleteOldCommentsJob(CommentService commentService) {
        this.commentService = commentService;
        logger = LoggerFactory.getLogger(Application.class);
    }

    @Override
    protected void executeInternal(@NotNull JobExecutionContext context) throws JobExecutionException {
        commentService.deleteCommentsOlderThen(2, 0, 0);
        logger.info("Deleted old comments");
    }
}
