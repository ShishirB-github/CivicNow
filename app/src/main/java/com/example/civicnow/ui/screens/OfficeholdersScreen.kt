package com.example.civicnow.ui.screens

import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.civicnow.R
import com.example.civicnow.network.CurrentRole
import com.example.civicnow.network.EventJurisdiction
import com.example.civicnow.network.Jurisdiction
import com.example.civicnow.network.Officeholder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException


@Composable
fun OfficeholdersScreen(
    civicNowUiState: CivicNowUiState,
    modifier: Modifier = Modifier,
    navController: NavHostController,
    fetchOfficeHoldersForZip: (String) -> Unit
) {
    when (civicNowUiState) {
        is CivicNowUiState.Loading -> LoadingScreen(modifier = modifier.fillMaxSize())
        is CivicNowUiState.Success -> OfficeholdersResultScreen(civicNowUiState.officeholders,
            navController = navController, fetchOfficeHoldersForZip, modifier = modifier.fillMaxWidth())

        is CivicNowUiState.Error -> ErrorScreen(modifier = modifier.fillMaxSize())
    }
}

@Composable
fun OfficeholdersResultScreen(
    officeholders: List<Officeholder>,
    navController: NavHostController,
    fetchOfficeHoldersForZip: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val layoutDirection = LocalLayoutDirection.current
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(
                start = WindowInsets.safeDrawing.asPaddingValues()
                    .calculateStartPadding(layoutDirection),
                end = WindowInsets.safeDrawing.asPaddingValues()
                    .calculateEndPadding(layoutDirection),
            ),
    ) {
        if (officeholders.isEmpty()) {
            Text(text = stringResource(R.string.no_results))
            return@Surface
        }
        Column(modifier = modifier) {
            Row (modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp) // spacing between elements
            ) {
                var zipCode by remember { mutableStateOf("") }
                TextField(
                    value = zipCode,
                    onValueChange = { zipCode = it},
                    modifier = Modifier.weight(1f), // take up remaining space
                    label = { Text("Enter Zip Code") }
                )

                Button( onClick = { fetchOfficeHoldersForZip(zipCode) }, modifier = Modifier.alignByBaseline()) {
                    Text(text = stringResource(R.string.submit))
                }
            }

            OfficeholdersList(
                officeholders = officeholders,
                navController = navController,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}

@Preview
@Composable
fun OfficeholdersResultScreenPreview() {
}

@Composable
fun OfficeholderCard(officeholder: Officeholder, navController: NavHostController, modifier: Modifier = Modifier) {
    // Helper function to calculate age
    fun calculateAge(birthDate: String): String {
        if (birthDate.isEmpty()) return ""
        return try {
            val birth = LocalDate.parse(birthDate, DateTimeFormatter.ISO_LOCAL_DATE)
            val age = Period.between(birth, LocalDate.now()).years
            "$age years old"
        } catch (e: DateTimeParseException) {
            "" // Return empty if date is malformed
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp, horizontal = 8.dp) // Add padding for spacing between cards
            .clickable {
                val encodedUrl = URLEncoder.encode(
                    officeholder.openstatesUrl,
                    StandardCharsets.UTF_8.toString()
                )
                navController.navigate("webview_route/$encodedUrl")
            },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp) // Add a subtle shadow
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // --- Photo ---
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(officeholder.image)
                    .crossfade(true)
                    .build(),
                // Use the placeholders you already have
                placeholder = painterResource(R.drawable.loading_img),
                error = painterResource(R.drawable.ic_placeholder_person),
                contentDescription = "Photo of ${officeholder.name}",
                contentScale = ContentScale.Crop, // Crop to fill the circle
                modifier = Modifier
                    .size(80.dp) // Slightly larger for better presence
                    .clip(CircleShape) // Make the image circular
            )

            Spacer(Modifier.width(16.dp))

            // --- Details Column ---
            Column(modifier = Modifier.weight(1f)) {
                // Office Title (e.g., "State Senator")
                Text(
                    text = officeholder.currentRole.title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                // Officeholder Name (e.g., "Jane Doe")
                Text(
                    text = officeholder.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(Modifier.height(4.dp))

                // Party and Age on the same line
                val age = calculateAge(officeholder.birthDate)
                val partyAndAge = if (age.isNotEmpty()) {
                    "${officeholder.party} • $age"
                } else {
                    officeholder.party
                }
                Text(
                    text = partyAndAge,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Preview
@Composable
fun OfficeholderCardPreview() {
    OfficeholderCard(
        Officeholder(
            id = "OH123456",
            name = "Jane Doe",
            party = "Independent",
            currentRole = CurrentRole(
                title = "State Senator",
                orgClassification = "12",
                divisionId = "1",
                district = "12"
            ),
            jurisdiction = Jurisdiction(
                id = "JUR456789",
                name = "California",
                classification = "state"
            ),
            givenName = "Jane",
            familyName = "Doe",
            image = "https://unitedstates.github.io/images/congress/450x550/S001150.jpg",
            email = "jane.doe@senate.ca.gov",
            gender = "Female",
            birthDate = "1975-08-15",
            deathDate = "",
            createdAt = "2023-06-01T10:15:30Z",
            updatedAt = "2025-07-12T08:20:45Z",
            openstatesUrl = "https://openstates.org/person/oh123456/"
        ),
        NavHostController(LocalContext.current),
        Modifier.fillMaxWidth()
    )
}
@Composable
fun OfficeholdersList(
    officeholders: List<Officeholder>,
    modifier: Modifier = Modifier,
    navController: NavHostController
) {
    LazyColumn(modifier = modifier) {
        items(officeholders) {
            OfficeholderCard(officeholder = it, navController, modifier = modifier)
        }
    }
}

@Composable
fun WebViewScreen(url: String?) {
    if (url == null) {
        Text("Invalid URL")
        return
    }

    AndroidView(
        factory = { context ->
            WebView(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                webViewClient = WebViewClient() // ensures links open in WebView, not browser
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true // important for modern sites
                settings.loadsImagesAutomatically = true
                settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                settings.cacheMode = WebSettings.LOAD_DEFAULT
                loadUrl(url)
            }
        },
        update = { webView ->
            webView.loadUrl(url)
        }
    )
}