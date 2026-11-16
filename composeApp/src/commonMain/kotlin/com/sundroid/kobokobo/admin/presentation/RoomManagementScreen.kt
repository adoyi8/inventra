package com.sundroid.kobokobo.admin.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sundroid.kobokobo.hotel.domain.model.Room
import com.sundroid.kobokobo.hotel.domain.model.StayCategory
import com.sundroid.kobokobo.hotel.presentation.HotelEvent
import com.sundroid.kobokobo.hotel.presentation.HotelState
import com.sundroid.kobokobo.hotel.presentation.HotelViewModel

@Composable
fun RoomManagementScreen(
    viewModel: HotelViewModel,
    state: HotelState
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Text(
                "Room Management",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            LazyColumn(
                modifier = Modifier.fillMaxWidth().weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(state.rooms) { room ->
                    RoomItem(
                        room = room,
                        onEdit = { /* TODO: Edit Room */ },
                        onDelete = { viewModel.onEvent(HotelEvent.DeleteRoom(room.id)) }
                    )
                }
            }
        }

        FloatingActionButton(
            onClick = { viewModel.onEvent(HotelEvent.ShowAddRoomDialog(true)) },
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Room")
        }

        if (state.showAddRoomDialog) {
            AddRoomDialog(
                onDismiss = { viewModel.onEvent(HotelEvent.ShowAddRoomDialog(false)) },
                onConfirm = { room -> viewModel.onEvent(HotelEvent.AddRoom(room)) }
            )
        }
    }
}

@Composable
fun RoomItem(room: Room, onEdit: () -> Unit, onDelete: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Room ${room.number}", style = MaterialTheme.typography.titleMedium)
                Text("Status: ${room.status}", style = MaterialTheme.typography.bodyMedium)
                Text("Short: ${room.priceShortTime} | Normal: ${room.priceNormalStay}", style = MaterialTheme.typography.bodySmall)
            }
            Row {
                IconButton(onClick = onEdit) { Icon(Icons.Default.Edit, "Edit") }
                IconButton(onClick = onDelete) { Icon(Icons.Default.Delete, "Delete") }
            }
        }
    }
}

@Composable
fun AddRoomDialog(onDismiss: () -> Unit, onConfirm: (Room) -> Unit) {
    var number by remember { mutableStateOf("") }
    var priceShort by remember { mutableStateOf("") }
    var priceNormal by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Room") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = number, onValueChange = { number = it }, label = { Text("Room Number") })
                OutlinedTextField(value = priceShort, onValueChange = { priceShort = it }, label = { Text("Short Time Price") })
                OutlinedTextField(value = priceNormal, onValueChange = { priceNormal = it }, label = { Text("Normal Stay Price") })
                OutlinedTextField(value = notes, onValueChange = { notes = it }, label = { Text("Notes") })
            }
        },
        confirmButton = {
            Button(onClick = {
                val room = Room(
                    number = number,
                    priceShortTime = priceShort.toDoubleOrNull() ?: 0.0,
                    priceNormalStay = priceNormal.toDoubleOrNull() ?: 0.0,
                    notes = notes
                )
                onConfirm(room)
            }) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
