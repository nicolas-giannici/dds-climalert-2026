package com.climalert.repository;

import com.climalert.entity.RegistroClima;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RegistroClimaRepository extends JpaRepository<RegistroClima, Long> {

    Optional<RegistroClima> findTopByOrderByFechaConsultaDesc();
}
