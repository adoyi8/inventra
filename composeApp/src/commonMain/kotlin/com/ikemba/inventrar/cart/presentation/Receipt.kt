package com.ikemba.inventrar.cart.presentation

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.rememberAsyncImagePainter
import com.ikemba.inventrar.core.presentation.components.CustomButton
import com.ikemba.inventrar.core.presentation.components.CustomText
import com.ikemba.inventrar.dashboard.utils.Util
import com.ikemba.inventrar.dashboard.utils.Util.currencyFormat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import printReceiptGem

@Composable
fun ReceiptScreen(receiptModel: ReceiptModel) {

    val padding = 20.dp
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
            receiptModel.logo?.let {
                val painter = rememberAsyncImagePainter(
                    model = receiptModel.logo,
                    onSuccess = {
                        val size = it.painter.intrinsicSize
                        val imageLoadResult = if (size.width > 1 && size.height > 1) {
                            Result.success(it.painter)
                        } else {
                            Result.failure(Exception("Invalid image dimensions"))
                        }
                    },
                    onError = {
                        it.result.throwable.printStackTrace()
                    }
                )
                // Logo and Title
                Image(
                    painter = painter,
                    contentDescription = "Logo",
                    modifier = Modifier.width(200.dp)
                )
            }

            Text(
                text = receiptModel!!.address,
                fontSize = 20.sp,
                modifier = Modifier.padding(top = padding)
            )



            CustomText(
                text = "Thank you for your Patronage!",
                fontWeight = FontWeight.Bold,
                size = 16.sp,
                modifier = Modifier.padding(top = padding)
            )
            CustomText(
                text = "Order No ${receiptModel.reference}",
                size = 14.sp,
                modifier = Modifier.padding(top = padding)
            )


            // Invoice Details
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = padding),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                CustomText(text = "Invoice: ${receiptModel.reference}", size = 14.sp)
                CustomText(text = ((receiptModel.date)), size = 14.sp)
                CustomText(text = (receiptModel.time), size = 14.sp)
            }


            Row(
                modifier = Modifier.fillMaxWidth().padding(top = padding),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                repeat(30) {
                    Text("-")
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = padding),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Text(text = "Item", fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                Text(
                    text = "Price(${Util.CURRENCY_CODE})",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                Text(text = "Qty", fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                Text(
                    text = "Total(${Util.CURRENCY_CODE})",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )

            }




            LazyColumn(Modifier.padding(top = padding)) {
                items(receiptModel.cartItems) { item ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = item.itemName!!, modifier = Modifier.weight(1f))
                        Text(text = currencyFormat(item.price), modifier = Modifier.weight(1f))
                        Text(text = item.quantity.toString(), modifier = Modifier.weight(1f))
                        Text(text = currencyFormat(item.price * item.quantity), modifier = Modifier.weight(1f))
                    }
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = padding),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                repeat(30) {
                    Text("-")
                }
            }

            // Summary
            Column(
                modifier = Modifier.fillMaxWidth().padding(top = padding),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {

                Row(modifier = Modifier.width(200.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    CustomText(text = "Subtotal ")
                    CustomText(text = currencyFormat(receiptModel.subTotal))
                }
                Row(modifier = Modifier.width(200.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    CustomText(text = "Discount ")
                    CustomText(text = currencyFormat(receiptModel.discount))
                }

                Row(modifier = Modifier.width(200.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    CustomText(text = "TAX ")
                    CustomText(text = currencyFormat(receiptModel.taxTotal))
                }
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = padding),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    repeat(30) {
                        Text("-")
                    }
                }
                Row(modifier = Modifier.width(200.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    CustomText(text = "Total ", fontWeight = FontWeight.Bold, size = 18.sp)
                    CustomText(
                        text = currencyFormat(receiptModel.grandTotal),
                        fontWeight = FontWeight.Bold,
                        size = 18.sp
                    )
                }
            }

            Row(modifier = Modifier.fillMaxWidth().padding(top = padding), horizontalArrangement = Arrangement.Start) {
                CustomText(text = "Cashier: ${receiptModel.cashier}", size = 14.sp)
            }

            // Print Receipt Button
            CustomButton(
                modifier = Modifier.padding(top = padding),
                text = "Print Receipt",
                onClick = {
                    CoroutineScope(Dispatchers.IO).launch {
                        printReceiptGem(receiptModel)
                    }
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
