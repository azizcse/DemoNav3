package com.tecfort.navigationtest.service

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val SheetBackground = Color(0xFFFFFFFF)
private val HandleColor = Color(0xFFCCCCCC)
private val TitleColor = Color(0xFF111111)
private val SubtitleColor = Color(0xFF444444)
private val BodyColor = Color(0xFF666666)
private val AccentGreen = Color(0xFF0A7B3E)
private val RejectRed = Color(0xFFD32F2F)
private val OnAccentWhite = Color(0xFFFFFFFF)

@Composable
fun FloatingOrderBottomSheetContent(
    offer: OrderOffer,
    onAccept: () -> Unit,
    onDismiss: () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        color = SheetBackground,
        shadowElevation = 16.dp,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 24.dp),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    modifier = Modifier
                        .width(44.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(HandleColor),
                )
            }

            Text(
                text = "New Order Offer",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TitleColor,
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = offer.restaurantName,
                fontSize = 15.sp,
                color = SubtitleColor,
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Pickup: ${offer.pickupAddress}",
                fontSize = 13.sp,
                color = BodyColor,
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Drop-off: ${offer.dropoffAddress}",
                fontSize = 13.sp,
                color = BodyColor,
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "%.1f km  •  ৳%.0f".format(offer.distanceKm, offer.payout),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = AccentGreen,
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = RejectRed,
                        contentColor = OnAccentWhite,
                    ),
                ) { Text("Reject", fontWeight = FontWeight.Bold) }

                Button(
                    onClick = onAccept,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AccentGreen,
                        contentColor = OnAccentWhite,
                    ),
                ) { Text("Accept", fontWeight = FontWeight.Bold) }
            }
        }
    }
}
