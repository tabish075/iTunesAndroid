# iTunes Movie Search App

## [Click Here To Download APK](https://app.bitrise.io/app/078ccb13-9ab9-4fed-a1ca-968e5aea3067/installable-artifacts/a74cd4de946b50b7/public-install-page/ac3e4609c76b7d0a3a26d7c2f4f91ec8)


## Overview

The iTunes Movie Search App is a master-detail Android application that allows users to search for movies using the iTunes Search API. Users can mark movies as favorites, persist their favorite movies, and view them offline. The app is built using modern Android development technologies and follows the MVVM architecture pattern.

### Features
- Search for movies from the iTunes API.
- View movie details including title, artwork, price, genre, and a long description.
- Mark/unmark movies as favorites on both list and detail screens.
- Favorites are persisted and displayed offline.
- Empty search state when no movies match the query.
- Placeholder images for missing artwork.
- Data persistence using Room database.

## API
The app fetches movie data from the iTunes Search API:
https://itunes.apple.com/search?term=star&country=au&media=movie


## Tech Stack
- **Kotlin**: Primary programming language.
- **Coroutines**: For handling asynchronous operations.
- **LiveData & ViewModel**: To manage UI-related data in a lifecycle-conscious way.
- **MVVM (Model-View-ViewModel)**: The architecture pattern followed in the app.
- **Repository Pattern**: For managing data sources and providing a clean API for data access.
- **Room Database**: For offline persistence and data caching.
- **Retrofit & OkHttp**: For network operations and API calls.
- **Dagger2/DaggerHilt**: For dependency injection.
- **Glide**: For efficient image loading and caching.
- **JUnit**: For writing unit tests.
- **Bitrise**: CI/CD pipeline setup for automated builds and APK distribution.

## Architecture
This app follows the **MVVM (Model-View-ViewModel)** architecture pattern, which helps separate concerns and improve maintainability and testability.

- **Model**: Handles data operations. It connects to the Room database, Retrofit API, and other data sources through a repository.
- **View**: Displays data to the user and passes user input to the ViewModel.
- **ViewModel**: Exposes LiveData objects and fetches data from the repository, handling business logic for the UI.

## Persistence
- **Favorites**: Users can mark/unmark movies as favorites, and the data is persisted locally using the Room database. This allows for offline access to favorites.
- **Last Visited Screen**: The app saves the last screen the user was on when the app is closed, allowing them to resume where they left off when reopening the app.

## CI/CD
- **Bitrise CI/CD**: The app is configured to automatically build and distribute APKs via Bitrise, ensuring that the latest version is available for download.
  - Trigger builds upon new commits to the main branch.
  - Distribute APK to testers.



