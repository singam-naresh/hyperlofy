package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.UserEntity
import com.example.data.UserRepository
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.GlassBorder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun OtpVerificationScreen(
    userId: String,
    onVerified: (UserEntity) -> Unit,
    onBackToLogin: () -> Unit
) {
    val context = LocalContext.current
    val userRepository = remember { UserRepository.getInstance(context) }
    val scope = rememberCoroutineScope()

    var otpCode by rememberSaveable { mutableStateOf("") }
    var errorMessage by rememberSaveable { mutableStateOf<String?>(null) }
    var infoMessage by rememberSaveable { mutableStateOf<String?>(null) }
    var loading by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF050505))
            .padding(18.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        Text(text = "OTP Verification", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Enter the six-digit code to verify your new Hyperlofy account.", color = Color.Gray, fontSize = 12.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(24.dp))

        GlassCard(modifier = Modifier.fillMaxWidth(), borderColor = GlassBorder) {
            Column(modifier = Modifier.fillMaxWidth().padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                OutlinedTextField(
                    value = otpCode,
                    onValueChange = { if (it.length <= 6) otpCode = it.filter { char -> char.isDigit() } },
                    placeholder = { Text(text = "123456", color = Color(0xFF7C7C7C)) },
                    label = { Text(text = "OTP Code") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = Color(0x0FFFFFFF),
                        unfocusedContainerColor = Color(0x0FFFFFFF),
                        focusedIndicatorColor = EmeraldGreen,
                        unfocusedIndicatorColor = Color(0x19FFFFFF),
                        cursorColor = EmeraldGreen,
                        focusedPlaceholderColor = Color(0xFF7C7C7C),
                        unfocusedPlaceholderColor = Color(0xFF7C7C7C)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                errorMessage?.let { Text(text = it, color = Color(0xFFFF6B6B), fontSize = 13.sp) }
                infoMessage?.let { Text(text = it, color = EmeraldGreen, fontSize = 13.sp) }

                Button(
                    onClick = {
                        scope.launch {
                            errorMessage = null
                            infoMessage = null
                            if (otpCode.length != 6) {
                                errorMessage = "OTP must be exactly 6 digits"
                                return@launch
                            }
                            loading = true
                            if (otpCode != "123456") {
                                errorMessage = "Invalid OTP code"
                                return@launch
                            }
                            try {
                                val user = withContext(Dispatchers.IO) {
                                    userRepository.getById(userId)
                                }
                                if (user == null) {
                                    errorMessage = "Unable to locate user account"
                                    return@launch
                                }
                                val verifiedUser = user.copy(isVerified = true)
                                withContext(Dispatchers.IO) {
                                    userRepository.update(verifiedUser)
                                }
                                onVerified(verifiedUser)
                            } catch (e: Exception) {
                                errorMessage = "Verification failed: ${e.localizedMessage ?: "Please try again."}"
                            } finally {
                                loading = false
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen)
                ) {
                    if (loading) {
                        CircularProgressIndicator(color = Color.Black, strokeWidth = 2.dp, modifier = Modifier.size(18.dp))
                    } else {
                        Text(text = "Verify OTP", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }

                TextButton(onClick = {
                    otpCode = ""
                    infoMessage = "OTP resent. Use 123456 for testing."
                    errorMessage = null
                }, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                    Text(text = "Resend OTP", color = EmeraldGreen)
                }

                TextButton(onClick = onBackToLogin, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                    Text(text = "Back to Login", color = Color.White)
                }
            }
        }
    }
}
