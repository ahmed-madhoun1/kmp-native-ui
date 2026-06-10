import Foundation
import SwiftUI
import Shared
import KMPNativeCoroutinesAsync
import KMPObservableViewModelSwiftUI

/**
 * A SwiftUI view that displays the detailed information of a selected museum object.
 */
struct DetailView: View {
    // Injects the shared KMP ViewModel and observes state changes natively
    @StateViewModel
    var viewModel = DetailViewModel(
        museumRepository: KoinDependencies().museumRepository
    )

    // The unique identifier of the museum object to be displayed
    let objectId: Int32

    var body: some View {
        VStack {
            // Once the ViewModel successfully fetches the object, display its details
            if let obj = viewModel.museumObject {
                ObjectDetails(obj: obj)
            }
        }
        .onAppear {
            // Trigger the data fetch when the view appears on the screen
            viewModel.setId(objectId: objectId)
        }
    }
}

/**
 * A reusable SwiftUI view that renders the specific fields and image of a [MuseumObject].
 */
struct ObjectDetails: View {
    // The underlying data model holding the object's properties
    var obj: MuseumObject

    var body: some View {
        // Allows the user to scroll vertically if the content exceeds screen height
        ScrollView {

            VStack {
                // Fetch and display the primary image asynchronously
                AsyncImage(url: URL(string: obj.primaryImageSmall)) { phase in
                    switch phase {
                    case .empty:
                        // Display a loading spinner while the image is downloading
                        ProgressView()
                    case .success(let image):
                        // Scale and display the successfully loaded image
                        image
                            .resizable()
                            .scaledToFill()
                            .clipped()
                    default:
                        // Fallback view on failure
                        EmptyView()
                    }
                }

                // A vertical stack holding all the textual data about the object
                VStack(alignment: .leading, spacing: 6) {
                    Text(obj.title)
                        .font(.title)

                    // Helper views to present labeled data concisely
                    LabeledInfo(label: "Artist", data: obj.artistDisplayName)
                    LabeledInfo(label: "Date", data: obj.objectDate)
                    LabeledInfo(label: "Dimensions", data: obj.dimensions)
                    LabeledInfo(label: "Medium", data: obj.medium)
                    LabeledInfo(label: "Department", data: obj.department)
                    LabeledInfo(label: "Repository", data: obj.repository)
                    LabeledInfo(label: "Credits", data: obj.creditLine)
                }
                .padding(16)
            }
        }
    }
}

/**
 * A helper SwiftUI view that displays a bold label followed by its associated text.
 */
struct LabeledInfo: View {
    var label: String
    var data: String

    var body: some View {
        Spacer()
        // Uses markdown inside the Text view to render the label as bold
        Text("**\(label):** \(data)")
    }
}
