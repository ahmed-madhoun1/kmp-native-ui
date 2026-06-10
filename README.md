# KMP Native UI Showcase

Welcome to the **KMP Native UI Showcase** project! This repository serves as a production-ready template demonstrating the power and flexibility of **Kotlin Multiplatform (KMP)**. 

If you are looking to build applications that share core business logic while providing a **100% native user experience** on both iOS and Android, this is the architecture you need.

## Why KMP with Native UI?

Many cross-platform frameworks compromise on UI performance or feel by drawing their own widgets. KMP takes a different approach:
- **Shared Business Logic**: Write your networking, data storage, ViewModels, and business rules once in Kotlin.
- **Native UI**: Use the official, native UI toolkits for each platform—**SwiftUI** for iOS and **Jetpack Compose** for Android. 
- **Production Ready**: This architecture is fully ready for production. Your iOS app will look, feel, and perform exactly like a native iOS app, using native iOS lists, buttons, bottom bars, and navigation stacks. The Android app follows the same principle using Material Design and Compose.

---

## Project Structure

This project is divided into three main components:

1. **`shared`**: The Kotlin Multiplatform module containing all the shared code.
   - `data/`: Contains models (`MuseumObject`), remote APIs (Ktor), local storage, and Repositories.
   - `di/`: Dependency Injection setup using **Koin**.
   - `screens/`: Shared **ViewModels** (e.g., `ListViewModel`, `DetailViewModel`) that expose state and handle business logic. This uses the `KMP-ObservableViewModel` library, making ViewModels observable in both Compose and SwiftUI natively.
   
2. **`androidApp`**: The native Android application.
   - Written entirely in Kotlin using **Jetpack Compose**.
   - Contains UI screens (`ListScreen`, `DetailScreen`) and Navigation.
   - Connects directly to the shared ViewModels via Koin dependency injection.

3. **`iosApp`**: The native iOS application.
   - Written entirely in Swift using **SwiftUI**.
   - Contains UI views (`ListView.swift`, `DetailView.swift`).
   - Connects to the shared ViewModels natively using `@StateViewModel`.

---

## How to Build a New Feature

Creating a new feature in this architecture follows a strict separation of concerns, ensuring your code remains clean, testable, and highly reusable.

### 1. Data Layer (Shared)
Start by defining your data models and fetching logic in the `shared` module.
- **Models**: Create your data classes in `shared/src/commonMain/kotlin/.../data/`.
- **API/Storage**: Implement your Ktor network calls or local database queries.
- **Repository**: Create a Repository interface and implementation to expose data to the presentation layer. If the logic is complex, introduce **Use Cases** to encapsulate specific business rules.
  
### 2. Dependency Injection (Shared)
Register your new Repository or Use Case in the Koin module.
- Open `shared/src/commonMain/kotlin/.../di/Koin.kt`.
- Add your repository to the Koin module: `single { MyNewRepository(...) }`.

### 3. Presentation Layer - ViewModels (Shared)
Write your ViewModel in the `shared` module so both platforms can use it.
- Create a new ViewModel class extending `ViewModel()` from the shared library.
- Expose state using `StateFlow` and annotate it with `@NativeCoroutinesState` for seamless Swift integration.
- Inject your Repository/Use Cases into the ViewModel.

### 4. Native UI - Android
Build the native UI for Android using Jetpack Compose.
- Navigate to `androidApp/src/main/kotlin/.../screens/`.
- Create a new Composable function (e.g., `MyNewScreen`).
- Inject the shared ViewModel using `val viewModel: MyNewViewModel = koinViewModel()`.
- Collect the state using `val state by viewModel.state.collectAsStateWithLifecycle()`.
- **Navigation**: Implement Jetpack Compose navigation (`NavHost`, `composable`) in your main routing file (e.g., `App.kt`). Navigation graphs are defined entirely in Kotlin using Compose, ensuring a completely native Android navigation experience with standard transitions and backstack management.

### 5. Native UI - iOS
Build the native UI for iOS using SwiftUI.
- Navigate to `iosApp/iosApp/`.
- Create a new SwiftUI View (e.g., `MyNewView.swift`).
- Inject the shared ViewModel using `@StateViewModel var viewModel = MyNewViewModel(repo: KoinDependencies().myNewRepository)`.
- Use native SwiftUI elements (`List`, `TabView`) based on the state provided by the shared ViewModel.
- **Navigation**: Implement native iOS navigation using SwiftUI's `NavigationStack` and `NavigationLink`. This ensures your app leverages Apple's built-in navigation behaviors, including swipe-to-back gestures, native toolbars, and standard iOS page transitions.

---

## Technical Stack

- **Kotlin Multiplatform**: The core technology sharing logic.
- **Network**: [Ktor](https://ktor.io/) for cross-platform HTTP requests.
- **Dependency Injection**: [Koin](https://insert-koin.io/) for sharing dependencies across the architecture.
- **ViewModels & Coroutines**: [KMP-ObservableViewModel](https://github.com/rickclephas/KMP-ObservableViewModel) and [KMP-NativeCoroutines](https://github.com/rickclephas/KMP-NativeCoroutines) to bridge Kotlin Coroutines/Flows natively to Swift Combine/Async-Await.
- **Android UI**: Jetpack Compose.
- **iOS UI**: SwiftUI.

## Conclusion

By adopting this architecture, you maximize development efficiency by sharing the vast majority of your codebase (everything below the UI layer) while maintaining a **0% compromise** on user experience. iOS users get a true iOS app, and Android users get a true Android app.
