# Frame — OTT Android assignment

A Kotlin, XML-based OTT demo with a featured hero, two horizontal RecyclerView rails, title details, and lifecycle-aware Media3 playback. No Compose is used.

## Build and run

Use Android Studio with JDK 17 or newer (its bundled JBR works), Android SDK 36, and internet access for the initial dependency download. Minimum Android version: 8.0 / API 26.

```sh
./gradlew assembleDebug
./gradlew testDebugUnitTest lintDebug
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

Open this folder in Android Studio, let Gradle sync, select a phone/emulator, and run `app`. Catalogue JSON is bundled; images and films require internet. APK: `app/build/outputs/apk/debug/app-debug.apk`.

## Architecture and structure

One app module keeps the exercise small. Package boundaries enforce the intended dependency direction:

- `presentation`: XML view binding, Activities, ViewModels, immutable UI states, RecyclerView ListAdapters, player listener adapter.
- `domain/model`: platform-independent content, rails and typed player events.
- `domain/repository`: content and analytics contracts.
- `domain/usecase`: home retrieval, selected-title retrieval, event recording.
- `data/remote`: replaceable data source interface and asset-backed mock implementation.
- `data/dto`, `data/mapper`: transport models and conversion to domain models.
- `data/repository`: repository implementations and final analytics logging.
- `assets/home.json`: mock API response containing hero ID, rail membership, and all title metadata/URLs.
- `OttApplication`: a small manual dependency container; no framework is needed for this scope.

Data flow: Activity → ViewModel → Use case → Repository interface → Repository implementation → Remote data source → JSON DTO → Domain mapper. UI never parses JSON or owns the catalogue. StateFlow exposes Loading, Success, Empty and Error; lifecycle-aware collectors stop when screens are not visible. Retry starts a fresh request and concurrent reloads are guarded. Cancellation is propagated rather than converted into an error.

## Navigation and playback

Explicit internal Activity navigation passes only a content ID. Detail and player reload the selected title through the domain contract. Android manages the back stack; buttons and system Back return to the previous screen. ViewModels survive configuration changes.

The player is created while the screen is started and released in `onStop`. Position and user play intent are retained in SavedStateHandle across rotation and saved-state restoration. PlayerView supplies play/pause, seek and progress controls. Audio focus, noisy-headphone handling and keep-screen-on while playing are enabled. Media3 buffering is visible; a recoverable playback error shows a retry button.

## Player events

`ExoPlayer → PlayerEventHandler → PlayerViewModel → RecordPlayerEventUseCase → EventRepository → LoggingEventRepository`.

Eight event types are supported: READY, STARTED, PAUSED, SEEKED, BUFFERING_STARTED, BUFFERING_ENDED, COMPLETED, ERROR. Each carries content ID and position; errors include the Media3 error-code name. Pause means play intent was turned off; buffering does not generate a false pause. Started is emitted when frames/audio are actually playing. A ViewModel channel serializes event recording. Logcat tag: `PlayerAnalytics`; messages use `PLAYER_<TYPE>`. The Activity only owns the player lifecycle and view wiring; event interpretation and logging live separately.

## Replacing the mock with an API

Implement `ContentRemoteDataSource.fetchHome()` with a Retrofit service or another HTTP client, returning `HomeDto`. Bind that implementation in `OttApplication` instead of `MockContentRemoteDataSource`. Repository contracts, use cases, domain models, ViewModels and screens stay unchanged. Add transport error mapping/authentication and a cache at the data boundary as the real backend requires. JSON rail membership is resolved through DTO IDs, independent of layout.

## Libraries and decisions

AndroidX AppCompat/Activity/Core, Lifecycle ViewModel and runtime, Kotlin Coroutines/StateFlow, RecyclerView, Material Components, Coil image loading, and Media3 ExoPlayer/UI/HLS. JUnit and coroutine-test verify mapping, repository selection, state transitions and event forwarding. XML view binding avoids unsafe view lookup. ListAdapter/DiffUtil handles list updates. Dependencies are pinned to compatible versions for SDK 36. Official player references: https://developer.android.com/media/media3/exoplayer/hello-world and https://developer.android.com/media/media3/exoplayer/listening-to-player-events.

## Manual acceptance checks

1. Launch: featured Sintel banner, description and View details appear; Trending now and Picked for you scroll horizontally.
2. Open hero and cards: corresponding title, image, description and Play button appear.
3. Play: film starts; pause/resume, scrub the seek bar, and check playback progress.
4. Rotate while playing/paused: title and position restore; paused intent stays paused.
5. Background/foreground: playback stops offscreen, resources release, and position restores on return.
6. Disconnect internet: playback error offers retry; reconnect and retry. Failed images have gradient placeholders.
7. Inspect Logcat for typed events; seek near the end to verify completion.
8. Use empty `content`/`rails` with empty `heroId` in the mock to inspect Empty; malformed JSON to inspect Error and Retry. Restore the asset afterward.

## Known limitations

This is a foreground-only demonstration: no DRM, login, downloads, subtitles, background playback, persistent catalogue cache, or real analytics backend. Public W3C and Mux sample-film URLs and illustrative Unsplash scenery can change or be unavailable. Images are illustrative, not official film artwork. Catalogue has four entries: two films with trailer/full-film editions, reused across two curated rails. Mock JSON is parsed on an IO dispatcher for each request; no artificial network delay is added. Analytics are logged locally, not durably uploaded. A paused player may restart buffering briefly on recreation. Publishing a hosted Git repository requires the submitter's hosting account and destination.
