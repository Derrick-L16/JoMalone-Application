
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Button
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.example.jomalonemobileapplication.theme.Cormorant
import com.example.jomalonemobileapplication.theme.DarkBrown
import com.example.jomalonemobileapplication.theme.Cream
import com.example.jomalonemobileapplication.theme.LightBrown

import com.example.jomalonemobileapplication.feature.perfumeCustomization.domain.model.CustomizationOption
import com.example.jomalonemobileapplication.feature.perfumeCustomization.domain.model.CustomizationQuestion
import com.example.jomalonemobileapplication.feature.perfumeCustomization.presentation.ImageOptionCard
import com.example.jomalonemobileapplication.feature.scentTest.domain.model.ScentQuestion
import com.example.jomalonemobileapplication.feature.scentTest.domain.model.QuestionType
import com.example.jomalonemobileapplication.feature.scentTest.domain.model.ScentOption


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
            androidx.compose.foundation.layout.Row(
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

                // Add spacer if odd number of items in the last row
                if (rowOptions.size == 1) {
                    Spacer(modifier = Modifier.width(150.dp))
                }
            }
        }
    }
}

//@Composable
//fun ImageButtonQuestion(
//    question : ScentQuestion,
//    onOptionSelected: (Int) -> Unit
//){
//    LazyVerticalGrid(
//        columns = GridCells.Fixed(2),
//        verticalArrangement = Arrangement.spacedBy(8.dp),
//        horizontalArrangement = Arrangement.spacedBy(8.dp)
//    ){
//        items(question.options.size) { index ->
//            val option = question.options[index]
//
//
//            Card(
//                onClick = { onOptionSelected(index) },
//                modifier = Modifier
//                    .width(150.dp)
//                    .height(150.dp)
////                    .fillMaxWidth()
////                    .height(120.dp)
//                    .border(width = 2.dp, color = DarkBrown, shape = MaterialTheme.shapes.medium),
//                shape = MaterialTheme.shapes.medium,
//                colors = CardDefaults.cardColors(
//                    containerColor = Cream, contentColor = Color.Black
//                )
//            ){
//                Column(
//                    modifier = Modifier
//                        .padding(8.dp)
//                        .fillMaxSize(),
//                    horizontalAlignment = Alignment.CenterHorizontally,
//                    verticalArrangement = Arrangement.Center
//                ){
//                    option.imageResId?.let {imageResId ->
//                        Image(
//                            painter = painterResource(imageResId),
//                            contentDescription = stringResource(option.textResId),
//                            modifier = Modifier
//                                .size(110.dp)
//                                .clip(MaterialTheme.shapes.medium)
//                        )
////                        Spacer(modifier = Modifier.height(8.dp))
//                    }
//                    option.textResId?.let { textResId ->
//                        Text(
//                            text = stringResource(textResId),
//                            fontSize = 14.sp,
//                            style = MaterialTheme.typography.bodySmall,
//                            textAlign = TextAlign.Center
//                        )
//                    }
//                }
//            }
//        }
//    }

//    @Composable
//    fun ImageButtonQuestion(
//        question: ScentQuestion,
//        onOptionSelected: (Int) -> Unit,
//        modifier: Modifier = Modifier
//    ) {
//        Column(
//            modifier = modifier,
//            horizontalAlignment = Alignment.CenterHorizontally,
//        ){
//            Text(
//                text = stringResource(question.questionResId),
//                fontSize = 18.sp,
//                style = MaterialTheme.typography.titleMedium,
//                textAlign = TextAlign.Center,
//                fontWeight = FontWeight.SemiBold,
//                modifier = modifier.padding(top = 16.dp, bottom = 16.dp)
//            )
//            Spacer(modifier = Modifier.height(8.dp))
//            LazyVerticalGrid(
//                columns = GridCells.Fixed(2),
//                modifier = Modifier.fillMaxWidth()
//                    .height(350.dp),
//                verticalArrangement = Arrangement.spacedBy(16.dp),
//                horizontalArrangement = Arrangement.spacedBy(16.dp)
//            ){
//                items(question.options.size) { index ->
//                    val option = question.options[index]
//                    ImageOptionCard(
//                        option = option,
//                        onClick = { onOptionSelected(option)
//                        }
//                    )
//                }
//            }
//        }
//    }

//@Composable
//fun ImageOptionCard(
//    option: ScentOption,
//    onClick: () -> Unit,
//    modifier : Modifier = Modifier
//){
//    Card(
//        onClick = onClick,
//        modifier = modifier
//            .width(150.dp)
//            .height(150.dp)
//            .border(width = 2.dp, color = DarkBrown, shape = MaterialTheme.shapes.medium),
//        shape = MaterialTheme.shapes.medium,
//        colors = CardDefaults.cardColors(
//            containerColor = LightBrown, contentColor = Color.Black
//        )
//
//    ){
//        Column(
//            modifier = Modifier.fillMaxSize(),
//            horizontalAlignment =  Alignment.CenterHorizontally,
//            verticalArrangement = Arrangement.Center
//        ){
//            Image(
//                painter = painterResource(option.imageResId),
//                contentDescription = stringResource(option.textResId),
//
//                modifier = Modifier
//                    .size(80.dp)
//                    .padding(8.dp),
//                contentScale = ContentScale.Crop
//            )
//            Spacer(modifier = Modifier.height(8.dp))
//            Text(
//                text = stringResource(option.textResId),
//                style = MaterialTheme.typography.bodySmall,
//                fontSize = 14.sp,
//                textAlign = TextAlign.Center,
//                modifier = Modifier.padding(8.dp),
//            )
//        }
//    }

