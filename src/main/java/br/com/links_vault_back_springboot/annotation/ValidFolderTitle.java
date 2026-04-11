package br.com.links_vault_back_springboot.annotation;

import br.com.links_vault_back_springboot.validator.FolderTitleValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = FolderTitleValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidFolderTitle {

    String message() default "Nome de pasta inválida.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
