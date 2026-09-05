# Spring Security JWT

API REST em Spring Boot com autenticação stateless via JWT assinado com par de chaves RSA, usando o Resource Server do Spring Security (OAuth2).

## Tecnologias

- Java 21
- Spring Boot 4.1.1
- Spring Security (OAuth2 Resource Server)
- Spring Data JPA
- H2 (banco em memória)
- Bean Validation
- Maven

## Como funciona

- O usuário se autentica em `POST /login` com Basic Auth (usuário e senha cadastrados no banco).
- Se as credenciais forem válidas, a API gera um JWT assinado com a chave privada RSA.
- Esse token deve ser enviado como `Authorization: Bearer <token>` nas próximas requisições.
- O `SecurityConfig` valida o token com a chave pública RSA e libera o acesso aos endpoints protegidos.
- Senhas são armazenadas com hash BCrypt.

## Endpoints

| Método | Rota          | Autenticação | Descrição                     |
|--------|---------------|--------------|--------------------------------|
| POST   | `/login`      | Basic Auth   | Autentica e retorna um JWT     |
| GET    | `/private`    | Bearer JWT   | Endpoint protegido de teste    |
| POST   | `/users`      | Público      | Cria um usuário                |
| GET    | `/users/{id}` | Bearer JWT   | Busca um usuário por id        |
| PUT    | `/users/{id}` | Bearer JWT   | Atualiza um usuário            |
| DELETE | `/users/{id}` | Bearer JWT   | Remove um usuário              |

## Par de chaves RSA

As chaves usadas para assinar e validar o JWT **não são versionadas** (estão no `.gitignore`). Gere o seu próprio par antes de rodar o projeto:

```bash
mkdir -p src/main/resources
openssl genrsa -out src/main/resources/app.key 2048
openssl rsa -in src/main/resources/app.key -pubout -out src/main/resources/app.pub
```

## Rodando a aplicação

```bash
./mvnw spring-boot:run
```

A aplicação sobe em `http://localhost:8080`, usando um banco H2 em memória (dados recriados a cada execução via `schema.sql`/`data.sql`).

## Testando

```bash
# login (usuário de exemplo criado pelo data.sql)
curl -u Maria:senha -X POST http://localhost:8080/login

# usando o token retornado
curl -H "Authorization: Bearer <token>" http://localhost:8080/private
```

## Estrutura do projeto

```
src/main/java/br/com/guilhermespadaro/
├── controller/       # Endpoints REST (users, private)
├── controller/auth/  # Endpoint de login
├── domain/           # Entidades JPA
├── exception/        # Tratamento global de exceções
├── repository/       # Repositórios Spring Data JPA
├── security/         # Configuração de segurança, geração e validação do JWT
└── service/          # Regras de negócio
```