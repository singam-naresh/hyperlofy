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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import com.example.data.SessionManager
import com.example.data.UserEntity
import com.example.data.UserRepository
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.util.UUID

// ==========================================
// CENTRALIZED SIMULATION STATE & REGISTRIES
// ==========================================
enum class PortalMode {
    CUSTOMER, HELPER, AUDITOR
}

enum class CustomerTab {
    HOME, ORDERS, TRACK, WALLET, CHAT, PROFILE
}

enum class AuthScreen {
    LOGIN, REGISTER, OTP, APP
}

data class OrderItem(val id: String, val store: String, val items: String, val fee: Double, val distance: Double, val zone: String, var status: String, val otp: String, var etaMinutes: Int, var agentAssigned: String? = null)

data class WalletTx(val id: String, val description: String, val amount: Double, val isCredit: Boolean, val timestamp: String)

data class ChatMsg(val id: String, val sender: String, val role: String, val content: String, var moderation: String = "PASSED", val time: String)

data class LiveAgent(val name: String, val phone: String, val vehicle: String, val rating: Double, val distanceKm: Double, val totalJobs: Int)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HyperlofyAppContainer() {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val sessionManager = remember { SessionManager.getInstance(context) }
    val userRepository = remember { UserRepository.getInstance(context) }
    var activeAuthScreen by rememberSaveable { mutableStateOf(AuthScreen.LOGIN) }
    var pendingUserId by rememberSaveable { mutableStateOf<String?>(null) }
    var loggedInUser by remember { mutableStateOf<UserEntity?>(null) }

    LaunchedEffect(context) {
        if (sessionManager.isLoggedIn()) {
            val userId = sessionManager.currentUserId()
            if (userId != null) {
                val user = userRepository.getById(userId)
                if (user != null && user.isVerified) {
                    loggedInUser = user
                    activeAuthScreen = AuthScreen.APP
                } else {
                    sessionManager.logout()
                    activeAuthScreen = AuthScreen.LOGIN
                }
            } else {
                activeAuthScreen = AuthScreen.LOGIN
            }
        } else {
            activeAuthScreen = AuthScreen.LOGIN
        }
    }

    // Portal & Tab state
    var currentPortal by remember { mutableStateOf(PortalMode.CUSTOMER) }
    var currentCustTab by remember { mutableStateOf(CustomerTab.HOME) }
    
    // Core Simulation States
    var walletBalance by remember { mutableStateOf(450.0) }
    val walletTransactions = remember {
        mutableStateListOf(
            WalletTx("TX-101", "Initial Wallet Allocation", 200.0, true, "12:15 PM"),
            WalletTx("TX-102", "Simulated Order Payment", 50.0, false, "01:10 PM"),
            WalletTx("TX-103", "Referral Inviter Payout Bonus", 300.0, true, "02:45 PM")
        )
    }

    // Agent onboarding compliance inputs
    var agentOnboardingLastName by remember { mutableStateOf("Singh") }
    var agentOnboardingFirstName by remember { mutableStateOf("Rajesh") }
    var agentOnboardingPhone by remember { mutableStateOf("9428292831") }
    var agentOnboardingPAN by remember { mutableStateOf("BNKPS1029F") }
    var agentOnboardingAadhaar by remember { mutableStateOf("3829 4829 1029") }
    var agentVehicleNo by remember { mutableStateOf("AP-26-Y-9293") }
    var agentOnboardingStatus by remember { mutableStateOf("PENDING") } // PENDING, APPROVED, REJECTED, SUSPENDED
    val verificationAuditLogs = remember {
        mutableStateListOf(
            "Agent Rajesh Singh loaded profile information to SQLite. Status set: PENDING.",
            "Identity documents queued for moderator background verification."
        )
    }
    var agentAvailabilityToggle by remember { mutableStateOf(true) }

    // Active simulated orders list
    val simulatedOrders = remember {
        mutableStateListOf(
            OrderItem("ORD-829", "Apollo Pharmacy", "Buy insulin injections", 45.0, 1.8, "Tirupati", "DELIVERED", "482810", 0, "Rajesh Singh"),
            OrderItem("ORD-902", "Vaikunta Food Plaza", "North Indian Meal Combo", 65.0, 3.2, "Tirupati", "COMPLETED", "291038", 0, "Rajesh Singh")
        )
    }

    // Active order simulation helpers
    var selectedOrderForTracking by remember { mutableStateOf<OrderItem?>(null) }
    
    // Chat System message simulator
    val activeChatMessages = remember {
        mutableStateListOf(
            ChatMsg("MSG-01", "Aravind", "CUSTOMER", "Hello Rajesh, please collect the invoice at Apollo counter.", "PASSED", "12:56 PM"),
            ChatMsg("MSG-02", "Rajesh", "AGENT", "Sure, I have arrived. Security is checking coupon bindings.", "PASSED", "12:57 PM")
        )
    }
    var chatMessageInput by remember { mutableStateOf("") }
    var chatSpeakerRole by remember { mutableStateOf("CUSTOMER") } // CUSTOMER or AGENT
    var isTypingSimulatorActive by remember { mutableStateOf(false) }

    // Multiplier/Auditor settings
    var dynamicBaseChargeMultiplier by remember { mutableStateOf(1.2) }
    var referralRewardValue by remember { mutableStateOf(100.0) }
    var fraudMitigationRiskThreshold by remember { mutableStateOf("MEDIUM") }
    var manualAuditsToggleState by remember { mutableStateOf(true) }
    
    val adminAuditTrails = remember {
        mutableStateListOf(
            "System: Base charge pricing multiplier adjusted dynamically to 1.2x.",
            "Auditor: Verified Rajesh compliance details successfully."
        )
    }

    // Simulated verified agents cards registry
    val verifiedAgentsRegistry = remember {
        listOf(
            LiveAgent("John Doe", "+91 93282 38290", "Motorcycle (KA-03-A281)", 4.9, 0.6, 142),
            LiveAgent("Rajesh Singh", "+91 94282 92831", "Electric Scooter (AP-26-Y-9293)", 4.8, 1.2, 58),
            LiveAgent("Preeti Desai", "+91 88472 09210", "Bicycle (Verified Helper)", 4.9, 2.5, 96),
            LiveAgent("Karthik Raja", "+91 98201 10293", "Motorcycle (TN-10-K-2943)", 4.7, 3.4, 218)
        )
    }

    // Real-Time Analytics Trend states
    var dailyLiveOrdersCount by remember { mutableStateOf(14) }
    var dailyGrossRevenueAmt by remember { mutableStateOf(1280.0) }
    var totalPlatformSuccessRate by remember { mutableStateOf(98.6) }
    
    // Custom Razorpay modal dialog sandbox
    var showRazorpayGatewayModal by remember { mutableStateOf(false) }
    var razorpayLoadAmount by remember { mutableStateOf("") }

    // Tracking Simulator animation loop trigger
    var trackingProgress by remember { mutableFloatStateOf(0f) }
    var simulatedCurrentSpeed by remember { mutableStateOf("32 km/h") }
    var currentGeofenceFeed by remember { mutableStateOf("Agent entered the Tirupati Hub geofence boundary.") }
    
    LaunchedEffect(key1 = simulatedOrders.size) {
        // Automatically start route progress loop if any order is active in tracking state
        while(true) {
            val activeTrack = simulatedOrders.firstOrNull { it.status in listOf("PAYMENT_SUCCESS", "ASSIGNED", "PICKED_AT_STORE", "OUT_FOR_DELIVERY") }
            if (activeTrack != null) {
                trackingProgress += 0.05f
                if (trackingProgress >= 1f) {
                    trackingProgress = 0f
                    // Step the order's state machine forward
                    val currentStatus = activeTrack.status
                    val nextStatus = when(currentStatus) {
                        "PAYMENT_SUCCESS" -> "ASSIGNED"
                        "ASSIGNED" -> {
                            currentGeofenceFeed = "Rider checked in at '${activeTrack.store}' geofence."
                            "PICKED_AT_STORE"
                        }
                        "PICKED_AT_STORE" -> "OUT_FOR_DELIVERY"
                        "OUT_FOR_DELIVERY" -> {
                            currentGeofenceFeed = "Rider reached destination coordinate: Delivered!"
                            // Increment stats
                            dailyLiveOrdersCount += 1
                            dailyGrossRevenueAmt += activeTrack.fee
                            coroutineScope.launch {
                                // Add transaction log
                                walletTransactions.add(0, WalletTx("TX-" + UUID.randomUUID().toString().substring(0,4).uppercase(), "Earnings payout for delivery - " + activeTrack.id, activeTrack.fee * 0.8, true, "Now"))
                            }
                            "DELIVERED"
                        }
                        else -> "DELIVERED"
                    }
                    activeTrack.status = nextStatus
                }
                
                // Shake up current speed randomly for visual polish
                simulatedCurrentSpeed = "${(24..45).random()} km/h"
            }
            delay(1500)
        }
    }

