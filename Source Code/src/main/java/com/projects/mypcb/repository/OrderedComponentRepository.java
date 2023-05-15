package com.projects.mypcb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.projects.mypcb.entity.OrderedComponent;

@Repository
public interface OrderedComponentRepository extends JpaRepository<OrderedComponent, Long> {
}
