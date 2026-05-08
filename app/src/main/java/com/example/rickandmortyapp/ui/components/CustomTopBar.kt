package com.example.rickandmortyapp.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTopBar() {

    CenterAlignedTopAppBar(

        title = {

            AnimatedGradientText(
                text = "RICK & MORTY",
                fontSize = 18.sp
            )

        },

        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp),

        colors = TopAppBarDefaults.topAppBarColors(

            containerColor = MaterialTheme.colorScheme.background,

            scrolledContainerColor = MaterialTheme.colorScheme.background

        )
    )
}