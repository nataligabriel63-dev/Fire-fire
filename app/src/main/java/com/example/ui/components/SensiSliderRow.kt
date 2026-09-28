package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CeifadorBorder
import com.example.ui.theme.CeifadorBorderActive
import com.example.ui.theme.CeifadorCyanGlow
import com.example.ui.theme.CeifadorRedPrimary
import com.example.ui.theme.CeifadorSurfaceHighlight
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun SensiSliderRow(
    title: String,
    subtitle: String,
    value: Int,
    range: ClosedFloatingPointRange<Float> = 0f..200f,
    accentColor: Color = CeifadorRedPrimary,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(CeifadorSurfaceHighlight.copy(alpha = 0.5f))
            .border(1.dp, CeifadorBorder, RoundedCornerShape(10.dp))
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title.uppercase(),
                    color = TextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = subtitle,
                    color = TextSecondary,
                    fontSize = 11.sp,
                    lineHeight = 14.sp
                )
            }

            // Numeric value badge with step controls
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(CeifadorSurfaceHighlight)
                        .border(1.dp, CeifadorBorderActive, RoundedCornerShape(6.dp))
                        .clickable { onValueChange((value - 1).coerceIn(range.start.toInt(), range.endInclusive.toInt())) }
                        .testTag("decrease_${title.lowercase().replace(" ", "_")}"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Remove,
                        contentDescription = "Diminuir $title",
                        tint = TextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .height(32.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(CeifadorBorder)
                        .border(1.dp, accentColor.copy(alpha = 0.6f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = value.toString(),
                        color = accentColor,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(CeifadorSurfaceHighlight)
                        .border(1.dp, CeifadorBorderActive, RoundedCornerShape(6.dp))
                        .clickable { onValueChange((value + 1).coerceIn(range.start.toInt(), range.endInclusive.toInt())) }
                        .testTag("increase_${title.lowercase().replace(" ", "_")}"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Aumentar $title",
                        tint = TextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        // Granular Slider
        Slider(
            value = value.toFloat(),
            onValueChange = { onValueChange(it.toInt()) },
            valueRange = range,
            colors = SliderDefaults.colors(
                thumbColor = accentColor,
                activeTrackColor = accentColor,
                inactiveTrackColor = CeifadorBorder
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 2.dp)
                .testTag("slider_${title.lowercase().replace(" ", "_")}")
        )
    }
}
