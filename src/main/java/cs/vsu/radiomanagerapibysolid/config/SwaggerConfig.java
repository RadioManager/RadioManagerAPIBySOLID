package cs.vsu.radiomanagerapibysolid.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                contact = @Contact(
                        name = "daniilShestopalov",
                        email = "danya.shestopalov@gmail.com",
                        url = "https://github.com/daniilShestopalov"
                ),
                description = "Swagger documentation for RadioManagerAPI. "
                        + "To authorize, call POST /auth: the JWT is stored in the httpOnly cookie \"jwt\" "
                        + "and is sent automatically with all subsequent requests from this page. "
                        + "POST /auth/logout clears it.",
                title = "Swagger RadioManagerAPI",
                version = "2.0"
        )
)
public class SwaggerConfig {
}
