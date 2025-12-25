# Листинги кода с комментариями

## Листинг А. Динамическое боковое меню с адаптацией под роль пользователя

**Файл:** `ui/components/DrawerMenu.kt`

```kotlin
// Формирование списка пунктов меню в зависимости от типа пользователя
// Используется конструкция when для сопоставления с образцом (pattern matching)
val menuItems = when (userType) {
    
    // Меню для обычного пользователя (родителя)
    // Включает полный набор функций: управление детьми, избранное, записи
    UserType.USER -> listOf(
        DrawerMenuItem("Главная", "home"),           // Главный экран с каталогом кружков
        DrawerMenuItem("Личный кабинет", "profile"), // Профиль пользователя
        DrawerMenuItem("Мои записи", "my_applications"), // Список поданных заявок
        DrawerMenuItem("Избранное", "favorites"),    // Избранные кружки
        DrawerMenuItem("Дети", "children")           // Управление профилями детей
    )
    
    // Меню для ребёнка (ограниченный функционал)
    // Отсутствует раздел "Дети", так как ребёнок не может управлять профилями
    UserType.CHILD -> listOf(
        DrawerMenuItem("Главная", "home"),
        DrawerMenuItem("Личный кабинет", "profile"),
        DrawerMenuItem("Мои записи", "my_applications"),
        DrawerMenuItem("Избранное", "favorites")
        // Раздел "Дети" недоступен для пользователей с ролью CHILD
    )
    
    // Меню для организатора кружков
    // Специализированные разделы для управления кружками и заявками
    UserType.ORGANIZER -> listOf(
        DrawerMenuItem("Главная", "home"),
        DrawerMenuItem("Личный кабинет", "profile"),
        DrawerMenuItem("Мои кружки", "my_clubs"),         // Управление своими кружками
        DrawerMenuItem("Заявки", "club_applications")     // Входящие заявки на запись
    )
    
    // Меню для администратора системы
    // Полный доступ к панелям управления всеми сущностями
    UserType.ADMIN -> listOf(
        DrawerMenuItem("Главная", "home"),
        DrawerMenuItem("Личный кабинет", "profile"),
        DrawerMenuItem("Пользователи", "admin_users"),    // Управление пользователями
        DrawerMenuItem("Кружки", "admin_clubs"),          // Модерация кружков
        DrawerMenuItem("Отзывы", "admin_reviews")         // Модерация отзывов
    )
    
    // Случай для неавторизованного пользователя (userType = null)
    // Доступна только главная страница
    null -> listOf(
        DrawerMenuItem("Главная", "home")
    )
}
```

**Описание:** Данный фрагмент кода демонстрирует реализацию ролевой модели доступа в приложении. Конструкция `when` языка Kotlin позволяет элегантно определить набор пунктов меню для каждого типа пользователя. Каждый `DrawerMenuItem` содержит отображаемое название и маршрут навигации. Такой подход обеспечивает гибкость и расширяемость системы — для добавления новой роли достаточно добавить новую ветку в выражение `when`.

---

## Листинг Б. Навигация приложения с ModalNavigationDrawer (Single Activity)

**Файл:** `MainActivity.kt`

