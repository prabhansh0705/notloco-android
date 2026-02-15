package com.notloco.android.ui.screens.auth

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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.notloco.android.data.models.UiState
import com.notloco.android.ui.components.ErrorMessage
import com.notloco.android.ui.components.LoadingIndicator
import com.notloco.android.ui.components.NLBackButton
import com.notloco.android.ui.components.NLPrimaryButton
import com.notloco.android.ui.components.NLTextField
import com.notloco.android.ui.components.PhoneNumberField
import com.notloco.android.ui.theme.GeomFamily
import com.notloco.android.ui.theme.NLBackgroundColor
import com.notloco.android.ui.theme.NLPrimaryColor
import com.notloco.android.ui.theme.NLTextPrimary
import com.notloco.android.ui.theme.NLTextSecondary
import com.notloco.android.ui.theme.NLWhite

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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(NLWhite)
                .statusBarsPadding()
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
                    text = "Sign Up",
                    fontSize = 34.sp,
                    fontFamily = GeomFamily,
                    fontWeight = FontWeight.Light,
                    color = NLTextPrimary,
                    letterSpacing = 0.37.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Create your NotLoco account",
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
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
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

                    Spacer(modifier = Modifier.height(4.dp))

                    NLPrimaryButton(
                        text = "Send Verification Code",
                        onClick = {
                            viewModel.signup(
                                name = name,
                                email = email,
                                phoneNumber = "$countryCode$phoneNumber"
                            )
                        },
                        enabled = name.isNotBlank() && email.isNotBlank() && phoneNumber.length == 10
                    )

                    if (signupState is UiState.Error) {
                        ErrorMessage(
                            message = (signupState as UiState.Error).message
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    // Bottom link - iOS style
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Already a member?",
                            fontFamily = GeomFamily,
                            color = NLTextPrimary,
                            fontSize = 15.sp,
                            letterSpacing = (-0.3).sp
                        )
                        TextButton(onClick = onNavigateToLogin) {
                            Text(
                                text = "Sign In",
                                fontWeight = FontWeight.SemiBold,
                                color = NLPrimaryColor,
                                fontFamily = GeomFamily,
                                fontSize = 15.sp,
                                letterSpacing = (-0.3).sp
                            )
                        }
                    }
                }
            }
        }

        if (signupState is UiState.Loading) {
            LoadingIndicator()
        }
    }
}
