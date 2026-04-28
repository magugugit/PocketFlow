package com.pocketflow.app.screens

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import com.pocketflow.app.data.AppState
import com.pocketflow.app.data.ExpenseCategory
import com.pocketflow.app.data.IncomeCategory
import com.pocketflow.app.ui.components.*
import com.pocketflow.app.ui.theme.*
import java.io.File
import java.time.LocalDate
import java.time.LocalTime
import java.util.Calendar

/**
 * Screen that lets the user record an expense or income.
 *
 * Required fields per assignment brief: amount, category, **date**, **start time**,
 * **end time**, **description**, and optionally a **photograph**.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionScreen(onBack: () -> Unit) {
    val context = LocalContext.current

    var typeIndex      by remember { mutableStateOf(0) } // 0=Expense 1=Income
    var amount         by remember { mutableStateOf("") }
    var description    by remember { mutableStateOf("") }
    var selectedExpCat by remember { mutableStateOf(ExpenseCategory.FOOD) }
    var selectedIncCat by remember { mutableStateOf(IncomeCategory.SALARY) }
    var date           by remember { mutableStateOf(LocalDate.now()) }
    var startTime      by remember { mutableStateOf<LocalTime?>(LocalTime.of(9, 0)) }
    var endTime        by remember { mutableStateOf<LocalTime?>(LocalTime.of(10, 0)) }
    var photoUri       by remember { mutableStateOf<String?>(null) }
    /** Pending URI we passed to the camera; only commits to [photoUri] on success. */
    var pendingPhotoUri by remember { mutableStateOf<Uri?>(null) }

    val isExpense = typeIndex == 0

    // Camera launcher — TakePicture writes the image to the URI we provide
    val takePicture = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        Log.d(TAG, "Camera result success=$success uri=$pendingPhotoUri")
        if (success) photoUri = pendingPhotoUri?.toString()
    }
    val requestCameraPermission = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        Log.d(TAG, "Camera permission granted=$granted")
        if (granted) {
            val uri = createImageUri(context)
            pendingPhotoUri = uri
            takePicture.launch(uri)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Add Transaction",
                        fontWeight = FontWeight.SemiBold,
                        fontSize   = 18.sp,
                        color      = TextPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, "Back", tint = TextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundMint)
            )
        },
        containerColor = BackgroundMint
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(Modifier.height(4.dp))

            // Expense / Income toggle
            Card(
                shape    = RoundedCornerShape(16.dp),
                colors   = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                ToggleTab(
                    options       = listOf("Expense", "Income"),
                    selectedIndex = typeIndex,
                    onSelect      = { typeIndex = it },
                    modifier      = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                )
            }

            // Amount input — uses NumberFormat-friendly decimal keyboard
            FormCard(label = "Amount") {
                OutlinedTextField(
                    value         = amount,
                    onValueChange = { raw ->
                        // Sanitise input — keep only digits and at most one decimal
                        val cleaned = raw.filter { it.isDigit() || it == '.' }
                        if (cleaned.count { it == '.' } <= 1) amount = cleaned
                    },
                    prefix        = {
                        Text("R ", fontSize = 22.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                    },
                    placeholder   = { Text("0.00", fontSize = 22.sp, color = TextLight) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    textStyle     = androidx.compose.ui.text.TextStyle(
                        fontSize   = 22.sp,
                        fontWeight = FontWeight.SemiBold,
                        color      = TextPrimary
                    ),
                    shape  = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor      = PrimaryGreen,
                        unfocusedBorderColor    = BorderLight,
                        focusedContainerColor   = Color(0xFFF9FAFB),
                        unfocusedContainerColor = Color(0xFFF9FAFB)
                    ),
                    modifier   = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }

            // Category grid
            FormCard(label = "Category") {
                if (isExpense) {
                    val cats = ExpenseCategory.entries
                    cats.chunked(4).forEach { row ->
                        Row(
                            modifier              = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            row.forEach { cat ->
                                Box(modifier = Modifier.weight(1f)) {
                                    CategoryChip(
                                        icon     = cat.icon,
                                        label    = cat.label,
                                        color    = cat.color,
                                        selected = selectedExpCat == cat,
                                        onClick  = { selectedExpCat = cat }
                                    )
                                }
                            }
                            repeat(4 - row.size) { Spacer(Modifier.weight(1f)) }
                        }
                        Spacer(Modifier.height(8.dp))
                    }
                } else {
                    val cats = IncomeCategory.entries
                    cats.chunked(4).forEach { row ->
                        Row(
                            modifier              = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            row.forEach { cat ->
                                Box(modifier = Modifier.weight(1f)) {
                                    CategoryChip(
                                        icon     = cat.icon,
                                        label    = cat.label,
                                        color    = cat.color,
                                        selected = selectedIncCat == cat,
                                        onClick  = { selectedIncCat = cat }
                                    )
                                }
                            }
                            repeat(4 - row.size) { Spacer(Modifier.weight(1f)) }
                        }
                        Spacer(Modifier.height(8.dp))
                    }
                }
            }

            // Date row
            FormCard(label = "Date") {
                PickerRow(
                    icon    = Icons.Filled.CalendarMonth,
                    text    = "%04d-%02d-%02d".format(date.year, date.monthValue, date.dayOfMonth),
                    onClick = { showDatePicker(context, date) { date = it } }
                )
            }

            // Time row (start + end)
            if (isExpense) {
                FormCard(label = "Start / End time") {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Box(modifier = Modifier.weight(1f)) {
                            PickerRow(
                                icon    = Icons.Filled.AccessTime,
                                text    = startTime?.let { "%02d:%02d".format(it.hour, it.minute) } ?: "Start",
                                onClick = {
                                    showTimePicker(context, startTime ?: LocalTime.of(9, 0)) {
                                        startTime = it
                                    }
                                }
                            )
                        }
                        Box(modifier = Modifier.weight(1f)) {
                            PickerRow(
                                icon    = Icons.Filled.AccessTime,
                                text    = endTime?.let { "%02d:%02d".format(it.hour, it.minute) } ?: "End",
                                onClick = {
                                    showTimePicker(context, endTime ?: LocalTime.of(10, 0)) {
                                        endTime = it
                                    }
                                }
                            )
                        }
                    }
                }
            }

            // Description (replaces the old "Note" field)
            FormCard(label = "Description") {
                OutlinedTextField(
                    value         = description,
                    onValueChange = { description = it },
                    placeholder   = { Text("What was this for?", color = TextLight) },
                    shape         = RoundedCornerShape(12.dp),
                    colors        = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor      = PrimaryGreen,
                        unfocusedBorderColor    = BorderLight,
                        focusedContainerColor   = Color(0xFFF9FAFB),
                        unfocusedContainerColor = Color(0xFFF9FAFB)
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3,
                    minLines = 2
                )
            }

            // Photo (expenses only — receipts/proof of purchase)
            if (isExpense) {
                FormCard(label = "Photo (optional)") {
                    if (photoUri != null) {
                        AsyncImage(
                            model              = photoUri,
                            contentDescription = "Expense photo",
                            contentScale       = ContentScale.Crop,
                            modifier           = Modifier
                                .fillMaxWidth()
                                .height(180.dp)
                                .clip(RoundedCornerShape(12.dp))
                        )
                        Spacer(Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(
                                onClick = { photoUri = null },
                                shape   = RoundedCornerShape(10.dp)
                            ) { Text("Remove", fontSize = 12.sp) }
                            OutlinedButton(
                                onClick = { requestCameraPermission.launch(android.Manifest.permission.CAMERA) },
                                shape   = RoundedCornerShape(10.dp)
                            ) { Text("Retake", fontSize = 12.sp) }
                        }
                    } else {
                        OutlinedButton(
                            onClick = { requestCameraPermission.launch(android.Manifest.permission.CAMERA) },
                            shape   = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().height(56.dp)
                        ) {
                            Icon(Icons.Filled.PhotoCamera, contentDescription = null,
                                tint = PrimaryGreen)
                            Spacer(Modifier.width(8.dp))
                            Text("Take photo", color = PrimaryGreen, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }

            // Save
            val parsedAmount = amount.toDoubleOrNull() ?: 0.0
            val canSave = parsedAmount > 0.0 &&
                (!isExpense || (startTime != null && endTime != null))
            PfPrimaryButton(
                text    = "Save Transaction",
                enabled = canSave,
                onClick = {
                    if (isExpense) {
                        AppState.addExpense(
                            category     = selectedExpCat,
                            amount       = parsedAmount,
                            description  = description.trim(),
                            date         = date,
                            startMinutes = startTime?.let { it.hour * 60 + it.minute },
                            endMinutes   = endTime?.let { it.hour * 60 + it.minute },
                            photoUri     = photoUri
                        )
                    } else {
                        AppState.addIncome(
                            category    = selectedIncCat,
                            amount      = parsedAmount,
                            description = description.trim(),
                            date        = date
                        )
                    }
                    Log.d(TAG, "Saved transaction (${if (isExpense) "expense" else "income"})")
                    onBack()
                }
            )

            Spacer(Modifier.height(8.dp))
        }
    }
}

// ─── Reusable form card ─────────────────────────────────────────────────────

@Composable
private fun FormCard(label: String, content: @Composable () -> Unit) {
    Card(
        shape    = RoundedCornerShape(16.dp),
        colors   = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(label, fontWeight = FontWeight.Medium, fontSize = 14.sp, color = TextSecondary)
            Spacer(Modifier.height(8.dp))
            content()
        }
    }
}

@Composable
private fun PickerRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFF9FAFB))
            .clickable(onClick = onClick)
            .padding(14.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(PrimaryGreen.copy(alpha = 0.12f))
        ) {
            Icon(icon, null, tint = PrimaryGreen, modifier = Modifier.size(16.dp))
        }
        Text(text, fontSize = 14.sp, color = TextPrimary)
    }
}

