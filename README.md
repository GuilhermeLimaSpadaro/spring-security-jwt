# Spring Security JWT

API REST desenvolvida com Spring Boot para estudo de autenticação e autorização utilizando Spring Security e JWT.

O projeto utiliza autenticação HTTP Basic no endpoint de login e gera tokens JWT assinados com um par de chaves RSA. As demais requisições protegidas utilizam `Bearer Token`.

## Tecnologias

- Java 21
- Spring Boot 4.1.1
- Spring Security
- OAuth2 Resource Server
- JWT
- RSA
- Spring Data JPA
- H2 Database
- BCrypt
- Maven

## Como funciona

1. O usuário é criado através de `POST /api/v1/users`.
2. A senha é armazenada utilizando BCrypt.
3. O usuário envia suas credenciais no `POST /login` utilizando Basic Auth. O username é o **nome** do usuário (`name`) e a senha é a cadastrada.
4. Após autenticação, a aplicação gera um JWT com validade de 1 hora.
5. O token deve ser enviado nas próximas requisições como:

```text
Authorization: Bearer <token>
```

6. O Spring Security valida o JWT utilizando a chave pública RSA.
7. Os endpoints protegidos exigem autenticação.

## Fluxo de autenticação

```text
Cliente
   │
   │ Basic Auth
   ▼
POST /login
   │
   ▼
Spring Security
   │
   ▼
JWT assinado com RSA
   │
   ▼
Authorization: Bearer <token>
   │
   ▼
Endpoint protegido
   │
   ▼
Validação da chave pública
```

## Endpoints

| Método | Endpoint | Acesso | Descrição |
|---|---|---|---|
| POST | `/login` | Público | Autentica e gera JWT |
| POST | `/api/v1/users` | Público | Cria usuário |
| GET | `/api/v1/users/{id}` | JWT | Busca usuário |
| PUT | `/api/v1/users/{id}` | JWT | Atualiza usuário |
| PUT | `/api/v1/users/{id}/password` | JWT | Atualiza senha |
| DELETE | `/api/v1/users/{id}` | JWT | Remove usuário |
| GET | `/private` | JWT | Endpoint protegido de teste |

O console do H2 (`/h2-console`) também está liberado para uso em desenvolvimento.

## Chaves RSA

O projeto utiliza:

```text
src/main/resources/app.key
src/main/resources/app.pub
```

A chave privada **não deve ser versionada no Git** (`*.key` está no `.gitignore`).

Gere um novo par localmente:

```bash
openssl genrsa -out src/main/resources/app.key 2048
openssl rsa -in src/main/resources/app.key -pubout -out src/main/resources/app.pub
```

> Se uma chave privada deste projeto já tiver sido publicada em um repositório remoto, gere um novo par e considere a chave anterior comprometida.

## Banco de dados

O projeto utiliza H2 em memória:

```text
jdbc:h2:mem:securitydb
```

O schema é criado pelo arquivo:

```text
src/main/resources/schema.sql
```

O Hibernate está configurado com:

```text
spring.jpa.hibernate.ddl-auto=none
```

## Como executar

### Pré-requisitos

- Java 21
- Maven
- OpenSSL para geração das chaves RSA

### Executar

```bash
./mvnw spring-boot:run
```

A aplicação fica disponível em:

```text
http://localhost:8080
```

## Testando

### Criar usuário

```bash
curl -X POST http://localhost:8080/api/v1/users \
  -H "Content-Type: application/json" \
  -d '{"name":"Maria","email":"maria@email.com","phone":"11999999999","password":"senha"}'
```

### Login

O login utiliza Basic Auth (nome do usuário e senha):

```bash
curl -u Maria:senha -X POST http://localhost:8080/login
```

O endpoint retorna o JWT.

### Endpoint protegido

Utilize o token recebido:

```bash
curl -H "Authorization: Bearer <token>" http://localhost:8080/private
```

## Estrutura

```text
src/main/java/br/com/guilhermespadaro
├── controller
│   └── auth
├── domain
├── dto
├── exception
├── repository
├── security
└── service
    └── auth
```

- `controller` — endpoints REST
- `service` — regras de negócio e autenticação
- `security` — configuração do Spring Security e JWT
- `repository` — acesso ao banco
- `domain` — entidade JPA
- `dto` — DTOs de usuário e de atualização de senha (atualmente apenas `PasswordUpdateRequest` é usado pelos endpoints; os controllers de usuário ainda trabalham com a entidade `User`)
- `exception` — tratamento global de erros

## Tratamento de erros

O projeto possui tratamento global (`@RestControllerAdvice`) para:

- Recurso não encontrado (`404`)
- Usuário não encontrado (`401`)
- Senha incorreta, por exemplo na troca de senha (`401`)

As respostas utilizam um formato padronizado de erro. Falhas de autenticação no `POST /login` são respondidas diretamente pelo Spring Security.

## Roadmap

Melhorias e correções planejadas para o projeto:

- Utilizar `UserRequest` e `UserResponse` nos endpoints de usuário, em vez de expor a entidade `User`
- Corrigir a exposição do campo `password` (hash BCrypt) nas respostas de `/api/v1/users`
- Aplicar Bean Validation nos DTOs de entrada

## Autor

**Guilherme Spadaro**