package com.example.demo.Controllers;

import com.example.demo.DTOS.*;
import com.example.demo.Exceptions.DatabaseErrorException;
import com.example.demo.Exceptions.QuestionNotFoundException;
import com.example.demo.Exceptions.ResponseMessage;
import com.example.demo.Exceptions.UserNotFoundException;
import com.example.demo.Models.Question;
import com.example.demo.Models.User;
import com.example.demo.Services.QuestionService;
import com.example.demo.Services.UserService;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "api/questions")
public class QuestionController {
    private  final QuestionService questionService;
    private  final UserService userService;
    @Autowired
    public QuestionController( QuestionService questionService, UserService userService){
        this.questionService = questionService;
        this.userService = userService;
    }
    private String[] getIdAndRole(String token) {
        String role = Jwts.parser()
                .setSigningKey("**")
                .parseClaimsJws(token)
                .getBody()
                .get("role", String.class);

        String userId = Jwts.parser()
                .setSigningKey("**")
                .parseClaimsJws(token)
                .getBody()
                .get("userId", String.class);

        return new String[]{userId, role};
    }
    @GetMapping("")
    public ResponseEntity<?> getQuestions(){
        try {
            List<Question> questions = questionService.getQuestions();
            if (questions.isEmpty()){
                return ResponseEntity.ok(new ResponseMessage("There are no questions."));
            }
            else{
                return ResponseEntity.ok(questions);
            }
        } catch (DatabaseErrorException e) {
            return  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
    @GetMapping("{questionId}")
    public ResponseEntity<?> getQuestionById(@PathVariable("questionId") Long questionId) {
        try {
            Question question = questionService.getQuestionById(questionId);

            UserDetailDTO userDetails = new UserDetailDTO(
                    question.getUser().getId(),
                    question.getUser().getFirstName(),
                    question.getUser().getLastName(),
                    question.getUser().getEmail(),
                    question.getUser().getRole()
            );

            List<QuestionAnswers> answers = question.getAnswers().stream()
                    .map(answer -> new QuestionAnswers(
                            answer.getId(),
                            answer.getContent(),
                            new UserDetailDTO(answer.getUser().getId(), answer.getUser().getFirstName(), answer.getUser().getLastName(),answer.getUser().getEmail(),answer.getUser().getRole())
                    ))
                    .toList();

            QuestionResponseDTO questionDTO = new QuestionResponseDTO(
                    question.getId(),
                    question.getTitle(),
                    question.getContent(),
                    userDetails,
                    answers
            );

            return ResponseEntity.ok(questionDTO);
        } catch (DatabaseErrorException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        } catch (QuestionNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }


    @GetMapping("byUser/{userId}")
    public ResponseEntity<?> getQuestionsByUser(@PathVariable("userId") Long userId) {
        try {
            User user = userService.GetUserById(userId)
                    .orElseThrow(() -> new UserNotFoundException("User not found."));

            List<Question> questions = questionService.getQuestionsByUserId(user.getId());

            if (questions.isEmpty()) {
                return ResponseEntity.ok("No questions from this user.");
            } else {
                List<QuestionResponseDTO> questionDTOs = questions.stream()
                        .map(question -> {
                            List<QuestionAnswers> answers = question.getAnswers().stream()
                                    .map(answer -> new QuestionAnswers(
                                            answer.getId(),
                                            answer.getContent(),
                                            new UserDetailDTO(answer.getUser().getId(), answer.getUser().getFirstName(), answer.getUser().getLastName(),answer.getUser().getEmail(),answer.getUser().getRole())
                                    ))
                                    .toList();

                            return new QuestionResponseDTO(
                                    question.getId(),
                                    question.getTitle(),
                                    question.getContent(),
                                    new UserDetailDTO(user.getId(), user.getFirstName(), user.getLastName(),user.getEmail(),user.getRole()),
                                    answers
                            );
                        })
                        .toList();

                return ResponseEntity.ok(questionDTOs);
            }
        } catch (DatabaseErrorException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
    @PostMapping("")
    public ResponseEntity<?> createQuestion(@RequestBody questionRequestDTO request,@RequestHeader("Authorization") String authorizationHeader){
        try {
            if (authorizationHeader == null || authorizationHeader.isEmpty()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ResponseMessage("Missing header!"));
            }
            String token = authorizationHeader.substring(7);
            String[] idAndRole = getIdAndRole(token);

            User user = userService.GetUserById(Long.parseLong(idAndRole[0])).orElseThrow(()-> new UserNotFoundException("User not found."));
            Question question = new Question();
            question.setTitle(request.getTitle());
            question.setContent(request.getContent());
            question.setUser(user);
            Question savedQuestion = questionService.createQuestion(question);
            return ResponseEntity.ok(savedQuestion);
        }
        catch (DatabaseErrorException e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        catch (UserNotFoundException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
        catch (IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
        catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
    @DeleteMapping("{questionId}")
    public ResponseEntity<?> deleteQuestionById(@PathVariable("questionId") Long questionId,@RequestHeader("Authorization") String authorizationHeader){

        try{
            if (authorizationHeader == null || authorizationHeader.isEmpty()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ResponseMessage("Missing header!"));
            }
            String token = authorizationHeader.substring(7);
            String[] idAndRole = getIdAndRole(token);

            Question question = questionService.getQuestionById(questionId);
            if(Integer.parseInt(idAndRole[1]) != 1){
                if (question.getUser().getId().equals(Long.parseLong(idAndRole[0]))){
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
                }
            }
            questionService.deleteQuestion(question);
            return ResponseEntity.status(HttpStatus.OK).body("Successfully deleted question");

        }
        catch (DatabaseErrorException e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        catch (QuestionNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
        catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
    @DeleteMapping("byUser/{userId}")
    public ResponseEntity<?> deleteQuestionByUser(@PathVariable("userId") Long userId,@RequestHeader("Authorization") String authorizationHeader){
        try{

            if (authorizationHeader == null || authorizationHeader.isEmpty()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ResponseMessage("Missing header!"));
            }
            String token = authorizationHeader.substring(7);
            String[] idAndRole = getIdAndRole(token);
            if(Integer.parseInt(idAndRole[1]) != 2){
                if (!userId.equals(Long.parseLong(idAndRole[0]))){
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
                }
            }
            questionService.deleteByUser(userId);
            return ResponseEntity.status(HttpStatus.OK).body("Successfully deleted question");

        }
        catch (DatabaseErrorException e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        catch (UserNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
        catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }


    @PutMapping("{questionId}")
    public ResponseEntity<?> updateQuestion(@PathVariable("questionId") Long questionId,@RequestBody updateQuestionDTO request,@RequestHeader("Authorization") String authorizationHeader){
        try{
            if (authorizationHeader == null || authorizationHeader.isEmpty()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ResponseMessage("Missing header!"));
            }
            String token = authorizationHeader.substring(7);
            String[] idAndRole = getIdAndRole(token);

            Question question = questionService.getQuestionById(questionId);
            if(Integer.parseInt(idAndRole[1]) != 1){
                if (question.getUser().getId().equals(Long.parseLong(idAndRole[0]))){
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
                }
            }
            question = questionService.updateQuestion(questionId,request);
            return ResponseEntity.ok(question);
        }
        catch (DatabaseErrorException e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        catch (QuestionNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
        catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());

        }
    }

}
