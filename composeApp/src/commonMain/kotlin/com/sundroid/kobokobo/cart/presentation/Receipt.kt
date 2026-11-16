package com.sundroid.kobokobo.cart.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sundroid.kobokobo.core.presentation.components.CustomButton
import com.sundroid.kobokobo.core.presentation.components.CustomText
import com.sundroid.kobokobo.dashboard.utils.Util
import com.sundroid.kobokobo.dashboard.utils.Util.currencyFormat

@Composable
fun ReceiptScreen(receiptModel: ReceiptModel) {

    val padding = 20.dp
    LaunchedEffect(receiptModel){
        println("amir "+ receiptModel.cartItems.size)
    }
    Box(
        modifier = Modifier.fillMaxWidth()

    ) {
        val stateVertical = rememberScrollState(0)


        Box(
            modifier = Modifier.fillMaxSize()
                .verticalScroll(stateVertical)
        ) {
        Column(
            modifier = Modifier
                .fillMaxWidth().heightIn(max = 3000.dp, min = 0.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
//            receiptModel.logo?.let {
//                val painter = rememberAsyncImagePainter(
//                    model = receiptModel.logo,
//                    onSuccess = {
//                        val size = it.painter.intrinsicSize
//                        val imageLoadResult = if (size.width > 1 && size.height > 1) {
//                            Result.success(it.painter)
//                        } else {
//                            Result.failure(Exception("Invalid image dimensions"))
//                        }
//                    },
//                    onError = {
//                        it.result.throwable.printStackTrace()
//                    }
//                )
//                // Logo and Title
//                Image(
//                    painter = painter,
//                    contentDescription = "Logo",
//                    modifier = Modifier.width(200.dp)
//                )
//            }

            CustomText(
                text = receiptModel!!.address,
                style = 20.sp,
                modifier = Modifier.padding(top = padding),
                color = Color.DarkGray
            )



            CustomText(
                text = "Thank you for your Patronage!",
                fontWeight = FontWeight.Bold,
                style = 16.sp,
                modifier = Modifier.padding(top = padding),
                color = Color.DarkGray
            )
            CustomText(
                text = "Order No ${receiptModel.reference}",
                style = 14.sp,
                modifier = Modifier.padding(top = padding),
                color = Color.DarkGray
            )


            // Invoice Details
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = padding),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                CustomText(text = "Invoice: ${receiptModel.reference}", style = 14.sp, color = Color.DarkGray)
                CustomText(text = ((receiptModel.date)), style = 14.sp, color = Color.DarkGray)
                CustomText(text = (receiptModel.time), style = 14.sp, color = Color.DarkGray)
            }


            Row(
                modifier = Modifier.fillMaxWidth().padding(top = padding),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                repeat(30) {
                    Text("-", color = Color.DarkGray)
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = padding),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                CustomText(text = "Item", fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), color = Color.DarkGray)
                CustomText(
                    text = "Price(${Util.CURRENCY_CODE})",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                    color = Color.DarkGray
                )
                CustomText(text = "Qty", fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), color = Color.DarkGray)
                CustomText(
                    text = "Total(${Util.CURRENCY_CODE})",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                    color = Color.DarkGray
                )

            }





            LazyColumn(Modifier.padding(top = padding)) {
                items(receiptModel.cartItems) { item ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        CustomText(text = item.itemName!!, modifier = Modifier.weight(1f), color = Color.DarkGray)
                        CustomText(text = currencyFormat(item.price), modifier = Modifier.weight(1f), color = Color.DarkGray)
                        CustomText(text = item.quantity.toString(), modifier = Modifier.weight(1f), color = Color.DarkGray)
                        CustomText(text = currencyFormat(item.price * item.quantity), modifier = Modifier.weight(1f), color = Color.DarkGray)
                    }
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = padding),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                repeat(30) {
                    CustomText("-", color = Color.DarkGray)
                }
            }

            // Summary
            Column(
                modifier = Modifier.fillMaxWidth().padding(top = padding),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {

                Row(modifier = Modifier.width(200.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    CustomText(text = "Subtotal ", color = Color.DarkGray)
                    CustomText(text = currencyFormat(receiptModel.subTotal), color = Color.DarkGray)
                }
                Row(modifier = Modifier.width(200.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    CustomText(text = "Discount ", color = Color.DarkGray)
                    CustomText(text = currencyFormat(receiptModel.discount), color = Color.DarkGray)
                }

                Row(modifier = Modifier.width(200.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    CustomText(text = "TAX ", color = Color.DarkGray)
                    CustomText(text = currencyFormat(receiptModel.taxTotal), color = Color.DarkGray)
                }
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = padding),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    repeat(30) {
                        CustomText("-", color = Color.DarkGray)
                    }
                }
                Row(modifier = Modifier.width(200.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    CustomText(text = "Total ", fontWeight = FontWeight.Bold, style = 18.sp, color = Color.DarkGray)
                    CustomText(
                        text = currencyFormat(receiptModel.grandTotal),
                        fontWeight = FontWeight.Bold,
                        style = 18.sp, color = Color.DarkGray
                    )
                }
            }

            Row(modifier = Modifier.fillMaxWidth().padding(top = padding), horizontalArrangement = Arrangement.Start) {
                CustomText(text = "Cashier: ${receiptModel.cashier}", style = 14.sp, color = Color.DarkGray)
            }

            // Print Receipt Button
            CustomButton(
                modifier = Modifier.padding(top = padding),
                text = "Print Receipt",
                onClick = {
//                    CoroutineScope(Dispatchers.IO).launch {
//                        printReceiptGem(receiptModel)
//                    }
                },
            )
        }
    }
//        VerticalScrollbar(
//            modifier = Modifier.align(Alignment.CenterEnd)
//                .fillMaxHeight(),
//            adapter = rememberScrollbarAdapter(stateVertical)
//        )
    }
}
