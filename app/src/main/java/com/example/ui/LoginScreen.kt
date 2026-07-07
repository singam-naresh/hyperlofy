package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.SessionManager
import com.example.data.UserEntity
import com.example.data.UserRepository
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.GlassBorder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

@Composable
fun LoginScreen(
    onLoginSuccess: (UserEntity) -> Unit,
    onCreateAccount: () -> Unit,
    onForgotPassword: () -> Unit,
    onUnverifiedAccount: (String) -> Unit
) {
    val context = LocalContext.current
    val userRepository = remember { UserRepository.getInstance(context) }
    val scope = rememberCoroutineScope()

    var identifier by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
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
        Text(text = "Welcome Back", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Login with email or phone to continue using Hyperlofy premium delivery.", color = Color.Gray, fontSize = 12.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(24.dp))

        GlassCard(modifier = Modifier.fillMaxWidth(), borderColor = GlassBorder) {
            Column(modifier = Modifier.fillMaxWidth().padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                AuthField(
                    label = "Email or Phone Number",
                    value = identifier,
                    onValueChange = { identifier = it },
                    placeholder = "Email or phone",
                    keyboardType = KeyboardType.Text,
                    leadingIcon = Icons.Default.Email
                )

                AuthField(
                    label = "Password",
                    value = password,
                    onValueChange = { password = it },
                    placeholder = "Enter your password",
                    keyboardType = KeyboardType.Password,
                    leadingIcon = Icons.Default.Lock,
                    isPassword = true
                )

                errorMessage?.let { Text(text = it, color = Color(0xFFFF6B6B), fontSize = 13.sp) }
                infoMessage?.let { Text(text = it, color = EmeraldGreen, fontSize = 13.sp) }

                Button(
                    onClick = {
                        scope.launch {
                            errorMessage = null
                            infoMessage = null
                            if (identifier.isBlank() || password.isBlank()) {
                                errorMessage = "Please enter both credential and password"
                                return@launch
                            }
                            loading = true
                            val normalizedCredential = if (identifier.contains("@")) {
                                identifier.trim().lowercase( Locale.getDefault())
                            } else {
                                identifier.trim()
                            }
                            try {
                                val user = withContext(Dispatchers.IO) {
                                    userRepository.login(normalizedCredential, password)
                                }
                                if (user == null) {
                                    errorMessage = "Invalid login credentials"
                                } else if (!user.isVerified) {
                                    onUnverifiedAccount(user.id)
                                } else {
                                    onLoginSuccess(user)
                                }
                            } catch (e: Exception) {
                                errorMessage = "Login failed: ${e.localizedMessage ?: "Please try again."}"
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
                        Text(text = "Login", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Create Account",
                        color = EmeraldGreen,
                        modifier = Modifier.clickable { onCreateAccount() }
                    )
                    Text(
                        text = "Forgot Password",
                        color = Color(0xFF9CA3AF),
                        modifier = Modifier.clickable { onForgotPassword() }
                    )
                }
            }
        }
    }
}

@Composable
private fun AuthField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    keyboardType: KeyboardType,
    leadingIcon: ImageVector,
    isPassword: Boolean = false
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = label, fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(text = placeholder, color = Color(0xFF7C7C7C)) },
            singleLine = true,
            leadingIcon = { Icon(imageVector = leadingIcon, contentDescription = null, tint = EmeraldGreen) },
            visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
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
    }
}