    if (activeAuthScreen != AuthScreen.APP) {
        when (activeAuthScreen) {
            AuthScreen.LOGIN -> {
                LoginScreen(
                    onLoginSuccess = { user ->
                        loggedInUser = user
                        sessionManager.login(user.id, user.email)
                        activeAuthScreen = AuthScreen.APP
                    },
                    onCreateAccount = { activeAuthScreen = AuthScreen.REGISTER },
                    onForgotPassword = {
                        activeAuthScreen = AuthScreen.LOGIN
                    },
                    onUnverifiedAccount = { userId ->
                        pendingUserId = userId
                        activeAuthScreen = AuthScreen.OTP
                    }
                )
            }
            AuthScreen.REGISTER -> {
                RegisterScreen(
                    onRegistered = { userId ->
                        pendingUserId = userId
                        activeAuthScreen = AuthScreen.OTP
                    },
                    onBackToLogin = { activeAuthScreen = AuthScreen.LOGIN }
                )
            }
            AuthScreen.OTP -> {
                val userId = pendingUserId
                if (userId != null) {
                    OtpVerificationScreen(
                        userId = userId,
                        onVerified = { verifiedUser ->
                            loggedInUser = verifiedUser
                            sessionManager.login(verifiedUser.id, verifiedUser.email)
                            activeAuthScreen = AuthScreen.APP
                        },
                        onBackToLogin = { activeAuthScreen = AuthScreen.LOGIN }
                    )
                } else {
                    LoginScreen(
                        onLoginSuccess = { user ->
                            loggedInUser = user
                            sessionManager.login(user.id, user.email)
                            activeAuthScreen = AuthScreen.APP
                        },
                        onCreateAccount = { activeAuthScreen = AuthScreen.REGISTER },
                        onForgotPassword = { activeAuthScreen = AuthScreen.LOGIN },
                        onUnverifiedAccount = { userId ->
                            pendingUserId = userId
                            activeAuthScreen = AuthScreen.OTP
                        }
                    )
                }
            }
            else -> {
                LoginScreen(
                    onLoginSuccess = { user ->
                        loggedInUser = user
                        sessionManager.login(user.id, user.email)
                        activeAuthScreen = AuthScreen.APP
                    },
                    onCreateAccount = { activeAuthScreen = AuthScreen.REGISTER },
                    onForgotPassword = { activeAuthScreen = AuthScreen.LOGIN },
                    onUnverifiedAccount = { userId ->
                        pendingUserId = userId
                        activeAuthScreen = AuthScreen.OTP
                    }
                )
            }
        }
    } else {
        // Main App Shell scaffold
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = DarkCanvas
        ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF030303),
                            Color(0xFF0C1410), // Velvet emerald background bottom glow
                            Color(0xFF000000)
                        )
                    )
                )
        ) {
            // Background subtle network grid line markings (Apple-polish design styling)
            Canvas(modifier = Modifier.fillMaxSize()) {
                val stepX = 60.dp.toPx()
                val stepY = 60.dp.toPx()
                var currentX = 0f
                while(currentX < size.width) {
                    drawLine(
                        color = Color(0x0610B981),
                        start = Offset(currentX, 0f),
                        end = Offset(currentX, size.height),
                        strokeWidth = 1f
                    )
                    currentX += stepX
                }
                var currentY = 0f
                while(currentY < size.height) {
                    drawLine(
                        color = Color(0x0610B981),
                        start = Offset(0f, currentY),
                        end = Offset(size.width, currentY),
                        strokeWidth = 1f
                    )
                    currentY += stepY
                }
            }

            // Render selected view based on ACTIVE Portal Role Mode
            AnimatedContent(
                targetState = currentPortal,
                transitionSpec = {
                    fadeIn(animationSpec = tween(220)) togetherWith fadeOut(animationSpec = tween(220))
                },
                label = "portal_content"
            ) { targetPortal ->
                when(targetPortal) {
                    PortalMode.CUSTOMER -> {
                        CustomerScreenLayout(
                            currentTab = currentCustTab,
                            onTabSelected = { currentCustTab = it },
                            currentUser = loggedInUser,
                            onLogout = {
                                loggedInUser = null
                                sessionManager.logout()
                                activeAuthScreen = AuthScreen.LOGIN
                            },
                            walletBalance = walletBalance,
                            walletHist = walletTransactions,
                            simOrders = simulatedOrders,
                            chatMsgs = activeChatMessages,
                            chatInput = chatMessageInput,
                            onChatInputChange = { chatMessageInput = it },
                            verifiedAgents = verifiedAgentsRegistry,
                            onAddFundsClick = { showRazorpayGatewayModal = true },
                            onPlaceCustomOrder = { store, item, distance, zoneName ->
                                val feeCalc = 30.0 + (distance * 12.0) * dynamicBaseChargeMultiplier
                                val calculatedFee = Math.round(feeCalc * 100.0) / 100.0
                                
                                if (walletBalance < calculatedFee) {
                                    // Insufficient funds alert simulated
                                    false
                                } else {
                                    walletBalance -= calculatedFee
                                    val newOrd = OrderItem(
                                        id = "ORD-" + (100..999).random(),
                                        store = store,
                                        items = item,
                                        fee = calculatedFee,
                                        distance = distance,
                                        zone = zoneName,
                                        status = "PAYMENT_SUCCESS",
                                        otp = (100000..999999).random().toString(),
                                        etaMinutes = (12..25).random(),
                                        agentAssigned = "Rajesh Singh"
                                    )
                                    simulatedOrders.add(0, newOrd)
                                    walletTransactions.add(0, WalletTx(
                                        id = "TX-" + (100..999).random(),
                                        description = "Escrow hold for order: ${newOrd.id}",
                                        amount = calculatedFee,
                                        isCredit = false,
                                        timestamp = "Now"
                                    ))
                                    currentCustTab = CustomerTab.ORDERS
                                    true
                                }
                            },
                            trackingVal = trackingProgress,
                            liveSpeed = simulatedCurrentSpeed,
                            geofenceFeed = currentGeofenceFeed,
                            currentPortal = currentPortal,
                            onPortalSelected = { currentPortal = it }
                        )
                    }
                    PortalMode.HELPER -> {
                        HelperScreenLayout(
                            firstName = agentOnboardingFirstName,
                            lastName = agentOnboardingLastName,
                            phone = agentOnboardingPhone,
                            pan = agentOnboardingPAN,
                            aadhaar = agentOnboardingAadhaar,
                            vehicle = agentVehicleNo,
                            onFirstNameChange = { agentOnboardingFirstName = it },
                            onLastNameChange = { agentOnboardingLastName = it },
                            onPhoneChange = { agentOnboardingPhone = it },
                            onPanChange = { agentOnboardingPAN = it },
                            onAadhaarChange = { agentOnboardingAadhaar = it },
                            onVehicleChange = { agentVehicleNo = it },
                            complianceStatus = agentOnboardingStatus,
                            auditLogs = verificationAuditLogs,
                            onInitiateSubmission = {
                                agentOnboardingStatus = "PENDING"
                                verificationAuditLogs.add(0, "Reregistered: Updated documentation fields submitted. Re-queuing verification.")
                            },
                            onApproveOnboardingSelf = {
                                agentOnboardingStatus = "APPROVED"
                                verificationAuditLogs.add(0, "Administrator: Successfully approved PAPERS. Rajesh is now live.")
                            },
                            onToggleAvailability = {
                                agentAvailabilityToggle = !agentAvailabilityToggle
                            },
                            isAvailable = agentAvailabilityToggle,
                            simOrders = simulatedOrders,
                            onChatAsAgent = { text ->
                                activeChatMessages.add(ChatMsg(
                                    "MSG-" + (100..999).random(),
                                    "Rajesh Singh",
                                    "AGENT",
                                    text,
                                    "PASSED",
                                    "Now"
                                ))
                            }
                        )
                    }
                    PortalMode.AUDITOR -> {
                        AuditorAdminScreenLayout(
                            kpiOrders = dailyLiveOrdersCount,
                            kpiRev = dailyGrossRevenueAmt,
                            kpiSuccess = totalPlatformSuccessRate,
                            settingsMultiplier = dynamicBaseChargeMultiplier,
                            onMultiplierChange = { dynamicBaseChargeMultiplier = it },
                            referralVal = referralRewardValue,
                            onReferralValChange = { referralRewardValue = it },
                            fraudScore = fraudMitigationRiskThreshold,
                            onFraudScoreChange = { fraudMitigationRiskThreshold = it },
                            manualAudits = manualAuditsToggleState,
                            onManualAuditsToggle = { manualAuditsToggleState = it },
                            auditLogs = adminAuditTrails,
                            onTriggerAuditLogAdd = { adminAuditTrails.add(0, it) },
                            activeOrdersRegistry = simulatedOrders,
                            onAdminApproveAgent = {
                                agentOnboardingStatus = "APPROVED"
                                verificationAuditLogs.add(0, "Sys Auditor Action: Profile APPROVED for delivery operations.")
                            },
                            onAdminRejectAgent = {
                                agentOnboardingStatus = "REJECTED"
                                verificationAuditLogs.add(0, "Sys Auditor Action: Profile REJECTED due to document mismatch.")
                            },
                            onAdminSuspendAgent = {
                                agentOnboardingStatus = "SUSPENDED"
                                verificationAuditLogs.add(0, "Sys Auditor Action: Rajesh Singh SUSPENDED due to safety policy breach.")
                            },
                            currentAgentStatus = agentOnboardingStatus
                        )
                    }
                }
            }

            // Razorpay payment integration secure modal simulator
            if (showRazorpayGatewayModal) {
                RazorpaySecurityModalSimulator(
                    amount = razorpayLoadAmount,
                    onAmountChange = { razorpayLoadAmount = it },
                    onDismiss = { showRazorpayGatewayModal = false },
                    onPaymentSuccess = { addAmt ->
                        walletBalance += addAmt
                        walletTransactions.add(0, WalletTx(
                            id = "PAY-" + (100..999).random(),
                            description = "Razorpay API Top-Up (Signature Verified)",
                            amount = addAmt,
                            isCredit = true,
                            timestamp = "Now"
                        ))
                        showRazorpayGatewayModal = false
                        razorpayLoadAmount = ""
                    }
                )
            }
        }
    }
}
}

