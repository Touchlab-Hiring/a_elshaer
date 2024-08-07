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

## 🎯 Features & User Experience
- ⌛️No boring loading time: Everything is loaded in realtime, we get the dog breeds and show them to the user instantly while the images are being loaded in the background, as soon as we get any image it is shown to the user.
- 🌐Offline first support: once the data is loaded, it's stored in the database and can be accessed even when the user is offline including images.
- ✨ Every swipe is a new dog: The user can swipeToRefresh to get a new image for every breed, this is a fun way to interact with the app.
- 🎨Material You Design: supports both dark and light mode.

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

[![Data flow](https://mermaid.ink/img/pako:eNq1lMFO4zAQhl9l5HOrveeAlBCQOAStsqJ7ycXE08Zq4snaDixCvPuO7QRKU8FpfWrtb2b--cfxq2hJociEwz8TmhZLLQ9WDo0BXqO0Xrd6lMZDDtJBbpQlrSAfxzWxqwKy0_hccc5-DdThvMaRnPZkXy7U-HkXkJIOUFhE5cLOGiuLmIhogFJ6-SgdJijfXl3tqoxrcDPOg-JT-AG3ZFsEi3uLrkvkrmK0zuC6w_YIe7KRTWey93CrLcd7PSBMTpsD-A5BLl2HVXOCslgy9NTK_iRHWGWxTUXu6ewkxHJnJ0K548fY8QfEwBxfo5-sgV4zSfuL8KLmYeRKmLx51r5bgSwq5rwZtAdttNcr3fV2tjGYC85bXO7D7FxQ_17qYR4Q9g7hd4fmRB9oB61sO1QXXJm7wr_cVnB4Zd5_0mlmMT3RGCePrDAJjpbRxAUHecAv5mWlUexwxGKOGP7F7C4kPBnXR_Fz8JMNU6TVOtnsQ4TinY-pwpVdQu4-h3xrzj3xLj2hhXwThMb4ZHQaKBAPWrvjOR7g9GUWYfqOpvDt8a31dvKd2IgB7SC14ifnNcQ2gmUO2IiMfyppj41ozBtzcvL068W0IuNI3IjUyfw8iWwv-b4tuzcqPCjvmxj_Vulhi-_b2z-0bIGX?type=png)](https://mermaid.live/edit#pako:eNq1lMFO4zAQhl9l5HOrveeAlBCQOAStsqJ7ycXE08Zq4snaDixCvPuO7QRKU8FpfWrtb2b--cfxq2hJociEwz8TmhZLLQ9WDo0BXqO0Xrd6lMZDDtJBbpQlrSAfxzWxqwKy0_hccc5-DdThvMaRnPZkXy7U-HkXkJIOUFhE5cLOGiuLmIhogFJ6-SgdJijfXl3tqoxrcDPOg-JT-AG3ZFsEi3uLrkvkrmK0zuC6w_YIe7KRTWey93CrLcd7PSBMTpsD-A5BLl2HVXOCslgy9NTK_iRHWGWxTUXu6ewkxHJnJ0K548fY8QfEwBxfo5-sgV4zSfuL8KLmYeRKmLx51r5bgSwq5rwZtAdttNcr3fV2tjGYC85bXO7D7FxQ_17qYR4Q9g7hd4fmRB9oB61sO1QXXJm7wr_cVnB4Zd5_0mlmMT3RGCePrDAJjpbRxAUHecAv5mWlUexwxGKOGP7F7C4kPBnXR_Fz8JMNU6TVOtnsQ4TinY-pwpVdQu4-h3xrzj3xLj2hhXwThMb4ZHQaKBAPWrvjOR7g9GUWYfqOpvDt8a31dvKd2IgB7SC14ifnNcQ2gmUO2IiMfyppj41ozBtzcvL068W0IuNI3IjUyfw8iWwv-b4tuzcqPCjvmxj_Vulhi-_b2z-0bIGX)

### 📡 Retrofit with Coroutines

- Advantages: Simplifies network requests with built-in support for suspend functions allowing for cleaner and more
  straightforward asynchronous operations.
- Implementation: API calls are abstracted in Retrofit interfaces, employing suspend functions for seamless asynchronous
  execution. Room is utilized for robust data persistence, handling data access, and manipulation with ease.

### ✍🏼 Room for Local Caching

- Enables offline capabilities and performance improvements through data caching.
- Utilize Room for data persistence, implementing DAOs for data access and manipulation

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
