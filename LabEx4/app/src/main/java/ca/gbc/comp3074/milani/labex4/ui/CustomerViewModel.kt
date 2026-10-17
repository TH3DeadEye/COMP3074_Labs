package ca.gbc.comp3074.milani.labex4.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import ca.gbc.comp3074.milani.labex4.data.Customer
import ca.gbc.comp3074.milani.labex4.data.CustomerDao
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

class CustomerViewModel(
    private val customerDao: CustomerDao
) : ViewModel() {

    // Room emits a new list every time the customers table changes
    val customers: Flow<List<Customer>> =
        customerDao.getAllCustomers()

    // null = not searching, otherwise the text the user searched for
    private val _searchQuery = MutableStateFlow<String?>(null)
    val searchQuery: StateFlow<String?> = _searchQuery.asStateFlow()

    // Search results (Student Challenge). null means "no search active".
    @OptIn(ExperimentalCoroutinesApi::class)
    val searchResults: Flow<List<Customer>?> =
        _searchQuery.flatMapLatest { query ->
            if (query == null) flowOf(null)
            else customerDao.searchCustomers(query)
        }

    fun addCustomer(
        name: String,
        age: Int,
        isActive: Boolean
    ) {
        viewModelScope.launch {
            customerDao.insert(
                Customer(
                    name = name,
                    age = age,
                    isActive = isActive
                )
            )
        }
    }

    // Optional Challenge: keeps the same id, so Room updates the existing row
    fun updateCustomer(customer: Customer) {
        viewModelScope.launch {
            customerDao.update(customer)
        }
    }

    fun deleteCustomer(customer: Customer) {
        viewModelScope.launch {
            customerDao.delete(customer)
        }
    }

    fun search(text: String) {
        _searchQuery.value = text.trim()
    }

    fun clearSearch() {
        _searchQuery.value = null
    }
}

// CustomerViewModel needs a CustomerDao, so the default factory can't build it
class CustomerViewModelFactory(
    private val customerDao: CustomerDao
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {
        if (modelClass.isAssignableFrom(CustomerViewModel::class.java)) {
            return CustomerViewModel(customerDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
