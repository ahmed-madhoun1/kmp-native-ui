import KMPObservableViewModelCore
import Shared

/**
 * An extension that bridges the shared Kotlin Multiplatform ViewModel
 * with the Swift `ViewModel` protocol provided by the KMPObservableViewModelSwiftUI library.
 * This enables the `@StateViewModel` wrapper to seamlessly observe Kotlin StateFlows in SwiftUI.
 */
extension Kmp_observableviewmodel_coreViewModel: ViewModel { }