```kotlin
// ModalNavigationDrawer — компонент Material 3 для реализации бокового меню
// Является корневым контейнером для всего содержимого приложения
ModalNavigationDrawer(
    drawerState = drawerState,              // Состояние меню (открыто/закрыто)
    gesturesEnabled = currentUser != null,  // Жесты доступны только авторизованным
    
    // Содержимое бокового меню
    drawerContent = {
        DrawerMenu(
            userType = currentUser?.userType,  // Передаём тип пользователя для адаптации меню
            
            // Обработчик нажатия на пункт меню
            onItemClick = { route ->
                scope.launch {                 // Запуск корутины для асинхронных операций
                    drawerState.close()        // Закрываем боковое меню
                    navController.navigate(route) {  // Выполняем навигацию
                        // Очищаем back stack до главного экрана
                        // Это предотвращает накопление экранов в стеке
                        popUpTo(Screen.Home) { inclusive = false }
                    }
                }
            },
            
            // Обработчик выхода из системы
            onLogout = {
                scope.launch {
                    drawerState.close()        // Закрываем меню
                    viewModel.logout()         // Очищаем данные сессии в ViewModel
                    navController.navigate(Screen.Login) {
                        // Полная очистка стека навигации при выходе
                        // inclusive = true удаляет и сам экран Login из стека
                        popUpTo(0) { inclusive = true }
                    }
                }
            }
        )
    }
) {
    // Основное содержимое приложения
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        // NavHost — контейнер для навигации между Composable-экранами
        // Реализует паттерн Single Activity Architecture
        NavHost(
            navController = navController,     // Контроллер навигации
            startDestination = Screen.Login    // Начальный экран — авторизация
        ) {
            // Здесь определяются все маршруты приложения через composable()
            // ...
        }
    }
}
```

**Описание:** Этот код демонстрирует реализацию Single Activity архитектуры с использованием Jetpack Compose Navigation. `ModalNavigationDrawer` оборачивает всё приложение и предоставляет боковое меню. Ключевые особенности: жесты для открытия меню доступны только после авторизации (`gesturesEnabled`), навигация выполняется асинхронно через корутины, при выходе полностью очищается стек навигации для предотвращения возврата к защищённым экранам.

---

## Листинг В. Визуализация рейтинга Топ-3 кружков с пьедесталом

**Файл:** `ui/screens/HomeScreen.kt`

```kotlin
// Проверяем наличие данных перед отрисовкой
if (topClubs.isNotEmpty()) {
    
    // Контейнер для первого места (располагается по центру сверху)
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,  // Центрирование по горизонтали
        verticalAlignment = Alignment.Bottom         // Выравнивание по нижнему краю
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // Заголовок с номером места и звездой
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "1",                      // Номер места
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    // Золотой цвет для первого места (HEX: #FFD700)
                    tint = Color(0xFFFFD700),
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            
            // Безопасное получение первого элемента списка
            // getOrNull возвращает null, если индекс выходит за границы
            topClubs.getOrNull(0)?.let { club ->
                ClubCardSmall(
                    club = club,
                    onClick = { onClubClick(club.id) },
                    modifier = Modifier.width(160.dp)  // Увеличенная ширина для 1 места
                )
            }
        }
    }

    Spacer(modifier = Modifier.height(12.dp))

    // Контейнер для второго и третьего мест (располагаются по бокам)
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly  // Равномерное распределение
    ) {
        // Второе место
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "2", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    // Серебряный цвет для второго места (HEX: #C0C0C0)
                    tint = Color(0xFFC0C0C0),
                    modifier = Modifier.size(20.dp)
                )
            }
            topClubs.getOrNull(1)?.let { club ->
                ClubCardSmall(
                    club = club,
                    onClick = { onClubClick(club.id) },
                    modifier = Modifier.width(140.dp)
                )
            }
        }

        // Третье место
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "3", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    // Бронзовый цвет для третьего места (HEX: #CD7F32)
                    tint = Color(0xFFCD7F32),
                    modifier = Modifier.size(20.dp)
                )
            }
            topClubs.getOrNull(2)?.let { club ->
                ClubCardSmall(
                    club = club,
                    onClick = { onClubClick(club.id) },
                    modifier = Modifier.width(140.dp)
                )
            }
        }
    }
}
```

**Описание:** Данный код реализует визуальное представление трёх лучших кружков в виде пьедестала почёта. Первое место отображается по центру с золотой звездой и увеличенной карточкой. Второе и третье места располагаются ниже по бокам с серебряной и бронзовой звёздами соответственно. Использование `getOrNull` обеспечивает безопасную работу даже при неполном списке топ-кружков. Цветовая схема звёзд соответствует олимпийским медалям.

---

