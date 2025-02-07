package com.rgr.rutappa.android.screen.common

import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import com.rgr.rutappa.android.R

@Composable
fun SocialWall() {
    val context = LocalContext.current
    Box {
        Image(
            painter = painterResource(id = R.drawable.ic_onda),
            contentDescription = null,
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.secondary)
        )
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 36.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally)
        ) {
            Image(
                modifier = Modifier
                    .size(24.dp)
                    .clickable {
                        val intent = Intent(ACTION_VIEW)
                        intent.setData(Uri.parse("https://www.instagram.com/destapalaslegumbres/"))
                        startActivity(context, intent, null)
                    },
                painter = painterResource(id = R.drawable.ic_instagram),
                contentDescription = null,
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
            )
            Image(
                modifier = Modifier
                    .size(24.dp)
                    .clickable {
                        val intent = Intent(ACTION_VIEW)
                        intent.setData(Uri.parse("https://www.facebook.com/destapalaslegumbres"))
                        startActivity(context, intent, null)
                    },
                painter = painterResource(id = R.drawable.ic_facebook),
                contentDescription = null,
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
            )
            Image(
                modifier = Modifier
                    .size(24.dp)
                    .clickable {
                        val intent = Intent(ACTION_VIEW)
                        intent.setData(Uri.parse("https://twitter.com/dstapalegumbres"))
                        startActivity(context, intent, null)
                    },
                painter = painterResource(id = R.drawable.ic_twitterx),
                contentDescription = null,
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
            )
        }
    }
}