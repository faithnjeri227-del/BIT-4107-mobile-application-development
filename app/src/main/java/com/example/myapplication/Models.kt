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
    val grade: String
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
