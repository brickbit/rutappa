package com.rgr.rutappa.domain.useCase

import com.rgr.rutappa.domain.provider.DocumentProvider
import com.rgr.rutappa.domain.provider.FirestoreProvider

class ResultUseCase(
    private val fireStoreProvider: FirestoreProvider,
    private val documentProvider: DocumentProvider,
) {
    suspend operator fun invoke(): Result<List<Pair<String, Int>>> {
        fireStoreProvider.getResult().getOrNull()?.let { tapas ->
            val data = tapas.mapNotNull { it }
            documentProvider.writeJson(data)
            val groupedTapas = data.groupBy {
                it.tapa
            }
            val result = groupedTapas.map {
                Pair(
                    it.key,
                    it.value.sumOf { it.vote }
                )
            }
            return Result.success(result.sortedByDescending { it.second })
        }
        return Result.failure(Error())
    }
}

