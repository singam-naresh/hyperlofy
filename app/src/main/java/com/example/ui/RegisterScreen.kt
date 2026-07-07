package com.example.ui

import android.util.Patterns
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
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
import com.example.data.RegistrationResult
import com.example.data.UserEntity
import com.example.data.UserRepository
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.GlassBorder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

@Composable
fun RegisterScreen(
    onRegistered: (String) -> Unit,
    onBackToLogin: () -> Unit
) {
    val context = LocalContext.current
    val userRepository = remember { UserRepository.getInstance(context) }
    val scope = rememberCoroutineScope()

    var fullName by rememberSaveable { mutableStateOf("") }
    var phoneNumber by rememberSaveable { mutableStateOf("") }
    var emailAddress by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }
    var errorMessage by rememberSaveable { mutableStateOf<String?>(null) }
    var infoMessage by rememberSaveable { mutableStateOf<String?>(null) }
    var loading by rememberSaveable { mutableStateOf(false) }

    fun validateInput(): String? {
        if (fullName.isBlank()) return "Full Name is required"
        if (phoneNumber.length != 10 || phoneNumber.any { !it.isDigit() }) return "Phone Number must be 10 digits"
        if (!Patterns.EMAIL_ADDRESS.matcher(emailAddress.trim()).matches()) return "Enter a valid email address"
        if (password.length < 6) return "Password must be at least 6 characters"
        if (password != confirmPassword) return "Passwords do not match"
        return null
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF050505))
            .padding(18.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        Text(text = "Create Hyperlofy Account", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Register once and access order tracking, wallet, and premium delivery features.", color = Color.Gray, fontSize = 12.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(24.dp))

        GlassCard(modifier = Modifier.fillMaxWidth(), borderColor = GlassBorder) {
            Column(modifier = Modifier.fillMaxWidth().padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                AuthField(label = "Full Name", value = fullName, onValueChange = { fullName = it }, placeholder = "John Doe", leadingIcon = Icons.Default.Person)
                AuthField(label = "Phone Number", value = phoneNumber, onValueChange = { if (it.length <= 10) phoneNumber = it.filter { ch -> ch.isDigit() } }, placeholder = "9876543210", keyboardType = KeyboardType.Phone, leadingIcon = Icons.Default.Phone)
                AuthField(label = "Email Address", value = emailAddress, onValueChange = { emailAddress = it }, placeholder = "name@example.com", keyboardType = KeyboardType.Email, leadingIcon = Icons.Default.Email)
                AuthField(label = "Password", value = password, onValueChange = { password = it }, placeholder = "Minimum 6 characters", keyboardType = KeyboardType.Password, leadingIcon = Icons.Default.Lock, isPassword = true)
                AuthField(label = "Confirm Password", value = confirmPassword, onValueChange = { confirmPassword = it }, placeholder = "Repeat password", keyboardType = KeyboardType.Password, leadingIcon = Icons.Default.Lock, isPassword = true)

                errorMessage?.let { Text(text = it, color = Color(0xFFFF6B6B), fontSize = 13.sp) }
                infoMessage?.let { Text(text = it, color = EmeraldGreen, fontSize = 13.sp) }

                Button(
                    onClick = {
                        scope.launch {
                            errorMessage = null
                            infoMessage = null
                            val validationError = validateInput()
                            if (validationError != null) {
                                errorMessage = validationError
                                return@launch
                            }

                            loading = true
                            val normalizedEmail = emailAddress.trim().lowercase(Locale.getDefault())
                            try {
                                val newUser = UserEntity(
                                    fullName = fullName.trim(),
                                    phoneNumber = phoneNumber.trim(),
                                    email = normalizedEmail,
                                    password = password,
                                    isVerified = false
                                )

                                val result = withContext(Dispatchers.IO) {
                                    userRepository.register(newUser)
                                }

                                when (result) {
                                    is RegistrationResult.Success -> onRegistered(result.userId)
                                    is RegistrationResult.Error -> errorMessage = result.message
                                }
                            } catch (e: Exception) {
                                errorMessage = "Registration failed: ${e.localizedMessage ?: "Please try again."}"
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
                        Text(text = "Create Account", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }

                TextButton(onClick = onBackToLogin, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                    Text(text = "Already have an account? Login", color = Color.White)
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
    keyboardType: KeyboardType = KeyboardType.Text,
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
