package com.example.civicnow.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.civicnow.R
import com.example.civicnow.network.EventData
import com.example.civicnow.network.EventJurisdiction
import com.example.civicnow.network.Location
import com.example.civicnow.network.Officeholder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.time.Instant
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@Composable
fun EventsScreen(
    civicNowUiState: CivicNowUiState,
    modifier: Modifier = Modifier,
    navController: NavHostController,
) {
    when (civicNowUiState) {
        is CivicNowUiState.Loading -> LoadingScreen(modifier = modifier.fillMaxSize())
        is CivicNowUiState.Success -> EventsResultScreen(
            civicNowUiState.events, navController = navController, modifier = modifier.fillMaxWidth()
        )

        is CivicNowUiState.Error -> ErrorScreen(modifier = modifier.fillMaxSize())
    }
}


@Composable
fun EventsResultScreen(
    events: List<EventData>,
    modifier: Modifier = Modifier,
    navController: NavHostController
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
        if (events.isEmpty()) {
            Text(text = stringResource(R.string.no_results))
            return@Surface
        }
        Column(modifier = modifier) {
            Text(
                text = "Success: ${events.size} Events retrieved",
                modifier = Modifier.padding(8.dp),
                style = MaterialTheme.typography.titleLarge
            )
            EventsList(events = events, navController = navController, modifier = Modifier.padding(8.dp),)
        }
    }
}

@Composable
fun EventCard(event: EventData, navController: NavHostController, modifier: Modifier = Modifier) {
    fun getDateTime(dateString: String): Pair<String, String> {
        if (dateString.isEmpty()) return Pair("NA", "")
        
        val instant = Instant.parse(dateString)
        val localDate = instant.atZone(TimeZone.getDefault().toZoneId()).toLocalDate()
        val localTime = instant.atZone(TimeZone.getDefault().toZoneId()).toLocalTime()
        return Pair(localDate.toString(), localTime.toString())
    }

    Card(modifier = modifier.fillMaxWidth()) {
        Row {
            Column {
                Text(
                    text = event.name,
                    modifier = Modifier.padding(2.dp),
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = event.description,
                    modifier = Modifier.padding(2.dp),
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = buildString {
                        append("Start Date: ")
                        val dateTime = getDateTime(event.startDate)
                        append(dateTime.first)
                        append(" ")
                        append(dateTime.second)
                    },
                    modifier = Modifier.padding(2.dp),
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    text = buildString {
                        append("End Date: ")
                        val dateTime = getDateTime(event.endDate)
                        append(dateTime.first)
                        append(" ")
                        append(dateTime.second)
                    },
                    modifier = Modifier.padding(2.dp),
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    text = "Status: " + event.status,
                    modifier = Modifier.padding(2.dp),
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    text = "Location: " + event.location.name,
                    modifier = Modifier.padding(2.dp),
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}

@Preview
@Composable
fun EventCardPreview() {
    EventCard(
        EventData(id = "evt_123456",
        name = "Community Clean-Up Day",
        jurisdiction = EventJurisdiction(
            id = "jur_7890",
            name = "San Francisco County",
            classification = "county"
        ),
        description = "A local event organized to clean up neighborhood parks and public spaces.",
        classification = "community-service",
        startDate = "2025-09-15",
        endDate = "2025-09-15",
        allDay = true,
        status = "confirmed",
        upstreamId = "ext_456789",
        deleted = false,
        location = Location(
            name = "Golden Gate Park",
            url = "https://sfparks.org/golden-gate"
        )
    ),
    NavHostController(LocalContext.current),
    Modifier.fillMaxWidth()
    )
}

@Composable
fun EventsList(
    events: List<EventData>,
    modifier: Modifier = Modifier,
    navController: NavHostController
) {
    LazyColumn(modifier = modifier) {
        items(events) {
            EventCard(event = it, navController, modifier = modifier)
        }
    }
}