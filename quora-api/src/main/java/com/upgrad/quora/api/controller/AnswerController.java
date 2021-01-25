package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.AnswerEditRequest;
import com.upgrad.quora.api.model.AnswerEditResponse;
import com.upgrad.quora.api.model.AnswerRequest;
import com.upgrad.quora.api.model.AnswerResponse;
import com.upgrad.quora.service.business.AnswerBusinessService;
import com.upgrad.quora.service.business.QuestionBusinessService;
import com.upgrad.quora.service.common.ConstantValues;
import com.upgrad.quora.service.common.GenericErrorCode;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.UUID;

@RestController
public class AnswerController {

    @Autowired
    private AnswerBusinessService answerBusinessService;

    @Autowired
    private QuestionBusinessService questionBusinessService;

    @Autowired
    private ModelMapper modelMapper;

    @RequestMapping(method = RequestMethod.POST, path= "/question/{questionId}/answer/create", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerResponse> createAnswer (@RequestHeader("authorization") final String authorization, @PathVariable("questionId") final String questionId, final AnswerRequest answerRequest) throws AuthorizationFailedException, InvalidQuestionException {

        GenericErrorCode err1 = GenericErrorCode.ATHR_001_CREATEANSWER;
        GenericErrorCode err2 = GenericErrorCode.ATHR_002_CREATEANSWER;
        GenericErrorCode err3 = GenericErrorCode.QUES_001_CREATEANSWER;


        QuestionEntity questionEntity = questionBusinessService.checkInvalidQuestion(questionId, err3);
        UserEntity userEntity =  questionBusinessService.userAuthenticateSignin(authorization, err1, err2);

        //Map request to entity
        //Map question request to entity
        //AnswerEntity answerEntity = modelMapper.map(answerRequest, AnswerEntity.class);
        AnswerEntity answerEntity = new AnswerEntity();
        answerEntity.setUuid(UUID.randomUUID().toString());
        answerEntity.setUser(userEntity);
        answerEntity.setQuestion(questionEntity);
        answerEntity.setDate(ZonedDateTime.now());
        answerEntity.setAns(answerRequest.getAnswer());


        //Create new answer
        AnswerEntity createdAnswerEntity = answerBusinessService.createAnswer(answerEntity);

        AnswerResponse answerResponse = new AnswerResponse().id(createdAnswerEntity.getUuid()).status(ConstantValues.ANSWER_CREATED);

        return new ResponseEntity<AnswerResponse>(answerResponse, HttpStatus.CREATED);


    }

    @RequestMapping(method = RequestMethod.PUT , path= "/answer/edit/{answerId}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerEditResponse> editAnswerContent (@RequestHeader("authorization") final String authorization, @PathVariable("answerId") final String answerId, final AnswerEditRequest answerEditRequest) throws AuthorizationFailedException, InvalidQuestionException, AnswerNotFoundException {

        GenericErrorCode err1 = GenericErrorCode.ATHR_001_EDITANSWER;
        GenericErrorCode err2 = GenericErrorCode.ATHR_002_EDITANSWER;
        GenericErrorCode err3 = GenericErrorCode.ATHR_003_EDITANSWER;
        GenericErrorCode err4 = GenericErrorCode.ANS_001_EDITANSWER;


        UserEntity userEntity =  questionBusinessService.userAuthenticateSignin(authorization, err1, err2);
        UserEntity userEntity1 = answerBusinessService.answerEditOwner(authorization, answerId, err3, err4);

        //Call service to get answer
        AnswerEntity answerEntity = answerBusinessService.getAnswerbyId(answerId);

        answerEntity.setAns(answerEditRequest.getContent());

        //Call service to update db
        answerBusinessService.editAnswer(answerEntity);

        //Return required response model
        AnswerEditResponse answerEditResponse = new AnswerEditResponse().id(answerEntity.getUuid()).status(ConstantValues.ANSWER_EDITED);

        return new ResponseEntity<AnswerEditResponse>(answerEditResponse, HttpStatus.OK);

    }


}