## Листинг Г. SQL-запрос с комплексной фильтрацией кружков

**Файл:** `data/dao/ClubDao.kt`

```kotlin
// Аннотация @Query указывает, что метод выполняет SQL-запрос к базе данных
// Room автоматически генерирует реализацию на этапе компиляции
@Query("""
    SELECT * FROM clubs 
    WHERE isActive = 1                                    -- Только активные кружки
    
    -- Фильтр по городу: если параметр пустой, условие игнорируется
    -- LIKE с подстановочными знаками позволяет частичное совпадение
    AND (:city = '' OR city LIKE '%' || :city || '%')
    
    -- Фильтр по категории: NULL означает "любая категория"
    AND (:category IS NULL OR category = :category)
    
    -- Фильтр по минимальному возрасту участника
    -- Кружок подходит, если его начальный возраст <= указанному
    AND (:minAge IS NULL OR ageFrom <= :minAge)
    
    -- Фильтр по максимальному возрасту участника  
    -- Кружок подходит, если его конечный возраст >= указанному
    AND (:maxAge IS NULL OR ageTo >= :maxAge)
    
    -- Фильтр по максимальной стоимости
    AND (:maxPrice IS NULL OR pricePerMonth <= :maxPrice)
    
    -- Сортировка по рейтингу (лучшие кружки сверху)
    ORDER BY rating DESC
""")
fun searchClubs(
    city: String = "",                    // Город (по умолчанию — любой)
    category: ClubCategory? = null,       // Категория (enum, может быть null)
    minAge: Int? = null,                  // Минимальный возраст ребёнка
    maxAge: Int? = null,                  // Максимальный возраст ребёнка
    maxPrice: Int? = null                 // Максимальная цена
): Flow<List<Club>>                       // Возвращает реактивный поток данных
```

**Описание:** Этот SQL-запрос демонстрирует мощный механизм фильтрации в Room. Каждое условие проверяет значение параметра: если параметр равен NULL или пустой строке, условие автоматически считается истинным и не влияет на выборку. Это позволяет использовать один универсальный запрос для любых комбинаций фильтров. Возвращаемый тип `Flow<List<Club>>` обеспечивает реактивное обновление UI при изменении данных в базе.

---

## Листинг Д. Диалог добавления отзыва с интерактивным выбором рейтинга

**Файл:** `ui/screens/ClubDetailScreen.kt`

```kotlin
// Composable-функция для отображения диалога добавления отзыва
@Composable
private fun ReviewDialog(
    onDismiss: () -> Unit,                // Callback закрытия диалога
    onSubmit: (Int, String) -> Unit       // Callback отправки: (рейтинг, текст)
) {
    // Состояние рейтинга: mutableIntStateOf оптимизирован для примитивов
    var rating by remember { mutableIntStateOf(5) }  // По умолчанию 5 звёзд
    // Состояние текста отзыва
    var text by remember { mutableStateOf("") }

    // AlertDialog — стандартный диалог Material 3
    AlertDialog(
        onDismissRequest = onDismiss,     // Закрытие при нажатии вне диалога
        title = { 
            Text("Оставить отзыв", fontWeight = FontWeight.Bold) 
        },
        text = {
            Column {
                Text("Оценка:", fontSize = 14.sp, color = TextSecondary)
                Spacer(modifier = Modifier.height(8.dp))
                
                // Интерактивный выбор рейтинга звёздами
                Row {
                    // repeat(5) создаёт 5 кнопок-звёзд
                    repeat(5) { index ->
                        IconButton(
                            onClick = { rating = index + 1 },  // Установка рейтинга
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(
                                // Условный выбор иконки: заполненная или пустая звезда
                                // Если index < rating, звезда заполнена
                                imageVector = if (index < rating) 
                                    Icons.Default.Star 
                                else 
                                    Icons.Default.StarBorder,
                                contentDescription = null,
                                tint = Color(0xFFFFD700),      // Золотой цвет
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Многострочное поле ввода текста отзыва
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    label = { Text("Ваш отзыв") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3                               // Минимум 3 строки
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSubmit(rating, text) },
                // Кнопка активна только при заполненном тексте
                enabled = text.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor)
            ) {
                Text("Отправить")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена", color = TextSecondary)
            }
        }
    )
}
```

