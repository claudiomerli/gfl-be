package it.xtreamdev.gflbe.controller;

import it.xtreamdev.gflbe.model.Notification;
import it.xtreamdev.gflbe.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.Month;
import java.util.List;

@RestController
@RequestMapping("/api/notification")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @GetMapping
    public List<Notification> find(
            @RequestParam(required = false, defaultValue = "false") Boolean loadDismissed
    ) {
        if (loadDismissed) {
            return notificationService.findAll();
        } else {
            return notificationService.findNotDismissed();
        }
    }

    @GetMapping("{id}")
    public void dismiss(@PathVariable Integer id) {
        this.notificationService.dismiss(id);
    }

    @PostMapping("email/approve-content/{idContent}")
    public void sendEmailForWaitApprovalContent(
            @PathVariable Integer idContent
    ) {
        this.notificationService.saveWaitForApprovalContentNotification(idContent, true);
    }

    @PostMapping("email/approve-content/{idProject}/{month}")
    public void sendEmailForWaitApprovalContent(
            @PathVariable Integer idProject,
            @PathVariable String month
    ) {
        this.notificationService.saveMonthClosedNotification(idProject, Month.valueOf(month), true);
    }
}
