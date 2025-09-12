package com.example.simplenote.ui.register.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.simplenote.core.ui.purpleColor
import com.example.simplenote.ui.home.activity.HomeActivity
import com.example.simplenote.ui.login.activity.LoginActivity
import com.example.simplenote.ui.register.viewmodel.RegisterNavigationEvent
import com.example.simplenote.ui.register.viewmodel.RegisterViewModel

class RegisterActivity: ComponentActivity() {
    private val viewModel: RegisterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val navigationEvent by viewModel.navigationEvent.collectAsState()
            val errorMessage by viewModel.errorMessage.collectAsState()
            val context = LocalContext.current

            LaunchedEffect(navigationEvent) {
                when (navigationEvent) {
                    RegisterNavigationEvent.ToHome -> {
                        startActivity(Intent(this@RegisterActivity, HomeActivity::class.java))
                        finish()
                        viewModel.onNavigationHandled()
                    }

                    RegisterNavigationEvent.ToLogin -> {
                        startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                        viewModel.onNavigationHandled()
                    }

                    RegisterNavigationEvent.None -> Unit
                }
            }

            RegisterScreen(
                errorMessage = errorMessage,
                onBackToLoginClick = { viewModel.onBackToLoginClicked() },
                onRegisterClick = { firstName, lastName, username, email, password, confirmPassword ->
                    viewModel.registerUser(firstName, lastName, username, email, password, confirmPassword, context)
                }
            )
        }
    }
    @Composable
    fun RegisterScreen(
        errorMessage: String?,
        onBackToLoginClick: () -> Unit,
        onRegisterClick: (firstName: String, lastName: String, username: String, email: String, password: String, confirmPassword: String) -> Unit
    ) {
        var firstName by remember { mutableStateOf("") }
        var lastName by remember { mutableStateOf("") }
        var username by remember { mutableStateOf("") }
        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var confirmPassword by remember { mutableStateOf("") }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(15.dp))

            Text(
                text = "Back to Login",
                color = purpleColor,
                fontSize = 14.sp,
                modifier = Modifier.clickable { onBackToLoginClick() }
            )

            Text(
                text = "Register",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "And start taking notes.",
                fontSize = 16.sp,
                color = Color.Gray
            )

            OutlinedTextField(value = firstName, onValueChange = { firstName = it }, label = { Text("First Name") })
            OutlinedTextField(value = lastName, onValueChange = { lastName = it }, label = { Text("Last Name") })
            OutlinedTextField(value = username, onValueChange = { username = it }, label = { Text("Username") })
            OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email Address") })
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation()
            )
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Retype Password") },
                visualTransformation = PasswordVisualTransformation()
            )

            Button(
                onClick = { onRegisterClick(firstName, lastName, username, email, password, confirmPassword) },
                colors = ButtonDefaults.buttonColors(containerColor = purpleColor),
                shape = RoundedCornerShape(50),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text(
                    text = "Register →",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            if (!errorMessage.isNullOrEmpty()) {
                Text(
                    text = errorMessage,
                    color = Color.Red,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Already have an account? Login here",
                color = Color.Blue,
                modifier = Modifier.clickable { onBackToLoginClick() }
            )
        }
    }
    override fun onStart() {
        super.onStart()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}