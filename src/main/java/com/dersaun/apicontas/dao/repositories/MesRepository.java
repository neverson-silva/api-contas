package com.dersaun.apicontas.dao.repositories;

import com.dersaun.apicontas.dao.models.Mes;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MesRepository extends JpaRepository<Mes, Long> {

    Mes findByAtualIsTrue();
}
