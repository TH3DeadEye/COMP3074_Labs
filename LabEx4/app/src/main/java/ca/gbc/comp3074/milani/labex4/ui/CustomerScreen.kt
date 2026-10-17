package ca.gbc.comp3074.milani.labex4.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ca.gbc.comp3074.milani.labex4.data.Customer
import ca.gbc.comp3074.milani.labex4.ui.theme.LabEx4Theme

// Connects the ViewModel's Flows to the stateless screen below
@Composable
fun CustomerApp(
    viewModel: CustomerViewModel,
    modifier: Modifier = Modifier
) {
    val customers by viewModel.customers
        .collectAsStateWithLifecycle(initialValue = emptyList())
    val searchResults by viewModel.searchResults
        .collectAsStateWithLifecycle(initialValue = null)
    val searchQuery by viewModel.searchQuery
        .collectAsStateWithLifecycle()

    CustomerScreen(
        // show search results while a search is active, otherwise everyone
        customers = searchResults ?: customers,
        searchQuery = searchQuery,
        onAddCustomer = viewModel::addCustomer,
        onUpdateCustomer = viewModel::updateCustomer,
        onDeleteCustomer = viewModel::deleteCustomer,
        onSearch = viewModel::search,
        onClearSearch = viewModel::clearSearch,
        modifier = modifier
    )
}

@Composable
fun CustomerScreen(
    customers: List<Customer>,
    searchQuery: String?,
    onAddCustomer: (String, Int, Boolean) -> Unit,
    onUpdateCustomer: (Customer) -> Unit,
    onDeleteCustomer: (Customer) -> Unit,
    onSearch: (String) -> Unit,
    onClearSearch: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Form state belongs to the UI; the customer list comes from Room
    var name by rememberSaveable { mutableStateOf("") }
    var ageText by rememberSaveable { mutableStateOf("") }
    var isActive by rememberSaveable { mutableStateOf(false) }
    var message by rememberSaveable { mutableStateOf<String?>(null) }
    var editingId by rememberSaveable { mutableStateOf<Int?>(null) }
    var searchText by rememberSaveable { mutableStateOf("") }

    fun resetForm() {
        name = ""
        ageText = ""
        isActive = false
        editingId = null
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Room Customer Demo",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Customer name") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = ageText,
            onValueChange = { newValue ->
                if (newValue.all { it.isDigit() }) {
                    ageText = newValue
                }
            },
            label = { Text("Age") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            ),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Active customer")
            Spacer(Modifier.width(8.dp))
            Switch(
                checked = isActive,
                onCheckedChange = { isActive = it }
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = {
                    val age = ageText.toIntOrNull()
                    if (name.isBlank() || age == null) {
                        message = "Enter a valid name and age."
                    } else {
                        val id = editingId
                        if (id == null) {
                            onAddCustomer(name.trim(), age, isActive)
                            message = "Customer submitted."
                        } else {
                            // same id -> Room updates the existing row
                            onUpdateCustomer(
                                Customer(
                                    id = id,
                                    name = name.trim(),
                                    age = age,
                                    isActive = isActive
                                )
                            )
                            message = "Customer updated."
                        }
                        resetForm()
                    }
                },
                modifier = Modifier.weight(1f)
            ) {
                Text(if (editingId == null) "Add Customer" else "Save Changes")
            }

            if (editingId != null) {
                OutlinedButton(
                    onClick = {
                        resetForm()
                        message = null
                    }
                ) {
                    Text("Cancel")
                }
            }
        }

        message?.let {
            Spacer(Modifier.height(8.dp))
            Text(it)
        }

        Spacer(Modifier.height(12.dp))

        // Student Challenge: search by name fragment
        OutlinedTextField(
            value = searchText,
            onValueChange = { searchText = it },
            label = { Text("Search by name") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = { onSearch(searchText) },
                modifier = Modifier.weight(1f)
            ) {
                Text("Search")
            }
            OutlinedButton(
                onClick = {
                    searchText = ""
                    onClearSearch()
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Clear")
            }
        }

        if (searchQuery != null) {
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Results for \"$searchQuery\"",
                style = MaterialTheme.typography.labelLarge
            )
        }

        Spacer(Modifier.height(12.dp))

        CustomerList(
            customers = customers,
            emptyText = if (searchQuery != null) {
                "No customers match your search."
            } else {
                "No customers in the database."
            },
            onEditCustomer = { customer ->
                name = customer.name
                ageText = customer.age.toString()
                isActive = customer.isActive
                editingId = customer.id
                message = "Editing customer #${customer.id}"
            },
            onDeleteCustomer = { customer ->
                if (editingId == customer.id) resetForm()
                onDeleteCustomer(customer)
            },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun CustomerList(
    customers: List<Customer>,
    emptyText: String,
    onEditCustomer: (Customer) -> Unit,
    onDeleteCustomer: (Customer) -> Unit,
    modifier: Modifier = Modifier
) {
    if (customers.isEmpty()) {
        Box(
            modifier = modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Text(emptyText)
        }
    } else {
        LazyColumn(
            modifier = modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(
                items = customers,
                key = { customer -> customer.id }
            ) { customer ->
                CustomerRow(
                    customer = customer,
                    onEdit = { onEditCustomer(customer) },
                    onDelete = { onDeleteCustomer(customer) }
                )
            }
        }
    }
}

@Composable
fun CustomerRow(
    customer: Customer,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = customer.name,
                    style = MaterialTheme.typography.titleMedium
                )
                Text("ID: ${customer.id}")
                Text("Age: ${customer.age}")
                Text(
                    if (customer.isActive)
                        "Active customer"
                    else
                        "Inactive customer"
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                TextButton(onClick = onEdit) {
                    Text("Edit")
                }
                TextButton(onClick = onDelete) {
                    Text("Delete")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CustomerScreenPreview() {
    LabEx4Theme {
        CustomerScreen(
            customers = listOf(
                Customer(id = 1, name = "Arman", age = 21, isActive = true),
                Customer(id = 2, name = "Sara", age = 34, isActive = false)
            ),
            searchQuery = null,
            onAddCustomer = { _, _, _ -> },
            onUpdateCustomer = {},
            onDeleteCustomer = {},
            onSearch = {},
            onClearSearch = {}
        )
    }
}
