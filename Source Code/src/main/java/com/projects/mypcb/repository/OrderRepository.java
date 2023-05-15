package com.projects.mypcb.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.projects.mypcb.entity.Order;
import com.projects.mypcb.global.OrderStatus;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findAll();
    List<Order> findByOrderStatus(OrderStatus orderStatus);
    List<Order> findByOrderByOrderDateAsc();
    List<Order> findByOrderByOrderDateDesc();
    List<Order> findByOrderStatusOrderByOrderDateDesc(OrderStatus orderStatus);

    @Query("SELECT SUM(o.totalCost), o.month, o.year FROM Order o WHERE o.orderDate BETWEEN :startDate AND :endDate GROUP BY o.month, o.year ORDER BY o.year, o.month")
    List<Object[]> findTotalRevenueByMonthBetweenDates(LocalDate startDate, LocalDate endDate);


    @Query(value = "WITH OrderedComponentsInDateRange AS (" +
    "SELECT oc.component_name, ct.name AS component_type_name, c.component_type_id, c.quantity, o.order_date " +
    "FROM ordered_component oc " +
    "JOIN orders o ON o.id = oc.order_id " +
    "JOIN component c ON c.name = oc.component_name " +
    "JOIN component_type ct ON ct.component_type_id = c.component_type_id " +
    "WHERE o.order_date BETWEEN :startDate AND :endDate " +
"), ComponentOrdersCount AS (" +
    "SELECT component_name, component_type_id, COUNT(*) AS order_count " +
    "FROM OrderedComponentsInDateRange " +
    "GROUP BY component_name, component_type_id " +
"), RankedComponents AS (" +
    "SELECT DISTINCT COC.component_name, OC.component_type_name, OC.component_type_id, COC.order_count, " +
    "DENSE_RANK() OVER (PARTITION BY OC.component_type_id ORDER BY COC.order_count DESC, OC.quantity ASC) AS rank " +
    "FROM ComponentOrdersCount COC " +
    "JOIN OrderedComponentsInDateRange OC ON COC.component_name = OC.component_name AND COC.component_type_id = OC.component_type_id " +
") " +
"SELECT component_name, component_type_name, component_type_id, order_count AS quantity_sold " +
"FROM RankedComponents " +
"WHERE rank = 1", nativeQuery = true)
List<Object[]> findMostOrderedComponentsByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);



@Query(value = "WITH OrderedComponentsInDateRange AS (" +
            "SELECT oc.component_name, ct.name AS component_type_name, c.component_type_id, c.quantity, c.price, o.order_date " +
            "FROM ordered_component oc " +
            "JOIN orders o ON o.id = oc.order_id " +
            "JOIN component c ON c.name = oc.component_name " +
            "JOIN component_type ct ON ct.component_type_id = c.component_type_id " +
            "WHERE o.order_date BETWEEN :startDate AND :endDate " +
        "), ComponentSalesCount AS (" +
            "SELECT component_name, component_type_id, COUNT(*) AS sales_count " +
            "FROM OrderedComponentsInDateRange " +
            "GROUP BY component_name, component_type_id " +
        "), RankedComponents AS (" +
            "SELECT DISTINCT CSC.component_name, OC.component_type_name, OC.component_type_id, CSC.sales_count, OC.quantity, OC.price, " +
            "DENSE_RANK() OVER (PARTITION BY OC.component_type_id ORDER BY CSC.sales_count ASC, OC.quantity * OC.price DESC) AS rank " +
            "FROM ComponentSalesCount CSC " +
            "JOIN OrderedComponentsInDateRange OC ON CSC.component_name = OC.component_name AND CSC.component_type_id = OC.component_type_id " +
        ") " +
        "SELECT component_name, component_type_name, component_type_id, sales_count, quantity * price AS inventory_volume " +
        "FROM RankedComponents " +
        "WHERE rank = 1", nativeQuery = true)
    List<Object[]> findLeastOrderedComponentsByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);


    }



