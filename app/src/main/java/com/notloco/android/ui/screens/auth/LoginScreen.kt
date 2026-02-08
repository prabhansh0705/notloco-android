package com.notloco.android.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.notloco.android.data.models.UiState
import com.notloco.android.ui.components.*
import com.notloco.android.ui.theme.NLBackgroundColor
import com.notloco.android.ui.theme.NLTextPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onNavigateToSignup: () -> Unit,
    onNavigateToOTP: (String) -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    var phoneNumber by remember { mutableStateOf("") }
    var countryCode by remember { mutableStateOf("+91") }
    val loginState by viewModel.loginState.collectAsState()

    LaunchedEffect(loginState) {
        if (loginState is UiState.Success) {
            onNavigateToOTP("$countryCode$phoneNumber")
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

                NLTitleText(text = "Login")

                Spacer(modifier = Modifier.height(24.dp))

                PhoneNumberField(
                    phoneNumber = phoneNumber,
                    countryCode = countryCode,
                    onPhoneNumberChange = { phoneNumber = it },
                    onCountryCodeChange = { countryCode = it }
                )

                NLPrimaryButton(
                    text = "Send Verification Code",
                    onClick = {
                        viewModel.login("$countryCode$phoneNumber")
                    },
                    enabled = phoneNumber.length == 10
                )

                if (loginState is UiState.Error) {
                    ErrorMessage(
                        message = (loginState as UiState.Error).message
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Not a member yet? ",
                        color = NLTextPrimary
                    )
                    TextButton(onClick = onNavigateToSignup) {
                        Text(
                            text = "Sign Up",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }

        if (loginState is UiState.Loading) {
            LoadingIndicator()
        }
    }
}
