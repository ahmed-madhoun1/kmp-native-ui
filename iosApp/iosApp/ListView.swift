import SwiftUI
import KMPNativeCoroutinesAsync
import KMPObservableViewModelSwiftUI
import Shared

/**
 * The main screen of the iOS application displaying a list of museum objects.
 */
struct ListView: View {
    // Injects the shared KMP ViewModel and marks it as observable for SwiftUI
    @StateViewModel
    var viewModel = ListViewModel(
        // Fetches the repository via the Koin wrapper class defined in the shared iOS code
        museumRepository: KoinDependencies().museumRepository
    )

    // Defines a flexible grid layout with a minimum width of 120 points per column
    let columns = [
        GridItem(.adaptive(minimum: 120), alignment: .top)
    ]

    var body: some View {
        ZStack {
            // Check if the ViewModel has fetched any objects
            if !viewModel.objects.isEmpty {
                // Native iOS navigation stack
                NavigationStack {
                    ScrollView {
                        // Native lazy loading grid
                        LazyVGrid(columns: columns, alignment: .leading, spacing: 20) {
                            ForEach(viewModel.objects, id: \.self) { item in
                                // Native navigation link passing the object ID to the DetailView
                                NavigationLink(destination: DetailView(objectId: item.objectID)) {
                                    ObjectFrame(obj: item)
                                }
                                .buttonStyle(PlainButtonStyle()) // Removes default button styling
                            }
                        }
                        .padding(.horizontal)
                    }
                }
            } else {
                // Loading or empty state
                Text("No data available")
            }
        }
    }
}

/**
 * A reusable SwiftUI view component that displays a single museum object in a grid cell.
 */
struct ObjectFrame: View {
    // The shared Kotlin data model representing the object
    let obj: MuseumObject

    var body: some View {
        VStack(alignment: .leading, spacing: 4) {
            GeometryReader { geometry in
                // Asynchronously loads the image from the remote URL
                AsyncImage(url: URL(string: obj.primaryImageSmall)) { phase in
                    switch phase {
                    case .empty:
                        // Shows a loading indicator while the image is being fetched
                        ProgressView()
                            .frame(width: geometry.size.width, height: geometry.size.width)
                    case .success(let image):
                        // Displays the loaded image filling the grid cell proportionally
                        image
                            .resizable()
                            .scaledToFill()
                            .frame(width: geometry.size.width, height: geometry.size.width)
                            .clipped()
                            .aspectRatio(1, contentMode: .fill)
                    default:
                        // Fallback view if the image fails to load
                        EmptyView()
                            .frame(width: geometry.size.width, height: geometry.size.width)
                    }
                }
            }
            .aspectRatio(1, contentMode: .fit)

            // Object title
            Text(obj.title)
                .font(.headline)

            // Artist name
            Text(obj.artistDisplayName)
                .font(.subheadline)

            // Creation date
            Text(obj.objectDate)
                .font(.caption)
        }
    }
}
