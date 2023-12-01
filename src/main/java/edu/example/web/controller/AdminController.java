package edu.example.web.controller;

import edu.example.dto.admin.OldCommentsDeletionTimeDto;
import edu.example.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.quartz.SchedulerException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @PostMapping
    @Operation(description = "Changes time interval at which old comments are deleted")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Changes successfully"),
            @ApiResponse(responseCode = "403", description = "Insufficient rights / unauthorised")
    })
    public void changeOldCommentsDeletionTime(@RequestBody @Valid OldCommentsDeletionTimeDto time) throws SchedulerException {
        adminService.changeOldCommentsDeletionTime(time.getHours(),
                time.getMinutes(),
                time.getSeconds());
    }

}
