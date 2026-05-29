import SwiftUI
import Shared
import GoogleSignIn

struct ComposeView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        let controller = MainViewControllerKt.MainViewController()

        // This bridges the Kotlin delegate to the native iOS Google Sign In SDK
        GoogleSignInProvider.shared.delegate = { onTokenReceived, onError in
            GIDSignIn.sharedInstance.signIn(withPresenting: controller) { result, error in
                if let error = error {
                    onError(error.localizedDescription)
                } else if let idToken = result?.user.idToken?.tokenString {
                    let accessToken = result?.user.accessToken.tokenString
                    onTokenReceived(idToken, accessToken)
                } else {
                    onError("Failed to get ID Token")
                }
            }
        }

        return controller
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}

struct ContentView: View {
    var body: some View {
        ComposeView()
            .ignoresSafeArea(.keyboard)
    }
}
