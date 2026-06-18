package com.example.fittrack.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fittrack.presentation.theme.CardDark
import com.example.fittrack.presentation.theme.TextGray
import com.example.fittrack.presentation.theme.TextWhite

@Composable
fun StatCard(
    title : String,
    value : String,
    modifier : Modifier = Modifier
){
    Column(
        modifier = modifier
            .background(
                color = CardDark,
                shape = RoundedCornerShape(20.dp)
            )
            .padding(20.dp)
    ){
        Text(
            text = value,
            color = TextWhite,
            fontSize = 24.sp
        )
        Text(
            text = title,
            color = TextGray,
            fontSize = 14.sp
        )
    }
}