package com.example.civicnow.ui.screens

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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.civicnow.R
import com.example.civicnow.network.EventData
import com.example.civicnow.network.EventJurisdiction
import com.example.civicnow.network.Location
import java.time.Instant
import java.util.TimeZone

@Composable
fun EventsScreen(
    civicNowUiState: CivicNowUiState,
    modifier: Modifier = Modifier,
    navController: NavHostController,
    fetchEventsForJurisdiction: (String) -> Unit
) {
    when (civicNowUiState) {
        is CivicNowUiState.Loading -> LoadingScreen(modifier = modifier.fillMaxSize())
        is CivicNowUiState.Success -> EventsResultScreen(
            civicNowUiState.events, navController = navController, fetchEventsForJurisdiction = fetchEventsForJurisdiction, modifier = modifier.fillMaxWidth()
        )

        is CivicNowUiState.Error -> ErrorScreen(modifier = modifier.fillMaxSize())
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventsResultScreen(
    events: List<EventData>,
    modifier: Modifier = Modifier,
    navController: NavHostController,
    fetchEventsForJurisdiction: (String) -> Unit
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
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp) // spacing between elements
            ) {
                var isExpanded by remember { mutableStateOf(false) }
                val jurisdictions = listOf(
                    "Alabama", "Alaska", "Arizona", "Arkansas", "California", "Colorado",
                    "Connecticut", "Delaware", "Florida", "Georgia", "Hawaii", "Idaho",
                    "Illinois", "Indiana", "Iowa", "Kansas", "Kentucky", "Louisiana",
                    "Maine", "Maryland", "Massachusetts", "Michigan", "Minnesota",
                    "Mississippi", "Missouri", "Montana", "Nebraska", "Nevada",
                    "New Hampshire", "New Jersey", "New Mexico", "New York",
                    "North Carolina", "North Dakota", "Ohio", "Oklahoma", "Oregon",
                    "Pennsylvania", "Rhode Island", "South Carolina", "South Dakota",
                    "Tennessee", "Texas", "Utah", "Vermont", "Virginia", "Washington",
                    "West Virginia", "Wisconsin", "Wyoming"
                )
                var selectedJurisdiction by remember { mutableStateOf(jurisdictions[0]) }

                // We use a Box to anchor the dropdown menu to the text field
                ExposedDropdownMenuBox(
                    expanded = isExpanded,
                    onExpandedChange = { isExpanded = it },
                    modifier = Modifier.weight(1f)
                ) {
                    TextField(
                        value = selectedJurisdiction,
                        onValueChange = {}, // onValueChange is not used for a read-only dropdown
                        readOnly = true,
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded)
                        },
                        colors = ExposedDropdownMenuDefaults.textFieldColors(),
                        modifier = Modifier.menuAnchor() // This is important!
                    )

                    ExposedDropdownMenu(
                        expanded = isExpanded,
                        onDismissRequest = { isExpanded = false }
                    ) {
                        jurisdictions.forEach { jurisdiction ->
                            DropdownMenuItem(
                                text = { Text(jurisdiction) },
                                onClick = {
                                    selectedJurisdiction = jurisdiction
                                    isExpanded = false
                                    // fetchEventsForJurisdiction(jurisdiction) // Optionally fetch on selection
                                }
                            )
                        }
                    }
                }

                Button(
                    onClick = { fetchEventsForJurisdiction(selectedJurisdiction) },
                    // modifier = Modifier.alignByBaseline() // alignByBaseline might not work as expected with the Box, align to center instead
                ) {
                    Text(text = stringResource(R.string.submit))
                }
            }

            EventsList(events = events, navController = navController, modifier = Modifier.padding(8.dp))
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