package com.tomdud.githubservice.configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;


@OpenAPIDefinition(
        info = @Info(
                contact = @Contact(
                        name = "tomdud-developer",
                        email = "tomasz.dudzik.it@gmail.com"
                ),
                description = "Documentation for githubservice",
                title = "githubservice",
                version = "V1"
        )
)
public class OpenApiConfiguration {  }
