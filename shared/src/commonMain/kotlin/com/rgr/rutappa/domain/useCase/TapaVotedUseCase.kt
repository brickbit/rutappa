package com.rgr.rutappa.domain.useCase

import com.rgr.rutappa.domain.repository.LocalRepository

class TapaVotedUseCase(
    private val localRepository: LocalRepository
) {
    suspend operator fun invoke(tapa: String): Boolean {
        return localRepository.getTapaVoted().contains(tapa)
    }
}