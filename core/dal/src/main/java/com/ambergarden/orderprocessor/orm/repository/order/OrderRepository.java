package com.ambergarden.orderprocessor.orm.repository.order;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.ambergarden.orderprocessor.orm.entity.order.Order;
import com.ambergarden.orderprocessor.orm.entity.order.OrderStatus;

/**
 * Repository for performing CRUD operations on orders
 */
@Repository
public interface OrderRepository extends CrudRepository<Order, Integer> {
   List<Order> findAllByProcessingNode(String processingNode);

   List<Order> findAllByOrderStatus(OrderStatus orderStatus);

   @Query("SELECT uo FROM Order uo WHERE lastUpdateTime < ?1 AND orderStatus = ?2")
   List<Order> findAllByLastUpdateTimeAndOrderStatus(Date lastUpdateTime, OrderStatus orderStatus);
}