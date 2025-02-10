package com.rgr.rutappa.android.screen.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rgr.rutappa.android.R

@Composable
fun Menu(
    onTapaClicked: () -> Unit,
    onPartnersClicked: () -> Unit,
    onCloseClicked: () -> Unit,
    onLogoutClicked: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xEE473829))
            .clickable {  },
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 28.dp),
            horizontalArrangement = Arrangement.End
        ) {
            Text(
                modifier = Modifier.clickable { onCloseClicked() },
                text = stringResource(R.string.x),
                fontSize = 32.sp,
                color = MaterialTheme.colorScheme.primary
            )
        }
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(48.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                modifier = Modifier.clickable { onTapaClicked() },
                text = stringResource(R.string.tapas),
                fontSize = 30.sp,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                modifier = Modifier.clickable { onPartnersClicked() },
                text = stringResource(R.string.partners),
                fontSize = 30.sp,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                modifier = Modifier.clickable { onLogoutClicked() },
                text = stringResource(R.string.logout),
                fontSize = 30.sp,
                color = MaterialTheme.colorScheme.primary
            )
        }
        Box(
            modifier = Modifier
                .size(24.dp)
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 48.dp)
        )
    }
}

@Preview
@Composable
fun MenuPreview() {
    Menu(
        onTapaClicked = {},
        onPartnersClicked = {},
        onCloseClicked = {},
        onLogoutClicked = {}
    )
}