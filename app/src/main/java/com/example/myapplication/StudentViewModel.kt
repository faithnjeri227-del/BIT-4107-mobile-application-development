package com.example.myapplication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.json.JSONArray
import java.net.URL
import javax.net.ssl.HttpsURLConnection

class StudentViewModel : ViewModel() {
    private val baseUrl = NetworkConfig.BASE_URL

    private val _student = MutableStateFlow(
        Student("1", "John Doe", "Form 4", "Highland High School", listOf("Biology", "Chemistry"))
    )
    val student: StateFlow<Student> = _student.asStateFlow()

    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users: StateFlow<List<User>> = _users.asStateFlow()

    private val _isLoadingUsers = MutableStateFlow(false)
    val isLoadingUsers: StateFlow<Boolean> = _isLoadingUsers.asStateFlow()

    init {
        fetchUsers()
    }

    fun fetchUsers() {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoadingUsers.value = true
            try {
                val url = URL("${baseUrl}users.json")
                val connection = url.openConnection() as HttpsURLConnection
                val text = connection.inputStream.bufferedReader().use { it.readText() }
                
                if (text == "null") {
                    _users.value = emptyList()
                    return@launch
                }
                
                val jsonArray = JSONArray(text)
                val userList = mutableListOf<User>()
                for (i in 0 until jsonArray.length()) {
                    val obj = jsonArray.getJSONObject(i)
                    userList.add(
                        User(
                            id = if (obj.has("id")) obj.getInt("id") else i,
                            name = obj.optString("name", "Unknown"),
                            email = obj.optString("email", ""),
                            companyName = if (obj.has("company")) {
                                val company = obj.getJSONObject("company")
                                company.optString("name", "")
                            } else if (obj.has("companyName")) {
                                obj.optString("companyName", "")
                            } else ""
                        )
                    )
                }
                _users.value = userList
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoadingUsers.value = false
            }
        }
    }

    private val _allExamResults = MutableStateFlow(
        listOf(
            // End Term
            ExamResult("English", 85, 100, "A", "End Term"),
            ExamResult("Kiswahili", 88, 100, "A", "End Term"),
            ExamResult("French", 75, 100, "B", "End Term"),
            ExamResult("Mathematics", 92, 100, "A", "End Term"),
            ExamResult("Biology", 80, 100, "A", "End Term"),
            ExamResult("Chemistry", 70, 100, "C", "End Term"),
            ExamResult("Physics", 85, 100, "A", "End Term"),
            ExamResult("Social Studies", 90, 100, "A", "End Term"),
            ExamResult("History", 95, 100, "A+", "End Term"),
            ExamResult("Business Studies", 88, 100, "A", "End Term"),
            ExamResult("Computer Studies", 98, 100, "A+", "End Term"),
            
            // Midterm
            ExamResult("English", 78, 100, "B", "Midterm"),
            ExamResult("Kiswahili", 90, 100, "A", "Midterm"),
            ExamResult("French", 70, 100, "C", "Midterm"),
            ExamResult("Mathematics", 85, 100, "A", "Midterm"),
            ExamResult("Biology", 82, 100, "A", "Midterm"),
            ExamResult("Chemistry", 75, 100, "B", "Midterm"),
            ExamResult("Physics", 80, 100, "A", "Midterm"),
            ExamResult("Social Studies", 85, 100, "A", "Midterm"),
            ExamResult("History", 92, 100, "A+", "Midterm"),
            ExamResult("Business Studies", 80, 100, "A", "Midterm"),
            ExamResult("Computer Studies", 95, 100, "A+", "Midterm"),
            
            // CAT
            ExamResult("English", 27, 30, "A", "CAT"),
            ExamResult("Kiswahili", 24, 30, "B", "CAT"),
            ExamResult("French", 21, 30, "C", "CAT"),
            ExamResult("Mathematics", 29, 30, "A+", "CAT"),
            ExamResult("Biology", 25, 30, "A", "CAT"),
            ExamResult("Chemistry", 23, 30, "B", "CAT"),
            ExamResult("Physics", 27, 30, "A", "CAT"),
            ExamResult("Social Studies", 26, 30, "A", "CAT"),
            ExamResult("History", 28, 30, "A+", "CAT"),
            ExamResult("Business Studies", 25, 30, "B", "CAT"),
            ExamResult("Computer Studies", 29, 30, "A+", "CAT")
        )
    )

    private val _notifications = MutableStateFlow(
        listOf(
            Notification("1", "Mid-Term Results Out", "The mid-term exam results have been published. Please review them in the Results tab.", "2024-03-10", "Academic"),
            Notification("2", "Fee Reminder", "The tuition fee for Q2 is due in 5 days.", "2024-03-12", "Fee"),
            Notification("3", "Parent-Teacher Meeting", "There will be a meeting on Saturday at 10:00 AM in the school hall.", "2024-03-15", "General")
        )
    )

    private val _notificationSearchQuery = MutableStateFlow("")
    val notificationSearchQuery = _notificationSearchQuery.asStateFlow()

    val notifications: StateFlow<List<Notification>> = combine(_notifications, _notificationSearchQuery) { list, query ->
        if (query.isBlank()) list
        else list.filter { it.title.contains(query, ignoreCase = true) || it.message.contains(query, ignoreCase = true) }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private val _examSearchQuery = MutableStateFlow("")
    val examSearchQuery = _examSearchQuery.asStateFlow()

    fun updateNotificationSearchQuery(query: String) {
        _notificationSearchQuery.value = query
    }

    fun updateExamSearchQuery(query: String) {
        _examSearchQuery.value = query
    }

    fun addNotification(title: String, message: String, type: String) {
        val id = System.currentTimeMillis().toString()
        val newNotification = Notification(id, title, message, "2024-03-25", type)
        _notifications.value = listOf(newNotification) + _notifications.value
    }

    fun updateNotification(updated: Notification) {
        _notifications.value = _notifications.value.map {
            if (it.id == updated.id) updated else it
        }
    }

    fun deleteNotification(id: String) {
        _notifications.value = _notifications.value.filter { it.id != id }
    }

    val examResults: StateFlow<List<ExamResult>> = combine(_student, _allExamResults, _examSearchQuery) { student, allResults, query ->
        val sciences = listOf("Biology", "Chemistry", "Physics")
        allResults.filter { result ->
            val matchesScience = if (sciences.contains(result.subject)) {
                student.selectedSciences.contains(result.subject)
            } else {
                true
            }
            val matchesSearch = result.subject.contains(query, ignoreCase = true)
            matchesScience && matchesSearch
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun updateSelectedSciences(sciences: List<String>) {
        _student.value = _student.value.copy(selectedSciences = sciences)
    }

    fun updateStudentInfo(name: String, id: String) {
        _student.value = _student.value.copy(name = name, id = id)
    }

    private val _fees = MutableStateFlow(
        listOf(
            Fee("Tuition Fee - Q1", 1200.0, "2023-10-15", isPaid = true, paymentMethod = "Bank Transfer"),
            Fee("Bus Fee - Q1", 200.0, "2023-10-20", isPaid = false),
            Fee("Library Fee", 50.0, "2023-11-01", isPaid = false),
            Fee("Accommodation Fee", 500.0, "2023-11-05", isPaid = false),
            Fee("Medical Fee", 100.0, "2023-11-10", isPaid = false)
        )
    )
    val fees: StateFlow<List<Fee>> = _fees.asStateFlow()

    private val _transactions = MutableStateFlow(
        listOf(
            PocketMoneyTransaction(50.0, "2023-10-01", "Weekly allowance"),
            PocketMoneyTransaction(20.0, "2023-10-05", "Books")
        )
    )
    val transactions: StateFlow<List<PocketMoneyTransaction>> = _transactions.asStateFlow()

    fun payFee(fee: Fee, method: String) {
        _fees.value = _fees.value.map {
            if (it.title == fee.title) it.copy(isPaid = true, paymentMethod = method) else it
        }
    }

    fun sendPocketMoney(amount: Double, note: String) {
        val newTransaction = PocketMoneyTransaction(amount, "2023-10-25", note)
        _transactions.value = listOf(newTransaction) + _transactions.value
    }
}
