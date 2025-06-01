package br.com.on.app.validators;

import br.com.caelum.stella.validation.CPFValidator;
import br.com.on.app.exception.MyPersonalException;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.apache.commons.validator.routines.EmailValidator;


@AllArgsConstructor
@RequiredArgsConstructor
public class MyPessoaValidators {

    private final String cpf;
    private final String email;
    private final String telefone;

    public static MyPessoaValidators of(String cpf, String email, String telefone) {
        return new MyPessoaValidators(cpf, email, telefone);
    }

    public MyPessoaValidators isCPFValido() {
        CPFValidator validator = new CPFValidator();
        try {
            validator.assertValid(cpf);
        } catch (Exception e) {
            throw new MyPersonalException("ERRO NA VALIDAÇÃO DO CPF", e);
        }

        return this;
    }

    public MyPessoaValidators isValidEmail() {
        boolean emailIsValid = EmailValidator.getInstance().isValid(email);

        if (!emailIsValid) {
            throw new MyPersonalException("ERRO NA VALIDAÇÃO DO EMAIL");
        }

        return this;
    }

    public void isTelefoneValido() {
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        try {
            Phonenumber.PhoneNumber numberProto = phoneUtil.parse(telefone, "BR");

            if (!phoneUtil.isValidNumber(numberProto)) {
                throw new MyPersonalException("ERRO NA VALIDAÇÃO DO TELEFONE");
            }

        } catch (NumberParseException e) {
            throw new MyPersonalException("ERRO NA VALIDAÇÃO DO TELEFONE", e);
        }
    }
}
