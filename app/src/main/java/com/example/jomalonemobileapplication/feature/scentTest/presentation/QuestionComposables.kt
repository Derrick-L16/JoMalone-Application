
package com.example.jomalonemobileapplication.feature.scentTest.presentation

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.Row

import com.example.jomalonemobileapplication.theme.Cormorant
import com.example.jomalonemobileapplication.theme.DarkBrown
import com.example.jomalonemobileapplication.theme.Cream

import com.example.jomalonemobileapplication.feature.scentTest.domain.model.ScentQuestion
import com.example.jomalonemobileapplication.feature.scentTest.domain.model.QuestionType


@Composable
fun QuestionComposables(
    question : ScentQuestion,
    onOptionSelected: (Int) -> Unit,
    modifier : Modifier = Modifier
) {

    question.titleResId?.let { titleResId ->

        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(titleResId),
            fontFamily = Cormorant,
            textDecoration = TextDecoration.Underline,
            style = MaterialTheme.typography.headlineMedium,
        )
    }

    Spacer(modifier = Modifier.height(26.dp))
    Text(
        text = stringResource(question.questionResId),
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold
    )
    Spacer(modifier = Modifier.height(24.dp))

    when (question.questionType){
        QuestionType.BUTTON_BASED -> ButtonBasedQuestion(
            question = question,
            onOptionSelected = onOptionSelected
        )
        QuestionType.IMAGE_BUTTON -> ImageButtonQuestion(
            question = question,
            onOptionSelected = onOptionSelected
        )
    }
}


@Composable
fun ButtonBasedQuestion(
    question : ScentQuestion,
    onOptionSelected: (Int) -> Unit,
){
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)){
        question.options.forEachIndexed { index, option ->
            OutlinedButton(
                onClick = { onOptionSelected(index)},
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .height(50.dp),
                border = BorderStroke(2.dp, DarkBrown),
                colors = ButtonDefaults.buttonColors(containerColor = Cream, contentColor = Color.Black),
                shape = MaterialTheme.shapes.medium,
            ){
                Text(
                    text = stringResource(option.textResId)
                )
            }
        }
    }
}

@Composable
fun ImageButtonQuestion(
    question: ScentQuestion,
    onOptionSelected: (Int) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Group options into rows of 2
        question.options.chunked(2).forEachIndexed { rowIndex, rowOptions ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
            ) {
                rowOptions.forEachIndexed { colIndex, option ->
                    val optionIndex = rowIndex * 2 + colIndex

                    Card(
                        onClick = { onOptionSelected(optionIndex) },
                        modifier = Modifier
                            .width(170.dp)
                            .height(150.dp)
                            .border(
                                width = 2.dp,
                                color = DarkBrown,
                                shape = MaterialTheme.shapes.medium
                            ),
                        shape = MaterialTheme.shapes.medium,
                        colors = CardDefaults.cardColors(
                            containerColor = Cream,
                            contentColor = Color.Black
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(8.dp)
                                .fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            option.imageResId?.let { imageResId ->
                                Image(
                                    painter = painterResource(imageResId),
                                    contentDescription = stringResource(option.textResId),
                                    modifier = Modifier
                                        .size(110.dp)
                                        .clip(MaterialTheme.shapes.medium)
                                )
                            }

                            Text(
                                text = stringResource(option.textResId),
                                fontSize = 14.sp,
                                style = MaterialTheme.typography.bodySmall,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                // add spacer if odd number of items in the last row
                if (rowOptions.size == 1) {
                    Spacer(modifier = Modifier.width(150.dp))
                }
            }
        }
    }
}


