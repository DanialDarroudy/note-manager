package com.example.simplenote.ui.start.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.simplenote.R
import com.example.simplenote.core.dependencyinjection.DependencyProvider
import com.example.simplenote.core.ui.purpleColor
import com.example.simplenote.ui.login.activity.LoginActivity
import com.example.simplenote.ui.start.viewmodel.StartViewModel

class StartActivity : ComponentActivity() {

    private val viewModel: StartViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val navigateToLogin by viewModel.navigateToLogin.collectAsState()
            LaunchedEffect(navigateToLogin) {
                if (navigateToLogin) {
                    startActivity(Intent(this@StartActivity, LoginActivity::class.java))
                    finish()
                    viewModel.onNavigationHandled()
                }
            }
            OnboardingScreen(
                onGetStartedClick = { viewModel.onGetStartedClicked() }
            )
        }
    }
    @Composable
    fun OnboardingScreen(onGetStartedClick: () -> Unit) {
        Box(
            modifier = Modifier.Companion
                .fillMaxSize()
                .background(purpleColor)
                .padding(24.dp),
            contentAlignment = Alignment.Companion.Center
        ) {
            Column(
                horizontalAlignment = Alignment.Companion.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.Companion.fillMaxSize()
            ) {
                Spacer(modifier = Modifier.Companion.height(40.dp))

                Image(
                    painter = painterResource(id = R.drawable.startimage),
                    contentDescription = null,
                    modifier = Modifier.Companion
                        .fillMaxWidth()
                        .height(250.dp)
                )

                Text(
                    text = "Jot down anything you want to achieve, today or in the future.",
                    color = Color.Companion.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Companion.Medium,
                    textAlign = TextAlign.Companion.Center,
                    modifier = Modifier.Companion.padding(horizontal = 16.dp)
                )

                Button(
                    onClick = onGetStartedClick,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Companion.White),
                    shape = RoundedCornerShape(50),
                    modifier = Modifier.Companion
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    Text(
                        text = "Let's Get Started →",
                        color = purpleColor,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Companion.Bold
                    )
                }
            }
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