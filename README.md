# Тема: веб-сервис анализа потребительских отзывов с LLM на базе RAG-архитектуры

Предварительно, система не будет нацелена на ручной парсинг (Web Scraping/Crawling) из внешних источников в обход официальных API не входит в рамки данного проекта. Сбор данных ограничен обработкой статических структурированных файлов и официальных экспортных выгрузок.

будет разбиение на 3 микросервиса:

Review Service отвечает за управление жизненным циклом отзывов: приём, хранение, индексацию и предоставление данных по запросу.

Ответственности сервиса:
- Приём пакетов отзывов в формате JSON или текстовых файлов
- Разбиение длинных отзывов на фрагменты
- Генерация эмбеддингов через модель эмбеддингов и сохранение в pgvector
- Предоставление CRUD-операций над отзывами

схема БД для этого микросервиса:
<img width="1049" height="651" alt="Screenshot 2026-07-21 232843" src="https://github.com/user-attachments/assets/b601ac5f-4e99-4f9c-b4e3-b0ff6c051fab" />
эндпоинты: GET	/api/v1/reviews	—	200 List<ReviewFullResponse>
GET	/api/v1/reviews/{id}	—	200 ReviewFullResponse
POST	/api/v1/reviews	CreateReviewRequest{brandId, content, rating, authorName, source, categoryNames[]}	201 ReviewFullResponse → триггерит Kafka review-processing-topic
PUT	/api/v1/reviews/{id}	UpdateReviewRequest{content, rating, categoryNames[]}	200 ReviewFullResponse
DELETE	/api/v1/reviews/{id}	—	204
POST	/api/v1/reviews/{id}/categories	AddCategoryToReviewRequest{reviewId, categoryId}	200 ReviewFullResponse
DELETE	/api/v1/reviews/{id}/categories/{categoryId}	RemoveCategoryFromReviewRequest	200 ReviewFullResponse

Analysis Service реализует бизнес-логику генерации аналитических отчётов. Сервис не хранит отзывы — он обращается к VectorStore для получения релевантного контекста и передаёт его в LLM.

Ответственности сервиса:
- Приём и управление заданиями на генерацию отчётов (tasks)
- Асинхронное выполнение RAG-конвейера
- Хранение статусов и результатов заданий
- Мониторинг расхода токенов

схема БД для этого микросервиса:
<img width="388" height="721" alt="Screenshot 2026-07-21 171110" src="https://github.com/user-attachments/assets/a90506f0-c0b9-4dfb-be90-a11f6eb4dffd" />
эндпоинты: 
GET	/api/v1/reports/brand/{brandId}	—	200 List<AnalysisReportDto> — результаты, пришедшие через analysis-results
POST	/api/v1/reports/{taskId}/acknowledge	—	204
GET	/api/v1/tasks	—	200 List<TaskDto>
GET	/api/v1/tasks/{id}	—	200 TaskDto
POST	/api/v1/tasks	CreateTaskRequest{brandId, brandName, query, topK}	201 TaskDto (вызывает startAnalysis() синхронно — тело запроса то же, что и у Kafka-события)
PUT	/api/v1/tasks/{id}	UpdateTaskRequest{status, result, errorMessage, tokenUsed}	200 TaskDto
DELETE	/api/v1/tasks/{id}	—	204
POST	/api/v1/tasks/start-analysis	CreateTaskRequest{brandId, brandName, query, topK}	201 TaskDto

Auth Service

Настройка Keycloak для системы:
Создаётся отдельный Realm reviews-system с двумя ролями:
ROLE_MANAGER — загрузка отзывов, запрос аналитики, 	просмотр результатов
ROLE_ADMIN — 	дополнительно: удаление данных, сброс 	кэша, просмотр мониторинга токенов
Процесс аутентификации:
- Пользователь отправляет логин и пароль на эндпоинт.
- Клиент 	передаёт токен в заголовке Authorization:	Bearer <token> при каждом запросе.
- Spring 	Security в каждом микросервисе валидирует подпись токена
- Роли 	из токена сопоставляются с требованиями к эндпоинту

схема БД для этого микросервиса:
<img width="1071" height="686" alt="Screenshot 2026-07-21 162039" src="https://github.com/user-attachments/assets/a98f23c0-9b8f-455a-b851-b6f68c683aca" />
эндпоинты:
POST	/api/v1/auth/register	public	RegisterRequest{username, password, email, roles[]}	201 JwtAuthResponse{accessToken, refreshToken}
POST	/api/v1/auth/login	public	LoginRequest{username, email, password}	200 JwtAuthResponse{accessToken, refreshToken}
POST	/api/v1/auth/refresh (добавлено)	public	RefreshTokenRequest{refreshToken}	200 JwtAuthResponse{accessToken, refreshToken}
POST	/api/v1/auth/logout (добавлено)	public	RefreshTokenRequest{refreshToken}	204 No Content
POST	/api/v1/roles	CreateRoleRequest{name, description}	201 RoleDto
PUT	/api/v1/roles/{id}	UpdateRoleRequest{description}	200 RoleDto
DELETE	/api/v1/roles/{id}	—	204



