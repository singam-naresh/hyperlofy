package com.example.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.util.UUID
import com.example.data.Order
import com.example.data.OrderRepository

// ==========================================
// PREMIUM CONSUMER DATA MODEL CLASS DECLARATIONS
// ==========================================

data class PremiumAgent(
    val id: String,
    val name: String,
    val rating: Float,
    val distanceKm: Double,
    val estimatedMinutes: Int,
    val vehicleType: String,
    val avatarColor: Color,
    val totalDeliveries: Int,
    val verifiedBadge: Boolean = true
)

data class PremiumOrder(
    val id: String,
    val storeName: String,
    val itemsDescription: String,
    val status: PremiumOrderStatus,
    val deliveryFee: Double,
    val distanceKm: Double,
    val etaMinutes: Int,
    val trackingProgress: Float, // 0.0f to 1.0f
    val otp: String,
    val dateString: String,
    val agent: PremiumAgent?
)

enum class PremiumOrderStatus {
    SUBMITTED,
    ALLOCATED,
    PICKUP_IN_PROGRESS,
    IN_TRANSIT,
    NEARBY,
    DELIVERED,
    CANCELLED
}

data class PremiumNotification(
    val id: String,
    val title: String,
    val body: String,
    val timeString: String,
    val isRead: Boolean = false
)

data class PackageClass(
    val title: String,
    val desc: String,
    val isAvailable: Boolean = true,
    val basePrice: Double,
    val icon: ImageVector
)

// Global simulation helper variables
val UrbanHubs = listOf("Gachibowli Area", "Jubilee Hills Central", "Madhapur Tech Zone", "Banjara Hills", "Kondapur Hub", "Hi-Tech Square")

// ==========================================
// PREMIUM ESCROW LEADERS / TRANSLUCENT CARDS
// ==========================================

@Composable
fun GlassmorphicCardPremium(
    modifier: Modifier = Modifier,
    borderColor: Color = Color(0x1CFFFFFF),
    backgroundColor: Color = Color(0x12FFFFFF),
    glowColor: Color = Color.Transparent,
    cornerRadius: Dp = 24.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    Box(
        modifier = modifier
            .drawBehind {
                if (glowColor != Color.Transparent) {
                    drawRoundRect(
                        color = glowColor,
                        cornerRadius = CornerRadius(cornerRadius.toPx(), cornerRadius.toPx()),
                        style = Stroke(width = 6.dp.toPx())
                    )
                }
            }
            .clip(RoundedCornerShape(cornerRadius))
            .background(backgroundColor)
            .border(1.dp, borderColor, RoundedCornerShape(cornerRadius))
            .padding(20.dp)
    ) {
        Column {
            content()
        }
    }
}

