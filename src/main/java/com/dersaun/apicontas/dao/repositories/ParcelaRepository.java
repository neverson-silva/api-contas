package com.dersaun.apicontas.dao.repositories;

import com.dersaun.apicontas.dao.models.Parcela;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParcelaRepository extends JpaRepository<Parcela, Long> {
}
