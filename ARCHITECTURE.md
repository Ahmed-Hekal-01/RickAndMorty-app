# Rick and Morty App - Architecture Guide

## Project Structure

```
com.example.rickandmortyapp/
├── data/                           # Data Layer
│   ├── api/                        # API interface & implementation
│   ├── database/                   # Local persistence
│   │   ├── dao/                    # Room DAOs
│   │   └── entity/                 # Room entities (@Entity)
│   ├── mapper/                     # Data transformations
│   ├── model/                      # Domain/Business models
│   ├── remote/                     # Remote data source
│   │   └── dto/                    # API response DTOs
│   └── repository/                 # Repository pattern implementations
├── di/                             # Dependency Injection (Hilt)
├── ui/                             # Presentation Layer
│   ├── components/                 # Reusable Composables
│   ├── screens/                    # Screen-level Composables
│   ├── theme/                      # Material Design theme
│   └── viewmodel/                  # ViewModels
└── util/                           # Shared utilities
```

## Architecture Layers

### 1. Data Layer (`data/`)
Handles all data operations and serves as the single source of truth.

#### Components:
- **API** (`data/api/`): Ktor interfaces and implementations
- **Remote DTOs** (`data/remote/dto/`): API response models
  - `CharacterDto`, `EpisodeDto`, `LocationDto`
  - `PageInfoDto` for pagination metadata
  - Response wrappers like `CharacterResponseDto`
- **Database Entities** (`data/database/entity/`): Room entities
  - Annotated with `@Entity`, `@PrimaryKey`, `@ColumnInfo`
- **DAOs** (`data/database/dao/`): Database access objects
- **Mappers** (`data/mapper/`): Convert between layers
  - DTO → Domain Model
  - Entity ↔ Domain Model
- **Models** (`data/model/`): Domain/business models used by UI
- **Repository** (`data/repository/`): Aggregates data from multiple sources

### 2. Presentation Layer (`ui/`)
Handles UI rendering and user interactions using Jetpack Compose.

#### Components:
- **Screens** (`ui/screens/`): Full-screen Composables
  - `CharactersScreen`, `CharacterDetailScreen`, `EpisodesScreen`
- **Components** (`ui/components/`): Reusable UI components
  - `CharacterCard`, `LoadingIndicator`
- **ViewModels** (`ui/viewmodel/`): Manage UI state and business logic
- **Theme** (`ui/theme/`): Material Design configuration

### 3. Dependency Injection (`di/`)
Provides dependencies throughout the app using Hilt.

### 4. Utilities (`util/`)
Shared helper functions, extensions, and constants.

## Data Flow

```
API Response (DTO)
       ↓
   [Mapper]
       ↓
Domain Model  →  Repository  →  ViewModel  →  UI (Composable)
       ↓
   [Mapper]
       ↓
Database Entity  →  Room DB
```

### Example Flow:
1. **API Call**: `RickAndMortyApi` fetches `CharacterDto`
2. **Mapping**: `CharacterDtoMapper` converts to `Character` (domain model)
3. **Caching**: `CharacterEntityMapper` converts to `CharacterEntity` for Room
4. **Repository**: Combines remote and local data sources
5. **ViewModel**: Exposes `StateFlow<List<Character>>` to UI
6. **UI**: Composables observe and render the state

## Key Principles

### Separation of Concerns
- **DTOs** handle API serialization
- **Entities** handle database persistence (Room annotations)
- **Models** are clean models for business logic

### Single Responsibility
- Each mapper handles one specific conversion
- Repositories coordinate data sources but don't contain business logic
- ViewModels manage UI state but don't access APIs directly

### Dependency Rule
- Dependencies point inward: UI → ViewModel → Repository → API/DB
- Inner layers know nothing about outer layers

## Technologies
- **UI**: Jetpack Compose
- **DI**: Hilt
- **Networking**: Ktor + OkHttp
- **Database**: Room
- **Async**: Kotlin Coroutines + Flow
- **Architecture**: MVVM + Repository Pattern

## Testing Strategy
- **Unit Tests**: ViewModels, Repositories, Mappers
- **Integration Tests**: Database DAOs, API clients
- **UI Tests**: Composable screens with test rules

---