// ==========================================
// THE ULTRA HYPERPREMIUM CONSUMER HOME
// ==========================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenPremium(
    modifier: Modifier = Modifier,
    initialWalletBalance: Double = 1250.0,
    onNavigateBack: (() -> Unit)? = null
) {
    val coroutineContextScope = rememberCoroutineScope()
    val hapticFeedback = LocalHapticFeedback.current
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current
    
    // Member balances
    var walletBalance by remember { mutableStateOf(initialWalletBalance) }
    var selectedHubLocation by remember { mutableStateOf(UrbanHubs[0]) }
    var primeTabSelection by remember { mutableStateOf("home") }
    
    // Sheet configurations
    var showLocationSelector by remember { mutableStateOf(false) }
    var showNotificationCenter by remember { mutableStateOf(false) }
    var showProfileModal by remember { mutableStateOf(false) }
    var showBookingSheet by remember { mutableStateOf(false) }
    var showTopUpDialog by remember { mutableStateOf(false) }
    
    // Delivery fields
    var pickupAddress by remember { mutableStateOf("") }
    var dropoffAddress by remember { mutableStateOf("") }
    var packageDetails by remember { mutableStateOf("") }
    var estimatedPrice by remember { mutableStateOf(145.0) }
    var selectedCategoryIndex by remember { mutableStateOf(0) }
    var customOrderError by remember { mutableStateOf("") }
    
    // Payment topups
    var topUpAmountStr by remember { mutableStateOf("500") }
    var topUpCardNum by remember { mutableStateOf("4355 2412 9901 3254") }
    
    // Core helper agents
    val verifiedHelpers = remember {
        listOf(
            PremiumAgent("agt_1", "Rohan Sharma", 4.95f, 1.2, 8, "Eco Scooter S1", Color(0xFF0D9488), 1420),
            PremiumAgent("agt_2", "Sameer Verma", 4.88f, 2.4, 12, "Electric Moto Max", Color(0xFF2563EB), 834),
            PremiumAgent("agt_3", "Ananya Reddy", 4.98f, 0.8, 5, "Carbon Frame Bicycle", Color(0xFFD97706), 2110),
            PremiumAgent("agt_4", "Karthik Pillai", 4.79f, 3.1, 15, "Insured Cargo Box", Color(0xFF9333EA), 492),
            PremiumAgent("agt_5", "Deepak Gupta", 4.91f, 1.9, 10, "Green Cruiser Volt", Color(0xFF059669), 1120)
        )
    }

    // Active order simulation fields
    var activeOrders by remember {
        mutableStateOf<List<PremiumOrder>>(emptyList())
    }
    
    var completedOrders by remember {
        mutableStateOf<List<PremiumOrder>>(
            listOf(
                PremiumOrder(
                    id = "HPY-44910",
                    storeName = "Aromatic Herbals Dispensary",
                    itemsDescription = "Organic medical throat syrup box",
                    status = PremiumOrderStatus.DELIVERED,
                    deliveryFee = 168.0,
                    distanceKm = 4.2,
                    etaMinutes = 0,
                    trackingProgress = 1.0f,
                    otp = "6718",
                    dateString = "Yesterday, 4:15 PM",
                    agent = verifiedHelpers[1]
                ),
                PremiumOrder(
                    id = "HPY-44102",
                    storeName = "Nisarga Groceries Central",
                    itemsDescription = "Fresh basil extract and high protein sourdough bread",
                    status = PremiumOrderStatus.DELIVERED,
                    deliveryFee = 112.0,
                    distanceKm = 1.8,
                    etaMinutes = 0,
                    trackingProgress = 1.0f,
                    otp = "2943",
                    dateString = "10 Jun, 11:20 AM",
                    agent = verifiedHelpers[2]
                )
            )
        )
    }
    val orderRepository = remember { OrderRepository.getInstance(context) }

    var appNotifications by remember {
        mutableStateOf(
            listOf(
                PremiumNotification("nt_1", "Welcome to Premium Delivery", "Verified security protocol has allocated ₹2,000 transit protection cover on all active bookings.", "Just now"),
                PremiumNotification("nt_2", "System Optimization Complete", "Jubilee Hills Central hub bandwidth expanded. Courier density in your zone spiked by 44%. Expected delivery delays reduced.", "3 hrs ago"),
                PremiumNotification("nt_3", "Wallet Balance Updated", "₹500.00 topped up securely into user pre-approved ledger escrow module.", "3 days ago")
            )
        )
    }

    // Services categories
    val deliveryCategories = remember {
        listOf(
            PackageClass("Grocery", "10 min", true, 95.0, Icons.Default.ShoppingCart),
            PackageClass("Food", "25 min", true, 120.0, Icons.Default.List),
            PackageClass("Medicine", "15 min", true, 110.0, Icons.Default.Favorite),
            PackageClass("Parcel", "20 min", true, 130.0, Icons.Default.Home),
            PackageClass("Documents", "15 min", true, 150.0, Icons.Default.Send),
            PackageClass("Electronics", "20 min", true, 200.0, Icons.Default.Build),
            PackageClass("Custom Request", "15 min", true, 110.0, Icons.Default.Star)
        )
    }

    // Order Simulation Tick Loop
    LaunchedEffect(activeOrders) {
        if (activeOrders.isNotEmpty()) {
            while (true) {
                delay(3000)
                var modified = false
                val updated = activeOrders.map { order ->
                    if (order.status != PremiumOrderStatus.DELIVERED) {
                        modified = true
                        val curProgress = order.trackingProgress
                        val nextProgress = (curProgress + 0.15f).coerceAtMost(1.0f)
                        
                        val newStatus = when {
                            nextProgress >= 1.0f -> PremiumOrderStatus.DELIVERED
                            nextProgress >= 0.80f -> PremiumOrderStatus.NEARBY
                            nextProgress >= 0.50f -> PremiumOrderStatus.IN_TRANSIT
                            nextProgress >= 0.25f -> PremiumOrderStatus.PICKUP_IN_PROGRESS
                            else -> PremiumOrderStatus.ALLOCATED
                        }
                        
                        val nextEta = ((1.0f - nextProgress) * order.etaMinutes * 1.3).toInt().coerceAtLeast(1)
                        val finalEta = if (newStatus == PremiumOrderStatus.DELIVERED) 0 else nextEta
                        
                        if (newStatus != order.status) {
                            val pushTitle = when(newStatus) {
                                PremiumOrderStatus.ALLOCATED -> "Premium Courier Assigned"
                                PremiumOrderStatus.PICKUP_IN_PROGRESS -> "Courier at Pickup Station"
                                PremiumOrderStatus.IN_TRANSIT -> "Parcel Dispatched & Cruising"
                                PremiumOrderStatus.NEARBY -> "Courier is Outside Gate"
                                PremiumOrderStatus.DELIVERED -> "Verified Parcel Delivered"
                                else -> "Transit Schedule Update"
                            }
                            val pushBody = when(newStatus) {
                                PremiumOrderStatus.ALLOCATED -> "Agent ${order.agent?.name} is on the way to ${order.storeName}."
                                PremiumOrderStatus.PICKUP_IN_PROGRESS -> "Items loaded and wrapped in double-seal custom containers."
                                PremiumOrderStatus.IN_TRANSIT -> "Courier is on the fast-track lane. Live parameters secure."
                                PremiumOrderStatus.NEARBY -> "Please hand over security PIN code ${order.otp} to confirm unpack."
                                PremiumOrderStatus.DELIVERED -> "Direct payout settled safely. Transit security closed."
                                else -> "Tracking progress updating safely."
                            }
                            appNotifications = listOf(
                                PremiumNotification(UUID.randomUUID().toString(), pushTitle, pushBody, "Now")
                            ) + appNotifications
                        }
                        
                        order.copy(
                            status = newStatus,
                            trackingProgress = nextProgress,
                            etaMinutes = finalEta
                        )
                    } else {
                        order
                    }
                }
                
                if (modified) {
                    val ongoing = updated.filter { it.status != PremiumOrderStatus.DELIVERED }
                    val isDone = updated.filter { it.status == PremiumOrderStatus.DELIVERED }
                    if (isDone.isNotEmpty()) {
                        completedOrders = isDone.map { it.copy(dateString = "Just Now") } + completedOrders
                    }
                    activeOrders = ongoing
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                } else {
                    break
                }
            }
        }
    }

    // ==========================================
    // THE ULTIMATE EMERALD BLACK LAYOUT
    // ==========================================
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = PureBlack,
        bottomBar = {
            PremiumBottomBar(
                currentTab = primeTabSelection,
                onTabSelected = { tab ->
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                    primeTabSelection = tab
                }
            )
        }
    ) { innerPadding ->
        
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding())
        ) {
            
            // Dynamic Radial gradient background drawing for responsive depth
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFF032616), Color.Transparent),
                        center = Offset(size.width * 0.15f, size.height * 0.12f),
                        radius = 420.dp.toPx()
                    ),
                    radius = 420.dp.toPx()
                )
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFF021B10), Color.Transparent),
                        center = Offset(size.width * 0.85f, size.height * 0.75f),
                        radius = 460.dp.toPx()
                    ),
                    radius = 460.dp.toPx()
                )
            }

            // CORE COMPOSITIONS CONTENT SWITCHER
            AnimatedContent(
                targetState = primeTabSelection,
                transitionSpec = {
                    fadeIn(animationSpec = tween(350)) togetherWith fadeOut(animationSpec = tween(200))
                },
                label = "PremiumTabs"
            ) { stateTab ->
                when (stateTab) {
                    "home" -> {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 18.dp)
                                .testTag("premium_scroll_view"),
                            verticalArrangement = Arrangement.spacedBy(24.dp),
                            contentPadding = PaddingValues(top = 18.dp, bottom = 48.dp)
                        ) {
                            
                            // 1. Sleek Logo & Status Header
                            item {
                                PremiumTopHeader(
                                    currentLocation = selectedHubLocation,
                                    onLocationClick = { showLocationSelector = true },
                                    onNotificationsClick = { showNotificationCenter = true },
                                    onProfileClick = { showProfileModal = true },
                                    unreadNotificationsCount = appNotifications.filter { !it.isRead }.size
                                )
                            }
                            
                            // 2. Large Hero Card (Visual centerpiece, min height 340dp with glowing 3D cube)
                            item {
                                HeroCardPremium(
                                    nearbyCount = 124,
                                    etaAverage = 12,
                                    insuredScale = "₹2,000",
                                    onRequestDelivery = {
                                        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                        showBookingSheet = true
                                    }
                                )
                            }
                            
                            // 3. Search Bar
                            item {
                                SearchBarPremium(
                                    onSearchClick = {
                                        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                        showBookingSheet = true
                                    }
                                )
                            }

                            // 4. Active Transit Tracker (only if activeOrders isNotEmpty)
                            if (activeOrders.isNotEmpty()) {
                                item {
                                    val topActive = activeOrders[0]
                                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "ACTIVE TRANSIT TRACKER",
                                                style = TextStyle(
                                                    color = Color.White,
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    letterSpacing = 1.8.sp
                                                )
                                            )
                                            Box(
                                                modifier = Modifier
                                                    .background(Color(0xFFFFB200).copy(alpha = 0.15f), CircleShape)
                                                    .border(1.dp, Color(0xFFFFB200).copy(alpha = 0.4f), CircleShape)
                                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                                            ) {
                                                Text(
                                                    text = "Realtime GPS Stream",
                                                    color = Color(0xFFFFB200),
                                                    fontSize = 8.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }

                                        OrderCardPremium(
                                            order = topActive,
                                            onCancel = {
                                                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                                activeOrders = activeOrders.filter { it.id != topActive.id }
                                                appNotifications = listOf(
                                                    PremiumNotification(
                                                        UUID.randomUUID().toString(),
                                                        "Order Terminated",
                                                        "Active parcel dispatch run successfully revoked. Escrow balance released.",
                                                        "Now"
                                                    )
                                                ) + appNotifications
                                            }
                                        )

                                        // Tracking live graphic canvas
                                        TrackingCardPremium(
                                            order = topActive,
                                            progress = topActive.trackingProgress
                                        )
                                    }
                                }
                            }
                            
                            // 5. Services Grid immediately after Search Bar
                            item {
                                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                    Text(
                                        text = "FAST TRANSIT SERVICES",
                                        style = TextStyle(
                                            color = Color.White,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            letterSpacing = 1.8.sp
                                        )
                                    )
                                    ServicesGridPremium(
                                        categories = deliveryCategories,
                                        onSelect = { index ->
                                            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                            selectedCategoryIndex = index
                                            pickupAddress = ""
                                            dropoffAddress = ""
                                            packageDetails = "Deliver custom ${deliveryCategories[index].title} box"
                                            showBookingSheet = true
                                        }
                                    )
                                }
                            }
                            
                            // 6. Wallet Preview directly below Services
                            item {
                                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                    Text(
                                        text = "PREMIUM SECURED CREDIT",
                                        style = TextStyle(
                                            color = Color.White,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            letterSpacing = 1.8.sp
                                        )
                                    )
                                    WalletCardPremium(
                                        balance = walletBalance,
                                        onTopUp = {
                                            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                            showTopUpDialog = true
                                        }
                                    )
                                }
                            }

                            // 7. Recent Concluded/Historic Orders directly below Wallet Card
                            item {
                                RecentConcludedPremium(
                                    history = completedOrders
                                )
                            }
                            
                            // 8. Referral rewards card directly below Recent Orders
                            item {
                                ReferralCardPremium(
                                    code = "HYPERPREM150",
                                    onCopy = {
                                        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                        clipboardManager.setText(AnnotatedString("HYPERPREM150"))
                                        appNotifications = listOf(
                                            PremiumNotification(
                                                UUID.randomUUID().toString(),
                                                "Voucher Copied",
                                                "Premium voucher code duplicated to local clipboard safely.",
                                                "Now"
                                            )
                                        ) + appNotifications
                                    }
                                )
                            }

                            // 9. Horizontal Verified Helper agents (at the bottom)
                            item {
                                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "VERIFIED HELPERS NEARBY",
                                            style = TextStyle(
                                                color = Color.White,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                letterSpacing = 1.8.sp
                                            )
                                        )
                                        Text(
                                            text = "100% Eco Fleet",
                                            style = TextStyle(
                                                color = EmeraldGreen,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        )
                                    }
                                    
                                    LazyRow(
                                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        items(verifiedHelpers) { helper ->
                                            HelperCardPremium(agent = helper) {
                                                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                                pickupAddress = "Central Station (${selectedHubLocation})"
                                                dropoffAddress = "My Residence plot"
                                                packageDetails = "Hyper-priority courier from ${helper.name}"
                                                selectedCategoryIndex = 4
                                                showBookingSheet = true
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    
                    "history" -> {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 18.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            contentPadding = PaddingValues(top = 18.dp, bottom = 48.dp)
                        ) {
                            item {
                                Text(
                                    text = "SECURED TRANSIT ARCHIVE",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White,
                                    modifier = Modifier.padding(top = 12.dp)
                                )
                                Text(
                                    text = "Completely verified runs and cryptographically locked history",
                                    fontSize = 12.sp,
                                    color = Color.Gray,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                            }

                            if (activeOrders.isEmpty() && completedOrders.isEmpty()) {
                                item {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(240.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("No transits archived", color = Color.Gray, fontSize = 13.sp)
                                    }
                                }
                            }

                            items(activeOrders) { active ->
                                GlassmorphicCardPremium(
                                    borderColor = EmeraldGreen.copy(alpha = 0.5f),
                                    backgroundColor = Color(0x2E022C1A)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(
                                                text = "ACTIVE: ${active.storeName}",
                                                color = Color.White,
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(active.itemsDescription, color = Color.Gray, fontSize = 11.sp)
                                        }
                                        Text("₹${active.deliveryFee}", color = EmeraldGreen, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    }
                                }
                            }

                            items(completedOrders) { record ->
                                GlassmorphicCardPremium {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    imageVector = Icons.Default.CheckCircle,
                                                    contentDescription = null,
                                                    tint = EmeraldGreen,
                                                    modifier = Modifier.size(13.dp)
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    text = record.storeName,
                                                    color = Color.White,
                                                    fontSize = 13.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                            Spacer(modifier = Modifier.height(3.dp))
                                            Text(record.itemsDescription, color = Color.Gray, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                            Text(record.dateString, color = Color.DarkGray, fontSize = 9.sp)
                                        }
                                        Column(horizontalAlignment = Alignment.End) {
                                            Text("₹${record.deliveryFee}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                            Text("Verified", color = EmeraldGreen, fontSize = 9.sp, fontWeight = FontWeight.SemiBold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                    
                    "wallet" -> {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 18.dp),
                            verticalArrangement = Arrangement.spacedBy(20.dp),
                            contentPadding = PaddingValues(top = 18.dp, bottom = 48.dp)
                        ) {
                            item {
                                Text(
                                    text = "SECURED ESCROW WALLET",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White,
                                    modifier = Modifier.padding(top = 12.dp)
                                )
                                Text(
                                    text = "Pre-allocated funds cleared dynamically upon successful courier verification",
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )
                            }

                            item {
                                WalletCardPremium(
                                    balance = walletBalance,
                                    onTopUp = { showTopUpDialog = true }
                                )
                            }

                            item {
                                GlassmorphicCardPremium(
                                    borderColor = Color(0x3B10B981)
                                ) {
                                    Text(
                                        text = "TRANSIT PROTECTION GUARANTEE",
                                        color = EmeraldGreen,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.5.sp
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Your booking automatically triggers custom ₹2,000 transit risk coverages. Absolutely free, backed directly by Hyperlofy secure assets.",
                                        color = Color.Gray,
                                        fontSize = 11.sp,
                                        lineHeight = 15.sp
                                    )
                                }
                            }

                            item {
                                Text(
                                    text = "HISTORIAL CREDIT / DEBIT TRANSITS",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    letterSpacing = 1.2.sp
                                )
                            }

                            items(completedOrders) { order ->
                                GlassmorphicCardPremium {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Box(
                                                modifier = Modifier
                                                    .size(32.dp)
                                                    .background(Color(0x1F00D68F), CircleShape),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Send,
                                                    contentDescription = null,
                                                    tint = EmeraldGreen,
                                                    modifier = Modifier.size(13.dp)
                                                )
                                            }
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Column {
                                                Text("Dispatch Settlement Debit", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                                Text("To: ${order.storeName}", color = Color.Gray, fontSize = 10.sp)
                                            }
                                        }
                                        Text("- ₹${order.deliveryFee}", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // ==========================================
            // PRETTY FLOATING BOTTOM SHEETS & MODALS
            // ==========================================

            // Dynamic bottom sheets custom presentation
            AnimatedVisibility(
                visible = showBookingSheet,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.72f))
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) { showBookingSheet = false },
                    contentAlignment = Alignment.BottomCenter
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(0.85f)
                            .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                            .background(Color(0xFF060B08))
                            .border(1.dp, Color(0x3B10B981), RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                            .clickable(enabled = false) {}
                            .padding(24.dp)
                    ) {
                        RequestDeliveryScreen(onDismiss = { showBookingSheet = false }, onOrderPlaced = { preview ->
                            activeOrders = listOf(preview) + activeOrders
                        })
                    }
                }
            }

            // Location picker dialog box
            if (showLocationSelector) {
                AlertDialog(
                    onDismissRequest = { showLocationSelector = false },
                    containerColor = Color(0xFF0A0D0B),
                    modifier = Modifier.border(1.dp, Color(0x3B10B981), RoundedCornerShape(24.dp)),
                    title = {
                        Text(
                            text = "Select Urban Delivery Hub",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    text = {
                        Column {
                            Text(
                                "Courier transit and response speeds adapt dynamically inside these critical hubs.",
                                color = Color.Gray,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )
                            UrbanHubs.forEach { zone ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .clickable {
                                            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                            selectedHubLocation = zone
                                            showLocationSelector = false
                                        }
                                        .padding(vertical = 12.dp, horizontal = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.LocationOn,
                                        contentDescription = null,
                                        tint = if (zone == selectedHubLocation) EmeraldGreen else Color.Gray,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = zone,
                                        color = if (zone == selectedHubLocation) Color.White else Color.LightGray,
                                        fontSize = 13.sp,
                                        fontWeight = if (zone == selectedHubLocation) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            }
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = { showLocationSelector = false }) {
                            Text("Done", color = EmeraldGreen, fontWeight = FontWeight.Bold)
                        }
                    }
                )
            }

            // Custom Notification Center alerts modal
            if (showNotificationCenter) {
                AlertDialog(
                    onDismissRequest = { showNotificationCenter = false },
                    containerColor = Color(0xFF0C100E),
                    modifier = Modifier
                        .fillMaxWidth(0.95f)
                        .border(1.dp, Color(0x1FFFFFFF), RoundedCornerShape(24.dp)),
                    title = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Premium Notifications Center", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            IconButton(onClick = { showNotificationCenter = false }) {
                                Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                            }
                        }
                    },
                    text = {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.height(320.dp)
                        ) {
                            if (appNotifications.isEmpty()) {
                                item {
                                    Text("No alarms inside premium tray.", color = Color.Gray, fontSize = 12.sp)
                                }
                            }
                            items(appNotifications) { item ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color(0x11FFFFFF))
                                        .padding(12.dp)
                                ) {
                                    Column {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(item.title, color = EmeraldGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                            Text(item.timeString, color = Color.Gray, fontSize = 9.sp)
                                        }
                                        Spacer(modifier = Modifier.height(3.dp))
                                        Text(item.body, color = Color.LightGray, fontSize = 11.sp, lineHeight = 14.sp)
                                    }
                                }
                            }
                        }
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                appNotifications = emptyList()
                                showNotificationCenter = false
                            }
                        ) {
                            Text("Dismiss All", color = Color.Red, fontSize = 12.sp)
                        }
                    }
                )
            }

            // User Profile Dialog box representation
            if (showProfileModal) {
                AlertDialog(
                    onDismissRequest = { showProfileModal = false },
                    containerColor = Color(0xFF0F1210),
                    modifier = Modifier.border(1.dp, Color(0x2110B981), RoundedCornerShape(24.dp)),
                    title = {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .background(EmeraldGreen, CircleShape)
                                    .border(2.dp, Color.White, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("NS", color = Color.Black, fontSize = 20.sp, fontWeight = FontWeight.Black)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Naresh Singam", color = Color.White, fontSize = 17.sp, fontWeight = FontWeight.Black)
                            Text("Premium Club Elite", color = EmeraldGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    },
                    text = {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Profile ID ID", color = Color.Gray, fontSize = 11.sp)
                                Text("NS-981-PREM", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Premium Member Since", color = Color.Gray, fontSize = 11.sp)
                                Text("June 2026", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Default Store Hub", color = Color.Gray, fontSize = 11.sp)
                                Text("Corporate Sector 4, Madhapur", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = { showProfileModal = false }) {
                            Text("Close", color = Color.White)
                        }
                    }
                )
            }

            // Simulated balance top-up dialogue Box
            if (showTopUpDialog) {
                AlertDialog(
                    onDismissRequest = { showTopUpDialog = false },
                    containerColor = Color(0xFF0A0F0C),
                    modifier = Modifier.border(1.dp, Color(0x3D10B981), RoundedCornerShape(24.dp)),
                    title = {
                        Text(
                            text = "Add Prepaid Transit Funds",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    text = {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text(
                                "Escrow balances automatically guard against loss. Backed with instant safe return.",
                                color = Color.Gray,
                                fontSize = 11.sp
                            )
                            
                            OutlinedTextField(
                                value = topUpAmountStr,
                                onValueChange = { topUpAmountStr = it },
                                label = { Text("Top Up Amount (₹)", color = Color.Gray) },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = EmeraldGreen,
                                    unfocusedBorderColor = Color(0x1FFFFFFF),
                                    focusedTextColor = Color.White
                                ),
                                textStyle = TextStyle(fontWeight = FontWeight.Bold, color = Color.White),
                                modifier = Modifier.fillMaxWidth()
                            )

                            OutlinedTextField(
                                value = topUpCardNum,
                                onValueChange = { topUpCardNum = it },
                                label = { Text("Simulated Bank Card", color = Color.Gray) },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = EmeraldGreen,
                                    unfocusedBorderColor = Color(0x1FFFFFFF),
                                    focusedTextColor = Color.White
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                val amtNum = topUpAmountStr.toDoubleOrNull() ?: 500.0
                                walletBalance += amtNum
                                showTopUpDialog = false
                                appNotifications = listOf(
                                    PremiumNotification(
                                        UUID.randomUUID().toString(),
                                        "Top Up Cleared",
                                        "Credential path verified. Loaded ₹$amtNum to prepaid credit ledger.",
                                        "Now"
                                    )
                                ) + appNotifications
                                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen)
                        ) {
                            Text("Add Funds", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showTopUpDialog = false }) {
                            Text("Cancel", color = Color.White)
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun HyperlofyLogoIcon(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.size(36.dp)) {
        val w = size.width
        val h = size.height
        val strokeW = 3.dp.toPx()
        val bracketW = 7.dp.toPx()
        val bracketH = 20.dp.toPx()
        val startY = (h - bracketH) / 2
        val endY = startY + bracketH

        // Draw left bracket "["
        val leftPath = Path().apply {
            moveTo(bracketW, startY)
            lineTo(strokeW, startY)
            lineTo(strokeW, endY)
            lineTo(bracketW, endY)
        }
        drawPath(
            path = leftPath,
            color = Color.White,
            style = Stroke(width = strokeW, cap = StrokeCap.Square)
        )

        // Draw right bracket "]"
        val rightPath = Path().apply {
            moveTo(w - bracketW, startY)
            lineTo(w - strokeW, startY)
            lineTo(w - strokeW, endY)
            lineTo(w - bracketW, endY)
        }
        drawPath(
            path = rightPath,
            color = Color.White,
            style = Stroke(width = strokeW, cap = StrokeCap.Square)
        )

        // Center emerald dot
        drawCircle(
            color = EmeraldGreen,
            radius = 3.5.dp.toPx(),
            center = Offset(w / 2, h / 2)
        )
    }
}

// ==========================================
// 1. TOP HEADER COMPONENT
// ==========================================
@Composable
fun PremiumTopHeader(
    currentLocation: String,
    onLocationClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onProfileClick: () -> Unit,
    unreadNotificationsCount: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(vertical = 12.dp)
            .testTag("premium_top_header"),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            HyperlofyLogoIcon()
            
            Column(
                modifier = Modifier.clickable { onLocationClick() }
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = currentLocation,
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Text(
                    text = "Hyperactive Hub",
                    color = Color.Gray,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Notifications Bell
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0x11FFFFFF))
                    .border(1.dp, Color(0x19FFFFFF), CircleShape)
                    .clickable { onNotificationsClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Notifications,
                    contentDescription = "Notifications",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
                if (unreadNotificationsCount > 0) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .align(Alignment.TopEnd)
                            .background(EmeraldGreen, CircleShape)
                    )
                }
            }

            // User Profile
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0x1400D68F))
                    .border(1.dp, Color(0x3300D68F), CircleShape)
                    .clickable { onProfileClick() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "H",
                    color = EmeraldGreen,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// ==========================================
// 2. LARGE HERO SECTION COMPONENT (320px+)
// ==========================================
@Composable
fun Glowing3DCube(
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "cube")
    val floatOffset by infiniteTransition.animateFloat(
        initialValue = -10f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "floating"
    )
    
    val pulseGlow by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1600, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    Box(
        modifier = modifier
            .size(130.dp)
            .offset(y = floatOffset.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val wHalf = w / 2
            val h = size.height
            val cy = h / 2 - 2.dp.toPx()
            val radius = 34.dp.toPx()
            
            // Layer 1: Dark Ground Shadow below the floating cube
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color.Black.copy(alpha = 0.7f), Color.Transparent),
                    center = Offset(wHalf, cy + radius + 15.dp.toPx()),
                    radius = (40.dp.toPx() * pulseGlow)
                ),
                radius = (40.dp.toPx() * pulseGlow),
                center = Offset(wHalf, cy + radius + 15.dp.toPx())
            )

            // Layer 2: Soft radial emerald background glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0x2B00D68F), Color.Transparent),
                    center = Offset(wHalf, cy),
                    radius = (85.dp.toPx() * pulseGlow)
                ),
                radius = (85.dp.toPx() * pulseGlow),
                center = Offset(wHalf, cy)
            )

            // Layer 3: Intense inner glowing core
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFF00FF88).copy(alpha = 0.35f * pulseGlow), Color.Transparent),
                    center = Offset(wHalf, cy),
                    radius = 45.dp.toPx()
                ),
                radius = 45.dp.toPx(),
                center = Offset(wHalf, cy)
            )

            val dx = radius * 0.866f
            val dy = radius * 0.5f

            // Top faced path
            val topPath = Path().apply {
                moveTo(wHalf, cy - radius)
                lineTo(wHalf + dx, cy - dy)
                lineTo(wHalf, cy)
                lineTo(wHalf - dx, cy - dy)
                close()
            }
            // Left faced path
            val leftPath = Path().apply {
                moveTo(wHalf, cy)
                lineTo(wHalf - dx, cy - dy)
                lineTo(wHalf - dx, cy + radius - dy)
                lineTo(wHalf, cy + radius)
                close()
            }
            // Right faced path
            val rightPath = Path().apply {
                moveTo(wHalf, cy)
                lineTo(wHalf + dx, cy - dy)
                lineTo(wHalf + dx, cy + radius - dy)
                lineTo(wHalf, cy + radius)
                close()
            }

            // Draw top face with a vibrant emerald glass gradient
            drawPath(
                topPath,
                Brush.verticalGradient(
                    listOf(Color(0xFF00FFCC), Color(0xFF00D68F))
                )
            )
            // Draw left face with deep, rich metallic emerald gradient
            drawPath(
                leftPath,
                Brush.verticalGradient(
                    listOf(Color(0xFF00965E), Color(0xFF003820))
                )
            )
            // Draw right face with premium gloss dark shadow-green gradient
            drawPath(
                rightPath,
                Brush.verticalGradient(
                    listOf(Color(0xCC00D68F), Color(0xCC001F0F))
                )
            )

            // Edge and corner highlights
            val neonEdge = Color(0xFF00FFCC).copy(alpha = 0.85f)
            val whiteGloss = Color(0x80FFFFFF)
            
            // Draw razor sharp structural creases
            drawLine(whiteGloss, Offset(wHalf, cy - radius), Offset(wHalf, cy), strokeWidth = 1.5.dp.toPx())
            drawLine(whiteGloss, Offset(wHalf - dx, cy - dy), Offset(wHalf, cy), strokeWidth = 1.2.dp.toPx())
            drawLine(whiteGloss, Offset(wHalf + dx, cy - dy), Offset(wHalf, cy), strokeWidth = 1.2.dp.toPx())
            
            drawLine(neonEdge, Offset(wHalf, cy), Offset(wHalf, cy + radius), strokeWidth = 2.dp.toPx())
            drawLine(neonEdge, Offset(wHalf - dx, cy + radius - dy), Offset(wHalf, cy + radius), strokeWidth = 1.5.dp.toPx())
            drawLine(neonEdge, Offset(wHalf + dx, cy + radius - dy), Offset(wHalf, cy + radius), strokeWidth = 1.5.dp.toPx())
            
            // Outer silhouette highlights for 3D realism
            drawLine(neonEdge, Offset(wHalf - dx, cy - dy), Offset(wHalf - dx, cy + radius - dy), strokeWidth = 1.2.dp.toPx())
            drawLine(neonEdge, Offset(wHalf + dx, cy - dy), Offset(wHalf + dx, cy + radius - dy), strokeWidth = 1.2.dp.toPx())
        }
    }
}

