package com.upgrad.quora.service.business;

import com.upgrad.quora.service.common.GenericErrorCode;
import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;

@Service
public class QuestionBusinessService {
    @Autowired
    private UserDao userDao;
    
    @Autowired
    private QuestionDao questionDao;

    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity createQuestion(QuestionEntity questionEntity)
    {
        return questionDao.createQuestion(questionEntity);
    }


    public UserEntity userAuthenticateSignin(String authorization, GenericErrorCode err1, GenericErrorCode err2) throws AuthorizationFailedException {

        UserAuthTokenEntity userAuthTokenEntity = userDao.fetchAuthToken(authorization);

        if (userAuthTokenEntity == null) {
            throw new AuthorizationFailedException(err1.getCode(), err1.getDefaultMessage());
        }

        if (userAuthTokenEntity.getLogoutAt() != null || userAuthTokenEntity.getExpiresAt().isBefore(ZonedDateTime.now())) {
            throw new AuthorizationFailedException(err2.getCode(), err2.getDefaultMessage());
        }

        UserEntity userEntity = userAuthTokenEntity.getUser();

        return userEntity;
    }

    public List<QuestionEntity> getAllQuestions() {
        return questionDao.getAllQuestions();
    }


//    public UserEntity userAuthenticateQuestion(QuestionEntity questionEntity, GenericErrorCode err1, GenericErrorCode err2) throws AuthorizationFailedException {
//
//        QuestionEntity getQuestionEntity = questionDao.
//
//        if (getUserEntity == null) {
//            throw new AuthorizationFailedException(err1.getCode(), err1.getDefaultMessage());
//        }
//
//        if (getUserEntity.getLogoutAt() != null || userAuthTokenEntity.getExpiresAt().isBefore(ZonedDateTime.now())) {
//            throw new AuthorizationFailedException(err2.getCode(), err2.getDefaultMessage());
//        }
//
//        UserEntity userEntity = userAuthTokenEntity.getUser();
//
//        return userEntity;
//    }

}