// Bottom Navigation Individual Portal Selector
@Composable
fun PortalTabItem(
    icon: ImageVector,
    label: String,
    active: Boolean,
    accentColor: Color,
    testTagStr: String,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .testTag(testTagStr)
            .clickable(
                onClick = onClick,
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            )
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(if (active) Color(0x1A10B981) else Color.Transparent)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (active) accentColor else Color(0xFF6B7280),
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = if (active) FontWeight.Bold else FontWeight.Medium,
            color = if (active) Color.White else Color(0xFF6B7280)
        )
    }
}

// Custom Glassmorphic visual card wrapper
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color(0x0CFFFFFF),
    borderColor: Color = GlassBorder,
    glow: Boolean = false,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(backgroundColor)
            .border(
                1.dp,
                brush = Brush.verticalGradient(
                    colors = listOf(borderColor, Color(0x02FFFFFF))
                ),
                shape = RoundedCornerShape(18.dp)
            )
            .drawBehind {
                if (glow) {
                    drawRoundRect(
                        color = Color(0x1210B981),
                        size = Size(size.width, size.height),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(18.dp.toPx()),
                        style = Stroke(width = 4.dp.toPx())
                    )
                }
            }
            .padding(16.dp)
    ) {
        content()
    }
}

// ==========================================
// PORTAL SCREEN 1: THE CUSTOMER APPLICATION
// ==========================================
@Composable
fun CustomerScreenLayout(
    currentTab: CustomerTab,
    onTabSelected: (CustomerTab) -> Unit,
    currentUser: UserEntity?,
    onLogout: () -> Unit,
    walletBalance: Double,
    walletHist: List<WalletTx>,
    simOrders: List<OrderItem>,
    chatMsgs: MutableList<ChatMsg>,
    chatInput: String,
    onChatInputChange: (String) -> Unit,
    verifiedAgents: List<LiveAgent>,
    onAddFundsClick: () -> Unit,
    onPlaceCustomOrder: (String, String, Double, String) -> Boolean,
    trackingVal: Float,
    liveSpeed: String,
    geofenceFeed: String,
    currentPortal: PortalMode,
    onPortalSelected: (PortalMode) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var showBookingSheet by remember { mutableStateOf(false) }
    
    // Booking sheet fields
    var bookingStore by remember { mutableStateOf("") }
    var bookingItem by remember { mutableStateOf("") }
    var bookingDistance by remember { mutableStateOf("2.5") }
    var bookingZone by remember { mutableStateOf("Indiranagar Zone") }
    var bookingError by remember { mutableStateOf("") }

    // Active overlay sheets for Live Chat and Detailed Tracking
    var activeChatOverlayOrder by remember { mutableStateOf<OrderItem?>(null) }
    var activeTrackOverlayOrder by remember { mutableStateOf<OrderItem?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF050505))
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // SECTION 1: Top Navigation (Header)
            CustomerTopHeader(
                location = "Indiranagar, Bangalore",
                onLocationClick = {},
                onNotificationClick = {},
                onProfileClick = { onTabSelected(CustomerTab.PROFILE) }
            )

            // Switchable View Container
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(bottom = 80.dp) // Leave room for floating bottom nav
            ) {
                when (currentTab) {
                    CustomerTab.HOME -> {
                        CustomerHomeView(
                            walletBalance = walletBalance,
                            onAddFundsClick = onAddFundsClick,
                            simOrders = simOrders,
                            verifiedAgents = verifiedAgents,
                            onPlaceCustomOrder = onPlaceCustomOrder,
                            onSelectTab = onTabSelected,
                            onRequestDeliveryClick = { showBookingSheet = true },
                            trackingProgress = trackingVal,
                            speedFeed = liveSpeed
                        )
                    }

                    CustomerTab.ORDERS -> {
                        CustomerOrdersView(
                            onTabSelected = onTabSelected
                        )
                    }

                    CustomerTab.TRACK -> {
                        CustomerTrackingView(
                            activeOrders = simOrders,
                            trackingProgress = trackingVal,
                            speedFeed = liveSpeed,
                            geofenceText = geofenceFeed
                        )
                    }

                    CustomerTab.WALLET -> {
                        CustomerWalletLedgerView(
                            balance = walletBalance,
                            history = walletHist,
                            onAddFundsClick = onAddFundsClick
                        )
                    }

                    CustomerTab.CHAT -> {
                        CustomerChatView(
                            messages = chatMsgs,
                            inputText = chatInput,
                            onInputTextChange = onChatInputChange,
                            isAgent = false
                        )
                    }

                    CustomerTab.PROFILE -> {
                        CustomerProfileDashboard(
                            user = currentUser,
                            onLogout = onLogout
                        )
                    }
                }
            }
        }

        // FLOATING BOTTOM GLASS NAVIGATION
        FloatingGlassBottomNav(
            currentTab = currentTab,
            onTabSelected = onTabSelected,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(horizontal = 18.dp, vertical = 12.dp)
        )

        // OVERLAY 1: Delivery request form
        if (showBookingSheet) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xCC000000))
                    .clickable { showBookingSheet = false },
                contentAlignment = Alignment.BottomCenter
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                        .background(Color(0xFF0F0F12))
                        .border(1.dp, Color(0x1F00D68F), RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                        .clickable(enabled = false) {}
                        .padding(24.dp)
                ) {
                    RequestDeliveryScreen(
                        onDismiss = { showBookingSheet = false },
                        onOrderPlaced = { _ -> showBookingSheet = false }
                    )
                }
            }
        }

        // OVERLAY 2: Chat Overlay
        activeChatOverlayOrder?.let { order ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xE0000000))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .statusBarsPadding()
                        .navigationBarsPadding()
                ) {
                    // Chat header
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF1E293B)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Person, contentDescription = null, tint = Color.White)
                            }
                            Column {
                                Text("Chat with Rider", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                Text("Assigned helper for Order ${order.id}", color = Color(0x99FFFFFF), fontSize = 11.sp)
                            }
                        }
                        IconButton(onClick = { activeChatOverlayOrder = null }) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                        }
                    }
                    
                    HorizontalDivider(color = Color(0x14FFFFFF))

                    // Message lists
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 10.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(chatMsgs) { msg ->
                            val isMe = msg.sender == "YOU"
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(
                                            topStart = 16.dp, 
                                            topEnd = 16.dp, 
                                            bottomStart = if (isMe) 16.dp else 4.dp, 
                                            bottomEnd = if (isMe) 4.dp else 16.dp
                                        ))
                                        .background(if (isMe) Color(0xFF00D68F) else Color(0x1AFFFFFF))
                                        .padding(12.dp)
                                ) {
                                    Column {
                                        Text(
                                            text = msg.content,
                                            color = if (isMe) Color.Black else Color.White,
                                            fontSize = 13.sp
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = msg.time,
                                            color = if (isMe) Color(0x99000000) else Color(0x66FFFFFF),
                                            fontSize = 9.sp,
                                            textAlign = TextAlign.End
                                        )
                                    }
                                }
                            }
                        }
                    }

                    HorizontalDivider(color = Color(0x14FFFFFF))

                    // Chat Input Bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = chatInput,
                            onValueChange = onChatInputChange,
                            placeholder = { Text("Write safe message...", color = Color.Gray) },
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF00D68F),
                                unfocusedBorderColor = Color(0x1CFFFFFF),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )
                        
                        IconButton(
                            onClick = {
                                if (chatInput.isNotBlank()) {
                                    chatMsgs.add(ChatMsg(
                                        id = UUID.randomUUID().toString(),
                                        sender = "YOU",
                                        role = "CUSTOMER",
                                        content = chatInput,
                                        time = "Now"
                                    ))
                                    onChatInputChange("")
                                }
                            },
                            colors = IconButtonDefaults.iconButtonColors(containerColor = Color(0xFF00D68F)),
                            modifier = Modifier.size(50.dp)
                        ) {
                            Icon(Icons.Default.Send, contentDescription = "Send", tint = Color.Black)
                        }
                    }
                }
            }
        }

        // OVERLAY 3: Detailed Live Tracking Sheet (Map view override)
        activeTrackOverlayOrder?.let { order ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xEE000000))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .statusBarsPadding()
                        .navigationBarsPadding()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Full Route Tracking", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            Text("Realtime Courier Dispatch Monitor • ${order.id}", color = Color(0x99FFFFFF), fontSize = 12.sp)
                        }
                        IconButton(onClick = { activeTrackOverlayOrder = null }) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                        }
                    }

                    HorizontalDivider(color = Color(0x14FFFFFF))

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        MiniMapPreview(
                            order = order,
                            trackingProgress = trackingVal,
                            speedFeed = liveSpeed
                        )
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp)
                            .clip(RoundedCornerShape(24.dp))
                            .background(Color(0x0AFFFFFF))
                            .border(1.dp, Color(0x14FFFFFF), RoundedCornerShape(24.dp))
                            .padding(16.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text("Tracking Logs & Geofence Feed", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF00D68F))
                                        .align(Alignment.CenterVertically)
                                )
                                Text(
                                    text = geofenceFeed,
                                    color = Color(0xFF00D68F),
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            Text(
                                text = "Secure Escrow Hold verified by Hyperlofy Compliance board. Courier PIN validated at recipient gate.",
                                color = Color(0x66FFFFFF),
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MiniMapPreview(
    order: OrderItem,
    trackingProgress: Float,
    speedFeed: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(240.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(Color(0x0CFFFFFF))
            .border(1.dp, Color(0x14FFFFFF), RoundedCornerShape(18.dp))
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                color = Color(0x0B10B981),
                radius = size.width / 3,
                center = Offset(size.width / 2, size.height / 2)
            )
            
            val originX = size.width * 0.2f
            val originY = size.height * 0.7f
            drawCircle(
                color = Color.White,
                radius = 8.dp.toPx(),
                center = Offset(originX, originY)
            )
            
            val destX = size.width * 0.8f
            val destY = size.height * 0.2f
            drawCircle(
                color = Color(0xFF10B981),
                radius = 8.dp.toPx(),
                center = Offset(destX, destY)
            )
            
            drawLine(
                color = Color.DarkGray,
                start = Offset(originX, originY),
                end = Offset(destX, destY),
                strokeWidth = 3f,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
            )
            
            val currentRiderX = originX + (destX - originX) * trackingProgress
            val currentRiderY = originY + (destY - originY) * trackingProgress
            
            drawCircle(
                color = Color(0xFF10B981),
                radius = 12.dp.toPx(),
                center = Offset(currentRiderX, currentRiderY)
            )
            
            drawCircle(
                color = Color.White,
                radius = 4.dp.toPx(),
                center = Offset(currentRiderX, currentRiderY)
            )
        }
        
        Box(modifier = Modifier.align(Alignment.BottomStart).background(Color(0xBA000000), RoundedCornerShape(8.dp)).padding(6.dp)) {
            Text(text = "Telemetry: 13.629° N, 79.419° E", color = Color.White, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
        }

        Box(modifier = Modifier.align(Alignment.TopEnd).background(Color(0xBA10B981), RoundedCornerShape(8.dp)).padding(6.dp)) {
            Text(text = "SPEED: $speedFeed", color = Color.Black, fontSize = 9.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
        }
    }
}

@Composable
fun FloatingGlassBottomNav(
    currentTab: CustomerTab,
    onTabSelected: (CustomerTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp)
            .border(1.dp, Color(0x21FFFFFF), RoundedCornerShape(20.dp)),
        color = Color(0xBE0F0F12),
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val tabs = listOf(
                CustomerTab.HOME to Icons.Default.Home,
                CustomerTab.ORDERS to Icons.Default.List,
                CustomerTab.TRACK to Icons.Default.LocationOn,
                CustomerTab.WALLET to Icons.Default.Star,
                CustomerTab.CHAT to Icons.Default.Send,
                CustomerTab.PROFILE to Icons.Default.Person
            )
            
            tabs.forEach { (tab, icon) ->
                val active = tab == currentTab
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(CircleShape)
                        .clickable { onTabSelected(tab) }
                        .padding(vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = tab.name,
                            tint = if (active) Color(0xFF00D68F) else Color(0xFF6B7280),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = tab.name,
                            color = if (active) Color.White else Color(0xFF6B7280),
                            fontSize = 8.sp,
                            fontWeight = if (active) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CustomerTopHeader(
    location: String,
    onLocationClick: () -> Unit,
    onNotificationClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable(onClick = onLocationClick)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0x1F00D68F))
                    .border(1.dp, Color(0x3300D68F), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = Color(0xFF00D68F),
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "HYPERLOFY",
                    color = Color(0xFF00D68F),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = location,
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        IconButton(
            onClick = onProfileClick,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color(0x0AFFFFFF))
                .border(1.dp, Color(0x14FFFFFF), CircleShape)
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Profile",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun HeroCard(onActionClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(Color(0xFF0C0C0E))
            .border(1.dp, Color(0x1A00D68F), RoundedCornerShape(24.dp))
            .padding(24.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(
                modifier = Modifier
                    .background(Color(0x1400D68F), CircleShape)
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "GET ANYTHING. DELIVERED.",
                    color = Color(0xFF00D68F),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }

            Text(
                text = "Premium Hyperlocal Courier Dispatch",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                lineHeight = 30.sp
            )

            Text(
                text = "Instantly request a verified runner to pick up anything from any point. Secure balance verification on checkout.",
                color = Color(0x99FFFFFF),
                fontSize = 13.sp,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = onActionClick,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00D68F)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("hero_booking_button")
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Express Request Runner", color = Color.Black, fontWeight = FontWeight.Bold)
                    Icon(Icons.Default.ArrowForward, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

@Composable
fun LargeSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    placeholder: String,
    onSearchTrigger: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(Color(0x0DFFFFFF))
            .border(1.dp, Color(0x14FFFFFF), RoundedCornerShape(18.dp))
            .clickable { onSearchTrigger() }
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = Color(0xFF00D68F),
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = if (query.isEmpty()) placeholder else query,
                color = if (query.isEmpty()) Color(0x55FFFFFF) else Color.White,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun VerifiedHelpersSection(verifiedAgents: List<LiveAgent>) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "VERIFIED NEARBY RUNNERS",
                color = Color.LightGray,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF00D68F))
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "${verifiedAgents.size} Online",
                    color = Color(0xFF00D68F),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(verifiedAgents) { agent ->
                Box(
                    modifier = Modifier
                        .width(200.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0x08FFFFFF))
                        .border(1.dp, Color(0x14FFFFFF), RoundedCornerShape(20.dp))
                        .padding(16.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = agent.name,
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFB200), modifier = Modifier.size(12.dp))
                                Text(text = "${agent.rating}", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        Text(text = agent.vehicle, color = Color.Gray, fontSize = 11.sp)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "${agent.distanceKm} km away", color = Color(0xFF00D68F), fontSize = 11.sp, fontWeight = FontWeight.Medium)
                            Text(text = "${agent.totalJobs} jobs", color = Color.LightGray, fontSize = 10.sp)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun CustomerHomeView(
    walletBalance: Double,
    onAddFundsClick: () -> Unit,
    simOrders: List<OrderItem>,
    verifiedAgents: List<LiveAgent>,
    onPlaceCustomOrder: (String, String, Double, String) -> Boolean,
    onSelectTab: (CustomerTab) -> Unit,
    onRequestDeliveryClick: () -> Unit,
    trackingProgress: Float,
    speedFeed: String
) {
    val df = remember { DecimalFormat("0.00") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
        contentPadding = PaddingValues(top = 8.dp, bottom = 24.dp)
    ) {
        // 2. Large Hero Section (320px+ premium interactive layout)
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 340.dp)
                    .clip(RoundedCornerShape(32.dp))
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF0A2B1D), // Vivid Forest Emerald
                                Color(0xFF03140E), // Safe Deep Onyx
                                Color(0xFF000000)
                            )
                        )
                    )
                    .border(1.dp, Color(0x2110B981), RoundedCornerShape(32.dp))
                    .padding(24.dp)
            ) {
                // Background subtle network glow effect (Apple Style)
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(Color(0x1A10B981), Color.Transparent),
                            center = Offset(size.width * 0.9f, size.height * 0.2f),
                            radius = 200.dp.toPx()
                        ),
                        radius = 200.dp.toPx()
                    )
                }

                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier
                                .background(Color(0x1F10B981), CircleShape)
                                .border(1.dp, Color(0x3B10B981), CircleShape)
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .background(Color(0xFF00D68F), CircleShape)
                            )
                            Text(
                                text = "INSTANT HYPERLOCAL",
                                color = Color(0xFF00D68F),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.2.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // Headline
                        Text(
                            text = "GET ANYTHING\nDELIVERED IN\nMINUTES",
                            color = Color.White,
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.5.sp,
                            lineHeight = 36.sp
                        )

                        Spacer(modifier = Modifier.height(2.dp))

                        // Subheadline
                        Text(
                            text = "Groceries, Food, Medicines, Electronics, Documents and Custom Deliveries",
                            color = Color(0xFF9CA3AF),
                            fontSize = 12.sp,
                            lineHeight = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Stats indicators Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("124", color = Color(0xFF00D68F), fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            Text("Verified Helpers", color = Color.Gray, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                        Column {
                            Text("12 min", color = Color(0xFF00D68F), fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            Text("Average ETA", color = Color.Gray, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                        Column {
                            Text("4,200", color = Color(0xFF00D68F), fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            Text("Orders Today", color = Color.Gray, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(22.dp))

                    // Large radiant CTA Button
                    Button(
                        onClick = onRequestDeliveryClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .testTag("request_delivery_large_cta"),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00D68F)),
                        shape = RoundedCornerShape(16.dp),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp, pressedElevation = 2.dp)
                    ) {
                        Text(
                            text = "REQUEST DELIVERY",
                            color = Color.Black,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.2.sp
                        )
                    }
                }
            }
        }

        // 3. Search Bar
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0x0CFFFFFF))
                    .border(1.dp, Color(0x14FFFFFF), RoundedCornerShape(20.dp))
                    .clickable { onRequestDeliveryClick() }
                    .padding(horizontal = 18.dp, vertical = 14.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = Color(0xFF00D68F),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "What do you need delivered today?",
                            color = Color.Gray,
                            fontSize = 13.sp,
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

        // 4. Verified Helpers Horizontal Scroll
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "ACTIVE VERIFIED HELPERS",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp
                )
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(verifiedAgents) { agent ->
                        Box(
                            modifier = Modifier
                                .width(180.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .background(Color(0x08FFFFFF))
                                .border(1.dp, Color(0x11FFFFFF), RoundedCornerShape(20.dp))
                                .padding(16.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = agent.name,
                                        color = Color.White,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Star,
                                            contentDescription = null,
                                            tint = Color(0xFFFFB200),
                                            modifier = Modifier.size(10.dp)
                                        )
                                        Spacer(modifier = Modifier.width(2.dp))
                                        Text(
                                            text = "${agent.rating}",
                                            color = Color.White,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                                Text(
                                    text = agent.vehicle,
                                    color = Color.Gray,
                                    fontSize = 10.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(6.dp)
                                                .background(Color(0xFF00D68F), CircleShape)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "Available",
                                            color = Color(0xFF00D68F),
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Text(
                                        text = "${agent.distanceKm} km",
                                        color = Color.LightGray,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 5. Active Order Card
        val activeOrder = simOrders.firstOrNull { it.status != "DELIVERED" && it.status != "COMPLETED" }
        if (activeOrder != null) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFF04120E),
                                    Color(0xFF0F120F)
                                )
                            )
                        )
                        .border(1.dp, Color(0x3300D68F), RoundedCornerShape(24.dp))
                        .clickable { onSelectTab(CustomerTab.ORDERS) }
                        .padding(20.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.ShoppingCart,
                                    contentDescription = null,
                                    tint = Color(0xFF00D68F),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "ACTIVE ORDER IN DISPATCH",
                                    color = Color(0xFF00D68F),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.2.sp
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .background(Color(0x1F00D68F), RoundedCornerShape(8.dp))
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = activeOrder.status,
                                    color = Color(0xFF00D68F),
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Text(
                            text = activeOrder.items,
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Store", color = Color.Gray, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                Text(activeOrder.store, color = Color.LightGray, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                            }
                            Column {
                                Text("ETA", color = Color.Gray, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                Text("${activeOrder.etaMinutes} mins", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            Column {
                                Text("Secure Code / OTP", color = Color.Gray, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                Text(activeOrder.otp, color = Color(0xFF00D68F), fontSize = 11.sp, fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace)
                            }
                        }
                    }
                }
            }
        }

        // 6. Live Tracking Preview (MiniMap render on canvas)
        if (activeOrder != null) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "LIVE TRACKING PREVIEW",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp
                    )
                    MiniMapPreview(
                        order = activeOrder,
                        trackingProgress = trackingProgress,
                        speedFeed = speedFeed,
                        modifier = Modifier.fillMaxWidth().height(160.dp)
                    )
                }
            }
        }

        // 7. Services Grid
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "EXPLORE SERVICES",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ServiceCard(
                        title = "Groceries",
                        description = "Instant Pantry & Veg",
                        icon = Icons.Default.ShoppingCart,
                        modifier = Modifier.weight(1f),
                        onClick = onRequestDeliveryClick
                    )
                    ServiceCard(
                        title = "Pharmacy",
                        description = "Direct Store Meds",
                        icon = Icons.Default.Star,
                        modifier = Modifier.weight(1f),
                        onClick = onRequestDeliveryClick
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ServiceCard(
                        title = "Documents",
                        description = "Secure Legal Transit",
                        icon = Icons.Default.Send,
                        modifier = Modifier.weight(1f),
                        onClick = onRequestDeliveryClick
                    )
                    ServiceCard(
                        title = "Electronics",
                        description = "Safe Padded Delivery",
                        icon = Icons.Default.Build,
                        modifier = Modifier.weight(1f),
                        onClick = onRequestDeliveryClick
                    )
                }
            }
        }

        // 8. Wallet Preview
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0x12FFFFFF),
                                Color(0x06FFFFFF)
                            )
                        )
                    )
                    .border(1.dp, Color(0x14FFFFFF), RoundedCornerShape(24.dp))
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(Color(0x0C00D68F), CircleShape)
                                .border(1.dp, Color(0x1A00D68F), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = Color(0xFF00D68F),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Hyperlofy Secure Balance",
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Current Ledger: ₹${df.format(walletBalance)}",
                                color = Color.Gray,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                    Button(
                        onClick = onAddFundsClick,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1F2937)),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Text(
                            text = "+ Top Up",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // 9. Referral Banner
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFF03120E),
                                Color(0xFF0A2B1D)
                            )
                        )
                    )
                    .border(1.dp, Color(0x1F00D68F), RoundedCornerShape(24.dp))
                    .padding(20.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .background(Color(0x2100D68F), CircleShape)
                            .border(1.dp, Color(0x3B00D68F), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = null,
                            tint = Color(0xFF00D68F),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Refer & Earn ₹150",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Share Hyperlofy with your friends and get rewarded instantly upon their first verified transaction.",
                            color = Color.Gray,
                            fontSize = 10.sp,
                            lineHeight = 14.sp
                        )
                    }
                }
            }
        }

        // 10. Recent Orders
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "RECENT ORDERS",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp
                )
                if (simOrders.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No order history found.", color = Color.Gray, fontSize = 11.sp)
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        simOrders.take(3).forEach { order ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(18.dp))
                                    .background(Color(0x06FFFFFF))
                                    .border(1.dp, Color(0x0AFFFFFF), RoundedCornerShape(18.dp))
                                    .padding(14.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Text(
                                                text = order.store,
                                                color = Color.White,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Box(
                                                modifier = Modifier
                                                    .background(
                                                        if (order.status == "COMPLETED" || order.status == "DELIVERED") Color(0x1100D68F) else Color(0x1FDDBC3B),
                                                        RoundedCornerShape(4.dp)
                                                    )
                                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                            ) {
                                                Text(
                                                    text = order.status,
                                                    color = if (order.status == "COMPLETED" || order.status == "DELIVERED") Color(0xFF00D68F) else Color(0xFFDDBC3B),
                                                    fontSize = 8.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = order.items,
                                            color = Color.Gray,
                                            fontSize = 11.sp,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                    Text(
                                        text = "₹${df.format(order.fee)}",
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
    }
}

@Composable
fun ServiceCard(
    title: String,
    description: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0x0CFFFFFF))
            .border(1.dp, Color(0x14FFFFFF), RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(Color(0x1A00D68F), CircleShape)
                    .border(1.dp, Color(0x3300D68F), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color(0xFF00D68F),
                    modifier = Modifier.size(16.dp)
                )
            }
            Column {
                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = description,
                    color = Color.Gray,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun CompactStatusBlock(label: String, value: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .background(Color(0x06FFFFFF), RoundedCornerShape(10.dp))
            .border(1.dp, Color(0x08FFFFFF), RoundedCornerShape(10.dp))
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = label, fontSize = 9.sp, color = Color.Gray)
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = value, fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun SuggestionTag(label: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clickable(onClick = onClick)
            .background(Color(0x1F222533), RoundedCornerShape(8.dp))
            .border(1.dp, Color(0x33374151), RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(text = label, color = Color.LightGray, fontSize = 10.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun ServiceGridItem(icon: ImageVector, title: String, desc: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Box(
        modifier = modifier
            .clickable(onClick = onClick)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0x0AFFFFFF))
            .border(1.dp, GlassBorder, RoundedCornerShape(16.dp))
            .padding(14.dp)
    ) {
        Column {
            Icon(imageVector = icon, contentDescription = null, tint = EmeraldGreen, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Text(text = desc, fontSize = 10.sp, color = Color.Gray, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
fun VerifiedAgentVisualCard(agent: LiveAgent) {
    Box(
        modifier = Modifier
            .width(180.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0x0AFFFFFF))
            .border(1.dp, GlassBorder, RoundedCornerShape(14.dp))
            .padding(12.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = agent.name, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Star, contentDescription = null, tint = AccentOrange, modifier = Modifier.size(10.dp))
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(text = "${agent.rating}", fontSize = 10.sp, color = TextPrimary, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = agent.vehicle, fontSize = 9.sp, color = Color.Gray)
            Divider(modifier = Modifier.padding(vertical = 6.dp), color = Color(0x0AFFFFFF))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "${agent.distanceKm} km away", fontSize = 9.sp, color = EmeraldGreen)
                Text(text = "${agent.totalJobs} jobs Done", fontSize = 9.sp, color = Color.Gray)
            }
        }
    }
}

// CUSTOMER SUBTAB 2: ORDERS HISTORY LIST
@Composable
fun CustomerOrdersView(onTabSelected: (CustomerTab) -> Unit) {
    val context = LocalContext.current
    val repo = remember { com.example.data.OrderRepository.getInstance(context) }
    val orders = remember { mutableStateListOf<com.example.data.Order>() }
    var selectedOrderId by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    suspend fun loadOrders() {
        try {
            val list = repo.getAll()
            orders.clear()
            orders.addAll(list)
        } catch (_: Exception) {
        }
    }

    LaunchedEffect(Unit) {
        loadOrders()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (orders.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(imageVector = Icons.Default.List, contentDescription = null, tint = Color.DarkGray, modifier = Modifier.size(52.dp))
                Spacer(modifier = Modifier.height(14.dp))
                Text(text = "No dispatches present.", color = Color.Gray, fontSize = 13.sp)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Text(text = "HISTORICAL & PAST DISPATCHES", fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                }
                items(orders) { order ->
                    GlassCard(modifier = Modifier.clickable { selectedOrderId = order.id }) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(text = order.id, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(text = order.itemName, fontSize = 11.sp, color = Color.LightGray)
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(text = order.category, color = Color.Gray, fontSize = 11.sp)
                                    Text(text = order.status, color = EmeraldGreen, fontSize = 11.sp)
                                }
                            }
                            Divider(modifier = Modifier.padding(vertical = 10.dp), color = Color(0x0BFFFFFF))
                            Text(text = "Created: ${java.text.SimpleDateFormat("yyyy-MM-dd HH:mm").format(java.util.Date(order.createdAt))}", color = Color.Gray, fontSize = 10.sp)
                        }
                    }
                }
            }
        }

        selectedOrderId?.let { orderId ->
            OrderDetailsScreen(
                orderId = orderId,
                onBack = {
                    selectedOrderId = null
                    coroutineScope.launch { loadOrders() }
                }
            )
        }
    }
}

@Composable
fun CustomerOrderItemCard(order: OrderItem, onTrackingTap: () -> Unit) {
    val df = remember { DecimalFormat("0.00") }
    GlassCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = order.id, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .background(Color(0xFF0F172A), RoundedCornerShape(6.dp))
                            .border(1.dp, GlassBorder, RoundedCornerShape(6.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(text = "OTP: ${order.otp}", color = LightEmerald, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = "${order.store} • ${order.items}", fontSize = 11.sp, color = Color.LightGray)
            }
            OrderStatusBadge(status = order.status)
        }
        Divider(modifier = Modifier.padding(vertical = 10.dp), color = Color(0x0BFFFFFF))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = "Delivery Carrier: ${order.agentAssigned ?: "Awaiting Assignment"}", fontSize = 10.sp, color = Color.Gray)
                Text(text = "Calculated Toll: ₹${df.format(order.fee)} (${df.format(order.distance)} km)", fontSize = 10.sp, color = Color.Gray)
            }
            if (order.status in listOf("PAYMENT_SUCCESS", "ASSIGNED", "PICKED_AT_STORE", "OUT_FOR_DELIVERY")) {
                Button(
                    onClick = onTrackingTap,
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                    modifier = Modifier.height(34.dp)
                ) {
                    Text(text = "Live Track", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun OrderStatusBadge(status: String) {
    val (bg, txt) = when(status) {
        "CREATED" -> Color(0xFF1E293B) to Color.White
        "PAYMENT_PENDING" -> Color(0xFF451A03) to Color(0xFFF97316)
        "PAYMENT_SUCCESS", "ASSIGNED" -> Color(0xFF064E3B) to Color(0xFF34D399)
        "PICKED_AT_STORE", "OUT_FOR_DELIVERY" -> Color(0xFF1E1B4B) to Color(0xFF818CF8)
        "DELIVERED", "COMPLETED" -> Color(0xFF0F2D1F) to Color(0xFF10B981)
        else -> Color(0xFF3F3F46) to Color.LightGray
    }
    Box(
        modifier = Modifier
            .background(bg, RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(text = status, color = txt, fontSize = 9.sp, fontWeight = FontWeight.Bold)
    }
}

// CUSTOMER SUBTAB 3: INTERACTIVE GPS ROUTE TRACKING MAP CANVAS
@Composable
fun CustomerTrackingView(
    activeOrders: List<OrderItem>,
    trackingProgress: Float,
    speedFeed: String,
    geofenceText: String
) {
    val activeTrack = activeOrders.firstOrNull { it.status in listOf("PAYMENT_SUCCESS", "ASSIGNED", "PICKED_AT_STORE", "OUT_FOR_DELIVERY") }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = "LIVE RIDER LOGS", fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
        
        if (activeTrack == null) {
            GlassCard(modifier = Modifier.fillMaxWidth().height(260.dp)) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(imageVector = Icons.Default.LocationOn, contentDescription = null, tint = Color.DarkGray, modifier = Modifier.size(52.dp))
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(text = "No active deliveries in transit.", color = Color.Gray, fontSize = 13.sp)
                    Text(text = "Place a custom order on Home screen to simulate live route telemetry.", color = Color.DarkGray, fontSize = 11.sp, textAlign = TextAlign.Center, modifier = Modifier.padding(horizontal = 16.dp))
                }
            }
        } else {
            // Interactive custom vector canvas showing path route
            GlassCard(
                glow = true,
                borderColor = Color(0x6610B981),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        // Background radar circle glow
                        drawCircle(
                            color = Color(0x0B10B981),
                            radius = size.width / 3,
                            center = Offset(size.width / 2, size.height / 2)
                        )
                        
                        // Origin checkpoint (Apollo / Store)
                        val originX = size.width * 0.2f
                        val originY = size.height * 0.7f
                        drawCircle(
                            color = Color.White,
                            radius = 8.dp.toPx(),
                            center = Offset(originX, originY)
                        )
                        
                        // Destination checkpoint (Client)
                        val destX = size.width * 0.8f
                        val destY = size.height * 0.2f
                        drawCircle(
                            color = Color(0xFF10B981),
                            radius = 8.dp.toPx(),
                            center = Offset(destX, destY)
                        )
                        
                        // Draw flight path line
                        drawLine(
                            color = Color.DarkGray,
                            start = Offset(originX, originY),
                            end = Offset(destX, destY),
                            strokeWidth = 3f,
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                        )
                        
                        // Draw traveler rider coordinate based on state completion parameter
                        val currentRiderX = originX + (destX - originX) * trackingProgress
                        val currentRiderY = originY + (destY - originY) * trackingProgress
                        
                        drawCircle(
                            color = Color(0xFF10B981),
                            radius = 12.dp.toPx(),
                            center = Offset(currentRiderX, currentRiderY)
                        )
                        
                        drawCircle(
                            color = Color.White,
                            radius = 4.dp.toPx(),
                            center = Offset(currentRiderX, currentRiderY)
                        )
                    }
                    
                    // Coordinates label
                    Box(modifier = Modifier.align(Alignment.BottomStart).background(Color(0xBA000000), RoundedCornerShape(8.dp)).padding(6.dp)) {
                        Text(text = "Telemetry: 13.629° N, 79.419° E", color = Color.White, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                    }

                    Box(modifier = Modifier.align(Alignment.TopEnd).background(Color(0xBA10B981), RoundedCornerShape(8.dp)).padding(6.dp)) {
                        Text(text = "SPEED: $speedFeed", color = Color.Black, fontSize = 9.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                    }
                }
            }

            // Real-Time telemetry logs audit block
            GlassCard {
                Text(text = "LIVE METRICS - ${activeTrack.id}", fontSize = 11.sp, color = EmeraldGreen, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(text = "Rider Name: Rajesh Singh", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Bold)
                        Text(text = "Current Status: ${activeTrack.status}", fontSize = 11.sp, color = Color.Gray)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(text = "Calculated ETA", fontSize = 9.sp, color = Color.Gray)
                        Text(text = "14 mins", fontSize = 13.sp, color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
                Divider(modifier = Modifier.padding(vertical = 8.dp), color = Color(0x0AFFFFFF))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Info, contentDescription = null, tint = EmeraldGreen, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = geofenceText, color = Color.LightGray, fontSize = 11.sp)
                }
            }
        }
    }
}

// CUSTOMER SUBTAB 4: LEDGER WALLET LOGS
@Composable
fun CustomerWalletLedgerView(
    balance: Double,
    history: List<WalletTx>,
    onAddFundsClick: () -> Unit
) {
    val df = remember { DecimalFormat("0.00") }
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            GlassCard(
                borderColor = Color(0x4410B981),
                backgroundColor = Color(0x1F10B981)
            ) {
                Text(text = "LEDGER ACCUMULATED BALANCES", fontSize = 11.sp, color = PaleEmerald, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "₹${df.format(balance)}", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Button(
                        onClick = onAddFundsClick,
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen)
                    ) {
                        Text(text = "+ Top-UP Wallet", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
                Divider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0x3310B981))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(text = "ESCROW HOLD FUNDS", fontSize = 9.sp, color = Color.Gray)
                        Text(text = "₹0.00", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Bold)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(text = "Double Ledger Integrity Check", fontSize = 9.sp, color = Color.Gray)
                        Text(text = "PASSING ACTIVE SECURE", fontSize = 11.sp, color = LightEmerald, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        item {
            Text(text = "ACCOUNT LEDGER ENTRIES", fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
        }

        if (history.isEmpty()) {
            item {
                Text(text = "No recorded transactions yet.", fontSize = 11.sp, color = Color.Gray)
            }
        } else {
            items(history) { tx ->
                GlassCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = tx.description, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(text = "${tx.id} • ${tx.timestamp}", fontSize = 10.sp, color = Color.Gray)
                        }
                        val prefix = if (tx.isCredit) "+" else "-"
                        val color = if (tx.isCredit) LightEmerald else Color.Red
                        Text(text = "$prefix ₹${df.format(tx.amount)}", color = color, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// CUSTOMER SUBTAB 5: REAL CHAT INTERFACES
@Composable
fun CustomerChatView(
    messages: MutableList<ChatMsg>,
    inputText: String,
    onInputTextChange: (String) -> Unit,
    isAgent: Boolean
) {
    val coroutineScope = rememberCoroutineScope()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "SECURE CHAT CONSOLE", fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
            Box(
                modifier = Modifier
                    .background(Color(0xFF0F261C), RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Text(text = "CHAT MODERATION ACTIVE", color = LightEmerald, fontSize = 8.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Messages scrolling log
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            reverseLayout = true
        ) {
            items(messages.asReversed()) { msg ->
                val selfSender = if (isAgent) msg.role == "AGENT" else msg.role == "CUSTOMER"
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = if (selfSender) Alignment.End else Alignment.Start
                ) {
                    Box(
                        modifier = Modifier
                            .background(
                                if (selfSender) Color(0xFF064E3B) else Color(0xFF1F2232),
                                RoundedCornerShape(
                                    topStart = 12.dp,
                                    topEnd = 12.dp,
                                    bottomStart = if (selfSender) 12.dp else 2.dp,
                                    bottomEnd = if (selfSender) 2.dp else 12.dp
                                )
                            )
                            .border(
                                1.dp,
                                if (selfSender) Color(0x3310B981) else GlassBorder,
                                RoundedCornerShape(12.dp)
                            )
                            .padding(10.dp)
                            .widthIn(max = 240.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = msg.sender, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(text = msg.time, color = Color.Gray, fontSize = 8.sp)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            val isRedacted = msg.moderation == "REDACTED"
                            val displayContent = if (isRedacted) "[Message Redacted: Policy Compliance Violation]" else msg.content
                            val displayColor = if (isRedacted) Color.Red else Color.LightGray
                            
                            Text(text = displayContent, color = displayColor, fontSize = 11.sp, lineHeight = 15.sp)
                            
                            if (msg.moderation != "PASSED") {
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(text = "Flagged: ${msg.moderation}", color = Color.Red, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Message input console fields with automated compliance scanner
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = inputText,
                onValueChange = onInputTextChange,
                placeholder = { Text("Send secure message...", color = Color.Gray, fontSize = 12.sp) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = EmeraldGreen,
                    unfocusedBorderColor = GlassBorder,
                    cursorColor = EmeraldGreen
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            IconButton(
                onClick = {
                    if (inputText.isNotBlank()) {
                        val originalText = inputText
                        val activeRole = if (isAgent) "AGENT" else "CUSTOMER"
                        val activeName = if (isAgent) "Rajesh Singh" else "Aravind"
                        
                        // Local instant spam/scam/phone filter moderation engine
                        val isSpamOrPhonePattern = originalText.contains(Regex("\\d{10}")) || 
                                                  originalText.lowercase().contains("refund shortcut") ||
                                                  originalText.lowercase().contains("hack payload")
                        val modTag = if (isSpamOrPhonePattern) {
                            if (originalText.lowercase().contains("hack")) "BLOCKED" else "REDACTED"
                        } else "PASSED"

                        val newMsg = ChatMsg(
                            id = "MSG-" + (100..999).random(),
                            sender = activeName,
                            role = activeRole,
                            content = originalText,
                            moderation = modTag,
                            time = "Now"
                        )
                        
                        if (modTag != "BLOCKED") {
                            messages.add(newMsg)
                        }
                        
                        onInputTextChange("")
                    }
                },
                modifier = Modifier
                    .size(46.dp)
                    .background(Color(0xFF0F2D1F), CircleShape)
                    .border(1.dp, Color(0x3310B981), CircleShape)
            ) {
                Icon(imageVector = Icons.Default.Send, contentDescription = "Send", tint = EmeraldGreen, modifier = Modifier.size(18.dp))
            }
        }
    }
}

// CUSTOMER SUBTAB 6: THE PROFILE DISPLAY PAGE
@Composable
fun CustomerProfileDashboard(user: UserEntity?, onLogout: () -> Unit) {
    val displayName = user?.fullName ?: "Hyperlofy Member"
    val displayEmail = user?.email ?: "not-available@hyperlofy.app"
    val displayPhone = user?.phoneNumber ?: "N/A"
    val initials = user?.fullName
        ?.split(" ")
        ?.filter { it.isNotBlank() }
        ?.mapNotNull { it.firstOrNull()?.uppercaseChar() }
        ?.joinToString("")
        ?.take(2)
        .orEmpty()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(text = "CLIENT COMPLIANCE PROFILE", fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.Bold)

        GlassCard {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(Color(0xFF022E1A), CircleShape)
                            .border(1.dp, Color(0xFF10B981), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = initials.ifBlank { "HX" }, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(text = displayName, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Text(text = displayEmail, fontSize = 11.sp, color = Color.Gray)
                    }
                }
                Divider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0x0AFFFFFF))
                ProfileRow(label = "Phone", value = displayPhone)
                ProfileRow(label = "Email", value = displayEmail)
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = {},
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF122D20)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(text = "Edit Profile", color = Color.White, fontSize = 12.sp)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Button(
                        onClick = onLogout,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF881212)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(text = "Logout", color = Color.White, fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                var showAddresses by remember { mutableStateOf(false) }
                if (showAddresses) {
                    AddressManagementScreen(onClose = { showAddresses = false })
                } else {
                    Button(onClick = { showAddresses = true }, modifier = Modifier.fillMaxWidth()) {
                        Text("Manage Addresses")
                    }
                }
            }
        }

        GlassCard {
            Text(text = "SECURITY AUDIT STATS", fontSize = 11.sp, color = EmeraldGreen, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(10.dp))
            ProfileStatRow(label = "Active Sessions", value = "1 Mobile Applet Session")
            ProfileStatRow(label = "PG Token Verification", value = "Secure Razorpay Test Mode")
            ProfileStatRow(label = "Data Isolation", value = "SQLite Encrypted Sandbox Active")
        }
    }
}

@Composable
private fun ProfileRow(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(text = label, fontSize = 10.sp, color = Color.Gray)
        Text(text = value, fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun ProfileStatRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 11.sp, color = Color.Gray)
        Text(text = value, fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
    }
}

// ==========================================
// PORTAL SCREEN 2: HELPER DRIVER BOARD
// ==========================================
@Composable
fun HelperScreenLayout(
    firstName: String,
    lastName: String,
    phone: String,
    pan: String,
    aadhaar: String,
    vehicle: String,
    onFirstNameChange: (String) -> Unit,
    onLastNameChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onPanChange: (String) -> Unit,
    onAadhaarChange: (String) -> Unit,
    onVehicleChange: (String) -> Unit,
    complianceStatus: String,
    auditLogs: List<String>,
    onInitiateSubmission: () -> Unit,
    onApproveOnboardingSelf: () -> Unit,
    onToggleAvailability: () -> Unit,
    isAvailable: Boolean,
    simOrders: List<OrderItem>,
    onChatAsAgent: (String) -> Unit
) {
    var agentChatCarrierInput by remember { mutableStateOf("") }
    
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "HYPERLOFY COMPLIANCE BOARD", fontSize = 10.sp, color = EmeraldGreen, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                    Text(text = "Helper Core Panel", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
                
                // Toggle Switch availability status
                Box(
                    modifier = Modifier
                        .clickable(onClick = onToggleAvailability)
                        .background(
                            if (isAvailable && complianceStatus == "APPROVED") Color(0xFF064E3B) else Color(0xFF1F2937),
                            RoundedCornerShape(10.dp)
                        )
                        .border(
                            1.dp,
                            if (isAvailable && complianceStatus == "APPROVED") Color(0x7F10B981) else GlassBorder,
                            RoundedCornerShape(10.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = if (isAvailable && complianceStatus == "APPROVED") "DUTY: ONLINE" else "DUTY: OFFLINE",
                        color = if (isAvailable && complianceStatus == "APPROVED") LightEmerald else Color.LightGray,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Onboarding Verification Document Status Block
        item {
            GlassCard(
                borderColor = when(complianceStatus) {
                    "APPROVED" -> Color(0xFF10B981)
                    "SUSPENDED" -> Color.Red
                    else -> Color(0xFFF59E0B)
                },
                backgroundColor = when(complianceStatus) {
                    "APPROVED" -> Color(0x0D10B981)
                    else -> Color(0x0CFFFFFF)
                }
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "DOCUMENT VERIFICATION WORKFLOW", fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(text = "$firstName $lastName (Active Carrier Profile)", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                    Box(
                        modifier = Modifier
                            .background(
                                when (complianceStatus) {
                                    "APPROVED" -> Color(0xFF0F5132)
                                    "SUSPENDED" -> Color(0xFF842029)
                                    else -> Color(0xFF664D03)
                                },
                                RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(text = complianceStatus, color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                }
                
                if (complianceStatus != "APPROVED") {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Compliance status is currently $complianceStatus. Complete papers upload or use self-approval diagnostics trigger down below.",
                        color = Color.LightGray,
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )
                }
            }
        }

        // Onboarding Paper forms
        if (complianceStatus != "APPROVED") {
            item {
                GlassCard {
                    Text(text = "COMPLIANCE PAPERS SUBMISSION", fontSize = 11.sp, color = EmeraldGreen, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(10.dp))
                    
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = firstName,
                            onValueChange = onFirstNameChange,
                            label = { Text("First Name") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = lastName,
                            onValueChange = onLastNameChange,
                            label = { Text("Last Name") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = vehicle,
                        onValueChange = onVehicleChange,
                        label = { Text("Registered Vehicle Plate Number") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = pan,
                            onValueChange = onPanChange,
                            label = { Text("PAN Number") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = aadhaar,
                            onValueChange = onAadhaarChange,
                            label = { Text("Aadhaar ID") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = onInitiateSubmission,
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "Upload Documents to Secure Safe", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    // Instant debugger trigger for convenience in fast simulation
                    TextButton(
                        onClick = onApproveOnboardingSelf,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "⚡ Diagnostic Override (Approve Profile instantly)", color = EmeraldGreen, fontSize = 11.sp)
                    }
                }
            }
        }

        // Verification Audit logs trace entries
        item {
            GlassCard {
                Text(text = "IDENTITY COMPLIANCE AUDIT AUDITING TRACES", fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                auditLogs.forEach { log ->
                    Row(modifier = Modifier.padding(vertical = 4.dp)) {
                        Box(
                            modifier = Modifier
                                .size(4.dp)
                                .clip(CircleShape)
                                .background(EmeraldGreen)
                                .align(Alignment.CenterVertically)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = log, color = Color.LightGray, fontSize = 10.sp, lineHeight = 14.sp)
                    }
                }
            }
        }

        // AGENT ACTIVE JOBS BOARD
        item {
            Text(text = "ACTIVE CARRIER DISPATCH ASSIGNMENTS", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White, letterSpacing = 1.sp)
            Spacer(modifier = Modifier.height(8.dp))
        }

        val activeDeliveries = simOrders.filter { it.status in listOf("PAYMENT_SUCCESS", "ASSIGNED", "PICKED_AT_STORE", "OUT_FOR_DELIVERY") }
        if (activeDeliveries.isEmpty()) {
            item {
                GlassCard {
                    Text(text = "No dispatches assigned to राजेश at the moment.", color = Color.Gray, fontSize = 12.sp)
                    Text(text = "(Place custom task under customer tab to auto dispatch a run)", color = Color.DarkGray, fontSize = 10.sp)
                }
            }
        } else {
            items(activeDeliveries) { ord ->
                GlassCard(glow = true, borderColor = Color(0x3310B981)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = ord.id, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            Text(text = "Collect: ${ord.store}", fontSize = 11.sp, color = EmeraldGreen)
                            Text(text = "Deliver: ${ord.items}", fontSize = 11.sp, color = Color.LightGray)
                        }
                        Box(
                            modifier = Modifier
                                .background(Color(0xFF1E1B4B), RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(text = ord.status, color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    Divider(modifier = Modifier.padding(vertical = 8.dp), color = Color(0x0AFFFFFF))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Distance: ${ord.distance} km | Earn potential: ₹${Math.round(ord.fee * 0.8 * 100.0)/100.0}", fontSize = 10.sp, color = Color.Gray)
                        Text(text = "OTP needed: ${ord.otp}", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // QUICK AGENT CHAT OUTSTREAM
        item {
            GlassCard {
                Text(text = "DRIVE COMPASS CHAT DIALOGUE CHANNEL", fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = agentChatCarrierInput,
                        onValueChange = { agentChatCarrierInput = it },
                        placeholder = { Text("Speak to client customer thread...", fontSize = 10.sp) },
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EmeraldGreen,
                            unfocusedBorderColor = GlassBorder,
                            cursorColor = EmeraldGreen
                        )
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    IconButton(
                        onClick = {
                            if (agentChatCarrierInput.isNotBlank()) {
                                onChatAsAgent(agentChatCarrierInput)
                                agentChatCarrierInput = ""
                            }
                        },
                        modifier = Modifier
                            .size(46.dp)
                            .background(Color(0xFF0F2C1F), CircleShape)
                    ) {
                        Icon(imageVector = Icons.Default.Send, contentDescription = null, tint = EmeraldGreen)
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// ==========================================
// PORTAL SCREEN 3: ADMINISTRATOR / AUDITOR CONSOLE
// ==========================================
@Composable
fun AuditorAdminScreenLayout(
    kpiOrders: Int,
    kpiRev: Double,
    kpiSuccess: Double,
    settingsMultiplier: Double,
    onMultiplierChange: (Double) -> Unit,
    referralVal: Double,
    onReferralValChange: (Double) -> Unit,
    fraudScore: String,
    onFraudScoreChange: (String) -> Unit,
    manualAudits: Boolean,
    onManualAuditsToggle: (Boolean) -> Unit,
    auditLogs: List<String>,
    onTriggerAuditLogAdd: (String) -> Unit,
    activeOrdersRegistry: List<OrderItem>,
    onAdminApproveAgent: () -> Unit,
    onAdminRejectAgent: () -> Unit,
    onAdminSuspendAgent: () -> Unit,
    currentAgentStatus: String
) {
    val df = remember { DecimalFormat("0.00") }
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column(modifier = Modifier.statusBarsPadding()) {
                Text(text = "HYPERLOFY ECOSYSTEM OVERSIGHT", fontSize = 10.sp, color = EmeraldGreen, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                Text(text = "Auditor Super Admin Console", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }

        // Live KPI metrics trend charts block
        item {
            GlassCard(borderColor = Color(0x7F10B981)) {
                Text(text = "REALTIME AGGREGATED DAILY SNAPSHOT", fontSize = 11.sp, color = EmeraldGreen, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AuditorKpiBox(label = "Daily Runs", value = "$kpiOrders", modifier = Modifier.weight(1f))
                    AuditorKpiBox(label = "Gross GMV", value = "₹${df.format(kpiRev)}", modifier = Modifier.weight(1f))
                    AuditorKpiBox(label = "Escrow Payouts", value = "₹${df.format(kpiRev * 0.8)}", modifier = Modifier.weight(1f))
                }
            }
        }

        // Settings adjustments setting panel
        item {
            GlassCard {
                Text(text = "DYNAMIC TARIFFS RATE CONFIG", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(10.dp))
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Ecosystem Multiplier Multiplier: ${df.format(settingsMultiplier)}x", color = Color.LightGray, fontSize = 12.sp)
                        Slider(
                            value = settingsMultiplier.toFloat(),
                            onValueChange = { onMultiplierChange(it.toDouble()) },
                            valueRange = 0.5f..3.0f,
                            colors = SliderDefaults.colors(
                                thumbColor = EmeraldGreen,
                                activeTrackColor = EmeraldGreen
                            )
                        )
                    }
                }
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Referral Reward Base Credit: ₹${df.format(referralVal)}", color = Color.LightGray, fontSize = 12.sp)
                        Slider(
                            value = referralVal.toFloat(),
                            onValueChange = { onReferralValChange(it.toDouble()) },
                            valueRange = 10f..500f,
                            colors = SliderDefaults.colors(
                                thumbColor = EmeraldGreen,
                                activeTrackColor = EmeraldGreen
                            )
                        )
                    }
                }
            }
        }

        // Fraud Mitigation panel
        item {
            GlassCard {
                Text(text = "ESCROW FRAUD ANOMALIES DETECTION", fontSize = 11.sp, color = EmeraldGreen, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(10.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Compliance Suspicious Risk Tolerance", fontSize = 12.sp, color = Color.LightGray)
                    Box(
                        modifier = Modifier
                            .clickable {
                                val next = when(fraudScore) {
                                    "LOW" -> "MEDIUM"
                                    "MEDIUM" -> "HIGH"
                                    "HIGH" -> "CRITICAL"
                                    else -> "LOW"
                                }
                                onFraudScoreChange(next)
                                onTriggerAuditLogAdd("Auditor changed scam penalty tolerance to $next.")
                            }
                            .background(Color(0xFF1F2937), RoundedCornerShape(6.dp))
                            .padding(8.dp)
                    ) {
                        Text(text = fraudScore, color = LightEmerald, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Halt payouts automatically under dispute flag", fontSize = 12.sp, color = Color.LightGray)
                    Switch(
                        checked = manualAudits,
                        onCheckedChange = {
                            onManualAuditsToggle(it)
                            onTriggerAuditLogAdd("Halt payouts toggle updated state: $it.")
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = EmeraldGreen,
                            checkedTrackColor = Color(0xFF064E3B)
                        )
                    )
                }
            }
        }

        // Compliance control overrides for राजेश
        item {
            GlassCard {
                Text(text = "DIAGNOSTIC WORKFLOW QUICK OVERRIDES", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                Text(text = "Quick alter agent Rajesh Singh onboarding profile:", fontSize = 11.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(10.dp))
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = onAdminApproveAgent,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F5132)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(text = "Approve Compliance", fontSize = 10.sp, color = Color.White)
                    }
                    Button(
                        onClick = onAdminRejectAgent,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF664D03)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(text = "Reject Papers", fontSize = 10.sp, color = Color.White)
                    }
                    Button(
                        onClick = onAdminSuspendAgent,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF842029)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(text = "Suspend Carrier", fontSize = 10.sp, color = Color.White)
                    }
                }
            }
        }

        // Live Auditor Logs trace
        item {
            GlassCard {
                Text(text = "ADMINISTRATIVE DOUBLE ENTRY AUDIT TRAILS", fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(10.dp))
                auditLogs.forEach { log ->
                    Text(text = "• $log", fontSize = 10.sp, color = Color.LightGray, lineHeight = 14.sp)
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun AuditorKpiBox(label: String, value: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .background(Color(0xFF111827), RoundedCornerShape(10.dp))
            .border(1.dp, GlassBorder, RoundedCornerShape(10.dp))
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = label, fontSize = 9.sp, color = Color.Gray)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = value, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
    }
}

// ==========================================
// PORTAL SECURE RAZORPAY GATEWAY MODAL SIMULATOR
// ==========================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RazorpaySecurityModalSimulator(
    amount: String,
    onAmountChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onPaymentSuccess: (Double) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF0F0F12),
        modifier = Modifier.border(1.dp, GlassBorder, RoundedCornerShape(24.dp)),
        title = null,
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Razorpay Brand Header
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF0B192C), RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp))
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF0070F3)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "R", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Razorpay Standard Checkout v3", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(text = "SECURE SANDBOX WALLET ADD FUNDS", fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(10.dp))
                
                OutlinedTextField(
                    value = amount,
                    onValueChange = onAmountChange,
                    placeholder = { Text("Enter top up amount (e.g. 500)") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF0070F3),
                        unfocusedBorderColor = GlassBorder,
                        cursorColor = Color(0xFF0070F3)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "This integrates verified double audit ledgers. Signing secret and webhook is validated via secure sandbox headers.",
                    color = Color.DarkGray,
                    fontSize = 10.sp,
                    textAlign = TextAlign.Center
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val addAmt = amount.toDoubleOrNull() ?: 0.0
                    if (addAmt > 0.0) {
                        onPaymentSuccess(addAmt)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0070F3))
            ) {
                Text(text = "Approve checkout Pay ₹$amount", color = Color.White, fontSize = 12.sp)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Cancel checkout", color = Color.Gray, fontSize = 12.sp)
            }
        }
    )
}