// ==========================================
// 2. LARGE HERO SECTION COMPONENT (320px+)
// ==========================================
@Composable
fun HeroCardPremium(
    nearbyCount: Int,
    etaAverage: Int,
    insuredScale: String,
    onRequestDelivery: () -> Unit
) {
    // Outer floating frame for double glass translucent border depth
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 340.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(Color(0x06FFFFFF))
            .border(
                1.dp,
                Brush.verticalGradient(
                    colors = listOf(Color(0x19FFFFFF), Color(0x03FFFFFF))
                ),
                RoundedCornerShape(28.dp)
            )
            .drawBehind {
                // Outer soft surrounding dark-ambient glow shadow
                drawRoundRect(
                    color = Color.Black.copy(alpha = 0.5f),
                    size = size,
                    cornerRadius = CornerRadius(28.dp.toPx(), 28.dp.toPx())
                )
            }
            .padding(1.5.dp) // Outer border spacing
    ) {
        // Inner Glass Card Body
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(26.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF0F1E19), // Dark Emerald Tinted glass
                            Color(0xFF050D0A), // Deep matte gradient
                            Color(0xFF000000)  // Deep cosmic pure black bottom
                        )
                    )
                )
                .border(
                    1.dp,
                    Brush.verticalGradient(
                        colors = listOf(Color(0x4000FFCC), Color(0x0500FFCC))
                    ),
                    RoundedCornerShape(26.dp)
                )
                .drawBehind {
                    // Internal high-intensity radial green backlight
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(Color(0x2400FF88), Color.Transparent),
                            center = Offset(size.width * 0.85f, size.height * 0.15f),
                            radius = 180.dp.toPx()
                        ),
                        radius = 180.dp.toPx()
                    )
                }
                .padding(24.dp)
                .testTag("premium_hero_card")
        ) {
            Column(
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxSize()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.weight(1.2f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // High-tech Spark Badge
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier
                                .background(Color(0x2100FF88), CircleShape)
                                .border(1.dp, Color(0x3B00FF88), CircleShape)
                                .padding(horizontal = 12.dp, vertical = 5.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .background(Color(0xFF00FF88), CircleShape)
                            )
                            Text(
                                text = "HYPERLOFY PRESTIGE FLEET",
                                color = Color(0xFF00FF88),
                                style = TextStyle(
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.5.sp
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = "Get Anything\nDelivered In\nMinutes.",
                            color = Color.White,
                            style = TextStyle(
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Black,
                                lineHeight = 36.sp,
                                letterSpacing = (-0.5).sp
                            )
                        )
                        Text(
                            text = "Groceries, Food, Medicines, Electronics, Documents, Custom Request",
                            color = Color.LightGray.copy(alpha = 0.65f),
                            style = TextStyle(
                                fontSize = 12.sp,
                                lineHeight = 16.sp,
                                fontWeight = FontWeight.Normal
                            )
                        )
                    }
                    Box(
                        modifier = Modifier.weight(0.8f),
                        contentAlignment = Alignment.Center
                    ) {
                        Glowing3DCube()
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Massive Statistics Highlights Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0x0AFFFFFF))
                        .border(1.dp, Color(0x10FFFFFF), RoundedCornerShape(16.dp))
                        .padding(vertical = 12.dp, horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$nearbyCount",
                            color = Color(0xFF00FF88),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = "Helpers Nearby",
                            color = Color.Gray,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                    }
                    Box(
                        modifier = Modifier
                            .height(24.dp)
                            .width(1.dp)
                            .background(Color(0x19FFFFFF))
                    )
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$etaAverage min",
                            color = Color(0xFF00FF88),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = "Average ETA",
                            color = Color.Gray,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                    }
                    Box(
                        modifier = Modifier
                            .height(24.dp)
                            .width(1.dp)
                            .background(Color(0x19FFFFFF))
                    )
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = insuredScale,
                            color = Color(0xFF00FF88),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = "Transit Insured",
                            color = Color.Gray,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Book Action Button
                Button(
                    onClick = onRequestDelivery,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .testTag("request_delivery_large_cta"),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00D68F)),
                    shape = RoundedCornerShape(28.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Book Delivery",
                            color = Color.Black,
                            style = TextStyle(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.2.sp
                            )
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

// ==========================================
// 3. SEARCH BAR COMPONENT
// ==========================================
@Composable
fun SearchBarPremium(
    onSearchClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0x0CFFFFFF))
            .border(1.dp, Color(0x14FFFFFF), RoundedCornerShape(20.dp))
            .clickable { onSearchClick() }
            .padding(horizontal = 18.dp, vertical = 15.dp)
            .testTag("premium_search_clickable")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = EmeraldGreen,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Search for pickup stores or custom checklists...",
                    color = Color.Gray,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = null,
                tint = Color.Gray,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

