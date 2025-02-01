//
//  DetailView.swift
//  iosApp
//
//  Created by Roberto García Romero on 5/2/24.
//  Copyright © 2024 orgName. All rights reserved.
//

import Foundation
import SwiftUI
import shared

struct DetailView: View {
    let id: String
    @ObservedObject var viewModel: IOSDetailViewModel

    init(id: String) {
        self.id = id
        self.viewModel = IOSDetailViewModel(id: id)
    }
    var body: some View {
        VStack {
            detailContent()
            errorContent(clearError: {
                viewModel.clearError()
            })
        }
        .onAppear {
            viewModel.startObserving()
        }
        .onDisappear {
            viewModel.dispose()
        }
    }
    
    func detailContent() -> AnyView {
        if(viewModel.state.isLoading) {
            return AnyView(LoadingView())
        } else {
            if(viewModel.state.voted) {
                return AnyView(
                    DetailScreen(
                        tapa: viewModel.state.tapa!,
                        voted: true,
                        voteTapa: { vote, tapa in
                            Task {
                                viewModel.voteTapa(vote: vote, tapa: tapa)
                            }
                        }
                    ).overlay(alignment: .top) {
                        Color("secondaryColor")
                            .background(Color("secondaryColor"))
                            .ignoresSafeArea(edges: .top)
                            .frame(height: 0)
                    }
                )
            } else {
                return AnyView(
                    DetailScreen(
                        tapa: viewModel.state.tapa!,
                        voted: viewModel.state.voted,
                        voteTapa: { vote, tapa in
                            Task {
                                viewModel.voteTapa(vote: vote, tapa: tapa)
                            }
                        }
                    ).overlay(alignment: .top) {
                        Color("secondaryColor")
                            .background(Color("secondaryColor"))
                            .ignoresSafeArea(edges: .top)
                            .frame(height: 0)
                    }
                )
            }
        }
    }
    
    func errorContent(clearError: () -> ()) -> AnyView {
        @State var showingAlert = false

        switch(viewModel.errorState) {
            case .noError: return AnyView(EmptyView())
            case .tapaVotedYet:
                showingAlert = true
                clearError()
                return AnyView(EmptyView().alert("No puedes volver a votar la misma tapa", isPresented: $showingAlert) {
                    Button("OK", role: .cancel) { }
                })
            case .writtingError:
                showingAlert = true
                clearError()
                return AnyView(EmptyView().alert("Se ha producido un error al enviar la votación", isPresented: $showingAlert) {
                    Button("OK", role: .cancel) { }
                })
            case .redingError:
                showingAlert = true
                clearError()
                return AnyView(EmptyView().alert("Se ha producido un error al enviar la votación", isPresented: $showingAlert) {
                    Button("OK", role: .cancel) { }
                })
            case .noUserError:
                showingAlert = true
                clearError()
                return AnyView(EmptyView().alert("Se ha producido un error al enviar la votación", isPresented: $showingAlert) {
                    Button("OK", role: .cancel) { }
                })
        }
    }
}

struct DetailScreen: View {
    let tapa: TapaItemBo
    let voted: Bool
    let voteTapa: (Int32, String) -> ()

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
            ZStack {
                
                ScrollView {}
                .safeAreaInset(edge: .bottom) {
                    VStack{
                        SocialWallView()
                    }
                }
                GeometryReader { geometry in
                    ScrollView {
                        VStack(alignment: .leading) {
                            TapaCover(tapa: tapa)
                            VStack(alignment: .leading) {
                                VStack(alignment: .leading) {
                                    Text(tapa.name.uppercased())
                                        .foregroundStyle(Color("secondaryColor"))
                                        .font(Font.custom("Berlin Sans FB Demi", size: 20))
                                    Text("\(tapa.local.name) \(tapa.local.province)")
                                        .foregroundStyle(Color("secondaryColor"))
                                        .font(Font.custom("Montserrat", size: 14))
                                    HStack {
                                        if(!tapa.local.instagram.isEmpty) {
                                            Image("instagram")
                                                .resizable()
                                                .frame(width: 24, height: 24)
                                                .onTapGesture {
                                                    if let url = URL(string: tapa.local.instagram), UIApplication.shared.canOpenURL(url) {
                                                        UIApplication.shared.open(url)
                                                    }
                                                }
                                        }
                                        if(!tapa.local.facebook.isEmpty) {
                                            Image("facebook")
                                                .resizable()
                                                .frame(width: 24, height: 24)
                                                .onTapGesture {
                                                    if let url = URL(string: tapa.local.facebook), UIApplication.shared.canOpenURL(url) {
                                                        UIApplication.shared.open(url)
                                                    }
                                                }
                                        }
                                    }
                                }.padding(.bottom)
                                
                                Text(tapa.shortDescription)
                                    .foregroundStyle(Color("secondaryColor"))
                                    .font(Font.custom("Montserrat", size: 16))
                                LegumesSection(tapa: tapa)
                                VoteSection(tapa: tapa, voted: voted, voteTapa: { vote,tapa in
                                    voteTapa(vote,tapa)
                                })
                            }
                            .padding(.horizontal,24)
                        }
                    }
                    .frame(maxWidth: .infinity,
                                               minHeight: geometry.size.height - 100,
                                               alignment: .top)
                    .padding(.bottom, 100)
                    .zIndex(1)
                    .safeAreaInset(edge: .top) {
                        VStack {
                            HeaderView(hasLogout: false, onItemClicked: {}).padding(.top,1)
                        }.zIndex(2)
                    }
                    
                }
            }
        }
    }
}

