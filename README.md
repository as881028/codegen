與ChatGPT-Canvas討論的結果, 晚點實做看看, 先記錄一下

---
你可以按照以下步驟在 Ubuntu 和 IntelliJ 中建立使用 Spring Boot 3.2 和 JDK 17 的專案，並透過 OpenAPI Codegen 自動生成 API 相關程式碼：

1. **安裝必要軟體**：
   - 在終端機中安裝 JDK 17 和 Maven：
     ```sh
     sudo apt update
     sudo apt install openjdk-17-jdk maven
     ```
   - 確認 Java 和 Maven 安裝成功：
     ```sh
     java -version
     mvn -version
     ```
   - 在 Ubuntu 中安裝 IntelliJ IDEA，可以從 [IntelliJ 官方網站](https://www.jetbrains.com/idea/download/) 下載安裝包，或者使用 Snap 安裝：
     ```sh
     sudo snap install intellij-idea-community --classic
     ```

2. **建立 Spring Boot 專案**：
   - 使用 [Spring Initializr](https://start.spring.io/) 建立一個 Spring Boot 3.2 專案，設定 JDK 版本為 17。選擇 `Maven` 作為構建工具，並加入所需的依賴，例如 `Spring Web`。
   - 下載生成的專案壓縮包，解壓後使用 IntelliJ IDEA 開啟。

3. **配置 `pom.xml`**：
   - 在你的 `pom.xml` 中加入 OpenAPI Codegen 的插件配置和 FF4J 依賴，這樣可以自動生成 API 相關的程式碼，並支持功能開關控制。
     ```xml
     <dependencies>
         <dependency>
             <groupId>org.springframework.boot</groupId>
             <artifactId>spring-boot-starter-web</artifactId>
         </dependency>
         <dependency>
             <groupId>org.ff4j</groupId>
             <artifactId>ff4j-spring-boot-starter</artifactId>
             <version>1.8</version>
         </dependency>
     </dependencies>

     <build>
         <plugins>
             <plugin>
                 <groupId>org.openapitools</groupId>
                 <artifactId>openapi-generator-maven-plugin</artifactId>
                 <version>6.0.1</version>
                 <executions>
                     <execution>
                         <goals>
                             <goal>generate</goal>
                         </goals>
                     </execution>
                 </executions>
                 <configuration>
                     <inputSpec>${project.basedir}/src/main/resources/api.yaml</inputSpec>
                     <generatorName>spring</generatorName>
                     <output>${project.basedir}/generated-sources/openapi</output>
                     <generateApiTests>true</generateApiTests>
                     <generateModelTests>true</generateModelTests>
                     <apiPackage>com.example.generated.api</apiPackage>
                     <modelPackage>com.example.generated.model</modelPackage>
                     <templateDirectory>${project.basedir}/templates</templateDirectory>
                     <configOptions>
                         <useTags>true</useTags>
                     </configOptions>
                 </configuration>
             </plugin>
         </plugins>
     </build>
     ```

4. **準備 OpenAPI 規範並添加功能開關控制**：
   - 在 `src/main/resources` 資料夾中建立 `api.yaml` 或 `api.json`，描述你所需的 API 規範，並使用 Vendor Extensions (`x-feature-toggle`) 來控制功能是否開啟。例如：
     ```yaml
     openapi: 3.0.0
     info:
       title: Sample API
       version: 1.0.0
     paths:
       /tw/books:
         get:
           summary: Get list of Chinese books
           tags:
             - tw
           x-feature-toggle: new-tw-books-feature
           responses:
             '200':
               description: A list of Chinese books
               content:
                 application/json:
                   schema:
                     type: array
                     items:
                       $ref: '#/components/schemas/TwChineseBook'
       /us/books:
         get:
           summary: Get list of English books
           tags:
             - us
           x-feature-toggle: new-us-books-feature
           responses:
             '200':
               description: A list of English books
               content:
                 application/json:
                   schema:
                     type: array
                     items:
                       $ref: '#/components/schemas/UsEnglishBook'
     components:
       schemas:
         TwChineseBook:
           type: object
           properties:
             id:
               type: integer
             title:
               type: string
             author:
               type: string
             language:
               type: string
               example: "Chinese"
         UsEnglishBook:
           type: object
           properties:
             id:
               type: integer
             title:
               type: string
             author:
               type: string
             language:
               type: string
               example: "English"
     ```

5. **修改生成代碼的模板來處理 Feature Toggle**：
   - 自定義 `mustache` 模板來讓代碼生成器讀取 `x-feature-toggle`，並在生成的控制器代碼中加入功能開關邏輯。
   - 修改 `apiController.mustache` 模板文件，讓它可以根據 `x-feature-toggle` 自動生成帶有功能開關檢查的代碼。例如：
     ```mustache
     @RestController
     @RequestMapping("{{{basePath}}}")
     public class {{classname}} {

         {{#operations}}
         {{#operation}}
         @GetMapping("{{{path}}}")
         public ResponseEntity<{{{returnType}}}> {{operationId}}() {
             {{#vendorExtensions.x-feature-toggle}}
             if (ff4j.check("{{vendorExtensions.x-feature-toggle}}")) {
                 // 新功能邏輯
                 return ResponseEntity.ok(service.getNew{{operationId}}());
             } else {
                 // 舊功能邏輯
                 return ResponseEntity.ok(service.get{{operationId}}());
             }
             {{/vendorExtensions.x-feature-toggle}}
         }
         {{/operation}}
         {{/operations}}
     }
     ```
   - 在專案根目錄下創建 `templates` 資料夾，並將自定義的模板文件放入其中。

6. **執行生成程式碼**：
   - 在終端機中進入專案目錄，使用以下命令執行代碼生成：
     ```sh
     mvn openapi-generator:generate
     ```
   - 生成的代碼會放在指定的 `output` 目錄下（例如 `generated-sources/openapi`），你可以將這些程式碼集成到 Spring Boot 專案中。

7. **專案結構建議**：
   - 專案的理想結構應該清晰區分生成的代碼與手動編寫的代碼，避免混淆並提高維護性。
   - 建議的專案結構如下：
     ```
     project-root/
     ├── src/
     │   ├── main/
     │   │   ├── java/
     │   │   │   └── com/
     │   │   │       └── example/
     │   │   │           ├── controller/         # 手動實現的控制器
     │   │   │           ├── service/            # 業務邏輯層
     │   │   │           ├── config/             # 配置類（例如 FF4J 配置）
     │   │   └── resources/
     │   │       ├── api.yaml                    # OpenAPI 規範文件
     │   │       └── application.properties      # Spring Boot 配置文件
     │   └── test/
     │       └── java/
     │           └── com/
     │               └── example/
     │                   ├── controller/         # 控制器的單元測試
     │                   ├── service/            # 業務邏輯層的單元測試
     │                   └── generated/          # 生成模型的單元測試
     ├── generated-sources/                      # OpenAPI 生成的代碼輸出目錄
     │   └── openapi/
     │       ├── com/example/generated/api/      # 生成的 API 接口
     │       └── com/example/generated/model/    # 生成的模型
     ├── templates/                              # 自定義的模板文件
     │   ├── apiController.mustache
     │   └── modelTest.mustache
     ├── pom.xml                                 # Maven 配置文件
     └── README.md                               # 專案描述文件
     ```
   - 在 `generated-sources/openapi` 中保留生成的代碼，不要手動修改，避免重新生成時被覆蓋。手動編寫的邏輯則應保留在 `main/java/com/example` 下的相應包中，確保生成代碼和手動編寫代碼的清晰區分。

8. **整合生成的程式碼**：
   - 確保生成的程式碼已加入你的專案的 `source` 路徑，例如在 Maven 中加入 `generated-sources/openapi` 資料夾作為額外的來源路徑。
     - 在 IntelliJ 中，右鍵點擊生成的代碼目錄，選擇 `Mark Directory as -> Generated Sources Root`。
   - 在 Spring Boot 的應用中，生成的控制器、模型、以及 API 界面可以被自動掃描並註冊，這樣就可以處理對應的 HTTP 請求。
   - 可以通過 Spring Boot 的 `@ComponentScan` 或在配置文件中顯式指定生成的包，使得生成的代碼能夠與手動編寫的代碼一起正常工作。

9. **透過模板自動生成基礎的單元測試**：
   - 你可以自定義 `mustache` 模板來生成每個模型的基礎單元測試，確保每個模型的基本功能被正確測試。
   - 以下是一個簡單的自定義 `modelTest.mustache` 模板範例，來為每個模型生成測試代碼：
     ```mustache
     package {{modelPackage}};

     import org.junit.jupiter.api.Test;
     import static org.junit.jupiter.api.Assertions.*;

     public class {{classname}}Test {
         @Test
         void test{{classname}}Model() {
             {{classname}} model = new {{classname}}();
             {{#vars}}
             model.set{{{baseName}}}({{#isPrimitiveType}}{{datatype}}Value{{/isPrimitiveType}}{{^isPrimitiveType}}new {{datatype}}(){{/isPrimitiveType}});
             assertNotNull(model.get{{{baseName}}}());
             {{/vars}}
         }
     }
     ```
   - 上述模板將自動為每個模型生成一個對應的測試類，並測試每個屬性的 getter 和 setter 方法。

10. **回歸測試的重要性**：
   - 增加回歸測試，確保新功能的引入不會影響現有功能的穩定性。
   - 自動化回歸測試，並將其集成到 CI/CD 流程中，確保每次代碼變更後自動驗證整體系統的正確性。
   - 加強生成測試代碼的覆蓋範圍，確保測試涵蓋 API 之間的交互和整合邏輯。
   - 為主要 API 增加回歸測試場景，尤其是功能開關開啟和關閉的組合情況。
   - 使用測試數據庫進行測試，確保測試過程不影響生產環境中的數據。

