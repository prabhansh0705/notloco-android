package com.notloco.android.ui.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.notloco.android.data.models.UiState
import com.notloco.android.ui.components.*
import com.notloco.android.ui.theme.NLBackgroundColor
import com.notloco.android.ui.theme.NLPrimaryColor
import com.notloco.android.ui.theme.NLTextPrimary
import com.notloco.android.ui.theme.NLTextSecondary

@OptIn(ExperimentalMaterial3Api::class)
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
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = NLBackgroundColor
                    )
                )
            },
            containerColor = NLBackgroundColor
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 32.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                NLTitleText(text = "Verify OTP")

                Text(
                    text = "We've sent a verification code to $phoneNumber",
                    fontSize = 16.sp,
                    color = NLTextSecondary,
                    textAlign = TextAlign.Start
                )

                Spacer(modifier = Modifier.height(24.dp))

                NLTextField(
                    value = otp,
                    onValueChange = { if (it.length <= 4) otp = it },
                    placeholder = "Enter 4-digit OTP",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                NLPrimaryButton(
                    text = "Verify",
                    onClick = {
                        viewModel.verifyOtp(phoneNumber, otp)
                    },
                    enabled = otp.length == 4
                )

                if (otpState is UiState.Error) {
                    ErrorMessage(
                        message = (otpState as UiState.Error).message
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Didn't receive code? ",
                        color = NLTextPrimary
                    )
                    TextButton(
                        onClick = { viewModel.resendOtp(phoneNumber) },
                        enabled = resendState !is UiState.Loading
                    ) {
                        Text(
                            text = "Resend",
                            fontWeight = FontWeight.Bold,
                            color = NLPrimaryColor
                        )
                    }
                }

                if (resendState is UiState.Success) {
                    Text(
                        text = "OTP resent successfully!",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        color = NLTextPrimary
                    )
                }

                Spacer(modifier = Modifier.weight(1f))
            }
        }

        if (otpState is UiState.Loading) {
            LoadingIndicator()
        }
    }
}
