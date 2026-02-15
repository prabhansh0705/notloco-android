package com.notloco.android.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.notloco.android.data.models.UiState
import com.notloco.android.ui.components.ErrorMessage
import com.notloco.android.ui.components.LoadingIndicator
import com.notloco.android.ui.components.NLBackButton
import com.notloco.android.ui.components.NLPrimaryButton
import com.notloco.android.ui.theme.GeomFamily
import com.notloco.android.ui.theme.NLBackgroundColor
import com.notloco.android.ui.theme.NLPrimaryColor
import com.notloco.android.ui.theme.NLSeparator
import com.notloco.android.ui.theme.NLSuccess
import com.notloco.android.ui.theme.NLTextPrimary
import com.notloco.android.ui.theme.NLTextSecondary
import com.notloco.android.ui.theme.NLWhite

@Composable
fun OTPScreen(
    phoneNumber: String,
    onNavigateToHome: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: OTPViewModel = hiltViewModel()
) {
    var otp by remember { mutableStateOf("") }
    val otpState by viewModel.otpState.collectAsState()
    val resendState by viewModel.resendState.collectAsState()

    LaunchedEffect(otpState) {
        if (otpState is UiState.Success) {
            onNavigateToHome()
            viewModel.resetState()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(NLWhite)
                .statusBarsPadding()
                .imePadding()
        ) {
            // iOS-style navigation bar with back button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .padding(horizontal = 4.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                NLBackButton(onClick = onNavigateBack)
            }

            // Title section - iOS large title style
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 28.dp)
            ) {
                Text(
                    text = "Verify OTP",
                    fontSize = 34.sp,
                    fontFamily = GeomFamily,
                    fontWeight = FontWeight.Light,
                    color = NLTextPrimary,
                    letterSpacing = 0.37.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Code sent to $phoneNumber",
                    fontSize = 15.sp,
                    fontFamily = GeomFamily,
                    color = NLTextSecondary,
                    letterSpacing = (-0.3).sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Content card - iOS style rounded top card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
                colors = CardDefaults.cardColors(containerColor = NLBackgroundColor),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp, vertical = 28.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // iOS-style OTP input boxes
                    OTPInputField(
                        otp = otp,
                        onOtpChange = { if (it.length <= 4) otp = it },
                        otpLength = 4
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    NLPrimaryButton(
                        text = "Verify",
                        onClick = { viewModel.verifyOtp(phoneNumber, otp) },
                        enabled = otp.length == 4
                    )

                    if (otpState is UiState.Error) {
                        ErrorMessage(
                            message = (otpState as UiState.Error).message
                        )
                    }

                    // Resend section - iOS style
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Didn't receive code?",
                            color = NLTextPrimary,
                            fontFamily = GeomFamily,
                            fontSize = 15.sp,
                            letterSpacing = (-0.3).sp
                        )
                        TextButton(
                            onClick = { viewModel.resendOtp(phoneNumber) },
                            enabled = resendState !is UiState.Loading
                        ) {
                            Text(
                                text = "Resend",
                                fontWeight = FontWeight.SemiBold,
                                color = NLPrimaryColor,
                                fontFamily = GeomFamily,
                                fontSize = 15.sp,
                                letterSpacing = (-0.3).sp
                            )
                        }
                    }

                    if (resendState is UiState.Success) {
                        Text(
                            text = "OTP resent successfully",
                            color = NLSuccess,
                            fontFamily = GeomFamily,
                            fontSize = 13.sp,
                            letterSpacing = (-0.2).sp
                        )
                    }
                }
            }
        }

        if (otpState is UiState.Loading) {
            LoadingIndicator()
        }
    }
}

/**
 * iOS-style OTP input with individual digit boxes
 */
@Composable
private fun OTPInputField(
    otp: String,
    onOtpChange: (String) -> Unit,
    otpLength: Int = 4
) {
    Box(contentAlignment = Alignment.Center) {
        // Hidden text field for keyboard input
        BasicTextField(
            value = otp,
            onValueChange = { if (it.length <= otpLength && it.all { c -> c.isDigit() }) onOtpChange(it) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            textStyle = TextStyle(color = NLWhite.copy(alpha = 0f)), // Invisible text
            cursorBrush = SolidColor(NLWhite.copy(alpha = 0f))
        )

        // Visual OTP boxes
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            for (i in 0 until otpLength) {
                val char = otp.getOrNull(i)?.toString() ?: ""
                val isCurrent = i == otp.length
                val isFilled = char.isNotEmpty()

                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(NLWhite)
                        .border(
                            width = if (isCurrent) 2.dp else 1.dp,
                            color = when {
                                isCurrent -> NLPrimaryColor
                                isFilled -> NLPrimaryColor.copy(alpha = 0.5f)
                                else -> NLSeparator.copy(alpha = 0.5f)
                            },
                            shape = RoundedCornerShape(12.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = char,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = GeomFamily,
                        color = NLTextPrimary,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