// ==========================================
// 4. HELPER CARD COMPONENT
// ==========================================
@Composable
fun HelperCardPremium(
    agent: PremiumAgent,
    onSelect: () -> Unit
) {
    Box(
        modifier = Modifier
            .width(190.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(Color(0x0DFFFFFF))
            .border(1.dp, Color(0x14FFFFFF), RoundedCornerShape(22.dp))
            .clickable { onSelect() }
            .padding(16.dp)
            .testTag("helper_card_${agent.id}")
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = agent.name,
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Rating",
                        tint = Color(0xFFFFB200),
                        modifier = Modifier.size(11.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "${agent.rating}",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }

            // Specs vehicle detail
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = EmeraldGreen,
                    modifier = Modifier.size(10.dp)
                )
                Text(
                    text = agent.vehicleType,
                    color = Color.LightGray,
                    fontSize = 10.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Divider(color = Color(0x0AFFFFFF))

            // Footer status indicators
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .background(EmeraldGreen, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Available",
                        color = EmeraldGreen,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Text(
                    text = "${agent.distanceKm} km",
                    color = Color.Gray,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// ==========================================
// 5. ACTIVE ORDER CARD COMPONENT
// ==========================================
@Composable
fun OrderCardPremium(
    order: PremiumOrder,
    onCancel: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        Color(0xFF03140F),
                        Color(0xFF0A0F0D)
                    )
                )
            )
            .border(1.dp, EmeraldGreen.copy(alpha = 0.35f), RoundedCornerShape(28.dp))
            .padding(20.dp)
            .testTag("active_order_card")
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            
            // Header Row: Status Indicator and verification labels
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = null,
                        tint = EmeraldGreen,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "SECURE PARCEL ROUTE RUN",
                        color = EmeraldGreen,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.5.sp
                    )
                }
                Box(
                    modifier = Modifier
                        .background(Color(0xFF0D9488).copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                        .border(1.dp, Color(0xFF0D9488).copy(alpha = 0.35f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = order.status.name,
                        color = EmeraldGreen,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }

            // Pickup point definitions
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = order.storeName,
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = order.itemsDescription,
                    color = Color.Gray,
                    fontSize = 11.sp,
                    lineHeight = 15.sp
                )
            }

            Divider(color = Color(0x0AFFFFFF))

            // Sub-metrics (ETA, security code verification)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("ESTIMATED ETA", color = Color.Gray, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    Text(
                        text = if (order.etaMinutes > 0) "${order.etaMinutes} mins" else "ARRIVING",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("VERIFICATION PIN / OTP", color = Color.Gray, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    Text(
                        text = order.otp,
                        color = EmeraldGreen,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.2.sp
                    )
                }

                Button(
                    onClick = onCancel,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1F2937)),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.height(30.dp)
                ) {
                    Text("Cancel", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// ==========================================
// 6. LIVE TRACKING PREVIEW (Pure Canvas Map)
// ==========================================
@Composable
fun TrackingCardPremium(
    order: PremiumOrder,
    progress: Float
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(Color(0xFF030604))
            .border(1.dp, Color(0x1FFFFFFF), RoundedCornerShape(24.dp))
            .padding(16.dp)
            .testTag("tracking_canvas_card")
    ) {
        val sweepTimeAngle = rememberInfiniteTransition(label = "").animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(2400, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = ""
        )

        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            // Coordinates
            val startPoint = Offset(100f, height / 2)
            val endPoint = Offset(width - 100f, height / 2)
            val midPoint = Offset(width / 2, height / 2 - 80f)

            // Curved highway road path
            val roadPath = Path().apply {
                moveTo(startPoint.x, startPoint.y)
                quadraticTo(midPoint.x, midPoint.y, endPoint.x, endPoint.y)
            }

            drawPath(
                path = roadPath,
                color = Color.DarkGray,
                style = Stroke(width = 6f, cap = StrokeCap.Round)
            )

            // Dynamic interpolation position
            val t = progress
            val riderX = (1 - t) * (1 - t) * startPoint.x + 2 * (1 - t) * t * midPoint.x + t * t * endPoint.x
            val riderY = (1 - t) * (1 - t) * startPoint.y + 2 * (1 - t) * t * midPoint.y + t * t * endPoint.y
            val courierPosition = Offset(riderX, riderY)

            // Highlight completed route with dashed green trail
            val completedPath = Path().apply {
                moveTo(startPoint.x, startPoint.y)
                val steps = 20
                for (i in 0..(steps * t).toInt()) {
                    val ratio = i.toFloat() / steps
                    val px = (1 - ratio) * (1 - ratio) * startPoint.x + 2 * (1 - ratio) * ratio * midPoint.x + ratio * ratio * endPoint.x
                    val py = (1 - ratio) * (1 - ratio) * startPoint.y + 2 * (1 - ratio) * ratio * midPoint.y + ratio * ratio * endPoint.y
                    lineTo(px, py)
                }
            }
            
            drawPath(
                path = completedPath,
                color = EmeraldGreen,
                style = Stroke(
                    width = 8f,
                    cap = StrokeCap.Round,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 10f), 0f)
                )
            )

            // Halos
            drawCircle(color = Color.Gray, radius = 10f, center = startPoint)
            drawCircle(color = Color.White, radius = 5f, center = startPoint)

            drawCircle(color = EmeraldGreen.copy(alpha = 0.25f), radius = 24f, center = endPoint)
            drawCircle(color = EmeraldGreen, radius = 10f, center = endPoint)
            drawCircle(color = Color.Black, radius = 4f, center = endPoint)

            // Animated pulsing courier orb
            drawCircle(color = EmeraldGreen.copy(alpha = 0.35f), radius = 30f + (sweepTimeAngle.value / 360f) * 15f, center = courierPosition)
            drawCircle(color = EmeraldGreen, radius = 12f, center = courierPosition)
            drawCircle(color = Color.White, radius = 6f, center = courierPosition)
        }

        // Overlay descriptions
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "LIVESTREAM ROUTE PREVIEW",
                    color = Color.Gray,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp
                )
                Text(
                    text = order.agent?.name ?: "Assigning Driver",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Text("START BASE", color = Color.Gray, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                    Text("Pickup Station", color = Color.LightGray, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("DESTINATION BASE", color = Color.Gray, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                    Text("My Residence", color = Color.LightGray, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

// ==========================================
// 7. SERVICES GRID COMPONENT
// ==========================================
@Composable
fun ServicesGridPremium(
    categories: List<PackageClass>,
    onSelect: (Int) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(horizontal = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(categories) { category ->
            val index = categories.indexOf(category)
            ServiceCardPremium(category = category) {
                onSelect(index)
            }
        }
    }
}

@Composable
fun ServiceCardPremium(
    category: PackageClass,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    // Elegant colors based on category title
    val (iconColor, bgColor) = when (category.title.lowercase()) {
        "grocery" -> Pair(Color(0xFFE5E7EB), Color(0x1AFFFFFF))
        "food" -> Pair(Color(0xFFFF7043), Color(0x1AFF7043))
        "medicine" -> Pair(Color(0xFF29B6F6), Color(0x1A29B6F6))
        "parcels", "parcel" -> Pair(Color(0xFFFFB74D), Color(0x1AFFB74D))
        "documents" -> Pair(Color(0xFFFF8A65), Color(0x1AFF8A65))
        "electronics" -> Pair(Color(0xFF81C784), Color(0x1A81C784))
        else -> Pair(Color(0xFF00D68F), Color(0x1A00D68F))
    }

    Box(
        modifier = modifier
            .width(88.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(Color(0x0CFFFFFF))
            .border(
                1.dp,
                Brush.verticalGradient(
                    colors = listOf(Color(0x12FFFFFF), Color(0x03FFFFFF))
                ),
                RoundedCornerShape(18.dp)
            )
            .clickable { onClick() }
            .padding(vertical = 14.dp, horizontal = 10.dp)
            .testTag("service_card_${category.title.lowercase()}")
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(bgColor, CircleShape)
                    .border(1.dp, iconColor.copy(alpha = 0.2f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = category.icon,
                    contentDescription = category.title,
                    tint = iconColor,
                    modifier = Modifier.size(20.dp)
                )
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = category.title,
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = category.desc,
                    color = Color.Gray,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

// ==========================================
// 8. WALLET CARD COMPONENT
// ==========================================
@Composable
fun WalletCardPremium(
    balance: Double,
    onTopUp: () -> Unit
) {
    val decimalFormatter = remember { DecimalFormat("0.00") }
    
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0x0EFFFFFF))
                .border(
                    1.dp,
                    Brush.verticalGradient(
                        colors = listOf(Color(0x1AFFFFFF), Color(0x03FFFFFF))
                    ),
                    RoundedCornerShape(24.dp)
                )
                .padding(20.dp)
                .testTag("wallet_premium_card")
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Wallet Balance",
                        color = Color.Gray,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "₹${decimalFormatter.format(balance)}",
                        color = Color.White,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Black
                    )
                }

                Button(
                    onClick = onTopUp,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00D68F)),
                    shape = RoundedCornerShape(20.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "Add Money",
                            color = Color.Black,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }
        }

        // Side-by-Side micro cards for Rewards and Vouchers (from Screen 3)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color(0x0CFFFFFF))
                    .border(1.dp, Color(0x14FFFFFF), RoundedCornerShape(18.dp))
                    .padding(14.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(Color(0x1400D68F), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = Color(0xFF00D68F),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "Rewards",
                            color = Color.Gray,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "12 Points",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color(0x0CFFFFFF))
                    .border(1.dp, Color(0x14FFFFFF), RoundedCornerShape(18.dp))
                    .padding(14.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(Color(0x1400D68F), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ThumbUp,
                            contentDescription = null,
                            tint = Color(0xFF00D68F),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "Vouchers",
                            color = Color.Gray,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "3 Available",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }
        }
    }
}

// ==========================================
// 9. REFERRAL CARD COMPONENT
// ==========================================
@Composable
fun ReferralCardPremium(
    code: String,
    onCopy: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        Color(0xFF004D2C), // Premium deep emerald
                        Color(0xFF001F11)
                    )
                )
            )
            .border(
                1.dp,
                Brush.horizontalGradient(
                    colors = listOf(Color(0x3D00D68F), Color(0x0C00D68F))
                ),
                RoundedCornerShape(24.dp)
            )
            .padding(20.dp)
            .testTag("referral_premium_card")
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .background(Color(0xFF00E676), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier.size(20.dp)
                )
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "Refer Friends, Earn ₹150",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Claim rewards directly inside escrow transactions on successful companion transits.",
                    color = Color.LightGray,
                    fontSize = 10.sp,
                    lineHeight = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = code,
                        color = Color(0xFF00E676),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 0.5.sp
                    )
                    Box(
                        modifier = Modifier
                            .background(Color.White.copy(alpha = 0.15f), CircleShape)
                            .clickable { onCopy() }
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "Copy",
                            color = Color.White,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

// ==========================================
// 10. RECENT COMPANIONS HISTORIC RUNS
// ==========================================
@Composable
fun RecentConcludedPremium(
    history: List<PremiumOrder>
) {
    val decimalFormatter = remember { DecimalFormat("0.00") }
    
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Recent Orders",
                style = TextStyle(
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            )
            Text(
                text = "View All >",
                style = TextStyle(
                    color = Color(0xFF00D68F),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0x0CFFFFFF))
                .border(
                    1.dp,
                    Brush.verticalGradient(
                        colors = listOf(Color(0x1AFFFFFF), Color(0x03FFFFFF))
                    ),
                    RoundedCornerShape(24.dp)
                )
                .padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                if (history.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No history available",
                            color = Color.Gray,
                            fontSize = 11.sp
                        )
                    }
                } else {
                    history.forEachIndexed { idx, order ->
                        if (idx > 0) {
                            HorizontalDivider(color = Color(0x0BFFFFFF))
                        }
                        
                        val isCancelled = order.status == PremiumOrderStatus.CANCELLED
                        val icon = when {
                            isCancelled -> Icons.Default.Close
                            order.storeName.contains("Dispensary", ignoreCase = true) || order.storeName.contains("Herb", ignoreCase = true) -> Icons.Default.Favorite
                            else -> Icons.Default.ShoppingCart
                        }
                        val tintColor = if (isCancelled) Color(0xFFEF5350) else Color(0xFF00D68F)
                        val circleBg = if (isCancelled) Color(0x1AEF5350) else Color(0x1A00D68F)
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                horizontalArrangement = Arrangement.spacedBy(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .background(circleBg, CircleShape)
                                        .border(1.dp, tintColor.copy(alpha = 0.15f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = null,
                                        tint = tintColor,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                
                                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                    Text(
                                        text = "Order #${order.id}",
                                        color = Color.White,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = if (isCancelled) "Cancelled\n${order.storeName}" else "Delivered\n${order.storeName}",
                                        color = if (isCancelled) Color.Gray else Color.LightGray,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        lineHeight = 14.sp,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = order.dateString,
                                        color = Color.Gray,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Normal
                                    )
                                }
                            }
                            
                            Text(
                                text = "₹${decimalFormatter.format(order.deliveryFee)}",
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// 11. FLOATING BOTTOM NAVIGATION BAR
// ==========================================
@Composable
fun PremiumBottomBar(
    currentTab: String,
    onTabSelected: (String) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(horizontal = 18.dp, vertical = 10.dp)
            .border(1.dp, Color(0x1FFFFFFF), RoundedCornerShape(26.dp))
            .shadow(16.dp, RoundedCornerShape(26.dp)),
        color = Color(0xDD000000), // Acrylic black backdrop
        shape = RoundedCornerShape(26.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            PremiumBottomNavItem(
                icon = Icons.Default.Home,
                label = "Home",
                active = currentTab == "home",
                tag = "nav_home_premium_tab"
            ) {
                onTabSelected("home")
            }

            PremiumBottomNavItem(
                icon = Icons.Default.List,
                label = "History",
                active = currentTab == "history",
                tag = "nav_history_premium_tab"
            ) {
                onTabSelected("history")
            }

            PremiumBottomNavItem(
                icon = Icons.Default.Star,
                label = "Ledger",
                active = currentTab == "wallet",
                tag = "nav_wallet_premium_tab"
            ) {
                onTabSelected("wallet")
            }
        }
    }
}

@Composable
fun PremiumBottomNavItem(
    icon: ImageVector,
    label: String,
    active: Boolean,
    tag: String,
    onClick: () -> Unit
) {
    val alpha = animateFloatAsState(targetValue = if (active) 1.0f else 0.5f, label = "AlphaAnim")

    Column(
        modifier = Modifier
            .testTag(tag)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { onClick() }
            .padding(6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (active) EmeraldGreen else Color.White,
            modifier = Modifier
                .size(22.dp)
                .alpha(alpha.value)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            color = if (active) EmeraldGreen else Color.LightGray,
            fontSize = 10.sp,
            fontWeight = if (active) FontWeight.Black else FontWeight.Bold,
            modifier = Modifier.alpha(alpha.value)
        )
    }
}

// ==========================================
// ALIASED BLUEPRINT COMPOSABLES TARGETS
// ==========================================

@Composable
fun HeroCard(
    nearbyHelpers: Int,
    etaMins: Int,
    ordersToday: Int,
    onRequestDelivery: () -> Unit
) {
    HeroCardPremium(
        nearbyCount = nearbyHelpers,
        etaAverage = etaMins,
        insuredScale = "₹2,000",
        onRequestDelivery = onRequestDelivery
    )
}

@Composable
fun HelperCard(
    agent: PremiumAgent,
    onHelperSelected: () -> Unit
) {
    HelperCardPremium(agent = agent, onSelect = onHelperSelected)
}

@Composable
fun WalletCard(
    balance: Double,
    onTopUpClick: () -> Unit
) {
    WalletCardPremium(balance = balance, onTopUp = onTopUpClick)
}

@Composable
fun OrderCard(
    order: PremiumOrder,
    onCancelOrder: () -> Unit
) {
    OrderCardPremium(order = order, onCancel = onCancelOrder)
}

@Composable
fun TrackingCard(
    order: PremiumOrder,
    progress: Float
) {
    TrackingCardPremium(order = order, progress = progress)
}

@Composable
fun ReferralCard(
    referralCode: String,
    onShareClick: () -> Unit
) {
    ReferralCardPremium(code = referralCode, onCopy = onShareClick)
}

@Composable
fun BottomNavigation(
    currentTab: String,
    onTabSelected: (String) -> Unit
) {
    PremiumBottomBar(currentTab = currentTab, onTabSelected = onTabSelected)
}

