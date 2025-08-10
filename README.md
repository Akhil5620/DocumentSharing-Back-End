# Dcument Sharing System - Application Flow & Configuration

##  **Application Startup Process**
## mvn spring-boot:run

###  **Entry Point: DocumentSharingApplication.java**
```java
@SpringBootApplication
public class DocumentSharingApplication {
    public static void main(String[] args) {
        SpringApplication.run(DocumentSharingApplication.class, args);
    }
}
```

**What happens:**
- Spring Boot scans for `@Configuration`, `@Service`, `@Repository`, `@Controller` classes
- Loads `application.yml` configuration
- Starts embedded Tomcat server
- Initializes all beans and dependencies

---

##  **Configuration Flow: application.yml → Components**

### **Configuration Structure**

```
application.yml
├── Server Configuration (Port, Context Path)
├── MongoDB Configuration (Database Connection)
├── Security Configuration (User, Password, Roles)
├── JWT Configuration (Secret, Expiration)
├── Azure Blob Storage (Connection, Container)
├── File Upload (Size, Types, Temp Dir)
├── Logging Configuration
└── Swagger/OpenAPI Configuration
```

---

##  *Configuration Injection Process**

###  *MongoDB Configuration Flow**

```yaml
# application.yml
spring:
  data:
    mongodb:
      uri: mongodb://localhost:27017/document_sharing_db
      database: document_sharing_db
```

**How it flows:**
1. **Spring Boot** reads MongoDB configuration from `application.yml`
2. **Auto-configuration** creates `MongoTemplate` and `MongoClient`
3. **Repository Interfaces** (`UserRepository`, `DocumentRepository`) are automatically implemented
4. **Service Classes** inject repositories and use them

```java
// UserRepository.java - Automatically connected to MongoDB
@Repository
public interface UserRepository extends MongoRepository<User, String> {
    // Spring Data MongoDB automatically implements these methods
}

// UserService.java - Uses the repository
@Service
public class UserService {
    @Autowired
    private UserRepository userRepository; // Injected by Spring
}
```

###  **Security Configuration Flow**

```yaml
# application.yml
spring:
  security:
    user:
      name: admin
      password: admin123
      roles: ADMIN
```

**How it flows:**
1. **Spring Security** reads user configuration
2. **SecurityConfig.java** configures security rules
3. **PasswordEncoder** bean is created for password hashing
4. **Controllers** use security context for authentication

```java
// SecurityConfig.java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Used by UserService
    }
}

// UserService.java
@Service
public class UserService {
    @Autowired
    private PasswordEncoder passwordEncoder; // Injected by Spring
    
    public UserDto.UserResponse createUser(UserDto.CreateUserRequest request) {
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        // Password is hashed before saving
    }
}
```

###  **JWT Configuration Flow**

```yaml
# application.yml
jwt:
  secret: your-super-secret-jwt-key-for-document-sharing-system-2024
  expiration: 86400000 # 24 hours
```

**How it flows:**
1. **@Value** annotation injects JWT configuration
2. **JWT Token Provider** uses these values
3. **Controllers** use JWT for authentication

```java
// JWT Token Provider (to be implemented)
@Component
public class JwtTokenProvider {
    @Value("${jwt.secret}")
    private String jwtSecret;
    
    @Value("${jwt.expiration}")
    private long jwtExpiration;
    
    public String generateToken(String username) {
        // Uses jwtSecret and jwtExpiration from application.yml
    }
}
```

###  **Azure Blob Storage Configuration Flow**

```yaml
# application.yml
azure:
  storage:
    connection-string: DefaultEndpointsProtocol=https;AccountName=yourstorageaccount;AccountKey=yourstoragekey;EndpointSuffix=core.windows.net
    container-name: documents
    account-name: yourstorageaccount
    account-key: yourstoragekey
```

**How it flows:**
1. **@Value** annotations inject Azure configuration
2. **AzureBlobStorageService** uses these values
3. **DocumentService** uses Azure service for file operations

```java
// AzureBlobStorageService.java
@Service
public class AzureBlobStorageService {
    @Value("${azure.storage.connection-string}")
    private String connectionString;
    
    @Value("${azure.storage.container-name}")
    private String containerName;
    
    public void init() {
        this.blobServiceClient = new BlobServiceClientBuilder()
                .connectionString(connectionString) // From application.yml
                .buildClient();
    }
}
```

###  **File Upload Configuration Flow**

```yaml
# application.yml
file:
  upload:
    max-size: 26214400 # 25MB
    allowed-types: pdf,doc,docx,txt,jpg,jpeg,png,gif
    temp-dir: /tmp/uploads
```

**How it flows:**
1. **@Value** annotations inject file upload settings
2. **DocumentService** validates file uploads
3. **Controllers** use these limits

```java
// DocumentService.java
@Service
public class DocumentService {
    @Value("${file.upload.max-size}")
    private long maxFileSize;
    
    @Value("${file.upload.allowed-types}")
    private String allowedTypes;
    
    public DocumentDto.DocumentResponse uploadDocument(MultipartFile file) {
        // Validates file size and type using values from application.yml
        if (file.getSize() > maxFileSize) {
            throw new RuntimeException("File too large");
        }
    }
}
```

---

##  **Component Architecture & Data Flow**

### **Complete Application Architecture**

