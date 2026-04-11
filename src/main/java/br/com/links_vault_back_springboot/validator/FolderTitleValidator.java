package br.com.links_vault_back_springboot.validator;

import br.com.links_vault_back_springboot.annotation.ValidFolderTitle;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class FolderTitleValidator implements ConstraintValidator<ValidFolderTitle, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {

        constraintValidatorContext.disableDefaultConstraintViolation();

        if (value == null || value.isEmpty()) {
            constraintValidatorContext
                    .buildConstraintViolationWithTemplate("Informe o nome da pasta.")
                    .addConstraintViolation();
            return false;
        }

        if (value.length() < 3 || value.length() > 80) {
            constraintValidatorContext
                    .buildConstraintViolationWithTemplate("A pasta não deve ultrapassar 80 caracteres.")
                    .addConstraintViolation();
            return false;
        }

        return true;
    }
}
