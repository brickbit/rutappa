//
//  LoadingView.swift
//  iosApp
//
//  Created by Roberto García Romero on 6/2/24.
//  Copyright © 2024 orgName. All rights reserved.
//

import SwiftUI

struct LoadingView: View {
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
          ProgressView()
              .progressViewStyle(CircularProgressViewStyle(tint: Color("secondaryColor")))
              .scaleEffect(2.0, anchor: .center)
      }
  }
}
