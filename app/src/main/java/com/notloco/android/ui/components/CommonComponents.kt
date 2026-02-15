package com.notloco.android.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.notloco.android.ui.theme.*

/**
 * iOS-style primary action button matching the NotLoco iOS app.
 * Full-width, rounded corners, orange background.
 */
@Composable
fun NLPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = NLPrimaryColor,
            contentColor = NLWhite,
            disabledContainerColor = NLPrimaryColor.copy(alpha = 0.4f),
            disabledContentColor = NLWhite.copy(alpha = 0.7f)
        ),
        shape = RoundedCornerShape(14.dp),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 0.dp,
            pressedElevation = 0.dp
        )
    ) {
        Text(
            text = text,
            fontSize = 17.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = GeomFamily,
            letterSpacing = (-0.4).sp
        )
    }
}

/**
 * iOS-style text field with underline focus indicator, matching NotLoco iOS.
 * Uses a clean, minimal design without heavy borders.
 */
@Composable
fun NLTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    singleLine: Boolean = true,
    maxLines: Int = 1,
    enabled: Boolean = true
) {
    var isFocused by remember { mutableStateOf(false) }
    val borderColor by animateColorAsState(
        targetValue = if (isFocused) NLPrimaryColor else NLSeparator.copy(alpha = 0.5f),
        animationSpec = tween(200),
        label = "borderColor"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(NLWhite)
            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
            .onFocusChanged { isFocused = it.isFocused }
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (leadingIcon != null) {
                leadingIcon()
                Spacer(modifier = Modifier.width(8.dp))
            }

            Box(modifier = Modifier.weight(1f)) {
                if (value.isEmpty()) {
                    Text(
                        text = placeholder,
                        color = NLTextSecondary,
                        fontSize = 16.sp,
                        fontFamily = GeomFamily,
                        letterSpacing = (-0.3).sp
                    )
                }
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = TextStyle(
                        fontFamily = GeomFamily,
                        fontSize = 16.sp,
                        color = NLTextPrimary,
                        letterSpacing = (-0.3).sp
                    ),
                    keyboardOptions = keyboardOptions,
                    keyboardActions = keyboardActions,
                    visualTransformation = visualTransformation,
                    singleLine = singleLine,
                    maxLines = maxLines,
                    enabled = enabled,
                    cursorBrush = SolidColor(NLPrimaryColor)
                )
            }

            if (trailingIcon != null) {
                Spacer(modifier = Modifier.width(8.dp))
                trailingIcon()
            }
        }
    }
}

/**
 * iOS-style large title text (matching iOS NavigationTitle .large)
 */
@Composable
fun NLTitleText(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        modifier = modifier,
        fontSize = 34.sp,
        fontWeight = FontWeight.Light,
        fontFamily = CalendasFamily,
        color = NLTextPrimary,
        letterSpacing = 0.37.sp
    )
}

/**
 * iOS-style loading overlay with blur-like effect
 */
@Composable
fun LoadingIndicator(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(NLOverlayBackground),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.size(100.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = NLWhite),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = NLPrimaryColor,
                    strokeWidth = 3.dp,
                    modifier = Modifier.size(36.dp)
                )
            }
        }
    }
}

/**
 * iOS-style error banner
 */
@Composable
fun ErrorMessage(
    message: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(NLError.copy(alpha = 0.08f))
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(
            text = message,
            color = NLError,
            fontSize = 14.sp,
            fontFamily = GeomFamily,
            textAlign = TextAlign.Center,
            lineHeight = 18.sp,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

/**
 * iOS-style phone number input field with country code.
 * Matches the iOS PhoneNumberView layout.
 */
@Composable
fun PhoneNumberField(
    phoneNumber: String,
    countryCode: String,
    onPhoneNumberChange: (String) -> Unit,
    onCountryCodeChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Country Code Field - iOS style
        var codeFocused by remember { mutableStateOf(false) }
        val codeBorderColor by animateColorAsState(
            targetValue = if (codeFocused) NLPrimaryColor else NLSeparator.copy(alpha = 0.5f),
            animationSpec = tween(200),
            label = "codeBorder"
        )

        Box(
            modifier = Modifier
                .width(80.dp)
                .height(52.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(NLWhite)
                .border(1.dp, codeBorderColor, RoundedCornerShape(12.dp))
                .onFocusChanged { codeFocused = it.isFocused },
            contentAlignment = Alignment.Center
        ) {
            BasicTextField(
                value = countryCode,
                onValueChange = onCountryCodeChange,
                modifier = Modifier.padding(horizontal = 12.dp),
                textStyle = TextStyle(
                    fontFamily = GeomFamily,
                    fontSize = 16.sp,
                    color = NLTextPrimary,
                    textAlign = TextAlign.Center,
                    letterSpacing = (-0.3).sp
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                singleLine = true,
                cursorBrush = SolidColor(NLPrimaryColor)
            )
        }

        // Phone Number Field - iOS style
        var phoneFocused by remember { mutableStateOf(false) }
        val phoneBorderColor by animateColorAsState(
            targetValue = if (phoneFocused) NLPrimaryColor else NLSeparator.copy(alpha = 0.5f),
            animationSpec = tween(200),
            label = "phoneBorder"
        )

        Box(
            modifier = Modifier
                .weight(1f)
                .height(52.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(NLWhite)
                .border(1.dp, phoneBorderColor, RoundedCornerShape(12.dp))
                .onFocusChanged { phoneFocused = it.isFocused }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    if (phoneNumber.isEmpty()) {
                        Text(
                            text = "Mobile Number",
                            color = NLTextSecondary,
                            fontSize = 16.sp,
                            fontFamily = GeomFamily,
                            letterSpacing = (-0.3).sp
                        )
                    }
                    BasicTextField(
                        value = phoneNumber,
                        onValueChange = onPhoneNumberChange,
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = TextStyle(
                            fontFamily = GeomFamily,
                            fontSize = 16.sp,
                            color = NLTextPrimary,
                            letterSpacing = (-0.3).sp
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        singleLine = true,
                        cursorBrush = SolidColor(NLPrimaryColor)
                    )
                }
            }
        }
    }
}

/**
 * iOS-style navigation back button
 */
@Composable
fun NLBackButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = onClick,
        modifier = modifier.size(44.dp)
    ) {
        androidx.compose.foundation.Image(
            painter = androidx.compose.ui.res.painterResource(
                id = com.notloco.android.R.drawable.ic_left_arrow_ios
            ),
            contentDescription = "Back",
            modifier = Modifier.size(18.dp)
        )
    }
}
