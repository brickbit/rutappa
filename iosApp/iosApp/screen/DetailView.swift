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
import Kingfisher

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
            viewModel.getLocation()
        }
        .onDisappear {
            viewModel.dispose()
        }
    }
    
    func detailContent() -> AnyView {
        if(viewModel.state.isLoading) {
            return AnyView(LoadingView())
        } else {
            if(true/*viewModel.state.voted*/) {
                return AnyView(
                    DetailScreen(
                        location: (viewModel.state.location?.first ?? "0.0") as String,
                        tapa: viewModel.state.tapa!,
                        voted: true,
                        voteTapa: { vote, tapa in
                            Task {
                                viewModel.voteTapa(vote: vote, tapa: tapa)
                            }
                        },
                        logoutAction: {
                            
                        },
                        deleteAccountAction: {
                            
                        },
                        navigateToPartnersAction: {
                            
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
                        location: (viewModel.state.location?.first ?? "0.0") as String,
                        tapa: viewModel.state.tapa!,
                        voted: true,//viewModel.state.voted,
                        voteTapa: { vote, tapa in
                            Task {
                                viewModel.voteTapa(vote: vote, tapa: tapa)
                            }
                        },
                        logoutAction: {
                            
                        },
                        deleteAccountAction: {
                            
                        },
                        navigateToPartnersAction: {
                            
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
    let location: String
    let tapa: TapaItemBo
    let voted: Bool
    let voteTapa: (Int32, String) -> ()
    let logoutAction: () -> ()
    let deleteAccountAction: () -> ()
    let navigateToPartnersAction: () -> ()
    
    @State private var showingLogout = false
    @State private var showingMenu = false
    
    var body: some View {
        ZStack {
            ZStack(alignment: .bottom) {
                ZStack(alignment: .top) {
                    ZStack {
                        GradientBackground()
                        Text(location)
                        DetailScreenContent(
                            tapa: tapa,
                            voted: voted,
                            voteTapa: voteTapa
                        )
                    }
                    HeaderView(
                        hasMenu: true,
                        onItemClicked: {
                            showingMenu.toggle()
                        }
                    )
                    .padding(.top,1)
                    .confirmationDialog("Change background", isPresented: $showingLogout) {
                        Button("Eliminar cuenta") { deleteAccountAction() }
                        Button("Cerrar sesión") { logoutAction() }
                        Button("Continuar logado", role: .cancel) { }
                    } message: {
                        Text("¿Desea cerrar la sesión o eliminar su cuenta?")
                    }
                }
                SocialWallView()
            }
        }
        if(showingMenu) {
            MenuRutapa(
                onCloseClicked: {
                    showingMenu.toggle()
                },
                onTapasClicked: {
                    showingMenu.toggle()
                },
                onPartnersClicked: {
                    showingMenu.toggle()
                    navigateToPartnersAction()
                },
                onLogoutClicked: {
                    showingMenu.toggle()
                    showingLogout.toggle()
                }
            )
        }
    }
}

struct DetailScreenContent: View {
    let tapa: TapaItemBo
    let voted: Bool
    let voteTapa: (Int32, String) -> ()

    var body: some View {
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
    }
}

struct TapaCover: View {
    let tapa: TapaItemBo
    
    var body: some View {
        ZStack(alignment: .bottomLeading) {
            KFImage(URL(string: tapa.photo))
                .cacheOriginalImage()
                .placeholder {
                    ProgressView()
                }
                .resizable()
                .aspectRatio(contentMode: .fit)
                .scaledToFill()
                .frame(maxWidth: .infinity, maxHeight: 300)
                .clipped()
                .padding(.top,100)
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
            Spacer(minLength: 100)
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
                        isLoading: state.isLoading,
                        tapa: state.tapa,
                        //state.voted,
                        location: state.location,
                        voteStatus: state.voteStatus
                        //state.canVote,
                        //state.canVote
                    )
                }
            })
        }
        
        func getLocation() {
            viewModel.getLocation()
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
    let bean = UIImage(named: "alubias")
    let lentil = UIImage(named: "lentejas")
    let chickpea = UIImage(named: "garbanzos")
    
    switch(legume.split(separator: " ")[0]) {
    case "Lenteja": return lentil!
    case "Garbanzo": return chickpea!
    default: return bean!
    }
}
