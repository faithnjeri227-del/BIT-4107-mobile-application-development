package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                ParentAppMainScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParentAppMainScreen(viewModel: StudentViewModel = viewModel()) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Results", "Fees", "Pocket Money")
    val icons = listOf(Icons.Default.Assessment, Icons.Default.Payments, Icons.Default.AccountBalanceWallet)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Parent Portal - Student App") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                )
            )
        },
        bottomBar = {
            NavigationBar {
                tabs.forEachIndexed { index, title ->
                    NavigationBarItem(
                        icon = { Icon(icons[index], contentDescription = title) },
                        label = { Text(title) },
                        selected = selectedTab == index,
                        onClick = { selectedTab = index }
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            StudentInfoCard(viewModel)
            
            when (selectedTab) {
                0 -> ExamResultsScreen(viewModel)
                1 -> FeesScreen(viewModel)
                2 -> PocketMoneyScreen(viewModel)
            }
        }
    }
}

@Composable
fun StudentInfoCard(viewModel: StudentViewModel) {
    val student by viewModel.student.collectAsState()
    Card(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = student.name, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text(text = "${student.form} | ${student.schoolName}", fontSize = 14.sp)
        }
    }
}

@Composable
fun ExamResultsScreen(viewModel: StudentViewModel) {
    val results by viewModel.examResults.collectAsState()
    val student by viewModel.student.collectAsState()
    val sciences = listOf("Biology", "Chemistry", "Physics")

    LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        item {
            Text(
                "Exam Results",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Select Sciences:", style = MaterialTheme.typography.titleMedium)
                    sciences.forEach { science ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                        ) {
                            Checkbox(
                                checked = student.selectedSciences.contains(science),
                                onCheckedChange = { checked ->
                                    val current = student.selectedSciences.toMutableList()
                                    if (checked) current.add(science) else current.remove(science)
                                    viewModel.updateSelectedSciences(current)
                                }
                            )
                            Text(text = science, modifier = Modifier.padding(start = 8.dp))
                        }
                    }
                }
            }
        }

        items(results) { result ->
            Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                Row(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = result.subject, fontWeight = FontWeight.Bold)
                        Text(text = "Score: ${result.score}/${result.totalMarks}")
                    }
                    Text(text = result.grade, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}

@Composable
fun FeesScreen(viewModel: StudentViewModel) {
    val fees by viewModel.fees.collectAsState()
    LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        item { Text("School Fees", style = MaterialTheme.typography.headlineSmall, modifier = Modifier.padding(vertical = 8.dp)) }
        items(fees) { fee ->
            Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                Row(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = fee.title, fontWeight = FontWeight.Bold)
                        Text(text = "Amount: $${fee.amount}")
                        Text(text = "Due: ${fee.dueDate}", fontSize = 12.sp)
                    }
                    if (fee.isPaid) {
                        Text("PAID", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    } else {
                        Button(onClick = { viewModel.payFee(fee) }) {
                            Text("Pay Now")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PocketMoneyScreen(viewModel: StudentViewModel) {
    val transactions by viewModel.transactions.collectAsState()
    var amountText by remember { mutableStateOf("") }
    var noteText by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Send Pocket Money", style = MaterialTheme.typography.headlineSmall)
        
        OutlinedTextField(
            value = amountText,
            onValueChange = { amountText = it },
            label = { Text("Amount") },
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
        )
        OutlinedTextField(
            value = noteText,
            onValueChange = { noteText = it },
            label = { Text("Note (Optional)") },
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
        )
        Button(
            onClick = {
                val amount = amountText.toDoubleOrNull() ?: 0.0
                if (amount > 0) {
                    viewModel.sendPocketMoney(amount, noteText)
                    amountText = ""
                    noteText = ""
                }
            },
            modifier = Modifier.align(Alignment.End).padding(vertical = 8.dp)
        ) {
            Text("Send Money")
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
        
        Text("Recent Transactions", style = MaterialTheme.typography.titleMedium)
        LazyColumn {
            items(transactions) { tx ->
                ListItem(
                    headlineContent = { Text("$${tx.amount}") },
                    supportingContent = { Text("${tx.date} - ${tx.note}") },
                    leadingContent = { Icon(Icons.Default.AccountBalanceWallet, contentDescription = null) }
                )
            }
        }
    }
}
