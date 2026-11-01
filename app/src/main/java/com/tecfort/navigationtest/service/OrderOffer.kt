package com.tecfort.navigationtest.service

data class OrderOffer(
    val id: String,
    val restaurantName: String,
    val pickupAddress: String,
    val dropoffAddress: String,
    val distanceKm: Double,
    val payout: Double,
)
