package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.TycoonSoundManager
import com.example.viewmodel.TycoonUiState
import com.example.viewmodel.TycoonViewModel

@Composable
fun SettingsScreen(
    uiState: TycoonUiState,
    onSubmitSecretCode: (String) -> Boolean,
    onAdminAddMillion: () -> Unit,
    onAdminAddBillion: () -> Unit,
    onAdminUnlockManagers: () -> Unit,
    onAdminMaxBusinesses: () -> Unit,
    onAdminReset: () -> Unit,
    modifier: Modifier = Modifier
) {
    var secretCodeInput by remember { mutableStateOf("") }
    var codeError by remember { mutableStateOf(false) }
    var soundEnabled by remember { mutableStateOf(true) }
    var hapticsEnabled by remember { mutableStateOf(true) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp),
        contentPadding = PaddingValues(bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Audio & Preferences Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF131E29)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1F2F3E))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "GAME PREFERENCES",
                        color = Color(0xFF94A3B8),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.VolumeUp, contentDescription = null, tint = Color(0xFF00FF88))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "Sound & Audio Effects", color = Color.White, fontSize = 13.sp)
                        }
                        Switch(
                            checked = soundEnabled,
                            onCheckedChange = {
                                soundEnabled = it
                                TycoonSoundManager.isSoundEnabled = it
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color(0xFF070B0E),
                                checkedTrackColor = Color(0xFF00FF88)
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Security, contentDescription = null, tint = Color(0xFF00FF88))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "Haptic Vibration Feedback", color = Color.White, fontSize = 13.sp)
                        }
                        Switch(
                            checked = hapticsEnabled,
                            onCheckedChange = { hapticsEnabled = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color(0xFF070B0E),
                                checkedTrackColor = Color(0xFF00FF88)
                            )
                        )
                    }
                }
            }
        }

        // Lifetime Statistics Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF131E29)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1F2F3E))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "CAREER STATS",
                        color = Color(0xFF94A3B8),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    StatRow("Total Manual Clicks", "${uiState.totalTaps}")
                    StatRow("Tapping Revenue", "$${TycoonViewModel.formatCompact(uiState.totalEarnedFromTaps)}")
                    StatRow("Business Automation Revenue", "$${TycoonViewModel.formatCompact(uiState.totalEarnedFromBusinesses)}")
                    StatRow("Market Trades Executed", "${uiState.totalTradesCount}")
                }
            }
        }

        // SECRET ADMIN MENU & DEVELOPER SETTINGS
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF121B24)),
                border = androidx.compose.foundation.BorderStroke(
                    1.5.dp,
                    if (uiState.isDeveloperAdmin) Color(0xFF00FF88) else Color(0xFF334155)
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (uiState.isDeveloperAdmin) Icons.Default.Security else Icons.Default.Lock,
                                contentDescription = null,
                                tint = if (uiState.isDeveloperAdmin) Color(0xFF00FF88) else Color(0xFF94A3B8),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "SECRET DEVELOPER MENU",
                                color = if (uiState.isDeveloperAdmin) Color(0xFF00FF88) else Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 0.5.sp
                            )
                        }

                        if (uiState.isDeveloperAdmin) {
                            Box(
                                modifier = Modifier
                                    .background(Color(0xFF0D291F), RoundedCornerShape(6.dp))
                                    .border(1.dp, Color(0xFF00FF88), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "UNLOCKED",
                                    color = Color(0xFF00FF88),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    if (!uiState.isDeveloperAdmin) {
                        Text(
                            text = "Enter secret developer access code to unlock admin controls:",
                            color = Color(0xFF94A3B8),
                            fontSize = 11.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = secretCodeInput,
                                onValueChange = { newVal ->
                                    if (newVal.length > secretCodeInput.length) {
                                        TycoonSoundManager.playKeypadBeep(newVal.last())
                                    }
                                    secretCodeInput = newVal
                                    codeError = false
                                },
                                placeholder = { Text("Enter access code", color = Color(0xFF64748B), fontSize = 12.sp) },
                                singleLine = true,
                                modifier = Modifier.weight(1f).height(50.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFF00FF88),
                                    unfocusedBorderColor = Color(0xFF22303D),
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                ),
                                shape = RoundedCornerShape(10.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = {
                                    val success = onSubmitSecretCode(secretCodeInput)
                                    if (!success) codeError = true
                                },
                                modifier = Modifier.height(50.dp),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF00FF88),
                                    contentColor = Color(0xFF070B0E)
                                )
                            ) {
                                Text("UNLOCK", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }

                        if (codeError) {
                            Text(
                                text = "Invalid developer code.",
                                color = Color(0xFFFF3B5C),
                                fontSize = 11.sp,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    } else {
                        // DEVELOPER ADMIN CONTROLS
                        Text(
                            text = "Developer Admin Privileges Active. Instant cheat buttons:",
                            color = Color(0xFF94A3B8),
                            fontSize = 11.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = onAdminAddMillion,
                                modifier = Modifier.weight(1f).height(42.dp),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF0D291F),
                                    contentColor = Color(0xFF00FF88)
                                ),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF00FF88))
                            ) {
                                Text("+$1,000,000", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            }

                            Button(
                                onClick = onAdminAddBillion,
                                modifier = Modifier.weight(1f).height(42.dp),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF0D291F),
                                    contentColor = Color(0xFF00FF88)
                                ),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF00FF88))
                            ) {
                                Text("+$1,000,000,000", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = onAdminUnlockManagers,
                                modifier = Modifier.weight(1f).height(42.dp),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF14202B),
                                    contentColor = Color.White
                                ),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2C3E50))
                            ) {
                                Text("Auto All Managers", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = onAdminMaxBusinesses,
                                modifier = Modifier.weight(1f).height(42.dp),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF14202B),
                                    contentColor = Color.White
                                ),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2C3E50))
                            ) {
                                Text("Max All Levels", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = onAdminReset,
                            modifier = Modifier.fillMaxWidth().height(42.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF2B1218),
                                contentColor = Color(0xFFFF3B5C)
                            ),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFF3B5C))
                        ) {
                            Icon(Icons.Default.RestartAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("RESET GAME PROGRESS", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = Color(0xFF64748B), fontSize = 12.sp)
        Text(text = value, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}
