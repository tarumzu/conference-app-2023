package io.github.droidkaigi.confsched2023.data.sessions

import co.touchlab.kermit.Logger
import io.github.droidkaigi.confsched2023.data.user.UserDataStore
import io.github.droidkaigi.confsched2023.model.SessionsRepository
import io.github.droidkaigi.confsched2023.model.Timetable
import io.github.droidkaigi.confsched2023.model.TimetableItem
import io.github.droidkaigi.confsched2023.model.TimetableItemId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

class DefaultSessionsRepository(
    private val sessionsApi: SessionsApiClient,
    private val userDataStore: UserDataStore,
    private val sessionCacheDataStore: SessionCacheDataStore,
) : SessionsRepository {

    override fun getTimetableStream(): Flow<Timetable> {
        return combine(
            sessionCacheDataStore.getTimetableStream(),
            userDataStore.getFavoriteSessionStream(),
        ) { timetable, favorites ->
            timetable.copy(bookmarks = favorites)
        }
            .onStart {
                Logger.d("DefaultSessionsRepository onStart getTimetableStream()")
                sessionCacheDataStore.save(sessionsApi.sessionsAllResponse())
                Logger.d("DefaultSessionsRepository onStart fetched")
            }
    }

    override fun getTimetableItemWithBookmarkStream(id: TimetableItemId): Flow<Pair<TimetableItem, Boolean>> {
        return getTimetableStream().map { timetable ->
            timetable.timetableItems.first { it.id == id } to timetable.bookmarks.contains(id)
        }
    }

    override suspend fun toggleBookmark(id: TimetableItemId) {
        userDataStore.toggleFavorite(id)
    }
}
