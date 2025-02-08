//
//  HeaderView.swift
//  iosApp
//
//  Created by Roberto García Romero on 22/2/24.
//  Copyright © 2024 orgName. All rights reserved.
//

import SwiftUI

struct HeaderView: View {
    let hasMenu: Bool
    let onItemClicked: () -> ()
    var menu = UIImage(named: "menu")

    var body: some View {
        ZStack(alignment: .topTrailing) {
            Image("header_onda")
                .resizable()
                .aspectRatio(contentMode: .fill)
                .scaledToFill()
                .frame(height: 120)
            if(hasMenu) {
                ZStack {
                    Image(uiImage: menu!)
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