**Описание:** Этот компонент реализует пользовательский интерфейс для добавления отзыва. Ключевые особенности: интерактивный выбор рейтинга через нажатие на звёзды (звёзды до выбранной позиции заполняются), валидация формы (кнопка отправки неактивна при пустом тексте), использование remember для сохранения состояния между рекомпозициями. Паттерн callback-функций позволяет родительскому компоненту обрабатывать результат.

---

## Листинг Е. Автоматический пересчёт рейтинга кружка в репозитории

**Файл:** `data/repository/AppRepository.kt`

```kotlin
// Метод добавления нового отзыва с автоматическим обновлением рейтинга
suspend fun insertReview(review: Review): Long {
    // Сохраняем отзыв в базу данных и получаем сгенерированный ID
    val id = reviewDao.insert(review)
    
    // Автоматически пересчитываем рейтинг кружка после добавления отзыва
    updateClubRating(review.clubId)
    
    return id  // Возвращаем ID созданного отзыва
}

// Метод удаления отзыва с пересчётом рейтинга
suspend fun deleteReview(id: Long, clubId: Long) {
    // Удаляем отзыв из базы данных
    reviewDao.deleteById(id)
    
    // Пересчитываем рейтинг после удаления
    // clubId передаётся отдельно, т.к. отзыв уже удалён
    updateClubRating(clubId)
}

// Метод добавления ответа организатора на отзыв
suspend fun addReplyToReview(id: Long, reply: String) = 
    reviewDao.addReply(id, reply)

// Получение среднего рейтинга кружка
// Elvis-оператор (?:) возвращает 0f, если отзывов нет
suspend fun getAverageRating(clubId: Long): Float = 
    reviewDao.getAverageRating(clubId) ?: 0f

// Получение количества отзывов для кружка
suspend fun getReviewCount(clubId: Long): Int = 
    reviewDao.getReviewCount(clubId)

// Приватный метод пересчёта и обновления рейтинга кружка
// Вызывается автоматически при любом изменении отзывов
suspend fun updateClubRating(clubId: Long) {
    // Вычисляем средний рейтинг по всем отзывам кружка
    val avgRating = reviewDao.getAverageRating(clubId) ?: 0f
    
    // Получаем общее количество отзывов
    val reviewCount = reviewDao.getReviewCount(clubId)
    
    // Обновляем данные в таблице кружков
    // Это обеспечивает денормализацию для быстрого доступа к рейтингу
    clubDao.updateRating(clubId, avgRating, reviewCount)
}
```

**Описание:** Данный код демонстрирует паттерн автоматического поддержания консистентности данных. При каждом добавлении или удалении отзыва вызывается метод `updateClubRating()`, который пересчитывает средний рейтинг и количество отзывов. Денормализация (хранение вычисляемых значений в таблице `clubs`) обеспечивает высокую производительность при отображении списков кружков, исключая необходимость агрегирующих запросов. Ключевое слово `suspend` указывает, что методы выполняются асинхронно в корутинах.

---

## Сводная таблица листингов

| Листинг | Название | Файл | Назначение |
|---------|----------|------|------------|
| А | Динамическое меню | DrawerMenu.kt | Ролевая модель доступа |
| Б | Навигация | MainActivity.kt | Single Activity архитектура |
| В | Топ-3 кружков | HomeScreen.kt | Визуализация рейтинга |
| Г | SQL-фильтрация | ClubDao.kt | Комплексный поиск |
| Д | Диалог отзыва | ClubDetailScreen.kt | Интерактивный UI |
| Е | Пересчёт рейтинга | AppRepository.kt | Консистентность данных |

