package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
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
                var showMainScreen by remember { mutableStateOf(false) }
                val viewModel: StudentViewModel = viewModel()
                
                if (showMainScreen) {
                    ParentAppMainScreen(viewModel)
                } else {
                    StudentEntryScreen(viewModel) {
                        showMainScreen = true
                    }
                }
            }
        }
    }
}

@Composable
fun StudentEntryScreen(viewModel: StudentViewModel, onContinue: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var admission by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                        MaterialTheme.colorScheme.background
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                modifier = Modifier.size(100.dp),
                shape = RoundedCornerShape(28.dp),
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.School,
                        contentDescription = null,
                        modifier = Modifier.size(56.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "Parent Portal",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onBackground
            )
            
            Text(
                text = "Monitor your child's academic journey",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 8.dp, bottom = 40.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(32.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Student Sign In",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 24.dp)
                    )

                    OutlinedTextField(
                        value = name,
                        onValueChange = { if (it.all { char -> char.isLetter() || char.isWhitespace() }) name = it },
                        label = { Text("Student Full Name") },
                        placeholder = { Text("e.g. Jane Smith") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = admission,
                        onValueChange = { if (it.all { char -> char.isDigit() }) admission = it },
                        label = { Text("Admission Number") },
                        placeholder = { Text("e.g. 12345") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        leadingIcon = { Icon(Icons.Default.Fingerprint, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    Button(
                        onClick = {
                            if (name.isNotBlank() && admission.isNotBlank()) {
                                viewModel.updateStudentInfo(name, admission)
                                onContinue()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp),
                        enabled = name.isNotBlank() && admission.isNotBlank(),
                        shape = RoundedCornerShape(16.dp),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                    ) {
                        Text("Access Portal", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParentAppMainScreen(viewModel: StudentViewModel = viewModel()) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Results", "Fees", "Pocket Money", "Notifications")
    val icons = listOf(Icons.Default.Assessment, Icons.Default.Payments, Icons.Default.AccountBalanceWallet, Icons.Default.Notifications)

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
                3 -> NotificationsScreen(viewModel)
            }
        }
    }
}

@Composable
fun StudentInfoCard(viewModel: StudentViewModel) {
    val student by viewModel.student.collectAsState()
    var showEditDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = student.name, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text(text = "Admission: ${student.id}", fontSize = 14.sp)
                Text(text = "${student.form} | ${student.schoolName}", fontSize = 14.sp)
            }
            IconButton(onClick = { showEditDialog = true }) {
                Icon(Icons.Default.Edit, contentDescription = "Edit Profile")
            }
        }
    }

    if (showEditDialog) {
        var name by remember { mutableStateOf(student.name) }
        var admission by remember { mutableStateOf(student.id) }

        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Edit Student Info") },
            text = {
                Column {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { if (it.all { char -> char.isLetter() || char.isWhitespace() }) name = it },
                        label = { Text("Student Name") },
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                    )
                    OutlinedTextField(
                        value = admission,
                        onValueChange = { if (it.all { char -> char.isDigit() }) admission = it },
                        label = { Text("Admission Number") },
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    viewModel.updateStudentInfo(name, admission)
                    showEditDialog = false
                }) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun ExamResultsScreen(viewModel: StudentViewModel) {
    val results by viewModel.examResults.collectAsState()
    val student by viewModel.student.collectAsState()
    val sciences = listOf("Biology", "Chemistry", "Physics")
    
    val groupedResults = results.groupBy { it.examType }
    val examTypesOrder = listOf("End Term", "Midterm", "CAT")

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

        examTypesOrder.forEach { examType ->
            val typeResults = groupedResults[examType]
            if (!typeResults.isNullOrEmpty()) {
                item {
                    Text(
                        text = examType,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                    )
                }
                
                items(typeResults) { result ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp).fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(text = result.subject, fontWeight = FontWeight.Bold)
                                Text(text = "Score: ${result.score}/${result.totalMarks}", style = MaterialTheme.typography.bodyMedium)
                            }
                            Text(
                                text = result.grade,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
        
        // Handle any other types not in our preferred order
        groupedResults.keys.filter { it !in examTypesOrder }.forEach { examType ->
            val typeResults = groupedResults[examType]!!
            item {
                Text(
                    text = examType,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                )
            }
            items(typeResults) { result ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = result.subject, fontWeight = FontWeight.Bold)
                            Text(text = "Score: ${result.score}/${result.totalMarks}", style = MaterialTheme.typography.bodyMedium)
                        }
                        Text(
                            text = result.grade,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NotificationsScreen(viewModel: StudentViewModel) {
    val notifications by viewModel.notifications.collectAsState()
    LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        item {
            Text(
                "Notifications",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
        items(notifications) { notification ->
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = when(notification.type) {
                        "Academic" -> MaterialTheme.colorScheme.primaryContainer
                        "Fee" -> MaterialTheme.colorScheme.secondaryContainer
                        else -> MaterialTheme.colorScheme.surfaceVariant
                    }
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = notification.title, fontWeight = FontWeight.Bold)
                        Text(text = notification.date, style = MaterialTheme.typography.bodySmall)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = notification.message, style = MaterialTheme.typography.bodyMedium)
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
