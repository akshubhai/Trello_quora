package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.QuestionRequest;
import com.upgrad.quora.api.model.QuestionResponse;
import com.upgrad.quora.service.business.QuestionBusinessService;
import com.upgrad.quora.service.common.ConstantValues;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/")
public class QuestionController {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private QuestionBusinessService questionBusinessService;

    @RequestMapping(method = RequestMethod.POST, path= "/question/create", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionResponse> signup(@RequestHeader("authorization") final String authorization, final QuestionRequest questionRequest) throws AuthenticationFailedException, AuthorizationFailedException {

        UserEntity userEntity = questionBusinessService.userAuthenticate(authorization);

        //Map question request to entity
        QuestionEntity questionEntity = modelMapper.map(questionRequest, QuestionEntity.class);
        questionEntity.setUuid(UUID.randomUUID().toString());
        questionEntity.setUser(userEntity);

        //Call service to create new question
        QuestionEntity createdQuestionEntity = questionBusinessService.createQuestion(questionEntity);

        //Create required response
        QuestionResponse questionResponse = new QuestionResponse().id(createdQuestionEntity.getUuid()).status(ConstantValues.QUESTION_CREATED);

        return new ResponseEntity<QuestionResponse>(questionResponse, HttpStatus.CREATED);

    }

}
