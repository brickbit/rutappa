package com.rgr.rutappa.domain.error

sealed class FirestoreError: Error() {
    data object NoError: FirestoreError()
    data object TapaVotedYet: FirestoreError()
    data object WritingError: FirestoreError()
    data object ReadingError: FirestoreError()
    data object NoUserError: FirestoreError()

}