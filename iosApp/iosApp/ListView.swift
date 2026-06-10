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
        museumRepository: KoinDependencies().museumRepository
    )

    let columns = [
        GridItem(.adaptive(minimum: 120), alignment: .top)
    ]
    
    // State to handle the Profile sheet presentation
    @State private var showProfileSheet = false

    var body: some View {
        // Native iOS TabView (Bottom Navigation)
        TabView {
            // First Tab: Home
            NavigationStack {
                ZStack {
                    if !viewModel.objects.isEmpty {
                        ScrollView {
                            LazyVGrid(columns: columns, alignment: .leading, spacing: 20) {
                                ForEach(viewModel.objects, id: \.self) { item in
                                    NavigationLink(destination: DetailView(objectId: item.objectID)) {
                                        ObjectFrame(
                                            obj: item,
                                            isLiked: viewModel.likedObjectIds.contains { $0.intValue == item.objectID },
                                            onToggleLike: {
                                                viewModel.toggleLike(objectId: item.objectID)
                                            }
                                        )
                                    }
                                    .buttonStyle(PlainButtonStyle())
                                }
                            }
                            .padding(.horizontal)
                        }
                    } else {
                        Text("No data available")
                    }
                }
                .navigationTitle("KMP Native UI")
                .toolbar {
                    // Profile button in the top right to open the native sheet
                    ToolbarItem(placement: .navigationBarTrailing) {
                        Button(action: {
                            showProfileSheet = true
                        }) {
                            Image(systemName: "person.crop.circle")
                        }
                    }
                }
                // Native Profile Sheet populated by shared KMP UserProfile
                .sheet(isPresented: $showProfileSheet) {
                    ProfileSheet(profile: viewModel.userProfile)
                }
            }
            .tabItem {
                Label("Home", systemImage: "house")
            }

            // Second Tab: Settings
            NavigationStack {
                Text("Settings Tab")
                    .font(.largeTitle)
                    .navigationTitle("Settings")
            }
            .tabItem {
                Label("Settings", systemImage: "gear")
            }
        }
    }
}

/**
 * A native iOS sheet view representing the User Profile.
 * This consumes the exact same UserProfile state from KMP as the Android Drawer.
 */
struct ProfileSheet: View {
    @Environment(\.presentationMode) var presentationMode
    let profile: UserProfile
    
    var body: some View {
        NavigationStack {
            List {
                Section {
                    HStack(spacing: 16) {
                        AsyncImage(url: URL(string: profile.avatarUrl)) { phase in
                            if let image = phase.image {
                                image.resizable().scaledToFill()
                            } else {
                                ProgressView()
                            }
                        }
                        .frame(width: 80, height: 80)
                        .clipShape(Circle())
                        
                        VStack(alignment: .leading, spacing: 4) {
                            Text(profile.name)
                                .font(.headline)
                            Text(profile.email)
                                .font(.subheadline)
                                .foregroundColor(.secondary)
                        }
                    }
                    .padding(.vertical, 8)
                }
                
                Section {
                    Button("My Profile") { }
                    Button("Favorites") { }
                }
            }
            .navigationTitle("Profile")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .navigationBarTrailing) {
                    Button("Done") {
                        presentationMode.wrappedValue.dismiss()
                    }
                }
            }
        }
    }
}

/**
 * A reusable SwiftUI view component that displays a single museum object in a grid cell.
 */
struct ObjectFrame: View {
    let obj: MuseumObject
    let isLiked: Bool
    let onToggleLike: () -> Void

    var body: some View {
        VStack(alignment: .leading, spacing: 4) {
            ZStack(alignment: .topTrailing) {
                GeometryReader { geometry in
                    AsyncImage(url: URL(string: obj.primaryImageSmall)) { phase in
                        switch phase {
                        case .empty:
                            ProgressView()
                                .frame(width: geometry.size.width, height: geometry.size.width)
                        case .success(let image):
                            image
                                .resizable()
                                .scaledToFill()
                                .frame(width: geometry.size.width, height: geometry.size.width)
                                .clipped()
                        default:
                            EmptyView()
                                .frame(width: geometry.size.width, height: geometry.size.width)
                        }
                    }
                }
                .aspectRatio(1, contentMode: .fit)
                .cornerRadius(12)
                
                // Native UI Toggle Button for iOS
                Button(action: {
                    onToggleLike()
                }) {
                    Image(systemName: isLiked ? "heart.fill" : "heart")
                        .foregroundColor(isLiked ? .red : .white)
                        .padding(8)
                        .background(Color.black.opacity(0.3))
                        .clipShape(Circle())
                }
                .padding(8)
            }

            Text(obj.title)
                .font(.headline)
                .lineLimit(1)

            Text(obj.artistDisplayName)
                .font(.subheadline)
                .lineLimit(1)

            Text(obj.objectDate)
                .font(.caption)
                .lineLimit(1)
        }
    }
}
