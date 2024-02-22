package com.rgr.rutappa.android.screen.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.rgr.rutappa.android.R

@Composable
fun Header(
    hasLogout: Boolean = true,
    onItemClicked: () -> Unit = {}
) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.BottomEnd
    ) {
        Box(contentAlignment = Alignment.Center) {
            Image(
                painter = painterResource(id = R.drawable.header_onda),
                contentDescription = null
            )
            if(hasLogout) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.TopEnd
                ) {
                    Image(
                        modifier = Modifier
                            .padding(end = 16.dp, bottom = 48.dp)
                            .size(24.dp)
                            .clickable { onItemClicked() },
                        painter = painterResource(id = R.drawable.ic_logout),
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
                    )
                }
            }
        }
        Image(
            modifier = Modifier
                .padding(end = 16.dp, top = 24.dp)
                .size(48.dp),
            painter = painterResource(id = R.drawable.tierra_sabor_logo),
            contentDescription = null
        )
    }
}