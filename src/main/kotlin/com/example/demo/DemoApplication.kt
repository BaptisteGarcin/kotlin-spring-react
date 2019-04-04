package com.example.demo

import com.altima.api.bootable.FullStackConfiguration
import com.altima.api.sugar.SugarConfiguration
import com.altima.api.tools.config.ConfigurationsBuilder
import com.altima.api.tools.config.IConfigurations
import com.altima.api.tools.config.repository.ConfigDatabase
import com.altima.api.tools.config.repository.ConfigServiceConfiguration
import com.altima.api.tools.config.repository.IConfigurationService
import com.altima.api.tools.context.ApplicationContext
import com.altima.api.tools.jdbc.Database
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.context.annotation.Primary
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import javax.sql.DataSource


@SpringBootApplication
@Import(value = [
    SugarConfiguration::class,
    FullStackConfiguration::class,
    ConfigServiceConfiguration::class
])

class DemoApplication {
    @Bean
    fun appContext(): ApplicationContext {
        return ApplicationContext("demo")
    }

    @Bean
    fun complexConfig(app: ApplicationContext, service: IConfigurationService): IConfigurations {
        return ConfigurationsBuilder.forApplication(app.applicationName)
                .usingConfigurationService(service)
                .build()
    }

    @Bean
    @Primary
    @ConfigurationProperties("api.datasource.altima-api")
    fun altimaApiDataSourceProperties(): DataSourceProperties {
        return DataSourceProperties()
    }

    @Bean(name = ["altimaApiDatasource"])
    @Primary
    @ConfigurationProperties("api.datasource.altima-api")
    fun altimaApiDataSource(): DataSource {
        return altimaApiDataSourceProperties().initializeDataSourceBuilder().build()
    }

    @Bean
    fun jdbcDispatcher(): ExecutorService {
        return Executors.newFixedThreadPool(20)
    }


    @Bean(name = ["altima-api"])
    @Autowired
    @ConfigDatabase
    fun altimaApiDatabase(jdbcDispatcher: ExecutorService): Database {
        return Database(altimaApiDataSource(), jdbcDispatcher)
    }
}

fun main(args: Array<String>) {
	runApplication<DemoApplication>(*args)

}

