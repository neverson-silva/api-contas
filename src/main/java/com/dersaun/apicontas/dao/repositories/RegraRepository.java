package com.dersaun.apicontas.dao.repositories;

import com.dersaun.apicontas.dao.models.Regra;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegraRepository extends JpaRepository<Regra, Long> {
    Regra findByAuthority(String authority);
}