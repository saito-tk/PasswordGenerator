package com.saitotk.passwordgenerator.presentation.password_generation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.runtime.remember
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.saitotk.passwordgenerator.domain.model.PasswordConfig

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordScreen(
    uiState: PasswordUiState,
    onEvent: (PasswordEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    if (uiState.isLoading) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
        item {
            Text(
                text = "パスワード生成",
                style = MaterialTheme.typography.headlineMedium
            )
        }

        item {
            PasswordLengthSection(
                length = uiState.config.length,
                onLengthChange = { onEvent(PasswordEvent.UpdateLength(it)) }
            )
        }

        item {
            PasswordCountSection(
                count = uiState.config.count,
                onCountChange = { onEvent(PasswordEvent.UpdateCount(it)) }
            )
        }

        item {
            CharacterTypeSection(
                config = uiState.config,
                onUppercaseChange = { onEvent(PasswordEvent.UpdateUseUppercase(it)) },
                onLowercaseChange = { onEvent(PasswordEvent.UpdateUseLowercase(it)) },
                onNumbersChange = { onEvent(PasswordEvent.UpdateUseNumbers(it)) },
                onSymbolsChange = { onEvent(PasswordEvent.UpdateUseSymbols(it)) }
            )
        }

        if (uiState.config.useSymbols) {
            item {
                SymbolSelectionSection(
                    config = uiState.config,
                    isSelectAll = uiState.isSelectAllSymbols,
                    onSymbolToggle = { symbol, selected ->
                        onEvent(PasswordEvent.UpdateSelectedSymbol(symbol, selected))
                    },
                    onSelectAllToggle = { onEvent(PasswordEvent.SelectAllSymbols(it)) },
                    onCustomSymbolsChange = { onEvent(PasswordEvent.UpdateCustomSymbols(it)) }
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = uiState.config.avoidRepeatingChars,
                    onCheckedChange = {
                        onEvent(PasswordEvent.UpdateAvoidRepeatingChars(it))
                    }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("同じ文字列が連続で使われない")
            }
        }

            if (uiState.errorMessage != null) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = uiState.errorMessage,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                modifier = Modifier.weight(1f)
                            )
                            TextButton(
                                onClick = { onEvent(PasswordEvent.ClearError) }
                            ) {
                                Text("閉じる")
                            }
                        }
                    }
                }
            }
        }
        
        // 固定された生成ボタン
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // 生成中のインジケーター
            if (uiState.isGenerating) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 3.dp
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "パスワードを生成中...",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            
            Button(
                onClick = { onEvent(PasswordEvent.GeneratePasswords) },
                enabled = uiState.isGenerateButtonEnabled,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("生成")
            }
        }
    }
}

@Composable
private fun PasswordLengthSection(
    length: Int,
    onLengthChange: (Int) -> Unit
) {
    Column {
        Text(
            text = "文字数: $length",
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = length.toString(),
            onValueChange = { value ->
                value.toLongOrNull()?.let { newLength ->
                    if (newLength >= 4 && newLength <= 100_000_000) {
                        onLengthChange(newLength.toInt())
                    }
                }
            },
            label = { Text("文字数 (4文字以上、最大1億桁)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun PasswordCountSection(
    count: Int,
    onCountChange: (Int) -> Unit
) {
    Column {
        Text(
            text = "生成件数: $count",
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(8.dp))
        Slider(
            value = count.toFloat(),
            onValueChange = { onCountChange(it.toInt()) },
            valueRange = 1f..25f,
            steps = 23,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun CharacterTypeSection(
    config: PasswordConfig,
    onUppercaseChange: (Boolean) -> Unit,
    onLowercaseChange: (Boolean) -> Unit,
    onNumbersChange: (Boolean) -> Unit,
    onSymbolsChange: (Boolean) -> Unit
) {
    Column {
        Text(
            text = "使用文字の選択",
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(8.dp))
        
        CheckboxRow(
            text = "英字（大文字）",
            checked = config.useUppercase,
            onCheckedChange = onUppercaseChange
        )
        
        CheckboxRow(
            text = "英字（小文字）",
            checked = config.useLowercase,
            onCheckedChange = onLowercaseChange
        )
        
        CheckboxRow(
            text = "数字",
            checked = config.useNumbers,
            onCheckedChange = onNumbersChange
        )
        
        CheckboxRow(
            text = "記号",
            checked = config.useSymbols,
            onCheckedChange = onSymbolsChange
        )
    }
}

@Composable
private fun CheckboxRow(
    text: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(
                selected = checked,
                onClick = { onCheckedChange(!checked) },
                role = Role.Checkbox
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = null
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text)
    }
}

@Composable
private fun SymbolSelectionSection(
    config: PasswordConfig,
    isSelectAll: Boolean,
    onSymbolToggle: (String, Boolean) -> Unit,
    onSelectAllToggle: (Boolean) -> Unit,
    onCustomSymbolsChange: (String) -> Unit
) {
    Column {
        Text(
            text = "記号設定",
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(8.dp))
        
        CheckboxRow(
            text = "すべて選択",
            checked = isSelectAll,
            onCheckedChange = onSelectAllToggle
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // 記号を複数行で表示するためのFlowRow風レイアウト
        val symbols = remember { config.availableSymbols.toList() }
        val chunkedSymbols = remember(symbols) { symbols.chunked(6) } // 1行あたり6個
        
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            chunkedSymbols.forEach { rowSymbols ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    rowSymbols.forEach { symbol ->
                        FilterChip(
                            onClick = {
                                val isSelected = config.selectedSymbols.contains(symbol)
                                onSymbolToggle(symbol, !isSelected)
                            },
                            label = { Text(symbol) },
                            selected = config.selectedSymbols.contains(symbol),
                            modifier = Modifier.weight(1f)
                        )
                    }
                    // 最後の行で足りない分はスペーサーで埋める
                    repeat(6 - rowSymbols.size) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        OutlinedTextField(
            value = config.customSymbols,
            onValueChange = onCustomSymbolsChange,
            label = { Text("カスタム記号") },
            placeholder = { Text("任意の記号を入力") },
            modifier = Modifier.fillMaxWidth()
        )
    }
}