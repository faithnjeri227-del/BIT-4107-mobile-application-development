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
        mapOf(
            "English" to ExamResult("English", 85, 100, "A"),
            "Kiswahili" to ExamResult("Kiswahili", 90, 100, "A"),
            "French" to ExamResult("French", 78, 100, "B"),
            "Mathematics" to ExamResult("Mathematics", 85, 100, "A"),
            "Biology" to ExamResult("Biology", 82, 100, "A"),
            "Chemistry" to ExamResult("Chemistry", 75, 100, "B"),
            "Physics" to ExamResult("Physics", 88, 100, "A"),
            "Social Studies" to ExamResult("Social Studies", 90, 100, "A"),
            "History" to ExamResult("History", 92, 100, "A+"),
            "Business Studies" to ExamResult("Business Studies", 84, 100, "A"),
            "Computer Studies" to ExamResult("Computer Studies", 95, 100, "A+")
        )
    )

    val examResults: StateFlow<List<ExamResult>> = combine(_student, _allExamResults) { student, allResults ->
        val results = mutableListOf<ExamResult>()
        results.add(allResults["English"]!!)
        results.add(allResults["Kiswahili"]!!)
        results.add(allResults["French"]!!)
        results.add(allResults["Mathematics"]!!)
        
        // Add selected sciences
        student.selectedSciences.forEach { science ->
            allResults[science]?.let { results.add(it) }
        }
        
        results.add(allResults["Social Studies"]!!)
        results.add(allResults["History"]!!)
        results.add(allResults["Business Studies"]!!)
        results.add(allResults["Computer Studies"]!!)
        results
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun updateSelectedSciences(sciences: List<String>) {
        _student.value = _student.value.copy(selectedSciences = sciences)
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
