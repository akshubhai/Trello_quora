package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.QuestionEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class QuestionDao {
    @PersistenceContext
    private EntityManager entityManager;

    public QuestionEntity createQuestion(QuestionEntity questionEntity) {
        entityManager.persist(questionEntity);
        return questionEntity;
    }

    public List<QuestionEntity> getAllQuestions(){
        try {
            return entityManager.createNamedQuery("questionAll", QuestionEntity.class).getResultList();
        }
        catch (NoResultException nre){
            return null;
        }
    }

    public QuestionEntity getQuestionbyId(String questionId){
        try {
            return entityManager.createNamedQuery("questionByUuid", QuestionEntity.class).setParameter("uuid", questionId).getSingleResult();
        }
        catch (NoResultException nre){
            return null;
        }
    }

    public void editQuestion(final QuestionEntity questionEntity) {
        entityManager.merge(questionEntity);
    }

    public QuestionEntity removeQuestion(QuestionEntity questionEntity) {
        entityManager.remove(questionEntity);
        return questionEntity;
    }
}
