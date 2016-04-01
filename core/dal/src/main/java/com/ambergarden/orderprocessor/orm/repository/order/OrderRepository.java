package com.ambergarden.orderprocessor.orm.repository.order;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.ambergarden.orderprocessor.orm.entity.order.Order;

@Repository
public interface OrderRepository extends CrudRepository<Order, Integer> {
}