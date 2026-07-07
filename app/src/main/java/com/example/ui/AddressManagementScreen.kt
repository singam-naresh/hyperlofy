package com.example.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AddressEntity
import com.example.data.AddressRepository
import com.example.data.SessionManager
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddressManagementScreen(
    onClose: () -> Unit
) {
    val ctx = LocalContext.current
    val session = SessionManager.getInstance(ctx)
    val userId = session.currentUserId()
    val repo = remember { AddressRepository.getInstance(ctx) }
    val scope = rememberCoroutineScope()

    var addresses by remember { mutableStateOf<List<AddressEntity>>(emptyList()) }
    var showAdd by remember { mutableStateOf(false) }
    var editing: AddressEntity? by remember { mutableStateOf(null) }

    LaunchedEffect(userId) {
        if (userId != null) {
            addresses = repo.getAddressesForUser(userId)
        }
    }

    fun reload() {
        scope.launch {
            addresses = if (userId != null) repo.getAddressesForUser(userId) else emptyList()
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Text("Saved Addresses", fontSize = 18.sp)
            IconButton(onClick = { showAdd = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (showAdd) {
            AddAddressScreen(userId = userId ?: "", existing = editing, onSaved = {
                showAdd = false
                editing = null
                reload()
            }, onCancel = {
                showAdd = false
                editing = null
            })
        } else {
            if (addresses.isEmpty()) {
                Text("No saved addresses", color = MaterialTheme.colorScheme.onSurfaceVariant)
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(addresses) { addr ->
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                    Text(addr.title, fontSize = 14.sp)
                                    if (addr.isDefault) Text("Default", color = MaterialTheme.colorScheme.primary)
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(addr.fullAddress, fontSize = 13.sp)
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                                    TextButton(onClick = {
                                        editing = addr
                                        showAdd = true
                                    }) { Text("Edit") }
                                    TextButton(onClick = {
                                        scope.launch {
                                            repo.deleteAddress(addr)
                                            reload()
                                        }
                                    }) { Text("Delete") }
                                    TextButton(onClick = {
                                        scope.launch {
                                            repo.setDefault(addr.id)
                                            reload()
                                        }
                                    }) { Text("Set Default") }
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(onClick = onClose, modifier = Modifier.fillMaxWidth()) {
            Text("Close")
        }
    }
}
