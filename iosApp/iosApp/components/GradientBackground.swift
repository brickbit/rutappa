//
//  GradientBackground.swift
//  iosApp
//
//  Created by Roberto on 8/2/25.
//  Copyright © 2025 orgName. All rights reserved.
//

import SwiftUI

struct GradientBackground: View {
    var body: some View {
        ZStack {
            Rectangle().fill(
                LinearGradient(
                    gradient: Gradient(
                        colors: [
                            Color.white,
                            Color("primaryColor").opacity(0.6),
                            Color("primaryColor")
                        ]
                    ),
                    startPoint: .top,
                    endPoint: .bottom
                )
            ).frame(maxWidth: .infinity, maxHeight: .infinity)
                .edgesIgnoringSafeArea(.all)
            
        }
    }
}


#Preview {
    GradientBackground()
}
