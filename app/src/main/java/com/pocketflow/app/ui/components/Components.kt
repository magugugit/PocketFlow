package com.pocketflow.app.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pocketflow.app.data.BudgetStatus
import com.pocketflow.app.ui.theme.*

@Composable
fun PocketFlowLogo(size: Int = 72) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(size.dp)
            .clip(RoundedCornerShape(size / 4))
            .background(Brush.linearGradient(listOf(GradientStart, GradientEnd)))
    ) {
        Icon(
            imageVector        = Icons.Filled.CreditCard,
            contentDescription = "PocketFlow",
            tint               = Color.White,
            modifier           = Modifier.size((size * 0.5).dp)
        )
    }
}

@Composable
fun ToggleTab(
    options: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(UnselectedTab)
            .padding(4.dp)
    ) {
        options.forEachIndexed { idx, label ->
            val selected = idx == selectedIndex
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (selected) PrimaryGreen else Color.Transparent)
                    .clickable { onSelect(idx) }
                    .padding(vertical = 10.dp)
            ) {
                Text(
                    text = label,
                    fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                    color = if (selected) Color.White else TextSecondary,
                    fontSize = 15.sp
                )
            }
        }
    }
}

@Composable
fun PfTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "",
    trailingIcon: @Composable (() -> Unit)? = null,
    isPassword: Boolean = false
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(label, fontWeight = FontWeight.Medium, fontSize = 14.sp, color = TextPrimary)
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = TextLight) },
            trailingIcon = trailingIcon,
            visualTransformation = if (isPassword) PasswordVisualTransformation()
                                   else VisualTransformation.None,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor      = PrimaryGreen,
                unfocusedBorderColor    = BorderLight,
                focusedContainerColor   = Color.White,
                unfocusedContainerColor = Color(0xFFF9FAFB)
            ),
            modifier   = Modifier.fillMaxWidth(),
            singleLine = true
        )
    }
}

@Composable
fun PfPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Button(
        onClick  = onClick,
        enabled  = enabled,
        shape    = RoundedCornerShape(14.dp),
        colors   = ButtonDefaults.buttonColors(
            containerColor         = PrimaryGreen,
            disabledContainerColor = PrimaryGreen.copy(alpha = 0.5f)
        ),
        modifier = modifier.fillMaxWidth().height(52.dp)
    ) {
        Text(text, fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = Color.White)
    }
}

@Composable
fun SectionHeader(title: String, actionLabel: String = "", onAction: () -> Unit = {}) {
    Row(
        modifier              = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.CenterVertically
    ) {
        Text(title, fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = TextPrimary)
        if (actionLabel.isNotEmpty()) {
            Text(
                actionLabel,
                color    = PrimaryTeal,
                fontSize = 13.sp,
                modifier = Modifier.clickable { onAction() }
            )
        }
    }
}

@Composable
fun AnimatedProgressBar(
    progress: Float,
    color: Color = PrimaryGreen,
    backgroundColor: Color = BorderLight,
    height: Int = 8,
    modifier: Modifier = Modifier
) {
    val anim by animateFloatAsState(
        targetValue   = progress,
        animationSpec = tween(800),
        label         = "bar"
    )
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height.dp)
            .clip(RoundedCornerShape(height.dp))
            .background(backgroundColor)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(anim)
                .fillMaxHeight()
                .clip(RoundedCornerShape(height.dp))
                .background(color)
        )
    }
}

@Composable
fun XpProgressBar(current: Int, max: Int, nextLevel: Int, modifier: Modifier = Modifier) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        AnimatedProgressBar(
            progress        = (current.toFloat() / max).coerceIn(0f, 1f),
            color           = XpGold,
            backgroundColor = Color.White.copy(alpha = 0.3f),
            height          = 8
        )
        Text(
            "$current / $max XP to Level $nextLevel",
            fontSize = 11.sp,
            color    = Color.White.copy(alpha = 0.9f)
        )
    }
}

@Composable
fun CategoryChip(
    icon: ImageVector,
    label: String,
    color: Color,
    selected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (selected) color.copy(alpha = 0.15f) else Color(0xFFF9FAFB))
            .border(
                width  = if (selected) 2.dp else 0.dp,
                color  = if (selected) color else Color.Transparent,
                shape  = RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
            .padding(12.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.15f))
        ) {
            Icon(
                imageVector        = icon,
                contentDescription = label,
                tint               = color,
                modifier           = Modifier.size(22.dp)
            )
        }
        Spacer(Modifier.height(6.dp))
        Text(
            text       = label,
            fontSize   = 11.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
            color      = if (selected) color else TextSecondary,
            maxLines   = 2,
            textAlign  = TextAlign.Center
        )
    }
}

fun budgetStatusColor(status: BudgetStatus) = when (status) {
    BudgetStatus.SAFE    -> SafeGreen
    BudgetStatus.WARNING -> WarningYellow
    BudgetStatus.OVER    -> DangerRed
}
