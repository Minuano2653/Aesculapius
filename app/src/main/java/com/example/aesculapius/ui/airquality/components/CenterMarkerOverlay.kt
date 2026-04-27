package com.example.aesculapius.ui.airquality.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.aesculapius.R

@Composable
fun BoxScope.CenterMarkerOverlay() {
    Image(
        painter = painterResource(id = R.drawable.map_pin),
        contentDescription = null,
        modifier = Modifier
            .align(Alignment.Center)
            .size(width = 32.dp, height = 40.dp)
            .offset(y = (-20).dp)
    )
}
