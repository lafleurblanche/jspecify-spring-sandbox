package net.konohana.sakuya.sandbox.configs

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

/**
 * 非同期 HTTP 通信用 [WebClient] の Bean 構成クラス。
 */
@Configuration
class WebClientConfig {

    /**
     * アプリケーション全体で共有可能な既定の [WebClient] インスタンスを提供します。
     *
     * @return 構成済み [WebClient]
     */
    @Bean
    fun webClient(): WebClient = WebClient.builder().build()
}
