package com.example.jomalonemobileapplication.feature.perfumeCustomization.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jomalonemobileapplication.theme.DarkBrown
import com.example.jomalonemobileapplication.theme.LightBrown
import com.example.jomalonemobileapplication.feature.perfumeCustomization.domain.model.CustomizationOption
import com.example.jomalonemobileapplication.feature.perfumeCustomization.domain.model.CustomizationQuestion
import com.example.jomalonemobileapplication.theme.Cream

@Composable
fun ImageBasedQuestion(
    question: CustomizationQuestion,
    onOptionSelected: (CustomizationOption) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ){
        Text(
            text = stringResource(question.questionResId),
            fontSize = 18.sp,
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.SemiBold,
            modifier = modifier.padding(top = 16.dp, bottom = 16.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxWidth()
                .height(350.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ){
            items(question.options.size) { index ->
                val option = question.options[index]
                ImageOptionCard(
                    option = option,
                    onClick = { onOptionSelected(option)
                    }
                )
            }
        }
    }
}

@Composable
fun ImageOptionCard(
    option: CustomizationOption,
    onClick: () -> Unit,
    modifier : Modifier = Modifier
    ){
    Card(
        onClick = onClick,
        modifier = modifier
            .width(150.dp)
            .height(150.dp)
            .border(width = 2.dp, color = DarkBrown, shape = MaterialTheme.shapes.medium),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = Cream, contentColor = Color.Black
        )

    ){
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment =  Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ){
            Image(
                painter = painterResource(option.imageResId),
                contentDescription = stringResource(option.textResId),

                modifier = Modifier
                    .size(80.dp)
                    .padding(8.dp),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(option.textResId),
                style = MaterialTheme.typography.bodySmall,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(8.dp),
            )
        }
    }
}