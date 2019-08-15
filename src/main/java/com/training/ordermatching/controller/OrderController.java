package com.training.ordermatching.controller;

import com.training.ordermatching.component.MatchingComponent;
import com.training.ordermatching.component.OrderLog;
import com.training.ordermatching.model.Order;
import com.training.ordermatching.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/orderMatching/order")
@Slf4j
public class OrderController {
    @Autowired
    private OrderService orderService;
    @Autowired
    private MatchingComponent matchingComponent;
    @Autowired
    private OrderLog orderLog;

    @PostMapping(value = "/submit")
    public void submitOrder(@RequestBody Order param){

        log.info("order："+param.getTraderName()+","+param.getPrice());

        Order order = new Order();
        order.setTraderName(param.getTraderName());
        order.setOrderType(param.getOrderType());
        order.setSymbol(param.getSymbol());
        order.setSide(param.getSide());
        order.setQuantity(param.getQuantity());
        order.setQuantityLeft(param.getQuantity());
        if (!param.getOrderType().equals("MKT")){
            order.setPrice(param.getPrice());
        }
        order.setStatus("pending");
        Timestamp createDate = new Timestamp(System.currentTimeMillis());
        order.setCreateDate(createDate);
        order.setLimitTime(param.getLimitTime());

        orderService.save(order);

        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String time = dateFormat.format(date);
        String fileName = time+".log";

        String message = "*********    The order is created at: "+createDate.toString()+",Order detail: "+order.toString()+System.getProperty("line.separator");
        orderLog.createFile(fileName);
        orderLog.writeFileAppend(fileName,message);

        matchingComponent.asyncMatching(order);
    }

    @PostMapping("/pendingOrders")
    public String getPendingOrders(@RequestBody List<String> symbols) {
        log.info("----------pendingOrders: symbol    "+symbols.toString());
        List<Order> orders = new ArrayList<>();
        for (String symbolName : symbols){
            orders.addAll(orderService.findPendingBuyOrderLimit20(symbolName));
            orders.addAll(orderService.findPendingSellOrderLimit20(symbolName));
        }
        JSONArray response = new JSONArray();
        for (Order order : orders) {
            JSONObject re = new JSONObject();
            re.put("symbol", order.getSymbol());
            re.put("side", order.getSide());
            re.put("quantity", order.getQuantityLeft());
            re.put("price",order.getPrice());
            re.put("create_date", order.getCreateDate());
            response.put(re);
        }

        return response.toString();
    }

    @GetMapping("/historyOrder")
    public List<Order> getHistoryOrder(@RequestParam("pageSize")Integer pageSize,@RequestParam("pageIndex")Integer pageIndex,@RequestParam("user_name")String userName){
        PageRequest pageRequest = PageRequest.of(pageIndex,pageSize,Sort.Direction.DESC,"create_date");
        Page<Order> orders = orderService.findOrdersByTraderName(userName,pageRequest);

        return orders.getContent();
    }

    @GetMapping("/historyMatch")
    public String getHistoryMatch(@RequestParam("symbol")String symbol){
        JSONArray response = new JSONArray();
        List<Order> orders = orderService.findMatchOrderBySymbol(symbol);

        for (Order order:orders){
            JSONObject re = new JSONObject();
            re.put("symbol",order.getSymbol());
            re.put("price",order.getPrice());
            re.put("date",order.getFinishDate());
            response.put(re);
        }
        return response.toString();
    }
}
