package dev.datlag.aniflow.other

data object StateSaver {
    var sekretLibraryLoaded: Boolean = false

    data object List {
        var homeOverview: Int = 0
        var homeOverviewOffset: Int = 0

        var mediaOverview: Int = 0
        var mediaOverviewOffset: Int = 0

        data object Home {
            var airingOverview: Int = 0
            var airingOverviewOffset: Int = 0

            var trendingOverview: Int = 0
            var trendingOverviewOffset: Int = 0

            var popularOverview: Int = 0
            var popularOverviewOffset: Int = 0

            var popularNextOverview: Int = 0
            var popularNextOverviewOffset: Int = 0
        }
    }
}