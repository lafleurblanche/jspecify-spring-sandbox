package net.konohana.sakuya.sandbox.configs

import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

/**
 * Web API 向け Cross-Origin Resource Sharing (CORS) 設定クラス。
 */
@Configuration
class CorsWebConfig : WebMvcConfigurer {

    /**
     * API エンドポイントに対する CORS マッピングを定義します。
     *
     * @param registry CORS 設定レジストリ
     */
    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/api/**")
            .allowedOriginPatterns(
                // TODO: 仮実装
                "http://localhost:*",
            )
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
            // TODO: 仮実装
            .allowedHeaders("*")
            .allowCredentials(true)
            .maxAge(3600)
    }
}
