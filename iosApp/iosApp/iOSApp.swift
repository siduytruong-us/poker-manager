import SwiftUI
import Shared

@main
struct iOSApp: App {
    init() {
        FirebaseInitializer.companion.doInit()
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}