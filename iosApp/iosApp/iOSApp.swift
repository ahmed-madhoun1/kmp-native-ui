import SwiftUI
import Shared

/**
 * The main entry point for the iOS application.
 */
@main
struct iOSApp: App {
    /**
     * Initializes the iOS App and triggers the Kotlin Multiplatform dependency injection (Koin) setup.
     */
    init() {
        // Calls the shared Kotlin module to initialize all Koin dependencies
        KoinKt.doInitKoin()
    }
    
    var body: some Scene {
        WindowGroup {
            // The initial view of the application
            ListView()            
        }
    }
}
