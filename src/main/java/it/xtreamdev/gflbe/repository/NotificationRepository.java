package it.xtreamdev.gflbe.repository;

import it.xtreamdev.gflbe.model.Notification;
import it.xtreamdev.gflbe.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Integer> {
    List<Notification> findByUserAndDismissedFalseOrderByCreatedDateDesc(User user);

    List<Notification> findByUserOrderByCreatedDateDesc(User user);

    @Modifying
    @Query("update Notification set dismissed = true where id = :id")
    @Transactional
    void dismiss(@Param("id") Integer id);
}
