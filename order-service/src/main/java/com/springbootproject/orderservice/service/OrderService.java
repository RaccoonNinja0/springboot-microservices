package com.springbootproject.orderservice.service;

import com.springbootproject.orderservice.dto.InventoryResponse;
import com.springbootproject.orderservice.dto.OrderLineItemsDto;
import com.springbootproject.orderservice.dto.OrderRequest;
import com.springbootproject.orderservice.model.Order;
import com.springbootproject.orderservice.model.OrderLineItems;
import com.springbootproject.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final WebClient webClient;
    public void placeOrder(OrderRequest orderRequest) throws IllegalAccessException {
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());
        List<OrderLineItems> orderLineItems = orderRequest.getOrderLineItemsDtoList()
                .stream()
                .map(orderLineItemsDto -> mapToDto(orderLineItemsDto))
                .toList();
        order.setOrderLineItemsList(orderLineItems);

        List<String> skuCodes = order.getOrderLineItemsList().stream().map(orderLineItem ->
                        orderLineItem
                                .getSkuCode()
                                .toString())
                                .toList();
        //communicate with inventory service using webClient to check product availability
        //append skucodes using uriBuilder.queryParam
        //the query will return InventoryResponse array in which every response has the isInStock boolean for each item.
        InventoryResponse[] stockResults = webClient.get().uri("http://localhost:8082/api/inventory",
                        uriBuilder -> uriBuilder.queryParam("skuCodes", skuCodes).build())
                .retrieve()
                .bodyToMono(InventoryResponse[].class)
                .block();

        boolean allProductInStock = Arrays.stream(stockResults).allMatch(inventoryResponse -> inventoryResponse.isInStock());
        if(allProductInStock){
            orderRepository.save(order);
        } else {
            throw new IllegalAccessException("Product is out of stock");
        }
    }

    private OrderLineItems mapToDto(OrderLineItemsDto orderLineItemsDto) {
        OrderLineItems orderLineItems = new OrderLineItems();
        orderLineItems.setSkuCode(orderLineItemsDto.getSkuCode());
        orderLineItems.setQuantity(orderLineItemsDto.getQuantity());
        orderLineItems.setPrice(orderLineItemsDto.getPrice());
        return orderLineItems;
    }
}
