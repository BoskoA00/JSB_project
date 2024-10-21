package com.example.demo.Services;

import com.example.demo.DTOS.updateQuestionDTO;
import com.example.demo.Exceptions.QuestionNotFoundException;
import com.example.demo.Exceptions.UserNotFoundException;
import com.example.demo.Models.Question;
import com.example.demo.Models.User;
import com.example.demo.Repositories.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final UserService userService;
    @Autowired
    public QuestionService( QuestionRepository questionRepository, UserService userService){
        this.questionRepository = questionRepository;
        this.userService = userService;
    }

    public List<Question> getQuestions(){
        return questionRepository.findAll();
    }

    public List<Question> getQuestionsByUserId(Long userId){
        return  questionRepository.getQuestionsByUserId(userId);
    }
    public Question getQuestionById(Long questionId){
        return  questionRepository.findById(questionId).orElseThrow(()-> new QuestionNotFoundException("Question with this id doesn't exist."));
    }

    public Question createQuestion(Question question) {
        if (question.getTitle() == null || question.getTitle().isEmpty() ||
                question.getContent() == null || question.getContent().isEmpty()) {
            throw new IllegalArgumentException("Question title and content must not be empty.");
        }
        return questionRepository.save(question);
    }

    public void deleteQuestion(Question question){
        questionRepository.delete(question);
    }
    public Question updateQuestion(Long questionId,updateQuestionDTO question) {

        Question questionToUpdate = questionRepository.findById(questionId)
                .orElseThrow(() -> new QuestionNotFoundException("Question with this id doesn't exist."));

        if (question.getTitle() != null && !question.getTitle().isEmpty()) {
            questionToUpdate.setTitle(question.getTitle());
        }

        if (question.getContent() != null && !question.getContent().isEmpty()) {
            questionToUpdate.setContent(question.getContent());
        }

        return questionRepository.save(questionToUpdate);
    }

    public void deleteByUser(Long userId){
        User user = userService.GetUserById(userId).orElseThrow(() -> new UserNotFoundException("User doesn't exist"));
        questionRepository.deleteByUser(userId);
    }

}
