package com.example.avaliacao_diogo_aguiar_1709169

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument

// --- Dados e modelos ---
data class ImageItem(
    val id: Int,
    val resId: Int,
    val name: String,
    val description: String,
    val bottlePrice: Float,
    val boxPrice: Float
)
data class CartItem(val product: ImageItem, val quantity: Int, val isBox: Boolean)

val cart = mutableStateListOf<CartItem>()

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Avaliacao_Diogo_Aguiar_1709169Theme {
                AppNavigation()
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    val douroWines = listOf(
        ImageItem(
            1,
            R.drawable.imagem1,
            "Bafarela Grande Reserva Tinto 2022",
            "“Bafarela Grande Reserva é um vinho que exprime, no seu melhor, a elegância de uma das especialidades da Casa Brites Aguiar. Uma edição limitada apresentada somente em anos de exceção.”",
            bottlePrice = 15.0f,
            boxPrice = 40.0f // preço para a caixa de 3 garrafas
        ),
        ImageItem(
            2,
            R.drawable.imagem3,
            "Brites de Aguiar Tinto 2019 75cl",
            "É um vinho intenso e elegante, ideal para acompanhar carnes vermelhas, caça grossa e queijos intensos.",
            bottlePrice = 12.0f,
            boxPrice = 33.0f
        )
    )

    val daoWines = listOf(
        ImageItem(
            3,
            R.drawable.imagem2,
            "Adega de Penalva – Touriga Nacional 2022",
            "A Touriga-Nacional é o ex libris das castas tintas do Dão e do País, sendo grandemente responsável pelo perfume dos grandes tintos do Dão. Com este vinho toda a nobreza da casta é reconhecida. Aristocrático!",
            bottlePrice = 13.0f,
            boxPrice = 35.0f
        )
    )

    NavHost(navController, startDestination = "home") {
        composable("home") {
            HomeScreen(onRegionSelected = { region ->
                navController.navigate("menu/$region")
            })
        }

        composable(
            "menu/{region}",
            arguments = listOf(navArgument("region") { type = NavType.StringType })
        ) { backStackEntry ->
            val region = backStackEntry.arguments?.getString("region")
            val images = if (region == "douro") douroWines else daoWines
            ImageMenu(
                images = images,
                onImageClick = { selectedItem ->
                    navController.navigate("details/${selectedItem.id}/$region")
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            "details/{imageId}/{region}",
            arguments = listOf(
                navArgument("imageId") { type = NavType.IntType },
                navArgument("region") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("imageId")
            val region = backStackEntry.arguments?.getString("region")
            val list = if (region == "douro") douroWines else daoWines
            val item = list.find { it.id == id }
            item?.let {
                ImageDetails(
                    image = it,
                    onBack = { navController.popBackStack() },
                    onCartClick = { navController.navigate("cart") }
                )
            }
        }

        composable("cart") {
            CartScreen(onBack = { navController.popBackStack() })
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(onRegionSelected: (String) -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Escolha a Região") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.vinha_fundo),
                contentDescription = "Imagem da vinha",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { onRegionSelected("douro") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Vinhos do Douro")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { onRegionSelected("dao") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Vinhos do Dão")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageMenu(images: List<ImageItem>, onImageClick: (ImageItem) -> Unit, onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Vinhos da Região") },
                navigationIcon = {
                    IconButton(onClick = { onBack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { padding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = padding,
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            items(images) { image ->
                Column(
                    modifier = Modifier
                        .padding(8.dp)
                        .clickable { onImageClick(image) },
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Card(
                        modifier = Modifier.size(150.dp),
                        shape = MaterialTheme.shapes.medium,
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Image(
                            painter = painterResource(id = image.resId),
                            contentDescription = image.name,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = image.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageDetails(image: ImageItem, onBack: () -> Unit, onCartClick: () -> Unit) {
    var quantity by remember { mutableStateOf(1) }
    var isBox by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalhes do Vinho") },
                navigationIcon = {
                    IconButton(onClick = { onBack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                },
                actions = {
                    IconButton(onClick = { onCartClick() }) {
                        Icon(Icons.Default.ShoppingCart, contentDescription = "Carrinho")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                shape = MaterialTheme.shapes.medium,
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Image(
                    painter = painterResource(id = image.resId),
                    contentDescription = image.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(image.name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text(image.description, style = MaterialTheme.typography.bodyLarge)

            Spacer(modifier = Modifier.height(16.dp))

            // Preços visíveis
            Text("Preço Garrafa: €${"%.2f".format(image.bottlePrice)}", style = MaterialTheme.typography.bodyMedium)
            Text("Preço Caixa (3 garrafas): €${"%.2f".format(image.boxPrice)}", style = MaterialTheme.typography.bodyMedium)

            Spacer(modifier = Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Quantidade:", modifier = Modifier.padding(end = 8.dp))
                IconButton(onClick = { if (quantity > 1) quantity-- }) {
                    Text("-")
                }
                Text(quantity.toString())
                IconButton(onClick = { quantity++ }) {
                    Text("+")
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = isBox, onCheckedChange = { isBox = it })
                Text("Comprar em caixa (3 garrafas)")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                cart.add(CartItem(image, quantity, isBox))
            }) {
                Text("Adicionar ao Carrinho")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Carrinho de Compras") },
                navigationIcon = {
                    IconButton(onClick = { onBack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            if (cart.isEmpty()) {
                Text("Carrinho vazio.", style = MaterialTheme.typography.titleMedium)
            } else {
                var total = 0f
                cart.forEach { item ->
                    val unitPrice = if (item.isBox) item.product.boxPrice else item.product.bottlePrice
                    val subtotal = item.quantity * unitPrice
                    total += subtotal

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(item.product.name)
                            Text("${item.quantity} x ${if (item.isBox) "Caixa (3 garrafas)" else "Garrafa"} = €${"%.2f".format(subtotal)}")
                        }
                        IconButton(onClick = { cart.remove(item) }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Remover")
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text("Total: €${"%.2f".format(total)}", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun Avaliacao_Diogo_Aguiar_1709169Theme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(),
        typography = Typography(),
        content = content
    )
}
