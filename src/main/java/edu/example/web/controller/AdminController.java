package edu.example.web.controller;

import edu.example.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.hibernate.validator.constraints.Range;
import org.quartz.SchedulerException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@PreAuthorize("hasAuthority('ADMIN')")
@RequestMapping("/admin")
@Tag(name = "Admin", description = "Admin panel functionality")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @PostMapping("/commentsCleaningInterval")
    @Operation(description = "Changes time interval at which old comments are deleted")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Changes successfully"),
            @ApiResponse(responseCode = "403", description = "Insufficient rights / unauthorized")
    })
    public void changeOldCommentsDeletionInterval(@RequestParam @Valid @Range(min = 1) int seconds) throws SchedulerException {
        adminService.changeOldCommentsDeletionInterval(seconds);
    }

    @PostMapping("/ageOfCommentsToDelete")
    @Operation(description = "Changes the age of comments that are considered old and will be deleted automatically")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Changes successfully"),
            @ApiResponse(responseCode = "403", description = "Insufficient rights / unauthorized")
    })
    public void changeAgeOfCommentsToDelete(@RequestParam @Valid @Range(min = 1) int days) throws SchedulerException {
        adminService.changeAgeOfCommentsToDelete(days);
    }
}
