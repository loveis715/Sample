package com.ambergarden.orderprocessor.controller;

import static com.ambergarden.orderprocessor.Constants.FIND_BY_ID_URL;
import static com.ambergarden.orderprocessor.Constants.ID_PATH_VARIABLE;
import static com.ambergarden.orderprocessor.Constants.ORDERS_URL;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ambergarden.orderprocessor.schema.beans.order.Order;
import com.ambergarden.orderprocessor.service.OrderService;

/**
 * Controller for create and retrieve order information
 */
@Controller
@RequestMapping(value = ORDERS_URL)
public class OrderController {
   @Autowired
   private OrderService orderService;

   /**
    * List all orders
    * @return a list of orders
    */
   @RequestMapping(method = RequestMethod.GET)
   @ResponseBody
   public List<Order> list() {
      return orderService.findAll();
   }

   /**
    * Create a new order
    * @param order an order with user's input
    * @return the saved order with its processing steps and states
    */
   @RequestMapping(method = RequestMethod.POST)
   @ResponseBody
   public Order create(@RequestBody Order order) {
      return orderService.create(order);
   }

   /**
    * Find order by specific id
    * @param id the id of the requested order
    * @return the order with that specific id
    */
   @RequestMapping(value = FIND_BY_ID_URL, method = RequestMethod.GET)
   @ResponseBody
   public Order findById(@PathVariable(ID_PATH_VARIABLE) int id) {
      return orderService.findById(id);
   }
}