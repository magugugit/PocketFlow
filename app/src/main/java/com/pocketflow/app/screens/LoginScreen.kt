package com.pocketflow.app.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pocketflow.app.ui.components.*
import com.pocketflow.app.ui.theme.*

@Composable
fun LoginScreen(onLoginSuccess: () -> Unit) {
    var selectedTab  by remember { mutableStateOf(0) }   // 0 = Login, 1 = Sign Up
    var email        by remember { mutableStateOf("") }
    var password     by remember { mutableStateOf("") }
    var fullName     by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundMint)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(72.dp))

            // Brand
            PocketFlowLogo(size = 80)
            Spacer(Modifier.height(16.dp))
            Text(
                "PocketFlow",
                fontSize   = 28.sp,
                fontWeight = FontWeight.Bold,
                color      = TextPrimary
            )
            Text(
                "Your Smart Budget Companion",
                fontSize = 14.sp,
                color    = TextSecondary
            )

            Spacer(Modifier.height(36.dp))

            // Auth card
            Card(
                shape  = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier  = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // Toggle
                    ToggleTab(
                        options       = listOf("Login", "Sign Up"),
                        selectedIndex = selectedTab,
                        onSelect      = { selectedTab = it },
                        modifier      = Modifier.fillMaxWidth()
                    )

                    // Fields
                    if (selectedTab == 1) {
                        PfTextField(
                            label          = "Full Name",
                            value          = fullName,
                            onValueChange  = { fullName = it },
                            placeholder    = "John Doe"
                        )
                    }

                    PfTextField(
                        label         = "Email",
                        value         = email,
                        onValueChange = { email = it },
                        placeholder   = "you@example.com"
                    )

                    PfTextField(
                        label         = "Password",
                        value         = password,
                        onValueChange = { password = it },
                        placeholder   = "••••••••",
                        isPassword    = !showPassword,
                        trailingIcon  = {
                            IconButton(onClick = { showPassword = !showPassword }) {
                                Icon(
                                    imageVector = if (showPassword) Icons.Filled.Visibility
                                                  else Icons.Filled.VisibilityOff,
                                    contentDescription = "Toggle password",
                                    tint = TextSecondary
                                )
                            }
                        }
                    )

                    // Forgot password
                    if (selectedTab == 0) {
                        Text(
                            "Forgot Password?",
                            color     = PrimaryTeal,
                            fontSize  = 13.sp,
                            modifier  = Modifier
                                .fillMaxWidth()
                                .clickable { /* TODO */ },
                            textAlign = TextAlign.End
                        )
                    }

                    // Action button
                    PfPrimaryButton(
                        text    = if (selectedTab == 0) "Login" else "Create Account",
                        onClick = onLoginSuccess
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            // Switch mode hint
            Row(horizontalArrangement = Arrangement.Center) {
                Text(
                    if (selectedTab == 0) "Don't have an account? " else "Already have an account? ",
                    color    = TextSecondary,
                    fontSize = 13.sp
                )
                Text(
                    if (selectedTab == 0) "Sign Up" else "Login",
                    color    = PrimaryGreen,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable { selectedTab = 1 - selectedTab }
                )
            }

            Spacer(Modifier.height(40.dp))
        }
    }
}
