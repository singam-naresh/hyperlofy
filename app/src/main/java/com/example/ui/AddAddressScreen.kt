package com.example.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.Alignment
import com.example.data.AddressEntity
import com.example.data.AddressRepository
import kotlinx.coroutines.launch
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAddressScreen(
    userId: String,
    existing: AddressEntity? = null,
    onSaved: (String) -> Unit,
    onCancel: () -> Unit
) {
    val ctx = LocalContext.current
    val repo = remember { AddressRepository.getInstance(ctx) }
    val scope = rememberCoroutineScope()

    var title by remember { mutableStateOf(existing?.title ?: "") }
    var fullAddress by remember { mutableStateOf(existing?.fullAddress ?: "") }
    var landmark by remember { mutableStateOf(existing?.landmark ?: "") }
    var city by remember { mutableStateOf(existing?.city ?: "") }
    var state by remember { mutableStateOf(existing?.state ?: "") }
    var pincode by remember { mutableStateOf(existing?.pincode ?: "") }
    var isDefault by remember { mutableStateOf(existing?.isDefault ?: false) }
    var error by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
        Text(if (existing == null) "Add Address" else "Edit Address", fontSize = 16.sp)
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title (Home, Office)") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = fullAddress, onValueChange = { fullAddress = it }, label = { Text("Full Address") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = landmark, onValueChange = { landmark = it }, label = { Text("Landmark") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = city, onValueChange = { city = it }, label = { Text("City") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = state, onValueChange = { state = it }, label = { Text("State") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = pincode, onValueChange = { pincode = it.filter { ch -> ch.isDigit() } }, label = { Text("Pincode") }, modifier = Modifier.fillMaxWidth())

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = isDefault, onCheckedChange = { isDefault = it })
            Text("Set as default")
        }

        if (error.isNotBlank()) Text(error, color = MaterialTheme.colorScheme.error)

        Spacer(modifier = Modifier.height(8.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = {
                error = ""
                if (title.isBlank() || fullAddress.isBlank() || city.isBlank() || state.isBlank() || pincode.isBlank()) {
                    error = "All fields except landmark are required"
                    return@Button
                }
                loading = true
                scope.launch {
                    try {
                        val addr = AddressEntity(
                            id = existing?.id ?: UUID.randomUUID().toString(),
                            userId = userId,
                            title = title.trim(),
                            fullAddress = fullAddress.trim(),
                            landmark = landmark.trim().ifEmpty { null },
                            city = city.trim(),
                            state = state.trim(),
                            pincode = pincode.trim(),
                            isDefault = isDefault
                        )

                        if (existing == null) {
                            repo.addAddress(addr)
                        } else {
                            repo.updateAddress(addr)
                        }

                        onSaved(addr.id)
                    } catch (e: Exception) {
                        error = "Failed to save address: ${e.localizedMessage}"
                    } finally {
                        loading = false
                    }
                }
            }, modifier = Modifier.weight(1f)) {
                Text(if (existing == null) "Save Address" else "Update")
            }

            OutlinedButton(onClick = onCancel, modifier = Modifier.weight(1f)) {
                Text("Cancel")
            }
        }
    }
}
