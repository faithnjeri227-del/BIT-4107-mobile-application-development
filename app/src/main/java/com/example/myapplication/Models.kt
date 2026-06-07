package com.example.myapplication

data class Student(
    val id: String,
    val name: String,
    val form: String,
    val schoolName: String,
    val selectedSciences: List<String> = listOf("Biology", "Chemistry", "Physics")
)

data class ExamResult(
    val subject: String,
    val score: Int,
    val totalMarks: Int,
    val grade: String,
    val examType: String // e.g., "End Term", "CAT", "Midterm"
)

data class Fee(
    val title: String,
    val amount: Double,
    val dueDate: String,
    val isPaid: Boolean
)

data class PocketMoneyTransaction(
    val amount: Double,
    val date: String,
    val note: String
)

data class Notification(
    val id: String,
    val title: String,
    val message: String,
    val date: String,
    val type: String // "Academic", "Fee", "General"
)
