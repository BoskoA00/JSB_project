package com.example.demo.Controllers;

import com.example.demo.DTOS.*;
import com.example.demo.Exceptions.*;
import com.example.demo.Models.Answer;
import com.example.demo.Models.Question;
import com.example.demo.Models.User;
import com.example.demo.Services.*;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.xml.crypto.Data;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "api/answers")
public class AnswerController {
    private final AnswerService answerService;
    private final UserService userService;
    private final QuestionService questionService;

    @Autowired
    public AnswerController( AnswerService answerService, QuestionService questionService, UserService userService){
        this.userService = userService;
        this.questionService = questionService;
        this.answerService = answerService;
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
    public ResponseEntity<?> GetAll(){
        try {
            List<Answer> answers = answerService.getAll();

            List<AnswersResponseDTO> answersResponse = answers.stream()
                    .map(answer -> {
                        UserDetailDTO userDetail = new UserDetailDTO(
                                answer.getUser().getId(),
                                answer.getUser().getFirstName(),
                                answer.getUser().getLastName(),
                                answer.getUser().getEmail(),
                                answer.getUser().getRole()
                        );

                        QuestionDetails questionDetails = new QuestionDetails(
                                answer.getQuestion().getId(),
                                answer.getQuestion().getTitle(),
                                answer.getQuestion().getContent(),
                                new UserDetailDTO(
                                        answer.getQuestion().getUser().getId(),
                                        answer.getQuestion().getUser().getFirstName(),
                                        answer.getQuestion().getUser().getLastName(),
                                        answer.getQuestion().getUser().getEmail(),
                                        answer.getQuestion().getUser().getRole()
                                )
                        );

                        return new AnswersResponseDTO(
                                answer.getId(),
                                answer.getContent(),
                                userDetail,
                                questionDetails
                        );
                    })
                    .collect(Collectors.toList());

            if(answersResponse.isEmpty()){
                return ResponseEntity.ok(new ResponseMessage("There are no answers"));
            }

            return ResponseEntity.ok(answersResponse);
        } catch (DatabaseErrorException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("byUser/{userId}")
    public ResponseEntity<?> getAnswersByUser(@PathVariable("userId") Long userId) {
        try {
            List<Answer> answers = answerService.getByUser(userId);

            List<AnswersResponseDTO> answersResponse = answers.stream()
                    .map(answer -> {
                        UserDetailDTO userDetail = new UserDetailDTO(
                                answer.getUser().getId(),
                                answer.getUser().getFirstName(),
                                answer.getUser().getLastName(),
                                answer.getUser().getEmail(),
                                answer.getUser().getRole()
                        );

                        QuestionDetails questionDetails = new QuestionDetails(
                                answer.getQuestion().getId(),
                                answer.getQuestion().getTitle(),
                                answer.getQuestion().getContent(),
                                new UserDetailDTO(
                                        answer.getQuestion().getUser().getId(),
                                        answer.getQuestion().getUser().getFirstName(),
                                        answer.getQuestion().getUser().getLastName(),
                                        answer.getQuestion().getUser().getEmail(),
                                        answer.getQuestion().getUser().getRole()
                                )
                        );

                        return new AnswersResponseDTO(
                                answer.getId(),
                                answer.getContent(),
                                userDetail,
                                questionDetails
                        );
                    })
                    .collect(Collectors.toList());

            if (answersResponse.isEmpty()) {
                return ResponseEntity.ok(new ResponseMessage("No answers found for this user."));
            }

            return ResponseEntity.ok(answersResponse);
        } catch (DatabaseErrorException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("byQuestion/{questionId}")
    public ResponseEntity<?> getAnswersByQuestion(@PathVariable("questionId") Long questionId) {
        try {
            List<Answer> answers = answerService.getByQuestion(questionId);

            List<AnswersResponseDTO> answersResponse = answers.stream()
                    .map(answer -> {
                        UserDetailDTO userDetail = new UserDetailDTO(
                                answer.getUser().getId(),
                                answer.getUser().getFirstName(),
                                answer.getUser().getLastName(),
                                answer.getUser().getEmail(),
                                answer.getUser().getRole()
                        );

                        QuestionDetails questionDetails = new QuestionDetails(
                                answer.getQuestion().getId(),
                                answer.getQuestion().getTitle(),
                                answer.getQuestion().getContent(),
                                new UserDetailDTO(
                                        answer.getQuestion().getUser().getId(),
                                        answer.getQuestion().getUser().getFirstName(),
                                        answer.getQuestion().getUser().getLastName(),
                                        answer.getQuestion().getUser().getEmail(),
                                        answer.getQuestion().getUser().getRole()
                                )
                        );

                        return new AnswersResponseDTO(
                                answer.getId(),
                                answer.getContent(),
                                userDetail,
                                questionDetails
                        );
                    })
                    .collect(Collectors.toList());

            if (answersResponse.isEmpty()) {
                return ResponseEntity.ok(new ResponseMessage("No answers found for this question."));
            }

            return ResponseEntity.ok(answersResponse);
        } catch (DatabaseErrorException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("")
    public ResponseEntity<?> createAnswer(@RequestBody createAnswerDTO request,@RequestHeader("Authorization") String authorizationHeader){
        try {
            if (authorizationHeader == null || authorizationHeader.isEmpty()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ResponseMessage("Missing header!"));
            }
            String token = authorizationHeader.substring(7);
            String[] idAndRole = getIdAndRole(token);

            Answer answer = new Answer();
            User user = userService.GetUserById(Long.parseLong(idAndRole[0])).get();
            Question question = questionService.getQuestionById(request.getQuestionId());

            answer.setContent(request.getContent());
            answer.setQuestion(question);
            answer.setUser(user);

            Answer savedAnswer = answerService.createAnswer(answer);
            AnswersResponseDTO createdAnswerDTO = new AnswersResponseDTO(
                    savedAnswer.getId(),
                    savedAnswer.getContent(),
                    new UserDetailDTO(
                            savedAnswer.getUser().getId(),
                            savedAnswer.getUser().getFirstName(),
                            savedAnswer.getUser().getLastName(),
                            savedAnswer.getUser().getEmail(),
                            savedAnswer.getUser().getRole()
                    ),
                    new QuestionDetails(
                            savedAnswer.getQuestion().getId(),
                            savedAnswer.getQuestion().getTitle(),
                            savedAnswer.getQuestion().getContent(),
                            new UserDetailDTO(
                                    savedAnswer.getQuestion().getUser().getId(),
                                    savedAnswer.getQuestion().getUser().getFirstName(),
                                    savedAnswer.getQuestion().getUser().getLastName(),
                                    savedAnswer.getQuestion().getUser().getEmail(),
                                    savedAnswer.getQuestion().getUser().getRole()
                            )
                    )
            );
            return ResponseEntity.ok(createdAnswerDTO);

        }
        catch (DatabaseErrorException e){
            return  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        catch (UserNotFoundException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
        catch (QuestionNotFoundException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
        catch (IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
        catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("{answerId}")
    public ResponseEntity<?> updateAnswer(@PathVariable("answerId") Long answerId,@RequestBody updateAnswerDTO request,@RequestHeader("Authorization") String authorizationHeader){
        try {
            if (authorizationHeader == null || authorizationHeader.isEmpty()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ResponseMessage("Missing header!"));
            }
            String token = authorizationHeader.substring(7);
            String[] idAndRole = getIdAndRole(token);

            Answer answer = answerService.getAnswerById(answerId);
            if(Integer.parseInt(idAndRole[1]) != 1){
                if (answer.getUser().getId().equals(Long.parseLong(idAndRole[0]))){
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ResponseMessage("Can't access!"));
                }
            }

            Answer updatedAnswer = answerService.updateAnswer(answerId,request.getContent());
            AnswersResponseDTO updatedAnswerDTO = new AnswersResponseDTO(
                    updatedAnswer.getId(),
                    updatedAnswer.getContent(),
                    new UserDetailDTO(
                            updatedAnswer.getUser().getId(),
                            updatedAnswer.getUser().getFirstName(),
                            updatedAnswer.getUser().getLastName(),
                            updatedAnswer.getUser().getEmail(),
                            updatedAnswer.getUser().getRole()
                    ),
                    new QuestionDetails(
                            updatedAnswer.getQuestion().getId(),
                            updatedAnswer.getQuestion().getTitle(),
                            updatedAnswer.getQuestion().getContent(),
                            new UserDetailDTO(
                                    updatedAnswer.getQuestion().getUser().getId(),
                                    updatedAnswer.getQuestion().getUser().getFirstName(),
                                    updatedAnswer.getQuestion().getUser().getLastName(),
                                    updatedAnswer.getQuestion().getUser().getEmail(),
                                    updatedAnswer.getQuestion().getUser().getRole()
                            )
                    )
            );
        return ResponseEntity.ok(updatedAnswerDTO);
        }
        catch (DatabaseErrorException e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        catch (IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
        catch (AnswerNotFound e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
        catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
    @DeleteMapping("{answerId}")
    public ResponseEntity<?> deleteAnswer(@PathVariable("answerId") Long answerId){
        try {
            Answer answer = answerService.getAnswerById(answerId);
            answerService.deleteAnswer(answer);
            return ResponseEntity.ok(new ResponseMessage("Answer successfully deleted."));
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
    public ResponseEntity<?> deleteByUser(@PathVariable("userId") Long userId){
        try{
            Optional<User> user = userService.GetUserById(userId);
            answerService.deleteByUser(user.get().getId());
            return ResponseEntity.ok(new ResponseMessage("Answers successfully deleted."));

        }
        catch (DatabaseErrorException e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        catch (UserNotFoundException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
        catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
    @DeleteMapping("byQuestion/{questionId}")
    public ResponseEntity<?> deleteByQuestion(@PathVariable("questionId") Long questionId){
        try{
            Question question = questionService.getQuestionById(questionId);
            answerService.deleteByQuestion(question.getId());
            return ResponseEntity.ok(new ResponseMessage("Answers successfully deleted."));

        }
        catch (DatabaseErrorException e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        catch (QuestionNotFoundException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
        catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