struct TapaCover: View {
    let tapa: TapaItemBo
    
    var body: some View {
        ZStack(alignment: .bottomLeading) {
            AsyncImage(
                url: URL(string: tapa.photo),
                content: { image in
                    image.resizable()
                        .aspectRatio(contentMode: .fit)
                        .scaledToFill()
                        .frame(maxWidth: .infinity, maxHeight: 300)
                        .clipped()
                },
                placeholder: {
                    Rectangle()
                        .frame(maxWidth: .infinity, maxHeight: 300)
                    
                }
            ).padding(.top,80)
        }
    }
}

struct LegumesSection: View {
    let tapa: TapaItemBo
    
    var body: some View {
        VStack {
            ScrollView() {
                VStack(alignment: .leading, spacing: 8) {
                    ForEach(tapa.legumes, id: \.self) { legume in
                        VStack(alignment: .leading) {
                            HStack {
                                Image(uiImage: getLegumeImage(legume: legume))
                                    .resizable()
                                    .frame(width: 36, height: 36)
                                Text(legume)
                                    .lineLimit(3)
                                    .foregroundStyle(Color("secondaryColor"))
                                    .font(Font.custom("Montserrat", size: 14))
                                    .multilineTextAlignment(.center)
                            }
                        }
                    }
                }
            }
        }
    }
}

struct VoteSection: View {
    let tapa: TapaItemBo
    let voted: Bool
    let voteTapa: (Int32, String) -> ()
    var vote = UIImage(named: "vote")?.withRenderingMode(.alwaysTemplate).withTintColor(UIColor(Color("primaryColor")))
    @State private var score: Double = 50

    var body: some View {
        VStack(alignment: .leading) {
            Text("Valora la tapa")
                .foregroundStyle(Color("secondaryColor"))
                .font(Font.custom("Berlin Sans FB Demi", size: 20))
            Text("Podrás otorgar una valoración a cada tapa una sola vez con una puntuación de 0 a 100.\nPara valorar la tapa mueve la barra para obtener la puntuación deseada. Confirma tu valoración pulsando el botón")
                .foregroundStyle(Color("secondaryColor"))
                .font(Font.custom("Montserrat", size: 16))
            if(!voted) {
                HStack {
                    Text("0")
                        .foregroundStyle(Color("secondaryColor"))
                        .font(Font.custom("Berlin Sans FB Demi", size: 20))
                    Slider(value: $score, in: 0...100)
                        .tint(Color("secondaryColor"))
                    Text("100")
                        .foregroundStyle(Color("secondaryColor"))
                        .font(Font.custom("Berlin Sans FB Demi", size: 20))
                }
                Button(action: {
                    voteTapa(Int32(score.rounded()), tapa.id)
                }, label: {
                    HStack {
                        Image(uiImage: vote!)
                            .resizable()
                            .tint(Color("primaryColor"))
                            .frame(width: 24, height: 24)
                        Text("Valora con \(Int(score.rounded())) puntos")
                            .foregroundStyle(Color("primaryColor"))
                            .font(Font.custom("Berlin Sans FB Demi", size: 20))
                    }
                })
                .frame(maxWidth: .infinity)
                .padding(16)
                .background(Color("secondaryColor"))
                .clipShape(RoundedRectangle(cornerRadius: 35, style: .continuous))
                .padding(.bottom,32)
            } else {
                Text("Ya has votado esta tapa")
                    .foregroundStyle(Color("secondaryColor"))
                    .font(Font.custom("Berlin Sans FB Demi", size: 20))
                    .padding()
            }
        }
    }
}

struct DetailView_Previews: PreviewProvider {
    static var previews: some View {
        MainView()
    }
}


extension DetailView {
    @MainActor class IOSDetailViewModel: ObservableObject {
        private let viewModel: DetailViewModel
                
        @Published var state: DetailStateSwift = DetailStateSwift()
        @Published var errorState: FirestoreErrorStateSwift = FirestoreErrorStateSwift.noError

        private var handle: DisposableHandle?

        init(id: String) {
            self.viewModel = DetailViewModel.shared
            self.viewModel.getDetail(configuration: 0, id: id)
        }
        
        // Observes to state changes
        func startObserving() {
            handle = viewModel.state.subscribe(onCollect: { state in
                if let state = state {
                    self.state = DetailStateSwift(
                        
                    )
                }
            })
        }
        
        func voteTapa(vote: Int32, tapa: String) {
            viewModel.vote(vote: vote, tapa: tapa)
        }
        
        func clearError() {
            viewModel.clearError()
        }
        
        // Removes the listener
        func dispose() {
            handle?.dispose()
        }
    }
}

func getLegumeImage(legume: String)-> UIImage {
    var bean = UIImage(named: "alubias")
    var lentil = UIImage(named: "lentejas")
    var chickpea = UIImage(named: "garbanzos")
    
    switch(legume.split(separator: " ")[0]) {
    case "Lenteja": return lentil!
    case "Garbanzo": return chickpea!
    default: return bean!
    }
}
