package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Order
import com.example.data.OrderRepository
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.GlassBorder
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailsScreen(
    orderId: String,
    onBack: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val repository = remember { OrderRepository.getInstance(context) }
    val scope = rememberCoroutineScope()
    var order by remember { mutableStateOf<Order?>(null) }
    var loading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val timelineStages = listOf("CREATED", "ASSIGNED", "PICKED_UP", "IN_TRANSIT", "DELIVERED")
    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()) }
    val currencyFormat = remember { DecimalFormat("0.00") }

    LaunchedEffect(orderId) {
        loading = true
        errorMessage = null
        try {
            order = repository.getById(orderId)
        } catch (ex: Exception) {
            errorMessage = ex.message
        }
        loading = false
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF050505))
            .padding(18.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Order Details",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = EmeraldGreen.copy(alpha = 0.5f),
                glow = true
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text("Premium Information", color = EmeraldGreen, fontSize = 12.sp, fontWeight = FontWeight.Black)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "View secure order metadata, status timeline, and delivery details for your Hyperlofy order.",
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            if (loading) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = EmeraldGreen)
                }
                return@Column
            }

            if (errorMessage != null) {
                Text(text = errorMessage ?: "Unable to load order.", color = Color.Red, fontSize = 13.sp)
                return@Column
            }

            val currentOrder = order
            if (currentOrder == null) {
                Text(text = "Order not found.", color = Color.Gray, fontSize = 13.sp)
                return@Column
            }

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    OrderDetailRow(label = "Order ID", value = currentOrder.id)
                    OrderDetailRow(label = "Pickup Address", value = currentOrder.pickupAddress)
                    OrderDetailRow(label = "Drop Address", value = currentOrder.dropAddress)
                    OrderDetailRow(label = "Item Name", value = currentOrder.itemName)
                    OrderDetailRow(label = "Category", value = currentOrder.category)
                    OrderDetailRow(label = "Item Value", value = "₹${currencyFormat.format(currentOrder.itemValue)}")
                    OrderDetailRow(label = "Special Instructions", value = currentOrder.specialInstructions.ifBlank { "None" })
                    OrderDetailRow(label = "Status", value = currentOrder.status)
                    OrderDetailRow(label = "Created", value = dateFormat.format(Date(currentOrder.createdAt)))
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(text = "Status Timeline", color = Color.Gray, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))
            val activeIndex = timelineStages.indexOf(currentOrder.status).coerceAtLeast(0)
            timelineStages.forEachIndexed { index, stage ->
                TimelineStep(
                    title = stage,
                    isActive = index <= activeIndex,
                    isCurrent = index == activeIndex
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            val nextStatus = when (currentOrder.status) {
                "CREATED" -> "ASSIGNED"
                "ASSIGNED" -> "PICKED_UP"
                "PICKED_UP" -> "IN_TRANSIT"
                "IN_TRANSIT" -> "DELIVERED"
                else -> null
            }

            Button(
                onClick = {
                    nextStatus?.let { status ->
                        scope.launch {
                            val updatedOrder = currentOrder.copy(status = status)
                            repository.update(updatedOrder)
                            order = updatedOrder
                        }
                    }
                },
                enabled = nextStatus != null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen)
            ) {
                Text(
                    text = nextStatus?.let { "Simulate Next Status" } ?: "Final Status Reached",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun OrderDetailRow(label: String, value: String) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
        Text(text = label, color = Color.Gray, fontSize = 11.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = value, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun TimelineStep(title: String, isActive: Boolean, isCurrent: Boolean) {
    val circleColor = if (isActive) EmeraldGreen else Color(0xFF374151)
    val labelColor = if (isCurrent) Color.White else Color.Gray

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(14.dp)
                .background(circleColor, shape = androidx.compose.foundation.shape.CircleShape)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(text = title.replace('_', ' '), color = labelColor, fontSize = 12.sp, fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal)
    }
}
