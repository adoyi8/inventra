package com.ikemba.inventrar.dashboard.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ikemba.inventrar.core.presentation.PrimaryColor
import com.ikemba.inventrar.core.presentation.components.CustomText
import com.ikemba.inventrar.dashboard.domain.Category
import com.ikemba.inventrar.dashboard.domain.Item
import com.ikemba.inventrar.dashboard.presentation.DashboardState
import com.ikemba.inventrar.dashboard.presentation.DashboardViewModel
import com.ikemba.inventrar.dashboard.utils.Util.currencyFormat
import inventrar.composeapp.generated.resources.Res
import inventrar.composeapp.generated.resources.scanner
import inventrar.composeapp.generated.resources.search
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel


@Composable
fun POSSCreenBody(viewModel: DashboardViewModel, modifier: Modifier = Modifier) {

    val state by viewModel.state.collectAsStateWithLifecycle()

    val selectedCategory = remember { mutableStateOf<Category>(Category("1", "1", "1")) }
    var searchValue by remember { mutableStateOf("") }
    // Search Bar

    Box(modifier = modifier) {
        Column() {
            OutlinedTextField(
                value = searchValue,
                onValueChange = { it ->
                    searchValue = it
                    viewModel.searchItems(it)
                },
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .padding(8.dp),
                placeholder = { Text("Search product list or Scan QR Code") },
                leadingIcon = {
                    Image(
                        painter = painterResource(Res.drawable.search),
                        contentDescription = "",
                        modifier = Modifier.size(25.dp)
                    )
                },
                trailingIcon = {
                    Image(
                        painter = painterResource(Res.drawable.scanner),
                        contentDescription = ""
                    )
                }
            )


            Column(Modifier.fillMaxWidth().fillMaxHeight().background(Color(0xFFe4e4e4))) {
                // Categories with Animated Selection
                CustomText(
                    "Categories",
                    color = Color.Black,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(8.dp),
                    size = 18.sp
                )
                Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                    state.categories.forEach { category ->
                        Text(
                            text = category.categoryName,
                            modifier = Modifier
                                .padding(8.dp)
                                .clickable {
                                    selectedCategory.value = category
                                    viewModel.selectCategory(category.categoryId)
                                }
                                .border(
                                    1.dp,
                                    if (selectedCategory.value == category) Color.Blue else Color.Gray,
                                    RoundedCornerShape(8.dp)
                                )
                                .padding(12.dp),
                            //      .animateContentSize(animationSpec = tween(300)),
                            color = if (selectedCategory.value == category) Color.Blue else Color.Black
                        )
                    }
                }


                CustomText(
                    "Product List",
                    color = Color.Black,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(8.dp),
                    size = 18.sp
                )
                // Product List with Animated Appearance
                val gridState = rememberLazyGridState()
                Box(
                    modifier = Modifier.fillMaxSize()
                                        ) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(5),
                    state = gridState,
                    modifier = Modifier.padding(end = 16.dp)

                    ) {
                    items(state.visibleItems) { item ->
                    //////    AnimatedVisibility(visible = true) {
                            ProductCard(viewModel = viewModel, item = item)
                        /////}
                    }
                }
//                    VerticalScrollbar(
//                        modifier = Modifier.align(Alignment.CenterEnd).padding(start = 16.dp).fillMaxHeight(),
//                        adapter = rememberScrollbarAdapter(gridState) // <-- Use gridState
//                    )
            }
            }
        }
        FloatingActionButton({viewModel.getProducts()}, modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp), containerColor = PrimaryColor){
            Icon(Icons.Filled.Refresh, contentDescription = "", tint = Color.White)
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductCard(viewModel: DashboardViewModel, item: Item) {
    Card(
        modifier = Modifier
            .padding(8.dp)
           // .clickable {viewModel.addItemToCart(item) }
            .pointerInput(item) { // Pass item or any key that makes sense to re-trigger this block if item changes
                detectTapGestures(
                    onTap = {
                        println("ProductCard tapped via pointerInput for item: ${item.name}") // For debugging
                        viewModel.addItemToCart(item)
                    },
                    onPress = {
                        // You can also react to press events if needed
                        println("ProductCard pressed via pointerInput for item: ${item.name}")
                        tryAwaitRelease() // Important to wait for release
                        println("ProductCard released via pointerInput for item: ${item.name}")
                    }
                )
            }
            .animateContentSize(animationSpec = tween(300)),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(0.dp)) {
            Box(modifier = Modifier.fillMaxWidth().height(100.dp).background(Color.LightGray)){
                // Placeholder Image
                item.image?.let {
                    BlurredImageBackground(imageUrl = item.image, content = {})
                }

            } // Placeholder Image
            CustomText(text = item.name, fontWeight = FontWeight.Light, modifier = Modifier.padding(horizontal = 8.dp), textAlign = TextAlign.Left)
            Row(Modifier.fillMaxWidth().padding(horizontal = 8.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                CustomText(text = "${currencyFormat(item.unitPrice)}", size = 14.sp, fontWeight = FontWeight.SemiBold)
                IconButton( onClick = {
                    viewModel.addItemToCart(item)
                }, )
                {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "",
                        tint = PrimaryColor
                        )
                }
            }
        }
    }
}