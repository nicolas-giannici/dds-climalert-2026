package com.climalert.repository;

import com.climalert.entity.AlertNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface AlertNotificationRepository extends JpaRepository<AlertNotification, Long> {

    long countByFechaEnvioAfter(LocalDateTime fecha);

    Optional<AlertNotification> findTopByOrderByFechaEnvioDesc();
}
