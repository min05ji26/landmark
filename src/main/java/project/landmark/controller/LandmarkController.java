package project.landmark.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import project.landmark.entity.Landmark;
import project.landmark.service.LandmarkService;

import java.util.List;

@RestController
@RequestMapping("/landmarks")
@RequiredArgsConstructor
public class LandmarkController {

    private final LandmarkService landmarkService;

    @GetMapping
    public List<Landmark> getAllLandmarkList(){
        return landmarkService.findAll();
    }
}
