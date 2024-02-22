//
//  SocialWallView.swift
//  iosApp
//
//  Created by Roberto García Romero on 12/2/24.
//  Copyright © 2024 orgName. All rights reserved.
//

import SwiftUI

struct SocialWallView: View {
    var instagram = UIImage(named: "instagram")?.withRenderingMode(.alwaysTemplate).withTintColor(UIColor(Color("primaryColor")))
    var facebook = UIImage(named: "facebook")?.withRenderingMode(.alwaysTemplate).withTintColor(UIColor(Color("primaryColor")))
    var twitterx = UIImage(named: "twitterx")?.withRenderingMode(.alwaysTemplate).withTintColor(UIColor(Color("primaryColor")))

    var body: some View {
        ZStack {
            GeometryReader { metrics in
                Image("onda")
                    .resizable()
                    .aspectRatio(contentMode: .fill)
                    .scaledToFill()
                    .frame(maxWidth: metrics.size.width)
            }.edgesIgnoringSafeArea(.bottom)
            HStack(spacing: 16) {
                Image(uiImage: instagram!)
                    .resizable()
                    .foregroundColor(Color("primaryColor"))
                    .frame(width: 24, height: 24)
                    .onTapGesture {
                        if let url = URL(string: "https://www.instagram.com/destapalaslegumbres/"), UIApplication.shared.canOpenURL(url) {
                            UIApplication.shared.open(url)
                        }
                    }
                Image(uiImage: facebook!)
                    .resizable()
                    .foregroundColor(Color("primaryColor"))
                    .frame(width: 24, height: 24)
                    .onTapGesture {
                        if let url = URL(string: "https://www.facebook.com/destapalaslegumbres"), UIApplication.shared.canOpenURL(url) {
                            UIApplication.shared.open(url)
                        }

                    }
                Image(uiImage: twitterx!)
                    .resizable()
                    .foregroundColor(Color("primaryColor"))
                    .frame(width: 24, height: 24)
                    .onTapGesture {
                        if let url = URL(string: "https://twitter.com/dstapalegumbres"), UIApplication.shared.canOpenURL(url) {
                            UIApplication.shared.open(url)
                        }

                    }
            }.padding(.top,40)
        }
        .frame(maxWidth: .infinity, maxHeight: 80)
        .edgesIgnoringSafeArea(.bottom)
    }
}