// ─── Pickers (use platform DatePickerDialog / TimePickerDialog) ────────────

private fun showDatePicker(
    context: android.content.Context,
    initial: LocalDate,
    onPicked: (LocalDate) -> Unit
) {
    val cal = Calendar.getInstance().apply {
        set(initial.year, initial.monthValue - 1, initial.dayOfMonth)
    }
    DatePickerDialog(
        context,
        { _, year, month, day ->
            onPicked(LocalDate.of(year, month + 1, day))
        },
        cal.get(Calendar.YEAR),
        cal.get(Calendar.MONTH),
        cal.get(Calendar.DAY_OF_MONTH)
    ).show()
}

private fun showTimePicker(
    context: android.content.Context,
    initial: LocalTime,
    onPicked: (LocalTime) -> Unit
) {
    TimePickerDialog(
        context,
        { _, hour, minute -> onPicked(LocalTime.of(hour, minute)) },
        initial.hour,
        initial.minute,
        true // 24-hour format
    ).show()
}

// ─── Camera URI helper ──────────────────────────────────────────────────────

/**
 * Create a private file inside `filesDir/expense_photos/` and return a content://
 * URI via FileProvider. The camera app writes the captured JPG to this URI.
 */
private fun createImageUri(context: android.content.Context): Uri {
    val dir = File(context.filesDir, "expense_photos").apply { mkdirs() }
    val file = File.createTempFile("photo_", ".jpg", dir)
    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        file
    )
}

private const val TAG = "PocketFlowAddTx"
