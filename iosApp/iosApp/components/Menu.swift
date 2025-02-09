//
//  Menu.swift
//  iosApp
//
//  Created by Roberto on 8/2/25.
//  Copyright © 2025 orgName. All rights reserved.
//

import SwiftUI

struct MenuRutapa: View {
    var onCloseClicked: () -> Void
    var onTapasClicked: () -> Void
    var onPartnersClicked: () -> Void
    var onLogoutClicked: () -> Void
    
    var body: some View {
        VStack(alignment: .center) {
            HStack(alignment: .lastTextBaseline) {
                Spacer()
                Text("X")
                    .foregroundStyle(Color("primaryColor"))
                    .font(Font.custom("Berlin Sans FB Demi", size: 24))
                    .padding(.top, 16)
                    .padding(.trailing, 20)
                    .onTapGesture {
                        onCloseClicked()
                    }
            }
            Spacer()
            VStack(spacing: 36) {
                Text("TAPAS")
                    .foregroundStyle(Color("primaryColor"))
                    .font(Font.custom("Berlin Sans FB Demi", size: 28))
                    .onTapGesture {
                        onTapasClicked()
                    }
                Text("PATROCINADORES")
                    .foregroundStyle(Color("primaryColor"))
                    .font(Font.custom("Berlin Sans FB Demi", size: 28))
                    .onTapGesture {
                        onPartnersClicked()
                    }
                Text("SALIR")
                    .foregroundStyle(Color("primaryColor"))
                    .font(Font.custom("Berlin Sans FB Demi", size: 28))
                    .onTapGesture {
                        onLogoutClicked()
                    }
            }
            Spacer()
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
        .background(Color("secondaryColor").opacity(0.95))
    }
}

#Preview {
    MenuRutapa(onCloseClicked: {}, onTapasClicked: {}, onPartnersClicked: {}, onLogoutClicked: {})
}
