    package com.example.demo.Services;

    import com.example.demo.DTOS.CreateAdRequestDTO;
    import com.example.demo.DTOS.UpdateAdRequestDTO;
    import com.example.demo.Exceptions.AdNotFoundException;
    import com.example.demo.Exceptions.DatabaseErrorException;
    import com.example.demo.Exceptions.UserNotFoundException;
    import com.example.demo.Models.Ad;
    import com.example.demo.Models.User;
    import com.example.demo.Repositories.AdsRepository;
    import jakarta.transaction.Transactional;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.stereotype.Service;
    import org.springframework.web.multipart.MultipartFile;

    import java.io.File;
    import java.io.IOException;
    import java.io.InputStream;
    import java.nio.file.Files;
    import java.nio.file.StandardCopyOption;
    import java.util.ArrayList;
    import java.util.List;
    import java.util.Optional;

    @Service
    public class AdService {

        private final AdsRepository adsRepository;
        private final UserService userService;

        @Autowired
        public AdService(AdsRepository adsRepository, UserService userService) {
            this.adsRepository = adsRepository;
            this.userService = userService;
        }

        public List<Ad> getAds() {
            return adsRepository.findAll();
        }

        public List<Ad> getAdsByUserId(Long userId) {

            Optional<User> user = userService.GetUserById(userId);
            if (user.isEmpty()) {
                throw new UserNotFoundException("User with this id doesn't exist.");
            }

            return adsRepository.getAdsByUserId(userId);
        }

        public Ad getAdById(Long adId) {
            return adsRepository.getAdById(adId)
                    .orElseThrow(() -> new AdNotFoundException("Ad with this id doesn't exist."));
        }

        @Transactional
        public void deleteAdsByUserId(Long userId) {
            Optional<User> user = userService.GetUserById(userId);

            if (user.isEmpty()) {
                throw new UserNotFoundException("User with this id doesn't exist.");
            }
            adsRepository.deleteByUserId(userId);
        }

        public void deleteAdById(Long adId) {
            Ad ad = adsRepository.getAdById(adId)
                    .orElseThrow(() -> new AdNotFoundException("Ad with this id doesn't exist."));

            String adDirectoryPath = "src/main/resources/static/AdsImages/" + ad.getId();
            File adDirectory = new File(adDirectoryPath);
            if (adDirectory.exists() && adDirectory.isDirectory()) {
                deleteDirectory(adDirectory);
            }

            adsRepository.deleteById(ad.getId());
        }

        private void deleteDirectory(File directory) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        deleteDirectory(file);
                    } else {
                        file.delete();
                    }
                }
            }
            directory.delete();
        }


        @Transactional
        public Ad createAd(Long userId, CreateAdRequestDTO adRequest, List<MultipartFile> images) throws DatabaseErrorException {
            User user = userService.GetUserById(userId)
                    .orElseThrow(() -> new UserNotFoundException("User with this id doesn't exist."));

            Ad ad = new Ad();
            ad.setTitle(adRequest.getTitle());
            ad.setCity(adRequest.getCity());
            ad.setCountry(adRequest.getCountry());
            ad.setPrice(adRequest.getPrice());
            ad.setSize(adRequest.getSize());
            ad.setType(adRequest.getType());
            ad.setUser(user);

            Ad savedAd = adsRepository.save(ad);

            String adDirectoryPath = "src/main/resources/static/AdsImages/" + savedAd.getId();
            new File(adDirectoryPath).mkdirs();

            List<String> savedImagePaths = new ArrayList<>();
            for (MultipartFile image : images) {
                if (image.isEmpty()) {
                    throw new DatabaseErrorException("Cannot upload empty file: " + image.getOriginalFilename());
                }

                String finalFilePath = adDirectoryPath + "/" + image.getOriginalFilename().replace(" ", "_");
                File finalFile = new File(finalFilePath);

                try (InputStream inputStream = image.getInputStream()) {
                    Files.copy(inputStream, finalFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    savedImagePaths.add("AdsImages/" + savedAd.getId() + "/" + image.getOriginalFilename().replace(" ", "_"));
                } catch (IOException e) {
                    throw new DatabaseErrorException("Error saving image: " + e.getMessage());
                }
            }

            ad.setImagePaths(savedImagePaths);
            return adsRepository.save(ad);
        }


        public Ad updateAd(Long adId, UpdateAdRequestDTO updatedAd) {

            Ad ad = adsRepository.getAdById(adId)
                    .orElseThrow(() -> new AdNotFoundException("Ad with this id doesn't exist"));

            if (updatedAd.getTitle() != null && !updatedAd.getTitle().trim().isEmpty()) {
                ad.setTitle(updatedAd.getTitle());
            }

            if (updatedAd.getCity() != null && !updatedAd.getCity().trim().isEmpty()) {
                ad.setCity(updatedAd.getCity());
            }

            if (updatedAd.getCountry() != null && !updatedAd.getCountry().trim().isEmpty()) {
                ad.setCountry(updatedAd.getCountry());
            }

            if (updatedAd.getPrice() != null) {
                ad.setPrice(updatedAd.getPrice());
            }

            if (updatedAd.getSize() != null) {
                ad.setSize(updatedAd.getSize());
            }

            if (updatedAd.getType() != null && (updatedAd.getType() == 0 || updatedAd.getType() == 1)) {
                ad.setType(updatedAd.getType());
            }

            return adsRepository.save(ad);
        }

        public void deleteByUser(Long userId){

            User user = userService.GetUserById(userId).orElseThrow(()->new UserNotFoundException("User doesn't exist."));
            userService.deleteUserById(userId);
        }

    }
