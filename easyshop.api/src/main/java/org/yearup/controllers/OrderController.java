package org.yearup.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.yearup.data.OrderDao;
import org.yearup.data.UserDao;
import org.yearup.models.Order;

import java.security.Principal;

@RestController
@RequestMapping("orders")
@CrossOrigin
@PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
public class OrderController {
    private OrderDao orderDao;
    private UserDao userDao;

    @Autowired
    public OrderController(OrderDao orderDao, UserDao userDao) {
        this.orderDao = orderDao;
        this.userDao = userDao;
    }

    // Add a POST method to create an order
    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public Order createOrder(Principal principal) {
        try {
            String username = principal.getName();
            int userId = userDao.getIdByUsername(username);
            Order order = orderDao.checkout(userId);
            if (order == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cart is empty.");
            }
            return order;
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to create order.");
        }
    }
}
