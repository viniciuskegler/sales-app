package com.viniciuskegler.salesapp.order.dto;

public record OrderNotificationDTO(Long orderId, String status, String message) {}
