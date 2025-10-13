package com.example.civicnow.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.civicnow.network.Election
import com.example.civicnow.R
import com.example.civicnow.ui.theme.CivicNowTheme

@Composable
fun ElectionScreen(
    civicNowUiState: CivicNowUiState,
    modifier: Modifier = Modifier,
) {
    when (civicNowUiState) {
        is CivicNowUiState.Loading -> LoadingScreen(modifier = modifier.fillMaxSize())
        is CivicNowUiState.Success -> ElectionsResultScreen(
            civicNowUiState.elections, modifier = modifier.fillMaxWidth()
        )

        is CivicNowUiState.Error -> ErrorScreen(modifier = modifier.fillMaxSize())
    }
}

/**
 * ResultScreen displaying number of photos retrieved.
 */
@Composable
fun ElectionsResultScreen(elections: List<Election>, modifier: Modifier = Modifier) {
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
        if (elections.isEmpty()) {
            Text(text = stringResource(R.string.no_results))
            return@Surface
        }
        Column(modifier = modifier) {
            ElectionsList(elections = elections, modifier = Modifier.padding(8.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ResultScreenPreview() {
    CivicNowTheme {
        ElectionsResultScreen(listOf(Election("100", "Election 1", "2023-01-01", "ocd 1")))
    }
}

@Composable
fun LoadingScreen(modifier: Modifier = Modifier) {
    Image(
        modifier = modifier.size(200.dp),
        painter = painterResource(R.drawable.loading_img),
        contentDescription = stringResource(R.string.loading)
    )
}

@Composable
fun ErrorScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_connection_error), contentDescription = ""
        )
        Text(text = stringResource(R.string.loading_failed), modifier = Modifier.padding(16.dp))
    }
}

@Composable
fun ElectionCard(election: Election, modifier: Modifier = Modifier) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column {
            Text(
                text = election.name,
                modifier = Modifier.padding(8.dp),
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = election.electionDay,
                modifier = Modifier.padding(8.dp),
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Composable
fun ElectionsList(elections: List<Election>, modifier: Modifier = Modifier) {
    LazyColumn(modifier = modifier) {
        items(elections) {
            ElectionCard(election = it, modifier = modifier)
        }
    }
}

@Preview
@Composable
fun ElectionCardPreview() {
    ElectionCard(
        Election("100", "Election 1", "2023-01-01", "ocd 1"),
        Modifier.fillMaxWidth()
    )
}



@Preview
@Composable
fun ElectionListPreview() {
    ElectionsList(
        listOf(
            Election("100", "Election 1", "2023-01-01", "ocd 1"),
            Election("100", "Election 1", "2023-01-01", "ocd 4")
        ),
        Modifier.fillMaxWidth()
    )
}