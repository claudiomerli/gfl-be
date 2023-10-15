package it.xtreamdev.gflbe.repository;

import it.xtreamdev.gflbe.model.Notification;
import it.xtreamdev.gflbe.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Integer> {
    List<Notification> findByUserAndDismissedFalse(User user);

    List<Notification> findByUser(User user);

    @Modifying
    @Query("update Notification set dismissed = true where id = id")
    @Transactional
    void dismiss(Integer id);
}
