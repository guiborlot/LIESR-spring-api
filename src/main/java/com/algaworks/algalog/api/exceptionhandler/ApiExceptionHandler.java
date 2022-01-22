package com.algaworks.algalog.api.exceptionhandler;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

    private MessageSource messageSource;

    public ApiExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {

        List<Problema.Campo> campos = new ArrayList<>();

        ex.getBindingResult().getAllErrors().stream().forEach(erro -> {
            campos.add(new Problema.Campo(((FieldError)erro).getField(), messageSource.getMessage(erro, LocaleContextHolder.getLocale())));
        });

//        for (ObjectError error: ex.getBindingResult().getAllErrors()){
//            String nome = ((FieldError)error).getField();
//            String mensagem = error.getDefaultMessage();
//
//            campos.add(new Problema.Campo(nome, mensagem));
//        }

        Problema problema = new Problema();
        problema.setStatus(status.value());
        problema.setDataHora(LocalDateTime.now());
        problema.setTitulo("Um ou mais campos estão inválidos. Faça o prenchimento correto e tente novamente");
        problema.setCampos(campos);

        return this.handleExceptionInternal(ex, problema ,headers, status, request);
    }

}
