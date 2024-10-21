package com.example.demo.Controllers;


import com.example.demo.DTOS.AdResponseDTO;
import com.example.demo.DTOS.CreateAdRequestDTO;
import com.example.demo.DTOS.UpdateAdRequestDTO;
import com.example.demo.DTOS.UserDetailDTO;
import com.example.demo.Exceptions.AdNotFoundException;
import com.example.demo.Exceptions.DatabaseErrorException;
import com.example.demo.Exceptions.ResponseMessage;
import com.example.demo.Exceptions.UserNotFoundException;
import com.example.demo.Models.Ad;
import com.example.demo.Services.AdService;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.swing.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "api/ads")
public class AdController {

    private final AdService adsService;
    @Autowired
    public AdController(AdService adsService){
        this.adsService = adsService;
    }
    private String[] getRoleAndId(String token) {
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
    public ResponseEntity<?> GetAds(){
        try {
            List<Ad> ads = adsService.getAds();

            List<AdResponseDTO> responses = ads.stream()
                    .map(ad -> new AdResponseDTO(
                            ad.getId(),
                            ad.getTitle(),
                            ad.getCity(),
                            ad.getCountry(),
                            ad.getPrice(),
                            ad.getSize(),
                            ad.getType(),
                            ad.getImagePaths(),
                            new UserDetailDTO(
                                    ad.getUser().getId(),
                                    ad.getUser().getFirstName(),
                                    ad.getUser().getLastName(),
                                    ad.getUser().getEmail(),
                                    ad.getUser().getRole()
                            )
                    ))
                    .collect(Collectors.toList());

            if(responses.isEmpty()){
                return ResponseEntity.ok(new ResponseMessage("No ads"));
            }
            return ResponseEntity.ok(responses);

        }
        catch (DatabaseErrorException e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/byUser/{userId}")
    public ResponseEntity<?> getAdsByUser(@PathVariable Long userId) {
        try {
            List<Ad> ads = adsService.getAdsByUserId(userId);
            List<AdResponseDTO> responses = ads.stream()
                    .map(ad -> new AdResponseDTO(
                            ad.getId(),
                            ad.getTitle(),
                            ad.getCity(),
                            ad.getCountry(),
                            ad.getPrice(),
                            ad.getSize(),
                            ad.getType(),
                            ad.getImagePaths(),
                            new UserDetailDTO(
                                    ad.getUser().getId(),
                                    ad.getUser().getFirstName(),
                                    ad.getUser().getLastName(),
                                    ad.getUser().getEmail(),
                                    ad.getUser().getRole()
                            )
                    ))
                    .collect(Collectors.toList());

            if (responses.isEmpty()) {
                return ResponseEntity.ok(new ResponseMessage("No ads found for the user"));
            }
            return ResponseEntity.ok(responses);
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (DatabaseErrorException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }


    @PostMapping(value = "", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> CreateAd(
            @RequestParam("title") String title,
            @RequestParam("city") String city,
            @RequestParam("country") String country,
            @RequestParam("price") Float price,
            @RequestParam("size") Float size,
            @RequestParam("type") Integer type,
            @RequestParam("images") List<MultipartFile> images,
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        try {

            String token = authorizationHeader.substring(7);
            String[] roleAndId = getRoleAndId(token);

            long userId = Long.parseLong(roleAndId[0]);
            CreateAdRequestDTO request = new CreateAdRequestDTO();
            request.setTitle(title);
            request.setCity(city);
            request.setCountry(country);
            request.setPrice(price);
            request.setSize(size);
            request.setType(type);

            Ad createdAd = adsService.createAd(userId, request, images);

            AdResponseDTO createdAdResponse = new AdResponseDTO(
                    createdAd.getId(),
                    createdAd.getTitle(),
                    createdAd.getCity(),
                    createdAd.getCountry(),
                    createdAd.getPrice(),
                    createdAd.getSize(),
                    createdAd.getType(),
                    createdAd.getImagePaths(),
                    new UserDetailDTO(
                            createdAd.getUser().getId(),
                            createdAd.getUser().getFirstName(),
                            createdAd.getUser().getLastName(),
                            createdAd.getUser().getEmail(),
                            createdAd.getUser().getRole()
                    )
            );

            return ResponseEntity.ok(createdAdResponse);
        } catch (DatabaseErrorException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }



    @DeleteMapping("{adId}")
    public ResponseEntity<?> deleteAd(@PathVariable Long adId,@RequestHeader("Authorization") String authorizationHeader) {
        try {
            if (authorizationHeader == null || authorizationHeader.isEmpty()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ResponseMessage("Missing header!"));
            }

            String token = authorizationHeader.substring(7);
            String[] roleAndId = getRoleAndId(token);

            Ad ad = adsService.getAdById(adId);
            if(Integer.parseInt(roleAndId[1]) != 1){
                if(!ad.getUser().getId().toString().equals(roleAndId[0])){
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ResponseMessage("Cannot access!"));
                }
            }

            adsService.deleteAdById(adId);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (AdNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (DatabaseErrorException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
    @PutMapping("{adId}")
    public ResponseEntity<?> updateAd(@PathVariable Long adId, @RequestBody UpdateAdRequestDTO request,@RequestHeader("Authorization") String authorizationHeader){
        try {
            if (authorizationHeader == null || authorizationHeader.isEmpty()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ResponseMessage("Missing header!"));
            }
            String token = authorizationHeader.substring(7);
            String[] roleAndId = getRoleAndId(token);

            Ad ad = adsService.getAdById(adId);
            if(Integer.parseInt(roleAndId[1]) != 1){
                if(!ad.getUser().getId().toString().equals(roleAndId[0])){
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ResponseMessage("Cannot access!"));
                }
            }

            Ad updatedAd = adsService.updateAd(adId,request);
            AdResponseDTO updatedAdResponse = new AdResponseDTO(
                    updatedAd.getId(),
                    updatedAd.getTitle(),
                    updatedAd.getCity(),
                    updatedAd.getCountry(),
                    updatedAd.getPrice(),
                    updatedAd.getSize(),
                    updatedAd.getType(),
                    updatedAd.getImagePaths(),
                    new UserDetailDTO(
                            updatedAd.getUser().getId(),
                            updatedAd.getUser().getFirstName(),
                            updatedAd.getUser().getLastName(),
                            updatedAd.getUser().getEmail(),
                            updatedAd.getUser().getRole()
                    )
            );

            return ResponseEntity.ok(updatedAdResponse);
        } catch (AdNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (DatabaseErrorException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
    @DeleteMapping("byUser/{userId}")
    public ResponseEntity<?> deleteByUser(@PathVariable("userId") Long userId){
        try {

            adsService.deleteByUser(userId);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Deleted successfully");
        }
        catch (UserNotFoundException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }



}
