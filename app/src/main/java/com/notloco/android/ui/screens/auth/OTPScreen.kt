package com.notloco.android.ui.screens.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.notloco.android.R
import com.notloco.android.data.models.UiState
import com.notloco.android.ui.components.ErrorMessage
import com.notloco.android.ui.components.LoadingIndicator
import com.notloco.android.ui.components.NLPrimaryButton
import com.notloco.android.ui.components.NLTextField
import com.notloco.android.ui.theme.GeomFamily
import com.notloco.android.ui.theme.NLBackgroundColor
import com.notloco.android.ui.theme.NLPrimaryColor
import com.notloco.android.ui.theme.NLTextPrimary
import com.notloco.android.ui.theme.NLTextSecondary
import com.notloco.android.ui.theme.NLWhite

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
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
                            Image(
                                painter = androidx.compose.ui.res.painterResource(id = R.drawable.ic_left_arrow_ios),
                                contentDescription = "Back",
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = NLWhite)
                )
            },
            containerColor = NLWhite
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(NLWhite)
            ) {
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
                        color = NLTextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Code sent to $phoneNumber",
                        fontSize = 14.sp,
                        fontFamily = GeomFamily,
                        color = NLTextSecondary
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
                    colors = CardDefaults.cardColors(containerColor = NLBackgroundColor)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        NLTextField(
                            value = otp,
                            onValueChange = { if (it.length <= 4) otp = it },
                            placeholder = "Enter 4-digit OTP",
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )

                        NLPrimaryButton(
                            text = "Verify",
                            onClick = { viewModel.verifyOtp(phoneNumber, otp) },
                            enabled = otp.length == 4
                        )

                        if (otpState is UiState.Error) {
                            ErrorMessage(message = (otpState as UiState.Error).message, modifier = Modifier.padding(0.dp))
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Didn't receive code? ",
                                color = NLTextPrimary,
                                fontFamily = GeomFamily,
                                fontSize = 14.sp
                            )
                            TextButton(
                                onClick = { viewModel.resendOtp(phoneNumber) },
                                enabled = resendState !is UiState.Loading
                            ) {
                                Text(
                                    text = "Resend",
                                    fontWeight = FontWeight.Medium,
                                    color = NLPrimaryColor,
                                    fontFamily = GeomFamily
                                )
                            }
                        }

                        if (resendState is UiState.Success) {
                            Text(
                                text = "OTP resent successfully",
                                color = NLTextSecondary,
                                fontFamily = GeomFamily,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }

        if (otpState is UiState.Loading) {
            LoadingIndicator()
        }
    }
}
