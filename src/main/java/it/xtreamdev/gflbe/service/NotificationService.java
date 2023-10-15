package it.xtreamdev.gflbe.service;

import it.xtreamdev.gflbe.model.Content;
import it.xtreamdev.gflbe.model.Notification;
import it.xtreamdev.gflbe.model.Project;
import it.xtreamdev.gflbe.model.User;
import it.xtreamdev.gflbe.model.enumerations.NotificationType;
import it.xtreamdev.gflbe.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Month;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

@Service
public class NotificationService {

    @Autowired
    private UserService userService;
    @Autowired
    private ContentService contentService;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private MailService mailService;

    @Value("${tilinko.customer-base-url}")
    private String customerBaseUrl;

    @Autowired
    private NotificationRepository notificationRepository;


    public List<Notification> findNotDismissed() {
        User user = userService.userInfo();
        return this.notificationRepository.findByUserAndDismissedFalse(user);
    }

    public List<Notification> findAll() {
        User user = userService.userInfo();
        return this.notificationRepository.findByUser(user);
    }

    public void dismiss(Integer id) {
        this.notificationRepository.dismiss(id);
    }

    ///////////////////////////
    public void saveWaitForApprovalContentNotification(Integer idContent, boolean sendEmail) {
        Content content = this.contentService.findById(idContent);

        this.notificationRepository.save(
                Notification
                        .builder()
                        .type(NotificationType.WAIT_FOR_APPROVAL)
                        .route(customerBaseUrl + "/tools/contents/" + content.getId())
                        .description("Contenuto in attesa di approvazione")
                        .user(content.getProjectCommission().getProject().getCustomer())
                        .build()
        );

        if (sendEmail) {
            this.mailService.sendWaitForApproval(content);
        }
    }

    public void saveMonthClosedNotification(Integer idProject, Month month, boolean sendEmail) {
        Project project = this.projectService.findById(idProject);

        this.notificationRepository.save(
                Notification
                        .builder()
                        .type(NotificationType.MONTH_CLOSED)
                        .route(customerBaseUrl + "/tools/projects?projectId=" + idProject) //TODO
                        .description(String.format("È stato chiuso il mese di %s un progetto", month.getDisplayName(TextStyle.FULL, Locale.ITALIAN)))
                        .user(project.getCustomer())
                        .build()
        );

        if (sendEmail) {
            this.mailService.sendClosedMonth(project, month);
        }
    }

}
