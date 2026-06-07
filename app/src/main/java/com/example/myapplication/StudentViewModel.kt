package com.example.myapplication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*

class StudentViewModel : ViewModel() {
    private val _student = MutableStateFlow(
        Student("1", "John Doe", "Form 4", "Highland High School", listOf("Biology", "Chemistry"))
    )
    val student: StateFlow<Student> = _student.asStateFlow()

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
            ExamResult("English", 45, 50, "A", "CAT"),
            ExamResult("Kiswahili", 40, 50, "B", "CAT"),
            ExamResult("French", 35, 50, "C", "CAT"),
            ExamResult("Mathematics", 48, 50, "A+", "CAT"),
            ExamResult("Biology", 42, 50, "A", "CAT"),
            ExamResult("Chemistry", 38, 50, "B", "CAT"),
            ExamResult("Physics", 45, 50, "A", "CAT"),
            ExamResult("Social Studies", 44, 50, "A", "CAT"),
            ExamResult("History", 47, 50, "A+", "CAT"),
            ExamResult("Business Studies", 41, 50, "B", "CAT"),
            ExamResult("Computer Studies", 49, 50, "A+", "CAT")
        )
    )

    val examResults: StateFlow<List<ExamResult>> = combine(_student, _allExamResults) { student, allResults ->
        val sciences = listOf("Biology", "Chemistry", "Physics")
        allResults.filter { result ->
            if (sciences.contains(result.subject)) {
                student.selectedSciences.contains(result.subject)
            } else {
                true
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private val _notifications = MutableStateFlow(
        listOf(
            Notification("1", "Mid-Term Results Out", "The mid-term exam results have been published. Please review them in the Results tab.", "2024-03-10", "Academic"),
            Notification("2", "Fee Reminder", "The tuition fee for Q2 is due in 5 days.", "2024-03-12", "Fee"),
            Notification("3", "Parent-Teacher Meeting", "There will be a meeting on Saturday at 10:00 AM in the school hall.", "2024-03-15", "General")
        )
    )
    val notifications: StateFlow<List<Notification>> = _notifications.asStateFlow()

    fun updateSelectedSciences(sciences: List<String>) {
        _student.value = _student.value.copy(selectedSciences = sciences)
    }

    fun updateStudentInfo(name: String, id: String) {
        _student.value = _student.value.copy(name = name, id = id)
    }

    private val _fees = MutableStateFlow(
        listOf(
            Fee("Tuition Fee - Q1", 1200.0, "2023-10-15", isPaid = true),
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

    fun payFee(fee: Fee) {
        _fees.value = _fees.value.map {
            if (it.title == fee.title) it.copy(isPaid = true) else it
        }
    }

    fun sendPocketMoney(amount: Double, note: String) {
        val newTransaction = PocketMoneyTransaction(amount, "2023-10-25", note)
        _transactions.value = listOf(newTransaction) + _transactions.value
    }
}
