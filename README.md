## informed-student-back-end

### How to run

#### Server

Для запуска сервера необходимо указать реквизиты для доступа к Postgres и MinIO, настройки SpringSecurity:
```
spring.datasource.username={Postgres username}
spring.datasource.password={Postgres password}

minio.datasource.username={MinIO username}
minio.datasource.password={MinIO password}

app.security.jwt.secret-key={Security key}
app.security.jwt.expiration-time={Key expiration time}

admin.username={Admin username}
admin.password={Admin password}
```
Security key можно сгенерировать на сайте: [seanwasere.com/generate-random-hex/](https://seanwasere.com/generate-random-hex/).
Key expiration time - в миллисекундах, можно использовать значение 3600000 (6 часов).
Admin username/password - логин и пароль администратора.

#### Запуск MinIO в Docker

Команда локально запустит контейнер MinIO из репозитория Docker:
```
docker run -p 9000:9000 -p 9001:9001 quay.io/minio/minio server /data --console-address ":9001"
```
Реквизиты по умолчанию:
```
minioadmin:minioadmin
```

#### Запуск Postgres в Docker

Команда запустит Postgres в контейнере
```
docker run --name {Container name} 
    -e POSTGRES_USER={Postgres username}
    -e POSTGRES_PASSWORD={Postgres password}
    -e POSTGRES_DB=postgres
    -d postgres
```

#### Tests

Тестирование происходит с использованием тестовых контейнеров, требуется только запущенный Docker

### Documentation

Документация API доступна в [Swagger](http://localhost:8080/swagger-ui/index.html). Для получения доступа ко всем 
end-point необходима авторизация через администратора.
