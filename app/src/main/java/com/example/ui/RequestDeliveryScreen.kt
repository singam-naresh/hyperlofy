package com.example.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Order
import com.example.data.OrderRepository
import kotlinx.coroutines.launch
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestDeliveryScreen(
    onDismiss: () -> Unit,
    onOrderPlaced: (com.example.ui.PremiumOrder) -> Unit
) {
    val ctx = LocalContext.current
    val repo = remember { OrderRepository.getInstance(ctx) }
    val session = com.example.data.SessionManager.getInstance(ctx)
    val addrRepo = com.example.data.AddressRepository.getInstance(ctx)
    val scope = rememberCoroutineScope()

    var pickup by remember { mutableStateOf("") }
    var drop by remember { mutableStateOf("") }
    var itemName by remember { mutableStateOf("") }
    var categoryIndex by remember { mutableStateOf(0) }
    var itemValueStr by remember { mutableStateOf("") }
    var specialInstructions by remember { mutableStateOf("") }
    var recipientName by remember { mutableStateOf("") }
    var recipientPhone by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }
    var savedAddresses by remember { mutableStateOf<List<com.example.data.AddressEntity>>(emptyList()) }
    var showAddrMenu by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }
    // step: 0=form, 1=summary, 2=success
    var step by remember { mutableStateOf(0) }
    var createdOrderId by remember { mutableStateOf<String?>(null) }
    var estimatedDistanceKm by remember { mutableStateOf(0.0) }
    var estimatedPrice by remember { mutableStateOf(0.0) }

    val categories = listOf("Groceries", "Medicines", "Documents", "Electronics", "Other")

    LaunchedEffect(session.currentUserId()) {
        val uid = session.currentUserId()
        if (uid != null) savedAddresses = addrRepo.getAddressesForUser(uid)
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("INSTANT PREMIUM TRANSIT", color = Color(0xFF10B981), fontSize = 11.sp)
                Text("Request Delivery", color = Color.White, fontSize = 18.sp)
            }
            IconButton(onClick = onDismiss) {
                Icon(Icons.Default.Close, contentDescription = "Close")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = pickup,
            onValueChange = { pickup = it },
            label = { Text("Pickup Address") },
            modifier = Modifier.fillMaxWidth()
        )

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            Button(onClick = { showAddrMenu = !showAddrMenu }, enabled = savedAddresses.isNotEmpty()) {
                Text("Select Saved Address")
            }
            DropdownMenu(expanded = showAddrMenu, onDismissRequest = { showAddrMenu = false }) {
                savedAddresses.forEach { a ->
                    DropdownMenuItem(text = { Text(a.title + " - " + a.city) }, onClick = {
                        pickup = a.fullAddress
                        showAddrMenu = false
                    })
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = drop,
            onValueChange = { drop = it },
            label = { Text("Drop Address") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = recipientName,
            onValueChange = { recipientName = it },
            label = { Text("Recipient Name") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = recipientPhone,
            onValueChange = { recipientPhone = it.filter { ch -> ch.isDigit() } },
            label = { Text("Recipient Phone") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = itemName,
            onValueChange = { itemName = it },
            label = { Text("Item Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = categories[categoryIndex],
                onValueChange = {},
                label = { Text("Package Type") },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = true },
                readOnly = true
            )
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                categories.forEachIndexed { idx, title ->
                    DropdownMenuItem(text = { Text(title) }, onClick = { categoryIndex = idx; expanded = false })
                }
            }
        }

        OutlinedTextField(
            value = itemValueStr,
            onValueChange = { itemValueStr = it.filter { ch -> ch.isDigit() || ch == '.' } },
            label = { Text("Item Value") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = specialInstructions,
            onValueChange = { specialInstructions = it },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (error.isNotBlank()) {
            Text(error, color = Color.Red)
        }

        Spacer(modifier = Modifier.weight(1f))

        when (step) {
            0 -> {
                Button(onClick = {
                    error = ""
                    if (pickup.isBlank()) { error = "Select pickup address"; return@Button }
                    if (drop.isBlank()) { error = "Select delivery address"; return@Button }
                    if (categoryIndex !in categories.indices) { error = "Select package type"; return@Button }
                    if (recipientName.isBlank()) { error = "Enter recipient name"; return@Button }
                    if (recipientPhone.length < 7) { error = "Enter valid recipient phone"; return@Button }
                    if (specialInstructions.isBlank()) { error = "Enter description"; return@Button }

                    val distance = 5.0 + (0..10).random()
                    val price = kotlin.math.max(50.0, distance * 12.0)
                    estimatedDistanceKm = distance
                    estimatedPrice = price
                    step = 1
                }, modifier = Modifier.fillMaxWidth()) {
                    Text("Next")
                }
            }
            1 -> {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Request Summary", fontSize = 16.sp, color = Color.White)
                        Spacer(modifier = Modifier.height(8.dp))
                        SummaryRow(label = "Pickup", value = pickup)
                        SummaryRow(label = "Delivery", value = drop)
                        SummaryRow(label = "Package Type", value = categories[categoryIndex])
                        SummaryRow(label = "Recipient", value = "$recipientName • $recipientPhone")
                        SummaryRow(label = "Description", value = specialInstructions)
                        SummaryRow(label = "Estimated Distance", value = "${String.format("%.1f", estimatedDistanceKm)} km")
                        SummaryRow(label = "Estimated Price", value = "₹${String.format("%.2f", estimatedPrice)}")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = { step = 0 }, modifier = Modifier.weight(1f)) {
                        Text("Back")
                    }
                    Button(onClick = {
                        val value = itemValueStr.toDoubleOrNull() ?: 0.0
                        val id = "HLF-${UUID.randomUUID().toString().substring(0, 8).uppercase()}"
                        val order = Order(
                            id = id,
                            pickupAddress = pickup,
                            dropAddress = drop,
                            itemName = itemName,
                            category = categories[categoryIndex],
                            itemValue = value,
                            specialInstructions = specialInstructions,
                            status = "CREATED",
                            createdAt = System.currentTimeMillis()
                        )

                        scope.launch {
                            try {
                                repo.insert(order)
                                createdOrderId = order.id
                                onOrderPlaced(
                                    PremiumOrder(
                                        id = order.id,
                                        storeName = order.pickupAddress,
                                        itemsDescription = order.itemName,
                                        status = PremiumOrderStatus.SUBMITTED,
                                        deliveryFee = estimatedPrice,
                                        distanceKm = estimatedDistanceKm,
                                        etaMinutes = 0,
                                        trackingProgress = 0.0f,
                                        otp = (1000..9999).random().toString(),
                                        dateString = "Just Now",
                                        agent = null
                                    )
                                )
                                step = 2
                            } catch (ex: Exception) {
                                error = "Failed to save order: ${ex.message}"
                            }
                        }
                    }, modifier = Modifier.weight(1f)) {
                        Text("Confirm Request")
                    }
                }
            }
            2 -> {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Request Created Successfully", fontSize = 16.sp, color = Color.White)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Order ID: ${createdOrderId ?: "-"}", color = Color.Gray)
                        Text("Estimated Price: ₹${String.format("%.2f", estimatedPrice)}", color = Color.Gray)
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(onClick = {
                                createdOrderId?.let { id ->
                                    onOrderPlaced(
                                        PremiumOrder(
                                            id = id,
                                            storeName = pickup,
                                            itemsDescription = itemName,
                                            status = PremiumOrderStatus.SUBMITTED,
                                            deliveryFee = estimatedPrice,
                                            distanceKm = estimatedDistanceKm,
                                            etaMinutes = 0,
                                            trackingProgress = 0.0f,
                                            otp = (1000..9999).random().toString(),
                                            dateString = "Just Now",
                                            agent = null
                                        )
                                    )
                                }
                                onDismiss()
                            }, modifier = Modifier.weight(1f)) {
                                Text("Track Order")
                            }
                            OutlinedButton(onClick = { onDismiss() }, modifier = Modifier.weight(1f)) {
                                Text("Back To Home")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SummaryRow(label: String, value: String) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Text(text = "$label:", color = Color.Gray, fontSize = 12.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = value, color = Color.White, fontSize = 14.sp)
    }
}
