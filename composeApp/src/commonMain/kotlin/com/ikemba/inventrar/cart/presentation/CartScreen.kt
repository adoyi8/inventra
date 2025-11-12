package com.ikemba.inventrar.cart.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ikemba.inventrar.core.presentation.PrimaryColor
import com.ikemba.inventrar.core.presentation.components.CustomButton
import com.ikemba.inventrar.core.presentation.components.CustomText
import com.ikemba.inventrar.dashboard.presentation.DashboardViewModel
import com.ikemba.inventrar.dashboard.presentation.components.SmallItemImage
import com.ikemba.inventrar.dashboard.utils.Util
import com.ikemba.inventrar.dashboard.utils.Util.currencyFormat
import inventrar.composeapp.generated.resources.Res
import inventrar.composeapp.generated.resources.add
import inventrar.composeapp.generated.resources.figma_card
import inventrar.composeapp.generated.resources.figma_cash
import inventrar.composeapp.generated.resources.figma_transfer
import inventrar.composeapp.generated.resources.pause
import inventrar.composeapp.generated.resources.remove
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CartScreen(viewModel: DashboardViewModel, modifier: Modifier = Modifier) {
    val state = viewModel.state.collectAsStateWithLifecycle()
    Column(modifier = modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {

        Row(modifier = Modifier.fillMaxWidth().weight(1f), horizontalArrangement = Arrangement.SpaceBetween) {
            Column() {
                CustomText("Your cart", fontWeight = FontWeight.SemiBold)
                CustomText(state.value.reference)
            }
            Column() {
                CustomButton(
                    onClick = { viewModel.holdOrder() },
                    text = "Put on hold",
                    backgroundColor = PrimaryColor,
                    contentColor = Color.White
                )
            }
        }

        // --- Enhancement: Use LazyColumn for Cart Items ---
        Box(
            modifier = Modifier.fillMaxWidth().weight(9f).padding(7.dp)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(state.value.cartItems, key = { it.itemId!! }) { item ->
                    // Add animation for item add/remove
                    this@Column.AnimatedVisibility(
                        visible = true,
                        enter = slideInHorizontally() + fadeIn(),
                        exit = slideOutHorizontally() + fadeOut(),
                        modifier = Modifier.animateItemPlacement()
                    ) {
                        Row(
                            Modifier.fillMaxWidth().clip(RoundedCornerShape(3.dp)).border(
                                BorderStroke(1.dp, Color.Gray)
                            ),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                          //  SmallItemImage(item.image, modifier = Modifier.padding(4.dp))
                            CustomText(item.itemName!!, modifier = Modifier.weight(8f).padding(start = 5.dp))
                            CustomText(currencyFormat(item.price), modifier = Modifier.weight(2.5f))
                            Row(Modifier.weight(3.5f), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically) {
                                IconButton(
                                    onClick = { viewModel.subtractItemFromCart(item) },
                                ) {
                                    Icon(
                                        modifier = Modifier.size(15.dp),
                                        painter = painterResource(Res.drawable.remove),
                                        contentDescription = "",
                                        tint = PrimaryColor
                                    )
                                }
                                Row(modifier = Modifier.width(20.dp), horizontalArrangement = Arrangement.Center) {
                                    CustomText(item.quantity.toString(), modifier = Modifier.fillMaxWidth())
                                }

                                IconButton(
                                    onClick = { viewModel.addItemToCart(item) },
                                ) {
                                    Icon(
                                        modifier = Modifier.size(15.dp),
                                        painter = painterResource(Res.drawable.add),
                                        contentDescription = "",
                                        tint = PrimaryColor
                                    )
                                }
                            }
                            CustomText(currencyFormat(item.getSubTotal()), modifier = Modifier.weight(3f))
                            IconButton(onClick = {
                                viewModel.removeItemFromCart(item)
                            }, modifier = Modifier.weight(1f)) {
                                Icon(Icons.Default.Clear, contentDescription = "", tint = Color.Red)
                            }
                        }
                    }
                }
            }
//            VerticalScrollbar(
//                modifier = Modifier.align(Alignment.CenterEnd)
//                    .fillMaxHeight(),
//                adapter = rememberScrollbarAdapter(stateVertical)
//            )
        }
        // --- End Enhancement ---

        Column(
            modifier = Modifier.fillMaxWidth().weight(4.5f).clip(
                RoundedCornerShape(10.dp)
            )
        ) {
            Column(modifier = Modifier.fillMaxWidth().weight(5f).padding(10.dp)) {
                CustomText("Order Summary", fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp).weight(1f))
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp).weight(1f),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CustomText("Subtotal")
                    CustomText(currencyFormat(state.value.cartItems.sumByDouble { it.price * it.quantity }))

                }
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp).weight(1f),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CustomText("Discount")
                    CustomText(currencyFormat(state.value.cartItems.sumByDouble { it.discount!!.toDouble() * it.quantity }))

                }
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp).weight(1f),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CustomText("TAX")
                    CustomText(currencyFormat(state.value.cartItems.sumByDouble { it.price * it.quantity * it.vatRate!! * 0.01 }))

                }

            }
            Row(
                modifier = Modifier.fillMaxWidth().weight(1f),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CustomText("Total", fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(horizontal = 20.dp))
                CustomText(
                    currencyFormat(state.value.cartItems.sumByDouble { ((it.price * (it.vatRate!! * 0.01)) * it.quantity) + (it.price * it.quantity) }), fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )

            }
        }
        HorizontalDivider(Modifier.padding(vertical = 12.dp))
        Column(
            modifier = Modifier.fillMaxWidth().padding(10.dp).weight(4.5f).clip(
                RoundedCornerShape(10.dp)
            )
        ) {
            Column(modifier = Modifier.fillMaxWidth().weight(4f).padding(horizontal = 10.dp)) {
                CustomText("Payment Method", fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                Spacer(modifier = Modifier.weight(0.2f))
                Row(
                    modifier = Modifier.fillMaxWidth().weight(1f).clickable(
                        onClick = {
                            viewModel.setPaymentMethod(Util.PAYMENT_METHOD_CARD)
                            viewModel.toggleShowConfirmTransaction(true)
                        }
                    ),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Image(
                        painter = painterResource(Res.drawable.figma_card),
                        contentDescription = "MasterCard",
                        modifier = Modifier.size(24.dp),
                        // colorFilter = ColorFilter.tint(Color.Cyan)
                    )
                    CustomText("Paid with card")

                }
                Row(
                    modifier = Modifier.fillMaxWidth().weight(1f)
                        .clickable(onClick = {
                            viewModel.setPaymentMethod(Util.PAYMENT_METHOD_CASH)
                            viewModel.toggleShowConfirmTransaction(true)
                        }),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Image(
                        painter = painterResource(Res.drawable.figma_cash),
                        contentDescription = "MasterCard",
                        modifier = Modifier.size(24.dp),
                        // colorFilter = ColorFilter.tint(Color.Green)
                    )
                    CustomText("Paid with cash")

                }
                Row(
                    modifier = Modifier.fillMaxWidth().weight(1f)
                        .clickable(
                            onClick = {
                                viewModel.setPaymentMethod(Util.PAYMENT_METHOD_TRANSFER)
                                viewModel.toggleShowConfirmTransaction(true)
                            }
                        ),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Image(
                        painter = painterResource(Res.drawable.figma_transfer),
                        contentDescription = "Bank Transfer",
                        modifier = Modifier.size(24.dp),
                        // colorFilter = ColorFilter.tint(CustomYellow)
                    )
                    CustomText("Paid with transfer")

                }

            }

            Row(modifier = Modifier.fillMaxWidth().weight(1.2f), horizontalArrangement = Arrangement.Center) {
                CustomButton(onClick = { viewModel.clearCart() }, text = "Clear")
            }
        }
    }
}