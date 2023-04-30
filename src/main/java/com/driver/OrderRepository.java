package com.driver;

import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class OrderRepository {

    Map<String, Order> orderMap = new HashMap<>(); // orderId, Order
    Map<String, DeliveryPartner> deliveryPartnerMap = new HashMap<>(); // partnerId, Partner

    Map<String, List<String>> partnerOrderMap = new HashMap<>(); // partnerId, List<orderId>

    Set<String> unassignedOrdersSet = new HashSet<>(); // orderId unassigned to delivery partners
    public String addOrder(Order order) {

        String key = order.getId();
        orderMap.put(key, order);
        unassignedOrdersSet.add(key);

        return "New order added successfully";
    }
    public String addPartner(String partnerId) {

        DeliveryPartner deliveryPartner = new DeliveryPartner(partnerId);
        deliveryPartnerMap.put(partnerId, deliveryPartner);

        return "New delivery partner added successfully";
    }


    public String addOrderPartnerPair(String orderId, String partnerId) {

        List<String> orders = new ArrayList<>();

        if (partnerOrderMap.containsKey(partnerId)) {
            orders = partnerOrderMap.get(partnerId);
        }

        orders.add(orderId);
        // the order is assigned to delivery partner
        // so we have to delete it from the unassigned order hashset
        unassignedOrdersSet.remove(orderId);
        partnerOrderMap.put(partnerId, orders);

        return "New order-partner pair added successfully";
    }


    public Order getOrderById(String orderId) {

        if (orderMap.containsKey(orderId)) {
            return orderMap.get(orderId);
        }
        return null;
    }

    public DeliveryPartner getPartnerById(String partnerId) {

        if (deliveryPartnerMap.containsKey(partnerId)) {
            return deliveryPartnerMap.get(partnerId);
        }
        return null;
    }


    public Integer getOrderCountByPartnerId(String partnerId) {

        // count no of orders in partnerOrderMap
        if (partnerOrderMap.containsKey(partnerId)) {
            return partnerOrderMap.get(partnerId).size();
        }
        return 0;
    }

    public List<String> getOrdersByPartnerId(String partnerId) {

        if (partnerOrderMap.containsKey(partnerId)) {
            return partnerOrderMap.get(partnerId);
        }
        return new ArrayList<>();
    }

    public List<String> getAllOrders() {

        List<String> orders = new ArrayList<>();

        for (String orderId : orderMap.keySet()) {
            orders.add(orderId);
        }
        return orders;
    }

    public Integer getCountOfUnassignedOrders() {

        return unassignedOrdersSet.size();
    }

    public Integer getOrdersLeftAfterGivenTimeByPartnerId(String time, String partnerId) {

        int timeInInteger = simpleTime(time);

        int count = 0;

        List<String> orders = getOrdersByPartnerId(partnerId);

        for (String orderId : orders) {

            Order order = orderMap.get(orderId);

            if (order.getDeliveryTime() > timeInInteger) {
                count++;
            }

        }
        return count;
    }

    public int simpleTime(String deliveryTime) {

        //deliveryTime  = HH*60 + MM
        int hours = Integer.parseInt(deliveryTime.substring(0, 2));
        int mins = Integer.parseInt(deliveryTime.substring(3));

        int time = hours * 60 + mins;
        return time;
    }

    public String getLastDeliveryTimeByPartnerId(String partnerId) {

        List<String> orders = getOrdersByPartnerId(partnerId);
        int lastOrderTime = 0;

        for (String orderId : orders) {

            Order order = orderMap.get(orderId);

            if (order.getDeliveryTime() > lastOrderTime) {
                lastOrderTime = order.getDeliveryTime();
            }
        }

        int hours = lastOrderTime / 60;
        int mins = lastOrderTime - (hours * 60);

        String time = "";

        if (hours < 10) {
            time = "0" + hours;
        }
        return time + ":" + mins;
    }

    public void deletePartnerById(String partnerId) {

        // to delete partner from data
        // 1. remove him from partnerMap
        // 2. shift his all assigned orders to unassigned set
        // 3. delete him form partnerOrderMap

        deliveryPartnerMap.remove(partnerId);

        List<String> orders = partnerOrderMap.get(partnerId);

        for (String orderId : orders) {
            unassignedOrdersSet.add(orderId);
        }

        partnerOrderMap.remove(partnerId);
    }
    public void deleteOrderById(String orderId) {

        // delete order from ordersMap
        // and from assigned partner map too

        orderMap.remove(orderId);

        for (Map.Entry<String, List<String>> entry : partnerOrderMap.entrySet()) {

            String deliverId = entry.getKey();
            List<String> orders = entry.getValue();

            if (orders.contains(orderId)) {
                orders.remove(orderId);
                partnerOrderMap.put(deliverId, orders);
            }
        }

    }
}