```
┌─────────────────────────────────────────────────────────────┐
│                    application.yml                         │
│  (All Configuration Values)                               │
└─────────────────────┬───────────────────────────────────────┘
                      │
                      ▼
┌─────────────────────────────────────────────────────────────┐
│                Spring Boot Application                     │
│  DocumentSharingApplication.java                          │
└─────────────────────┬───────────────────────────────────────┘
                      │
                      ▼
┌─────────────────────────────────────────────────────────────┐
│                    Configuration Layer                     │
│  ├── SecurityConfig.java (Security Rules)                │
│  ├── OpenApiConfig.java (API Documentation)              │
│  └── MongoDB Auto-Configuration                          │
└─────────────────────┬───────────────────────────────────────┘
                      │
                      ▼
┌─────────────────────────────────────────────────────────────┐
│                     Repository Layer                       │
│  ├── UserRepository.java (MongoDB User Operations)       │
│  └── DocumentRepository.java (MongoDB Document Operations)│
└─────────────────────┬───────────────────────────────────────┘
                      │
                      ▼
┌─────────────────────────────────────────────────────────────┐
│                     Service Layer                          │
│  ├── UserService.java (User Business Logic)              │
│  ├── DocumentService.java (Document Business Logic)       │
│  └── AzureBlobStorageService.java (File Storage)         │
└─────────────────────┬───────────────────────────────────────┘
                      │
                      ▼
┌─────────────────────────────────────────────────────────────┐
│                    Controller Layer                        │
│  ├── AuthController.java (Authentication APIs)            │
│  └── DocumentController.java (Document Management APIs)   │
└─────────────────────┬───────────────────────────────────────┘
                      │
                      ▼
┌─────────────────────────────────────────────────────────────┐
│                    External Systems                        │
│  ├── MongoDB (Database)                                   │
│  ├── Azure Blob Storage (File Storage)                   │
│  └── Client Applications (Frontend)                       │
└─────────────────────────────────────────────────────────────┘
```

---

##  **Request Flow Example**

###  **Document Upload Request Flow**

```
1. Client Request
   POST /api/documents/upload
   └── MultipartFile + Metadata

2. DocumentController
   @PostMapping("/upload")
   └── Calls DocumentService.uploadDocument()

3. DocumentService
   ├── Validates file size (from application.yml)
   ├── Validates file type (from application.yml)
   ├── Calls AzureBlobStorageService.uploadDocument()
   └── Saves metadata to MongoDB

4. AzureBlobStorageService
   ├── Uses connection-string (from application.yml)
   ├── Uses container-name (from application.yml)
   └── Uploads file to Azure Blob Storage

5. UserService
   ├── Uses PasswordEncoder (from SecurityConfig)
   └── Saves user data to MongoDB

6. Response
   └── Document metadata with shareable link
```

---

## 🔧 **Configuration Injection Methods**

### **@Value Annotation**
```java
@Value("${jwt.secret}")
private String jwtSecret;

@Value("${azure.storage.connection-string}")
private String connectionString;
```

###  **@ConfigurationProperties**
```java
@ConfigurationProperties(prefix = "azure.storage")
public class AzureStorageProperties {
    private String connectionString;
    private String containerName;
    // getters and setters
}
```

###  **Environment Object**
```java
@Autowired
private Environment environment;

String dbUrl = environment.getProperty("spring.data.mongodb.uri");
```

###  **Auto-Configuration**
```java
// Spring Boot automatically configures:
// - MongoTemplate from spring.data.mongodb.*
// - Security from spring.security.*
// - Server from server.*
```

---

## **Key Configuration Points**

###  **MongoDB Connection**
- **Config Source:** `spring.data.mongodb.uri`
- **Used By:** `UserRepository`, `DocumentRepository`
- **Auto-configured:** `MongoTemplate`, `MongoClient`

###  **Security Configuration**
- **Config Source:** `spring.security.user.*`
- **Used By:** `SecurityConfig`, `UserService`
- **Auto-configured:** `PasswordEncoder`, Security rules

### **Azure Storage**
- **Config Source:** `azure.storage.*`
- **Used By:** `AzureBlobStorageService`
- **Manual Injection:** `@Value` annotations

### **File Upload Limits**
- **Config Source:** `file.upload.*`
- **Used By:** `DocumentService`
- **Manual Injection:** `@Value` annotations

### **JWT Settings**
- **Config Source:** `jwt.*`
- **Used By:** JWT Token Provider (to be implemented)
- **Manual Injection:** `@Value` annotations

---

##  **Application Startup Sequence**

```
1. DocumentSharingApplication.main() called
2. SpringApplication.run() starts
3. Spring Boot auto-configuration:
   ├── Reads application.yml
   ├── Creates MongoDB connection
   ├── Sets up Security configuration
   ├── Initializes all @Service beans
   └── Starts embedded Tomcat server
4. Application ready on http://localhost:8080/api
5. Swagger UI available at http://localhost:8080/api/swagger-ui.html
```

---

##  **Summary**

The `application.yml` file is the **central configuration hub** that:

 **Provides all configuration values** to the entire application  
 **Auto-configures** MongoDB, Security, and Server settings  
 **Injects values** into services via `@Value` annotations  
 **Enables environment-specific** deployment (dev, staging, prod)  
 **Centralizes** all configuration in one place  

**Everything flows from `application.yml` → Spring Boot → Components → External Systems!** 
