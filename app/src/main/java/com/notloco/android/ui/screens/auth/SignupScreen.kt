package com.notloco.android.ui.screens.auth

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
import androidx.compose.foundation.text.KeyboardOptions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignupScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToOTP: (String) -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: SignupViewModel = hiltViewModel()
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var countryCode by remember { mutableStateOf("+91") }
    val signupState by viewModel.signupState.collectAsState()

    LaunchedEffect(signupState) {
        if (signupState is UiState.Success) {
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
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                NLTitleText(text = "Sign Up")

                Spacer(modifier = Modifier.height(8.dp))

                NLTextField(
                    value = name,
                    onValueChange = { name = it },
                    placeholder = "Full Name",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                )

                NLTextField(
                    value = email,
                    onValueChange = { email = it },
                    placeholder = "Email",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )

                PhoneNumberField(
                    phoneNumber = phoneNumber,
                    countryCode = countryCode,
                    onPhoneNumberChange = { phoneNumber = it },
                    onCountryCodeChange = { countryCode = it }
                )

                NLPrimaryButton(
                    text = "Send Verification Code",
                    onClick = {
                        viewModel.signup(
                            name = name,
                            email = email,
                            phoneNumber = "$countryCode$phoneNumber"
                        )
                    },
                    enabled = name.isNotEmpty() && 
                            email.isNotEmpty() && 
                            phoneNumber.length == 10
                )

                if (signupState is UiState.Error) {
                    ErrorMessage(
                        message = (signupState as UiState.Error).message
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Already a member? ",
                        color = NLTextPrimary
                    )
                    TextButton(onClick = onNavigateToLogin) {
                        Text(
                            text = "Sign In",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }

        if (signupState is UiState.Loading) {
            LoadingIndicator()
        }
    }
}
