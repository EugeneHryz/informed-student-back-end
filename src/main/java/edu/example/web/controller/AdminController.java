package edu.example.web.controller;

import edu.example.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.hibernate.validator.constraints.Range;
import org.quartz.SchedulerException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
@Tag(name = "Admin", description = "Admin panel functionality")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @PostMapping("/oldCommentsPeriod")
    @Operation(description = "Changes time interval at which old comments are deleted")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Changes successfully"),
            @ApiResponse(responseCode = "403", description = "Insufficient rights / unauthorised")
    })
    public void changeOldCommentsDeletionTime(@Valid @Range(min = 1) int seconds) throws SchedulerException {
        adminService.changeOldCommentsDeletionTime(seconds);
    }

}
