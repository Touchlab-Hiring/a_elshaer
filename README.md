# Dogify

This app simply fetches information about dog breeds from [here](https://dog.ceo/dog-api/documentation/) and displays
them in a grid.

## Data

A retrofit service `DogService` is already set up for fetching all the breed names and a random image for a given breed.

## UI

It has only one Activity `MainActivity` which loads a list of dog breeds from the network and displays them in a grid.
In the onCreate function we set up the recyclerView and kick off an asyncTask to load the data.
In the doInBackground function of the async task we create a retrofit service and call getBreeds to get the breed names.
Then in onPostExecute we add them all to the adapter and which binds them to the view.

# 🚀 Solution

This project aims to modernize an Android application that displays a list of dog breeds.
We're taking this mobile app to the next level by refactoring it with modern practices and architectures, making it more
efficient and user-friendly.

## 🏗 Architecture

### 🌟 MVVM

- **Why MVVM?** It boosts modularity and testability by neatly separating UI, business logic, and data access layers.
- **How We Implement It:**
	- ViewModel: Manages UI-related data with ease.
	- StateFlow: Streamlines state management and data flow to the UI, ensuring a responsive app

### 📦 Package-Based Project Structure

- Benefits: Offers simplicity and clarity, especially for smaller projects. It makes development a breeze.
- Our Approach: We categorize code into packages based on functionality (e.g., network, database, ui, viewmodels.

### ⏳ Asynchronous Processing with Kotlin Coroutines

- Why Coroutines? They provide a modern and efficient method for handling background tasks, making the old AsyncTask
  obsolete.
- Integration: Kotlin Coroutines are used for all asynchronous operations, including network requests and database
  transactions, working smoothly with Retrofit and Room.

## 💾 Data Handling

[![Data flow](https://mermaid.ink/img/pako:eNqFk8FOwzAMhl_Fynm8QA9IGwUJoUpTJ3bqxTTusGiSkqQghHh3nLQIthXYYVrtr7_t3_O7ap0mVahAzyPZlkrGg0fTWJDPgD5yywPaCGvAAGurvWMN62E4J_ZVQvZMr5Vo9udAnfI1DS5wdP5tocb2NiGlO8DGE-mQIudYuclCzhkoMeIDBpqg9cXl5b4qpIYMEyJoyU6ZfSWpuoAbiu3jj3gtYanx_UrP8uU60NLDQ-5hlt7eXkwSNcXR21_B3rkBOueBUCrlzJRYLMcGD5TxE_KsXiaPlcpNAVdShWYZtPqnDFn9PWRidxSTccGNvqXUe_RjfJyYcjNVuzY8-QYvjHDnYs8Wbnr3-iU1W5yMhxA9ff1XxOE0XAH3g7xOcD8vDvsIHXuZNbIhsQc128OCJVtP3cl2Fo0YZo70CXlsySJGfaDkSx5eoLTEP7ZjnKf_2kGtObKz2P_ZziImnaiVMuQNspYjfE_hRklnhhpVyE-N_qlRjf0QDsfodm-2VYXsjVZqzEbPB3scvNbpwlTRoQy8UpQfq-nS88F_fAItlz5L?type=png)](https://mermaid.live/edit#pako:eNqFk8FOwzAMhl_Fynm8QA9IGwUJoUpTJ3bqxTTusGiSkqQghHh3nLQIthXYYVrtr7_t3_O7ap0mVahAzyPZlkrGg0fTWJDPgD5yywPaCGvAAGurvWMN62E4J_ZVQvZMr5Vo9udAnfI1DS5wdP5tocb2NiGlO8DGE-mQIudYuclCzhkoMeIDBpqg9cXl5b4qpIYMEyJoyU6ZfSWpuoAbiu3jj3gtYanx_UrP8uU60NLDQ-5hlt7eXkwSNcXR21_B3rkBOueBUCrlzJRYLMcGD5TxE_KsXiaPlcpNAVdShWYZtPqnDFn9PWRidxSTccGNvqXUe_RjfJyYcjNVuzY8-QYvjHDnYs8Wbnr3-iU1W5yMhxA9ff1XxOE0XAH3g7xOcD8vDvsIHXuZNbIhsQc128OCJVtP3cl2Fo0YZo70CXlsySJGfaDkSx5eoLTEP7ZjnKf_2kGtObKz2P_ZziImnaiVMuQNspYjfE_hRklnhhpVyE-N_qlRjf0QDsfodm-2VYXsjVZqzEbPB3scvNbpwlTRoQy8UpQfq-nS88F_fAItlz5L)

### 📡 Retrofit with Coroutines

- Advantages: Simplifies network requests with built-in support for suspend functions allowing for cleaner and more
  straightforward asynchronous operations.
- Implementation: API calls are abstracted in Retrofit interfaces, employing suspend functions for seamless asynchronous
  execution. Room is utilized for robust data persistence, handling data access, and manipulation with ease.

### ✍🏼 Room for Local Caching

- Enables offline capabilities and performance improvements through data caching.
- Utilize Room for data persistence, implementing DAOs for data access and manipulation

## 📃 Pagination and Data Fetching

### 📚 Paging Library

- Why Use Paging? It efficiently handles data loading and ensures smooth scrolling, even with large datasets.
- We use the Paging 3 library for pagination, combining local (Room) and remote (Retrofit) data sources with the help of
  RemoteMediator for an optimized data fetch strategy.

## 🎨 UI/UX Enhancements

- Implementing Image Loading with Glide for quick and efficient image rendering.

## 🧪 Testing and Quality Assurance

- Employ unit and UI testing tools (JUnit, MockK, Espresso) for comprehensive testing coverage.

## Things i would choose in a real project

### Pagination with RemoteMediator
I would include pagination with RemoteMediator to fetch data from the network and store it in the database. But i didn't include it in this project because the API doesn't support pagination and doesn't have a lot of data.

### Modularized Architecture

- Emphasizing test-driven development, we aim for a robustly written codebase, supplemented by comprehensive unit and UI
  testing.
- Considering a shift towards a modularized architecture for the application to enhance build and testing phases,
  promoting reusability, scalability, and collaboration.

**💡Although these advanced practices were not included in this project due to its scope, they represent our commitment
to stability, reliability, and continuous improvement.**

### Crash Reporting Integration

- Monitors app stability and captures crash reports for timely fixes.
- Integrate a crash reporting tool (e.g., Firebase Crashlytics) to collect and analyze crash data, improving app
  reliability.

### CI/CD for Automated Publishing

- Streamlines the build, test, and release process, ensuring consistent quality and faster deployment.
- Set up a CI/CD pipeline (e.g., GitHub Actions, GitLab CI/CD) for automated testing, building, and deployment to app
  stores, including beta distribution channels for testing.
