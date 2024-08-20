package com.example.tasklistapp

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import androidx.compose.foundation.lazy.items
import com.example.tasklistapp.ui.theme.TaskListAppTheme
import androidx.compose.ui.Alignment

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TaskListAppTheme {
                RecetaListScreen()
            }
        }
    }
}

@Composable
fun RecetaListScreen(){

    val context = LocalContext.current

    val recetas = remember { mutableStateListOf<Receta>()}
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted) {
            Toast.makeText(context, "Permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    // solicitar permisos en el inicio
    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
        } else {
            permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }

    RecetaListContent(
        recetas = recetas,
        onAddReceta = { title, imageUri ->
            recetas.add(Receta(title, imageUri))
        }
    )
}

@Composable
fun RecetaListContent(
    recetas: List<Receta>,
    onAddReceta: (String, String?) -> Unit
){
    var newRecetaTitle by remember { mutableStateOf("")}
    var newRecetaImageUri by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Text(text = "Lista de Recetas", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        // Caja de texto para el titulo
        OutlinedTextField(
            value = newRecetaTitle,
            onValueChange = { newRecetaTitle = it },
            label = { Text("Título de Receta") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Caja de texto para el enlace de la imagen
        OutlinedTextField(
            value = newRecetaImageUri,
            onValueChange = { newRecetaImageUri = it },
            label = { Text("Enlace de Imagen") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ){
            Button(
                onClick = {
                    if (newRecetaTitle.isNotEmpty()) {
                        onAddReceta(newRecetaTitle, newRecetaImageUri.ifEmpty { null })
                        newRecetaTitle = ""
                        newRecetaImageUri = ""
                    }
                }
            ) {
                Text("Agregar")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        RecetaList(recetas)
    }
}

@Composable
fun RecetaList(recetas: List<Receta>) {
    Column {
        recetas.forEach { receta ->
            CustomRecetaItem(receta)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

// componente Costum que contenga una imagen y un texto
@Composable
fun CustomRecetaItem(receta: Receta) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable {},
        verticalAlignment = Alignment.CenterVertically
    ) {
        // la imagen con Coil
        receta.imageUri?.let { uri ->
            AsyncImage(
                model = uri,
                contentDescription = "Imagen de Receta",
                modifier = Modifier.size(64.dp)
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // titulo de la receta
        Text(text = receta.title, style = MaterialTheme.typography.bodyLarge)
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    TaskListAppTheme {
        RecetaListScreen()
    }
}

data class Receta(val title: String, val imageUri: String?)
