//
//  HeaderView.swift
//  iosApp
//
//  Created by Roberto García Romero on 22/2/24.
//  Copyright © 2024 orgName. All rights reserved.
//

import SwiftUI

struct HeaderView: View {
    let hasLogout: Bool
    let onItemClicked: () -> ()
    var logout = UIImage(named: "logout")

    var body: some View {
        ZStack(alignment: .topTrailing) {
            GeometryReader { metrics in
                Image("header_onda")
                    .resizable()
                    .aspectRatio(contentMode: .fill)
                    .scaledToFill()
                    .frame(maxWidth: metrics.size.width)
                    .frame(height: 120)
            }.edgesIgnoringSafeArea(.top)
            if(hasLogout) {
                ZStack {
                    Image(uiImage: logout!)
                        .resizable()
                        .tint(Color("primaryColor"))
                        .frame(width: 24, height: 24)
                        .padding()
                        .onTapGesture {
                            onItemClicked()
                        }
                }
            }
        }.edgesIgnoringSafeArea(.top)
    }
}
