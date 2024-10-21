package com.example.demo.Services;

import com.example.demo.Exceptions.AnswerNotFound;
import com.example.demo.Models.Answer;
import com.example.demo.Repositories.AnswerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AnswerService {
    private final AnswerRepository answerRepository;
    @Autowired
    public AnswerService(AnswerRepository answerRepository){
        this.answerRepository = answerRepository;
    }
    public List<Answer> getAll(){
        return answerRepository.findAll();
    }
    public List<Answer> getByUser(Long userId){
        return answerRepository.getAnswersByUserId(userId);
    }
    public List<Answer> getByQuestion(Long questionId){
        return answerRepository.getAnswersByQuestionId(questionId);
    }
    public Answer getAnswerById(Long answerId){
        return answerRepository.findById(answerId).orElseThrow(()->new AnswerNotFound("Answer doesn't exist"));
    }
    public void deleteAnswer(Answer answer){
        answerRepository.delete(answer);
    }
    public Answer createAnswer(Answer answer){
        if(answer.getContent() == null || answer.getContent().isEmpty()){
            throw new IllegalArgumentException("Content must exist");
        }
        return answerRepository.save(answer);
    }
    public Answer updateAnswer(Long id,String newContent){
        Answer answer = answerRepository.findById(id).orElseThrow(()-> new AnswerNotFound("Answer doesn't exist."));
        if(newContent == null || newContent.isEmpty()){
            throw new IllegalArgumentException("Content must be given.");
        }
        answer.setContent(newContent);
        return answerRepository.save(answer);
    }
    public void deleteByUser(Long userId){
        answerRepository.deleteByUser(userId);
    }
    public void deleteByQuestion(Long questionId){
        answerRepository.deleteByQuestion(questionId);
    }

}
